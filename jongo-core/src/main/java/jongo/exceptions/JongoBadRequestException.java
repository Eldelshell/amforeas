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
package jongo.exceptions;

import javax.ws.rs.core.Response;

import jongo.rest.xstream.JongoError;
import jongo.rest.xstream.JongoResponse;

/**
 * Exception used to indicate that the client request is somehow broken. This exception produces
 * a {@link jongo.rest.xstream.JongoError} with a 400 HTTP error code.
 * @author Alejandro Ayuso 
 */
public class JongoBadRequestException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    private String resource;
    
    public JongoBadRequestException(String msg){
        super(msg);
        this.resource = "unknown";
    }
    
    public JongoBadRequestException(String msg, String resource){
        super(msg);
        this.resource = resource;
    }
    
    /**
     * Generates a jersey ready response to be sent by the container. This response has a 400 HTTP error code.
     * @return a jersey ready response to be sent by the container.
     */
    public Response getResponse(){
        JongoResponse error = new JongoError(this.resource, Response.Status.BAD_REQUEST.getStatusCode(), this.getMessage());
        return error.getResponse();
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
