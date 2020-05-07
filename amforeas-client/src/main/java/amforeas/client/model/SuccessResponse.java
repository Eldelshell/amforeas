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

package amforeas.client.model;

import java.util.List;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represent that an operation has succeeded. 
 */
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class SuccessResponse implements AmforeasResponse {

    private boolean success = true;
    private Response.Status status;
    private List<Row> rows;
    private String resource;
    private Pagination pagination;

    public SuccessResponse() {
        this.status = Response.Status.OK;
    }

    /**
     * Instantiates a new success response for the given resource and results with the
     * given HTTP code.
     * @param resource the name of the resource being accessed
     * @param results a list of {@link amforeas.rest.xstream.Row} with the results of the operation
     * @param status a HTTP code to give to the client
     */
    public SuccessResponse(String resource, List<Row> results, Response.Status status) {
        this.resource = resource;
        this.rows = results;
        this.status = status;
    }

    /**
     * Instantiates a new success response for the given resource and results with a 200 HTTP code.
     * @param resource the name of the resource being accessed
     * @param results a list of {@link amforeas.rest.xstream.Row} with the results of the operation
     */
    public SuccessResponse(String resource, List<Row> results) {
        this.resource = resource;
        this.rows = results;
        this.status = Response.Status.OK;
    }

    /**
     * Instantiates a new success response for the given resource and results with a 200 HTTP code.
     * @param resource the name of the resource being accessed
     * @param results a list of {@link amforeas.rest.xstream.Row} with the results of the operation
     * @param pagination - the {@link amforeas.rest.xstream.Pagination} object
     */
    public SuccessResponse(String resource, List<Row> results, Pagination pagination) {
        this.resource = resource;
        this.rows = results;
        this.pagination = pagination;
        this.status = Response.Status.OK;
    }

    @Override
    @XmlTransient
    @JsonIgnore
    public Response getResponse () {
        return Response.status(this.status).entity(this).build();
    }

    @Override
    public String getResource () {
        return resource;
    }

    public List<Row> getRows () {
        return rows;
    }

    @Override
    public Status getStatus () {
        return status;
    }

    @Override
    public boolean isSuccess () {
        return success;
    }

    public void setResource (String resource) {
        this.resource = resource;
    }

    public void setRows (List<Row> rows) {
        this.rows = rows;
    }

    public void setStatus (Status status) {
        this.status = status;
    }

    public void setSuccess (boolean success) {
        this.success = success;
    }

    public Pagination getPagination () {
        return pagination;
    }

    public void setPagination (Pagination pagination) {
        this.pagination = pagination;
    }

}
