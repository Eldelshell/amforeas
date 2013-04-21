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

import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Alejandro Ayuso 
 */
public class JongoMapConverter implements Converter {
    
    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {
        Map<String, Object> map = (Map<String, Object>)o;
        for(String key: map.keySet()){
            Object val = map.get(key);
            writer.startNode(key.toLowerCase());
            if(val != null){
                writer.setValue(val.toString());
            }else{
                writer.setValue("");
            }
            
            writer.endNode();
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {
        Map<String, Object> map = new HashMap<String, Object>();
        MultivaluedMap<String, String> mv = new MultivaluedMapImpl();
        while(reader.hasMoreChildren()){
            reader.moveDown();
            mv.add(reader.getNodeName(), reader.getValue());
            map.put(reader.getNodeName(), reader.getValue());
            reader.moveUp();
        }
        
        if(uc.getRequiredType().equals(MultivaluedMap.class)){
            return mv;
        }else{
            return map;
        }
        
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(HashMap.class) || type.equals(MultivaluedMap.class);
    }
    
}
