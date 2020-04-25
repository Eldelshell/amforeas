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
package amforeas.exceptions;

import javax.ws.rs.core.Response;

import amforeas.rest.xstream.AmforeasError;
import amforeas.rest.xstream.AmforeasResponse;

/**
 * Exception used to indicate that the client request is somehow broken. This exception produces
 * a {@link amforeas.rest.xstream.AmforeasError} with a 400 HTTP error code.
 * @author Alejandro Ayuso 
 */
public class AmforeasBadRequestException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    private String resource;
    
    public AmforeasBadRequestException(String msg){
        super(msg);
        this.resource = "unknown";
    }
    
    public AmforeasBadRequestException(String msg, String resource){
        super(msg);
        this.resource = resource;
    }
    
    /**
     * Generates a jersey ready response to be sent by the container. This response has a 400 HTTP error code.
     * @return a jersey ready response to be sent by the container.
     */
    public Response getResponse(){
        AmforeasResponse error = new AmforeasError(this.resource, Response.Status.BAD_REQUEST.getStatusCode(), this.getMessage());
        return error.getResponse();
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
