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
package org.amforeas.mocks;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import amforeas.rest.xstream.JongoResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.amforeas.XmlXstreamTest;

/**
 *
 * @author Alejandro Ayuso 
 */
public class JongoClient {
    
    private static final String jongoUrl = "http://localhost:8080/amforeas/demo1/";
    
    public JongoResponse doGET(final String url){
        return doRequest(url, "GET");
    }
    
    public JongoResponse doDELETE(final String url){
        return doRequest(url, "DELETE");
    }
    
    public JongoResponse doPUT(final String url, final String jsonParameters){
        return doRequest(url, "PUT", jsonParameters);
    }
    
    public JongoResponse doPOST(final String url, final String jsonParameters){
        return doRequest(url, "POST", jsonParameters);
    }
    
    public JongoResponse doPOST(final String url, final List<NameValuePair> parameters){
        return doRequest(url, parameters);
    }
    
    private JongoResponse doRequest(final String url, final String method){
        JongoResponse response = null;
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(jongoUrl + url).openConnection();
            con.setRequestMethod(method);
            con.setDoOutput(true);
            con.setRequestProperty("Accept", MediaType.APPLICATION_XML);
            BufferedReader r = null;
            
            if(con.getResponseCode() != Response.Status.OK.getStatusCode()){
                r = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }else{
                r = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }
            StringBuilder rawresponse = new StringBuilder();
            String strLine = null;
            while((strLine = r.readLine()) != null){
                rawresponse.append(strLine);
                rawresponse.append("\n");
            }
            try{
                response = XmlXstreamTest.successFromXML(rawresponse.toString());
            }catch(Exception e){
                response = XmlXstreamTest.errorFromXML(rawresponse.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }
    
    private JongoResponse doRequest(final String url, final String method, final String jsonParameters){
        JongoResponse response = null;
        
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(jongoUrl + url).openConnection();
            con.setRequestMethod(method);
            con.setRequestProperty("Accept", MediaType.APPLICATION_XML);
            con.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
            con.setRequestProperty("Content-Length", "" + Integer.toString(jsonParameters.getBytes().length));
            con.setDoOutput(true);
            con.setDoInput(true);
            
            DataOutputStream wr = new DataOutputStream (con.getOutputStream());
            wr.writeBytes (jsonParameters);
            wr.flush ();
            wr.close ();
            
//            BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));
            
            BufferedReader r = null;
            if(con.getResponseCode() != Response.Status.OK.getStatusCode() && con.getResponseCode() != Response.Status.CREATED.getStatusCode()){
                r = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }else{
                r = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }
            
            StringBuilder rawresponse = new StringBuilder();
            String strLine = null;
            while((strLine = r.readLine()) != null){
                rawresponse.append(strLine);
                rawresponse.append("\n");
            }
            
            try{
                response = XmlXstreamTest.successFromXML(rawresponse.toString());
            }catch(Exception e){
                response = XmlXstreamTest.errorFromXML(rawresponse.toString());
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return response;
    }
    
    private JongoResponse doRequest(final String url, final List<NameValuePair> parameters){
        final String urlParameters = URLEncodedUtils.format(parameters, "UTF-8");
        JongoResponse response = null;
        
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(jongoUrl + url).openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", MediaType.APPLICATION_XML);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
            con.setDoOutput(true);
            con.setDoInput(true);
            
            DataOutputStream wr = new DataOutputStream (con.getOutputStream());
            wr.writeBytes (urlParameters);
            wr.flush ();
            wr.close ();
            
            BufferedReader r = null;
            if(con.getResponseCode() != Response.Status.CREATED.getStatusCode()){
                r = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }else{
                r = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }
            
            StringBuilder rawresponse = new StringBuilder();
            String strLine = null;
            while((strLine = r.readLine()) != null){
                rawresponse.append(strLine);
                rawresponse.append("\n");
            }
            
            try{
                response = XmlXstreamTest.successFromXML(rawresponse.toString());
            }catch(Exception e){
                response = XmlXstreamTest.errorFromXML(rawresponse.toString());
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return response;
        
    }
}
