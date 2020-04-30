/**
 * Copyright (C) Alejandro Ayuso
 *
 * This file is part of Amforeas. Amforeas is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * Amforeas is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Amforeas. If not, see <http://www.gnu.org/licenses/>.
 */
package org.amforeas.demo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import amforeas.rest.xstream.AmforeasResponse;
import amforeas.rest.xstream.ErrorResponse;
import amforeas.rest.xstream.Row;
import amforeas.rest.xstream.SuccessResponse;

/**
 *
 */
public class AmforeasClient {

    private static final String aUrl = "http://localhost:8080/amforeas/demo1/";

    public AmforeasResponse doGET (final String url) {
        return doRequest(url, "GET");
    }

    public AmforeasResponse doDELETE (final String url) {
        return doRequest(url, "DELETE");
    }

    public AmforeasResponse doPUT (final String url, final String jsonParameters) {
        return doRequest(url, "PUT", jsonParameters);
    }

    public AmforeasResponse doPOST (final String url, final String jsonParameters) {
        return doRequest(url, "POST", jsonParameters);
    }

    public AmforeasResponse doPOST (final String url, final List<NameValuePair> parameters) {
        return doRequest(url, parameters);
    }

    private AmforeasResponse doRequest (final String url, final String method) {
        AmforeasResponse response = null;
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(aUrl + url).openConnection();
            con.setRequestMethod(method);
            con.setDoOutput(true);
            con.setRequestProperty("Accept", MediaType.APPLICATION_XML);
            BufferedReader r = null;

            if (con.getResponseCode() != Response.Status.OK.getStatusCode()) {
                r = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            } else {
                r = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }
            StringBuilder rawresponse = new StringBuilder();
            String strLine = null;
            while ((strLine = r.readLine()) != null) {
                rawresponse.append(strLine);
                rawresponse.append("\n");
            }
            try {
                response = successFromXML(rawresponse.toString());
            } catch (Exception e) {
                response = errorFromXML(rawresponse.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }

    private AmforeasResponse doRequest (final String url, final String method, final String jsonParameters) {
        AmforeasResponse response = null;

        try {
            HttpURLConnection con = (HttpURLConnection) new URL(aUrl + url).openConnection();
            con.setRequestMethod(method);
            con.setRequestProperty("Accept", MediaType.APPLICATION_XML);
            con.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
            con.setRequestProperty("Content-Length", "" + Integer.toString(jsonParameters.getBytes().length));
            con.setDoOutput(true);
            con.setDoInput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(jsonParameters);
            wr.flush();
            wr.close();

            // BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));

            BufferedReader r = null;
            if (con.getResponseCode() != Response.Status.OK.getStatusCode() && con.getResponseCode() != Response.Status.CREATED.getStatusCode()) {
                r = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            } else {
                r = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }

            StringBuilder rawresponse = new StringBuilder();
            String strLine = null;
            while ((strLine = r.readLine()) != null) {
                rawresponse.append(strLine);
                rawresponse.append("\n");
            }

            try {
                response = successFromXML(rawresponse.toString());
            } catch (Exception e) {
                response = errorFromXML(rawresponse.toString());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return response;
    }

    private AmforeasResponse doRequest (final String url, final List<NameValuePair> parameters) {
        final String urlParameters = URLEncodedUtils.format(parameters, "UTF-8");
        AmforeasResponse response = null;

        try {
            HttpURLConnection con = (HttpURLConnection) new URL(aUrl + url).openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", MediaType.APPLICATION_XML);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
            con.setDoOutput(true);
            con.setDoInput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            BufferedReader r = null;
            if (con.getResponseCode() != Response.Status.CREATED.getStatusCode()) {
                r = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            } else {
                r = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }

            StringBuilder rawresponse = new StringBuilder();
            String strLine = null;
            while ((strLine = r.readLine()) != null) {
                rawresponse.append(strLine);
                rawresponse.append("\n");
            }

            try {
                response = successFromXML(rawresponse.toString());
            } catch (Exception e) {
                response = errorFromXML(rawresponse.toString());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return response;

    }

    private SuccessResponse successFromXML (final String xml) {
        XStream xStreamInstance = new XStream();
        xStreamInstance.setMode(XStream.NO_REFERENCES);
        xStreamInstance.autodetectAnnotations(false);
        xStreamInstance.alias("response", SuccessResponse.class);
        xStreamInstance.alias("row", Row.class);
        xStreamInstance.alias("cells", HashMap.class);
        xStreamInstance.alias("roi", Integer.class);
        xStreamInstance.registerConverter(new AmforeasMapConverter());
        xStreamInstance.addImplicitCollection(SuccessResponse.class, "rows", Row.class);
        return (SuccessResponse) xStreamInstance.fromXML(xml);
    }

    private ErrorResponse errorFromXML (final String xml) {
        XStream xStreamInstance = new XStream();
        xStreamInstance.setMode(XStream.NO_REFERENCES);
        xStreamInstance.autodetectAnnotations(false);
        xStreamInstance.alias("response", ErrorResponse.class);
        return (ErrorResponse) xStreamInstance.fromXML(xml);
    }

    static class AmforeasMapConverter implements Converter {

        @Override
        public void marshal (Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {
            Map<String, Object> map = (Map<String, Object>) o;
            for (String key : map.keySet()) {
                Object val = map.get(key);
                writer.startNode(key.toLowerCase());
                if (val != null) {
                    writer.setValue(val.toString());
                } else {
                    writer.setValue("");
                }

                writer.endNode();
            }
        }

        @Override
        public Object unmarshal (HierarchicalStreamReader reader, UnmarshallingContext uc) {
            Map<String, Object> map = new HashMap<String, Object>();
            MultivaluedMap<String, String> mv = new MultivaluedHashMap<>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                mv.add(reader.getNodeName(), reader.getValue());
                map.put(reader.getNodeName(), reader.getValue());
                reader.moveUp();
            }

            if (uc.getRequiredType().equals(MultivaluedMap.class)) {
                return mv;
            } else {
                return map;
            }

        }

        @Override
        public boolean canConvert (Class type) {
            return type.equals(HashMap.class) || type.equals(MultivaluedMap.class);
        }

    }
}
