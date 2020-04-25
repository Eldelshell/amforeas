/**
 * Copyright (C) 2011, 2012 Alejandro Ayuso
 *
 * This file is part of Amforeas.
 * Amforeas is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * Amforeas is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Amforeas.  If not, see <http://www.gnu.org/licenses/>.
 */
package amforeas.filter;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Amforeas Format Filters implementations are to be used as a container filter to modify responses.
 * The idea is that by creating your own filters, you can
 * generate different transports. For example, someone might want to use XStream to
 * generate the XML and JSON. Someone else might want to use Protobuf. By implementing a ProtobufFormatFilter
 * and changing it in the web.xml file, you can use protobuf serialization.
 * @see com.sun.jersey.api.container.filter.GZIPContentEncodingFilter
 * @author Alejandro Ayuso 
 */
public interface AmforeasFormatFilter {
    
    /**
     * Gives a different format to a response.
     * @param response the Response to be modified.
     * @param mime the mime of the response.
     * @return a modified Response.
     */
    public Response format(Response response, final MediaType mime);
    
}
