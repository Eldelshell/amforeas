/**
 * Copyright (C) 2011, 2012 Alejandro Ayuso
 *
 * This file is part of Jongo.
 * Jongo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * Jongo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Jongo.  If not, see <http://www.gnu.org/licenses/>.
 */

package jongo.filter;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import jongo.JongoUtils;
import jongo.rest.xstream.JongoError;
import jongo.rest.xstream.JongoHead;
import jongo.rest.xstream.JongoSuccess;
import jongo.rest.xstream.Row;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default filter which is used by Jongo to modify the requests and responses. Here's were we generate the
 * JSON and XML output for all the {@link jongo.rest.xstream.JongoResponse} objects and add some
 * headers which might prove useful.
 * In this case, we're doing the serialization to XML and JSON manually because it's not that hard
 * and I can save on dependencies.
 * @author Alejandro Ayuso 
 */
public class DefaultFormatFilter implements ContainerResponseFilter, JongoFormatFilter {

    private static final Logger l = LoggerFactory.getLogger(DefaultFormatFilter.class);

    /**
     * Filters all requests & responses from a JongoWS
     * @param cr
     * @param cr1
     * @return 
     */
    @Override
    public ContainerResponse filter(ContainerRequest cr, ContainerResponse cr1) {
        final MediaType mime = getMediaTypeFromRequest(cr);
        final Response incResponse = cr1.getResponse();
        final Response formattedResponse = format(incResponse, mime);
        cr1.setResponse(formattedResponse);
        setHeadersToResponse(cr1, formattedResponse.getEntity(), mime);
        return cr1;
    }

    /**
     * Gives format to a given {@linkplain javax.ws.rs.core.Response}. Basically we add headers and generate
     * the appropriate JSON or XML representation of them.
     * @param response a {@linkplain javax.ws.rs.core.Response} from a JongoWS
     * @param mime a {@linkplain javax.ws.rs.core.MediaType}
     * @return a modified {@linkplain javax.ws.rs.core.Response}
     */
    @Override
    public Response format(Response response, final MediaType mime) {
        final Object entity = response.getEntity();
        final Integer status = response.getStatus();
        if(entity instanceof JongoSuccess){
            return formatSuccessResponse((JongoSuccess)entity, mime, status);
        }else if(entity instanceof JongoError){
            return formatErrorResponse((JongoError)entity, mime, status);
        }else if(entity instanceof JongoHead){
            return formatHeadResponse((JongoHead)entity, mime, status);
        }else{
            return response;
        }
    }
    
    /**
     * Adds the DATE, Content-Length, Content-MD5 and Content-Type headers to the given 
     * {@linkplain com.sun.jersey.spi.container.ContainerResponse}
     * @param cr1 a {@linkplain com.sun.jersey.spi.container.ContainerResponse}.
     * @param entity the response body. Used to generate the Content-Length, Content-MD5 headers.
     * @param mime a {@linkplain javax.ws.rs.core.MediaType} used to normalize the Content-Type header.
     */
    private void setHeadersToResponse(ContainerResponse cr1, final Object entity, final MediaType mime){
        if(entity != null){
            cr1.getHttpHeaders().add(HttpHeaders.DATE, JongoUtils.getDateHeader());
            cr1.getHttpHeaders().add("Content-MD5", JongoUtils.getMD5Base64(entity.toString()));
            cr1.getHttpHeaders().add(HttpHeaders.CONTENT_LENGTH, JongoUtils.getOctetLength(entity.toString()));
            if(isXMLCompatible(mime))
                cr1.getHttpHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML);
            else
                cr1.getHttpHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        }
    }
    
    /**
     * For a given {@linkplain com.sun.jersey.spi.container.ContainerRequest} obtain the ACCEPT header
     * and return a {@linkplain javax.ws.rs.core.MediaType} which is compatible with Jongo (JSON or XML). This
     * is because some received mime types were behaving like it was expected or simply because the 
     * {@linkplain javax.ws.rs.core.MediaType.valueOf()} method wasn't able to parse them. This is particularly
     * true for browsers which use something like {@code text/html,application/xhtml+xml,application/xml;q=0.9,*\/*;q=0.8}
     * @param cr {@linkplain com.sun.jersey.spi.container.ContainerRequest} to get the ACCEPT header.
     * @return if the {@linkplain javax.ws.rs.core.MediaType.valueOf()} is able to parse the MIME we return it. Else
     * an exception is catched and we return jongo default transport (application/json)
     */
    private MediaType getMediaTypeFromRequest(final ContainerRequest cr){
        MediaType mime;
        final String rawMime = cr.getHeaderValue(HttpHeaders.ACCEPT);
        try{
            mime = MediaType.valueOf(rawMime);
        }catch(IllegalArgumentException e){
            l.debug("Failed to parse MIME in Accept header " + rawMime + ". Returning default application/json");
            mime = MediaType.valueOf(MediaType.APPLICATION_JSON);
        }
        return mime;
    }
    
    /**
     * For a given {@linkplain javax.ws.rs.core.MediaType} check for compatibility. Basically we're changing the
     * default implementation of the {@linkplain javax.ws.rs.core.MediaType.isWildcardType} to return false.
     * @param mime a {@linkplain javax.ws.rs.core.MediaType} MIME
     * @return true if the given {@linkplain javax.ws.rs.core.MediaType} is application/xml compatible.
     */
    private boolean isXMLCompatible(final MediaType mime){
        if(mime.isWildcardType())
            return false;
        if(mime.isCompatible(MediaType.valueOf(MediaType.APPLICATION_XML)))
            return true;
        return false;
    }
    
    /**
     * Generates a new {@linkplain javax.ws.rs.core.Response} for a given 
     * {@link jongo.rest.xstream.JongoSuccess} generating the appropriate XML or JSON representation 
     * and setting the Content-Count and Content-Location headers.
     * @param response a {@link jongo.rest.xstream.JongoSuccess} to be converted to JSON or XML.
     * @param mime the {@linkplain javax.ws.rs.core.MediaType} used to determine the transport format.
     * @param status the current HTTP code of the response.
     * @return a new {@linkplain javax.ws.rs.core.Response} with the new headers and the content body
     * in either XML or JSON.
     */
    private Response formatSuccessResponse(final JongoSuccess response, final MediaType mime, final Integer status) {
        String res;
        l.debug("Formatting Success Response");
        if(isXMLCompatible(mime)){
            StringBuilder b = new StringBuilder("<response><success>");
            b.append(response.isSuccess());b.append("</success><resource>");
            b.append(response.getResource());b.append("</resource><rows>");
            for(Row r : response.getRows()){
                b.append("<row roi=\"");
                b.append(r.getRoi());
                b.append("\"><cells>");
                for (String key : r.getCells().keySet()) {
                    String val = r.getCells().get(key);
                    b.append("<");
                    b.append(key.toLowerCase());
                    b.append(">");
                    b.append(val);
                    b.append("</");
                    b.append(key.toLowerCase());
                    b.append(">");
                }
                b.append("</cells></row>");
            }
            b.append("</rows></response>");
            res = b.toString();
        }else{
            StringBuilder b = new StringBuilder("{");
            b.append("\"success\":");b.append(response.isSuccess());
            b.append(",\"cells\":[ "); //this last space is important!
            for(Row r : response.getRows()){
                List<String> args = new ArrayList<String>();
                for (String key : r.getCells().keySet()) {
                    String val = r.getCells().get(key);
                    if (StringUtils.isNumeric(val)) {
                        if (StringUtils.isWhitespace(val)) {
                            args.add("\"" + key.toLowerCase() + "\"" + ":" + "\"\"");
                        } else {
                            args.add("\"" + key.toLowerCase() + "\"" + ":" + val);
                        }
                    } else {
                        args.add("\"" + key.toLowerCase() + "\"" + ":" + "\"" + val + "\"");
                    }
                }

                b.append("{");
                b.append(StringUtils.join(args, ","));
                b.append("}");
                b.append(",");
            }
            b.deleteCharAt(b.length() - 1);
            b.append("]}");
            res = b.toString();
        }
        
        return Response.status(status)
                .entity(res)
                .header("Content-Count", response.getRows().size())
                .header(HttpHeaders.CONTENT_LOCATION, response.getResource())
                .build();
    }

    /**
     * Generates a new {@linkplain javax.ws.rs.core.Response} for a given 
     * {@link jongo.rest.xstream.JongoError} generating the appropriate XML or JSON representation 
     * and setting the Content-Location header.
     * @param response a {@link jongo.rest.xstream.JongoSuccess} to be converted to JSON or XML.
     * @param mime the {@linkplain javax.ws.rs.core.MediaType} used to determine the transport format.
     * @param status the current HTTP code of the response.
     * @return a new {@linkplain javax.ws.rs.core.Response} with the new headers and the content body
     * in either XML or JSON.
     */
    private Response formatErrorResponse(final JongoError response, final MediaType mime, final Integer status) {
        String res;
        l.debug("Formatting Error Response");
        if(isXMLCompatible(mime)){
            StringBuilder b = new StringBuilder("<response><success>")
                .append(response.isSuccess())
                .append("</success><message>")
                .append(response.getMessage())
                .append("</message>");
            if( response.getSqlCode() != null && response.getSqlState() != null){
                b.append("<sqlState>").append(response.getSqlState()).append("</sqlState>");
                b.append("<sqlCode>").append(response.getSqlCode()).append("</sqlCode>");
            }
            b.append("</response>");
            res = b.toString();
        }else{
            StringBuilder b = new StringBuilder("{")
                .append("\"success\":")
                .append(response.isSuccess())
                .append(",\"message\":\"")
                .append(response.getMessage());
            if( response.getSqlCode() != null && response.getSqlState() != null){
                b.append("\",\"SQLState\":\"").append(response.getSqlState());
                b.append("\",\"SQLCode\":\"").append(response.getSqlCode());
            }
            b.append("\"}");
            res = b.toString();
        }
        return Response.status(status)
                .entity(res)
                .type(mime)
                .header("Content-Location", response.getResource())
                .build();
    }

    /**
     * Generates a new {@linkplain javax.ws.rs.core.Response} for a given 
     * {@link jongo.rest.xstream.JongoHead} generating the appropriate XML or JSON representation 
     * and setting the Content-Location header.
     * @param response a {@link jongo.rest.xstream.JongoSuccess} to be converted to JSON or XML.
     * @param mime the {@linkplain javax.ws.rs.core.MediaType} used to determine the transport format.
     * @param status the current HTTP code of the response.
     * @return a new {@linkplain javax.ws.rs.core.Response} with the new headers.
     */
    private Response formatHeadResponse(final JongoHead response, final MediaType mime, final Integer status) {
        final List<String> args = new ArrayList<String>();
        for(Row row : response.getRows()){
            final String columnname = row.getCells().get("columnName");
            final String columntype = row.getCells().get("columnType");
            final String columnsize = row.getCells().get("columnSize");
            StringBuilder b = new StringBuilder(columnname)
                .append("=")
                .append(columntype)
                .append("(")
                .append(columnsize)
                .append(")");
            args.add(b.toString());
        }
        String res = StringUtils.join(args, ";");
        return Response.status(status)
                .type(mime)
                .header("Content-Location", response.getResource())
                .header(StringUtils.capitalize(response.getResource()), res)
                .build();
    }
}
