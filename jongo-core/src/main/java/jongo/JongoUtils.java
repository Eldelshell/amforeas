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

package jongo;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;

import jongo.config.JongoConfiguration;
import jongo.exceptions.JongoBadRequestException;
import jongo.exceptions.StartupException;
import jongo.jdbc.StoredProcedureParam;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

/**
 * Collection of commonly used methods and constants.
 * @author Alejandro Ayuso 
 */
public class JongoUtils {
    
    private static final Logger l = LoggerFactory.getLogger(JongoUtils.class);
    
    /**
     * Check if a string has the ISO date time format. Uses the ISODateTimeFormat.dateTime() from JodaTime
     * and returns a DateTime instance. The correct format is yyyy-MM-ddTHH:mm:ss.SSSZ
     * @param arg the string to check
     * @return a DateTime instance if the string is in the correct ISO format.
     */
    public static DateTime isDateTime(final String arg){
        if(arg == null)  return null;
        DateTimeFormatter f = ISODateTimeFormat.dateTime();
        DateTime ret = null;
        try{
            ret = f.parseDateTime(arg);
        }catch(IllegalArgumentException e){
            l.debug(arg + " is not a valid ISO DateTime");
        }
        return ret;
    }
    
    /**
     * Check if a string has the ISO date format. Uses the ISODateTimeFormat.date() from JodaTime
     * and returns a DateTime instance. The correct format is yyyy-MM-dd or yyyyMMdd
     * @param arg the string to check
     * @return a DateTime instance if the string is in the correct ISO format.
     */
    public static DateTime isDate(final String arg){
        if(arg == null) return null;
        DateTime ret = null;
        DateTimeFormatter df;
        if(arg.contains("-")){
            df = ISODateTimeFormat.date();
        }else{
            df = ISODateTimeFormat.basicDate();
        }
        
        try{
            ret = df.parseDateTime(arg);
        }catch(IllegalArgumentException e){
            l.debug(arg + " is not a valid ISO date");
        }
        
        return ret;
    }
    
    /**
     * Check if a string has the ISO time format. Uses the ISODateTimeFormat.time() from JodaTime
     * and returns a DateTime instance. The correct format is HH:mm:ss.SSSZZ or HHmmss.SSSZ
     * @param arg the string to check
     * @return a DateTime instance if the string is in the correct ISO format.
     */
    public static DateTime isTime(final String arg){
        if(arg == null) return null;
        DateTime ret = null;
        DateTimeFormatter df;
        if(arg.contains(":")){
            df = ISODateTimeFormat.time();
        }else{
            df = ISODateTimeFormat.basicTime();
        }
        
        try{
            ret = df.parseDateTime(arg);
        }catch(IllegalArgumentException e){
            l.debug(arg + " is not a valid ISO time");
        }
        
        return ret;
    }
    
    /**
     * Convert a String in camelCase to a String separated by white spaces. 
     * I.E. "thisStringInCamelCase" to "this String In Camel Case"
     * @param s String in camelCase
     * @return same string with white spaces.
     */
    public static String splitCamelCase(String s) {
        return s.replaceAll(String.format("%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])", "(?<=[A-Za-z])(?=[^A-Za-z0-9_])"), " ");
    }
    
    /**
     * Convert a List of String to a vararg of Objects. The values are deduced from their format
     * and converted to the corresponding java.sql.Types so they are correctly saved in the databases.
     * @param values a List of String with the values to be converted
     * @return a vararg of java.sql Objects
     */
    public static Object[] parseValues(List<String> values){
        List<Object> res = new ArrayList<Object>();
        
        for(String val : values){
            res.add(parseValue(val));
        }
        
        return res.toArray();
    }
    
    /**
     * Infers the java.sql.Types of the given String and returns the JDBC mappable Object corresponding to it.
     * The conversions are like this:
     * String -> String
     * Numeric -> Integer
     * Date or Time -> Date
     * Decimal -> BigDecimal
     * ??? -> TimeStamp
     * @param val a String with the value to be mapped
     * @return a JDBC mappable object instance with the value
     */
    public static Object parseValue(String val){
        Object ret = null;
        if(!StringUtils.isWhitespace(val) && StringUtils.isNumeric(val)){
            try{
                ret = Integer.valueOf(val);
            }catch(Exception e){
                l.debug(e.getMessage());
            }
        }else{
            DateTime date = JongoUtils.isDateTime(val);
            if(date != null){
                l.debug("Got a DateTime " + date.toString(ISODateTimeFormat.dateTime()));
                ret = new java.sql.Timestamp(date.getMillis());
            }else{
                date = JongoUtils.isDate(val);
                if(date != null){
                    l.debug("Got a Date " + date.toString(ISODateTimeFormat.date()));
                    ret = new java.sql.Date(date.getMillis());
                }else{
                    date = JongoUtils.isTime(val);
                    if(date != null){
                        l.debug("Got a Time " + date.toString(ISODateTimeFormat.time()));
                        ret = new java.sql.Time(date.getMillis());
                    }
                }
            }
            
            if(ret == null && val != null){
                l.debug("Not a datetime. Try someting else. ");
                try{
                    ret = new BigDecimal(val);
                }catch(NumberFormatException e){
                    l.debug(e.getMessage());
                    ret = val;
                }
            }
        }
        return ret;
    }
    
    /**
     * Converts a vararg of Object to a String representation of its content
     * @param params a vararg of objects
     * @return a String like "[1,2,3,5,hello,0.1]"
     */
    public static String varargToString(Object... params){
        StringBuilder b = new StringBuilder("[");
        b.append(StringUtils.join(params, ","));
        b.append("]");
        return b.toString();
    }
    
    /**
     * Reads a String in JSON format and returns a MultivaluedMap representation of it.
     * @return a MultivaluedMap with the keys/values as represented by the incoming JSON string.
     * @throws JongoBadRequestException if the JSON string is not readable.
     */
    public static Map<String, String> getParamsFromJSON(final String json) throws JongoBadRequestException{
        if(StringUtils.isBlank(json))
            throw new JongoBadRequestException("Invalid number of arguments for request " + json);
        try {
            Map<String, String> ret = new ObjectMapper().readValue(json, new TypeReference<Map<String, String>>(){});
            return ret;
        } catch (Exception ex) {
            throw new JongoBadRequestException(ex.getMessage());
        }
    }
    
    /**
     * From a JSON string generated a list of {@link jongo.jdbc.StoredProcedureParam}. The format of this JSON is:
     * [
     *  {"value":2010, "name":"year", "outParameter":false, "type":"INTEGER", "index":1},
     *  {"name":"out_total", "outParameter":true, "type":"INTEGER", "index":2}
     * ]
     * @param json a string with the JSON representation of a {@link jongo.jdbc.StoredProcedureParam}
     * @return a list of {@link jongo.jdbc.StoredProcedureParam}.
     * @throws JongoBadRequestException if we fail to parse the JSON for any reason.
     */
    public static List<StoredProcedureParam> getStoredProcedureParamsFromJSON(final String json) throws JongoBadRequestException{
        if(StringUtils.isBlank(json))
            throw new JongoBadRequestException("Invalid number of arguments for request " + json);
        try {
            List<StoredProcedureParam> ret = new ObjectMapper().readValue(json, new TypeReference<List<StoredProcedureParam>>(){});
            return ret;
        } catch (Exception ex) {
            throw new JongoBadRequestException(ex.getMessage());
        }
    }
    
    /**
     * Calls the JongoConfiguration.instanceOf() method and returns the given instance of {@link jongo.config.JongoConfiguration}
     * @return an instance of {@link jongo.config.JongoConfiguration}
     * @throws StartupException if {@link jongo.config.JongoConfiguration} fails to instantiate.
     */
    public static JongoConfiguration loadConfiguration() throws StartupException{
        JongoConfiguration configuration = null;
        try{
            configuration = JongoConfiguration.instanceOf();
        }catch(IllegalArgumentException e){
            l.error(e.getMessage());
        }
        
        if(configuration == null){
            throw new StartupException("Failed to load configuration", true);
        }
        
        return configuration;
    }
    
    /**
     * Generates a HashMap with the first value of a MultivaluedMap because working with this "maps" is a PITA.
     * @param mv the MultivaluedMap with the keys and values
     * @return a Map (Hash) with the first value corresponding to the key.
     */
    public static Map<String, String> hashMapOf(final MultivaluedMap<String, String> mv){
        if(mv == null)
            throw new IllegalArgumentException("Invalid null argument");
        
        Map<String, String> map = new HashMap<String, String>();
        for(String k : mv.keySet()){
            String v = mv.getFirst(k);
            if(StringUtils.isNotEmpty(v))
                map.put(k, v);
        }
        return map;
    }
    
    /**
     * Generates a Base64-encoded binary MD5 sum of the content of the response as described by rfc1864
     * @param input the string
     * @return Base64-encoded binary MD5 sum of the string
     */
    public static String getMD5Base64(String input) {
        if(input == null)
            throw new IllegalArgumentException("Invalid null argument");

        String ret = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes("UTF-8"));
            byte[] rawData = digest.digest();
            BASE64Encoder bencoder = new BASE64Encoder();
            ret = bencoder.encode(rawData);
        } catch (Exception ex) {
            l.error(ex.getMessage());
        }
        
        return ret;
    }
    
    /**
     * The length of the request body in octets (8-bit bytes)
     * @param input the string
     * @return an Integer with the length of the request body in octets.
     */
    public static Integer getOctetLength(String input){
        if(input == null)
            throw new IllegalArgumentException("Invalid null argument");
        
        byte[] responseBytes;
        int result = 0;
        try {
            responseBytes = input.getBytes("UTF-8");
            result = responseBytes.length;
        } catch ( UnsupportedEncodingException ex ) {
            l.error(ex.getMessage());
        }
        return result;
    }
    
    /**
     * The date and time that the message was sent
     * @return The date and time that the message was sent
     */
    public static String getDateHeader(){
        return new DateTime().toString(ISODateTimeFormat.dateTime());
    }
    
    /**
     * Generates a string like {call stmt(?,?,?)} to be used by a CallableStatement to execute a function
     * or a procedure.
     * @param queryName the name of the function or procedure
     * @param paramsSize the amount of parameters to be passed to the function/procedure
     * @return a string ready to be fed to a CallableStatement
     */
    public static String getCallableStatementCallString(final String queryName, final Integer paramsSize){
        if(StringUtils.isBlank(queryName))
            throw new IllegalArgumentException("The name can't be null, empty or blank" );
        
        StringBuilder b = new StringBuilder("{CALL ");
        b.append(queryName);
        b.append("(");
        for(int i = 0; i < paramsSize; i++){
            b.append("?,");
        }
        if(b.charAt(b.length() - 1) == ','){
            b.deleteCharAt(b.length() - 1);
        }
        b.append(")}");
        return b.toString();
    }
}
