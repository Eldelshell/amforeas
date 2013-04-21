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

package jongo.rest.xstream;

import java.util.List;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represent that an operation has succeeded. 
 * @author Alejandro Ayuso 
 */
@XmlRootElement(name="response")
public class JongoSuccess implements JongoResponse{
    
    private boolean success = true;
    private Response.Status status;
    private List<Row> rows;
    private String resource;
    
    public JongoSuccess(){}
    
    /**
     * Instantiates a new success response for the given resource and results with the
     * given HTTP code.
     * @param resource the name of the resource being accessed
     * @param results a list of {@link jongo.rest.xstream.Row} with the results of the operation
     * @param status a HTTP code to give to the client
     */
    public JongoSuccess(String resource, List<Row> results, Response.Status status) {
        this.resource = resource;
        this.rows = results;
        this.status = status;
    }
    
    /**
     * Instantiates a new success response for the given resource and results with a 200 HTTP code.
     * @param resource the name of the resource being accessed
     * @param results a list of {@link jongo.rest.xstream.Row} with the results of the operation
     */
    public JongoSuccess(String resource, List<Row> results) {
        this.resource = resource;
        this.rows = results;
        this.status = Response.Status.OK;
    }
    
    @Override
    public Response getResponse() {
        return Response.status(this.status).entity(this).build();
    }
    
    @Override
    public String getResource() {
        return resource;
    }

    public List<Row> getRows() {
        return rows;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
