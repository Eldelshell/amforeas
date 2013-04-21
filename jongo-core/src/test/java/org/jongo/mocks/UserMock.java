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
package org.jongo.mocks;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 *
 * @author Alejandro Ayuso 
 */
public class UserMock {
    public int id;
    public String name;
    public int age;
    public BigDecimal credit;
    public DateTime birthday;
    public DateTime lastupdate;
    
    private static final SecureRandom random = new SecureRandom();
    private static final DateTimeFormatter dateTimeFTR = ISODateTimeFormat.dateTime();
    private static final DateTimeFormatter dateFTR = ISODateTimeFormat.date();

    public static UserMock getRandomInstance(){
        final UserMock instance = new UserMock();
        instance.name = new BigInteger(100, random).toString(32);
        instance.age = 1 + (int)(Math.random() * ((100 - 1) + 1));
        instance.birthday = dateFTR.parseDateTime(getRandomBirthDate());
        instance.lastupdate = new DateTime();
        instance.credit = new BigDecimal(random.nextDouble());
        return instance;
    }
    
    public static String getRandomBirthDate(){
        String year = String.valueOf(1950 + (int)(Math.random() * ((2010 - 1950) + 1)));
        String month = String.valueOf(1 + (int)(Math.random() * ((12 - 1) + 1)));
        String day = String.valueOf(1 + (int)(Math.random() * ((30 - 1) + 1)));
        if(month.length() == 1) month = "0" + month; 
        if(day.length() == 1) day = "0" + day; 
        return year + "-" + month + "-" + day;
    }
    
    public List<NameValuePair> toNameValuePair(){
        List<NameValuePair> al = new ArrayList<NameValuePair>();
        al.add(new BasicNameValuePair("name", name));
        al.add(new BasicNameValuePair("age", String.valueOf(age)));
        al.add(new BasicNameValuePair("birthday", birthday.toString(dateFTR)));
        al.add(new BasicNameValuePair("lastupdate", lastupdate.toString(dateTimeFTR)));
        al.add(new BasicNameValuePair("credit", credit.toPlainString()));
        return al;
    }
    
    public Map<String,String> toMap(){
        Map<String, String> m = new HashMap<String, String>();
        m.put("name", name);
        m.put("age", String.valueOf(age));
        m.put("birthday", birthday.toString(dateFTR));
        m.put("lastupdate", lastupdate.toString(dateTimeFTR));
        m.put("credit", credit.toPlainString());
        return m;
    }
    
    public String toJSON(){
        StringBuilder b = new StringBuilder("{");
        b.append("\"name\":\"");b.append(name);
        b.append("\",\"birthday\":\"");b.append(birthday.toString(dateFTR));
        b.append("\",\"lastupdate\":\"");b.append(lastupdate.toString(dateTimeFTR));
        b.append("\",\"credit\":");b.append(credit);
        b.append(",\"age\":");b.append(age);
        b.append("}");
        return b.toString();
    }
    
    public static UserMock instanceOf(final Map<String, String> columns){
        UserMock instance = new UserMock();
        for(String k: columns.keySet()){
            if(k.equalsIgnoreCase("id")){
                instance.id = Integer.valueOf(columns.get(k));
            }else if(k.equalsIgnoreCase("name")){
                instance.name = columns.get(k);
            }else if(k.equalsIgnoreCase("age")){
                instance.age = Integer.valueOf(columns.get(k));
            }else if(k.equalsIgnoreCase("credit")){
                instance.credit = new BigDecimal(columns.get(k));
            }else if(k.equalsIgnoreCase("birthday")){
                instance.birthday = dateFTR.parseDateTime(columns.get(k));
            }else if(k.equalsIgnoreCase("lastupdate")){
                instance.lastupdate = dateTimeFTR.parseDateTime(columns.get(k));
            }else{
                System.out.println("Failed to parse column " + k + " with value " + columns.get(k));
            }
        }
        return instance;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserMock other = (UserMock) obj;
        if (this.id != other.id) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.age != other.age) {
            return false;
        }
        if (this.credit != other.credit && (this.credit == null || !this.credit.equals(other.credit))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.id;
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 67 * hash + this.age;
        hash = 67 * hash + (this.credit != null ? this.credit.hashCode() : 0);
        return hash;
    }
}
