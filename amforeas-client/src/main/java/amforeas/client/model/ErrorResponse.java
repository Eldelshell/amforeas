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

import java.sql.SQLException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents an error response object. Can be processed with JAX.
 */
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorResponse implements AmforeasResponse {

    private String resource;
    private boolean success = false;
    private Response.Status status;
    private String message;
    private String sqlState;
    private Integer sqlCode;

    public ErrorResponse() {
        this.status = Response.Status.BAD_REQUEST;
    }

    /**
     * Instantiates a new AmforeasError for a given resource and HTTP code.
     * @param resource the name of the resource being accessed
     * @param status a HTTP code to give to the client
     */
    public ErrorResponse(String resource, Response.Status status) {
        this.resource = resource;
        this.status = status;
        this.message = status.getReasonPhrase();
        this.sqlState = null;
        this.sqlCode = null;
    }

    /**
     * Instantiates a new AmforeasError for a given resource, error code and message.
     * @param resource the name of the resource being accessed
     * @param errorCode a HTTP code to give to the client
     * @param message a message explaining the error
     */
    @Deprecated
    public ErrorResponse(String resource, Integer errorCode, String message) {
        this.resource = resource;
        this.status = Response.Status.fromStatusCode(errorCode);
        this.message = message;
        this.sqlState = null;
        this.sqlCode = null;
    }

    /**
     * Instantiates a new AmforeasError for a given resource, error code and message.
     * @param resource the name of the resource being accessed
     * @param status a HTTP code to give to the client
     * @param message a message explaining the error
     */
    public ErrorResponse(String resource, Response.Status status, String message) {
        this.resource = resource;
        this.status = status;
        this.message = message;
        this.sqlState = null;
        this.sqlCode = null;
    }

    /**
     * Special instance of a AmforeasError for SQLExceptions where we return the
     * driver SqlState and SqlCode with a 400 HTTP code.
     * @param resource the name of the resource being accessed
     * @param ex the exception thrown by the driver.
     */
    public ErrorResponse(final String resource, final SQLException ex) {
        this.resource = resource;
        this.status = Response.Status.BAD_REQUEST;
        this.message = ex.getMessage();
        this.sqlState = ex.getSQLState();
        this.sqlCode = ex.getErrorCode();
    }

    @Override
    @XmlTransient
    @JsonIgnore
    public Response getResponse () {
        return Response.status(getStatus()).entity(this).build();
    }

    @Override
    public Status getStatus () {
        return status;
    }

    public String getMessage () {
        return message;
    }

    @Override
    public String getResource () {
        return resource;
    }

    @Override
    public boolean isSuccess () {
        return success;
    }

    public Integer getSqlCode () {
        return sqlCode;
    }

    public String getSqlState () {
        return sqlState;
    }

    public void setStatus (Response.Status status) {
        this.status = status;
    }
}
