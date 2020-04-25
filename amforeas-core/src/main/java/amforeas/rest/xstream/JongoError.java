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

package amforeas.rest.xstream;

import java.sql.SQLException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents an error response object. Can be processed with JAX.
 * @author Alejandro Ayuso 
 */
@XmlRootElement(name="response")
public class AmforeasError implements AmforeasResponse {
    
    private String resource;
    private boolean success = false;
    private Integer status;
    private String message;
    private String sqlState;
    private Integer sqlCode;
    
    public AmforeasError(){}

    /**
     * Instantiates a new AmforeasError for a given resource and HTTP code.
     * @param resource the name of the resource being accessed
     * @param status a HTTP code to give to the client
     */
    public AmforeasError(String resource, Response.Status status) {
        this.resource = resource;
        this.status = status.getStatusCode();
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
    public AmforeasError(String resource, Integer errorCode, String message) {
        this.resource = resource;
        this.status = errorCode;
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
    public AmforeasError(String resource, Response.Status status, String message) {
        this.resource = resource;
        this.status = status.getStatusCode();
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
    public AmforeasError(final String resource, final SQLException ex){
        this.resource = resource;
        this.status = 400;
        this.message = ex.getMessage();
        this.sqlState = ex.getSQLState();
        this.sqlCode = ex.getErrorCode();
    }

    @Override
    public Response getResponse() {
        return Response.status(getStatus()).entity(this).build();
    }

    @Override
    public Status getStatus() {
        return Response.Status.fromStatusCode(status);
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String getResource() {
        return resource;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    public Integer getSqlCode() {
        return sqlCode;
    }

    public String getSqlState() {
        return sqlState;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
