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

package amforeas.rest;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import amforeas.AmforeasUtils;
import amforeas.AmforeasWS;
import amforeas.PerformanceLogger;
import amforeas.RestController;
import amforeas.jdbc.LimitParam;
import amforeas.jdbc.OrderParam;
import amforeas.rest.xstream.AmforeasError;
import amforeas.rest.xstream.Usage;

/**
 *
 * @author Alejandro Ayuso
 */
@Path("/")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class AmforeasWSImpl implements AmforeasWS {
    
//    private static final Logger l = LoggerFactory.getLogger(AmforeasWSImpl.class);
    private static final Usage u = Usage.getInstance();
    
    @Context UriInfo ui;
    
    @GET
    @Path("{alias}")
    @Override
    public Response dbMeta(@PathParam("alias") String alias) {
        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.DBMETA);
        try{
            return new RestController(alias).getDatabaseMetadata().getResponse();
        }catch(IllegalArgumentException e){
            return new AmforeasError(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        }finally{
            p.end();
        }
    }
    
    @HEAD
    @Path("{alias}/{table}")
    @Override
    public Response resourceMeta(@PathParam("alias") String alias, @PathParam("table") String table) {
        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.RSMETA);
        try{
            return new RestController(alias).getResourceMetadata(table).getResponse();
        }catch(IllegalArgumentException e){
            return new AmforeasError(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        }finally{
            p.end();
        }
    }
    
    @GET
    @Path("{alias}/{table}")
    @Override
    public Response getAll(@PathParam("alias") String alias, @PathParam("table") String table, @DefaultValue("id") @HeaderParam("Primary-Key") String pk) {
        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.READALL);
        MultivaluedMap<String, String> pathParams = ui.getQueryParameters();
        LimitParam limit = LimitParam.valueOf(pathParams);
        OrderParam order = OrderParam.valueOf(pathParams, pk);
        
        Response response = null;
        try{
            response = new RestController(alias).getAllResources(table, limit, order).getResponse();
        }catch(IllegalArgumentException e){
            response = new AmforeasError(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        }catch(Exception e){
        	response = new AmforeasError(alias, Response.Status.INTERNAL_SERVER_ERROR, e.getMessage()).getResponse();
        }finally{
            u.addRead(p.end(), response.getStatus());
        }
        return response;
    }
    
    @GET
    @Path("{alias}/{table}/{id}")
    @Override
    public Response get(@PathParam("alias") String alias, @PathParam("table") String table, @DefaultValue("id") @HeaderParam("Primary-Key") String pk, @PathParam("id") String id) {
        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.READ);
        MultivaluedMap<String, String> pathParams = ui.getQueryParameters();
        LimitParam limit = LimitParam.valueOf(pathParams);
        OrderParam order = OrderParam.valueOf(pathParams, pk);
        
        Response response = null;
        try{
            response = new RestController(alias).getResource(table, pk, id, limit, order).getResponse();
        }catch(IllegalArgumentException e){
            response = new AmforeasError(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        }finally{
            u.addRead(p.end(), response.getStatus());
        }
        return response;
        
    }

    @POST
    @Path("{alias}/{table}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response insert(@PathParam("alias") String alias, @PathParam("table") final String table, @DefaultValue("id") @HeaderParam("Primary-Key") String pk, final String jsonRequest) {
        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.CREATE);
        Response response = null;
        try{
            response = new RestController(alias).insertResource(table, pk, jsonRequest).getResponse();
        }catch(IllegalArgumentException e){
            response = new AmforeasError(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        }finally{
            u.addCreate(p.end(), response.getStatus());
        }
        return response;
    }
    
    @POST
    @Path("{alias}/{table}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Override
    public Response insert(@PathParam("alias") String alias, @PathParam("table") final String table, @DefaultValue("id") @HeaderParam("Primary-Key") String pk, final MultivaluedMap<String, String> formParams) {
        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.CREATE);
        Map<String, String> map = AmforeasUtils.hashMapOf(formParams);
        
        Response response = null;
        try{
            response = new RestController(alias).insertResource(table, pk, map).getResponse();
        }catch(IllegalArgumentException e){
            response = new AmforeasError(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        }finally{
            u.addCreate(p.end(), response.getStatus());
        }
        return response;
    }

    @PUT
    @Path("{alias}/{table}/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response update(@PathParam("alias") String alias, @PathParam("table") final String table, @DefaultValue("id") @HeaderParam("Primary-Key") String pk, @PathParam("id") final String id, final String jsonRequest) {
        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.UPDATE);
        Response response = null;
        try{
            response = new RestController(alias).updateResource(table, pk, id, jsonRequest).getResponse();
        }catch(IllegalArgumentException e){
            response = new AmforeasError(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        }finally{
            u.addUpdate(p.end(), response.getStatus());
        }
        return response;
    }
    
    @DELETE
    @Path("{alias}/{table}/{id}")
    @Override
    public Response delete(@PathParam("alias") String alias, @PathParam("table") final String table, @DefaultValue("id") @HeaderParam("Primary-Key") String pk, @PathParam("id") final String id) {
        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.UPDATE);
        
        Response response = null;
        try{
            response = new RestController(alias).deleteResource(table, pk, id).getResponse();
        }catch(IllegalArgumentException e){
            response = new AmforeasError(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        }finally{
            u.addDelete(p.end(), response.getStatus());
        }
        return response;
    }
    
    @GET
    @Path("{alias}/{table}/{column}/{arg}")
    @Override
    public Response find(@PathParam("alias") String alias, @PathParam("table") String table, @PathParam("column") final String col, @PathParam("arg") final String arg) {
        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.READ);
        MultivaluedMap<String, String> pathParams = ui.getQueryParameters();
        LimitParam limit = LimitParam.valueOf(pathParams);
        OrderParam order = OrderParam.valueOf(pathParams);
        
        Response response = null;
        try{
            response = new RestController(alias).findResources(table, col, arg, limit, order).getResponse();
        }catch(IllegalArgumentException e){
            response = new AmforeasError(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        }finally{
            u.addRead(p.end(), response.getStatus());
        }
        return response;
    }
    
    @GET
    @Path("{alias}/{table}/dynamic/{query}")
    @Override
    public Response findBy(@PathParam("alias") String alias, @PathParam("table") final String table, @PathParam("query") String query, @QueryParam("args") List<String> values) {
        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.READ);
        MultivaluedMap<String, String> pathParams = ui.getQueryParameters();
        LimitParam limit = LimitParam.valueOf(pathParams);
        OrderParam order = OrderParam.valueOf(pathParams);
        
        Response response = null;
        try{
            response = new RestController(alias).findByDynamicFinder(table, query, values, limit, order).getResponse();
        }catch(IllegalArgumentException e){
            response = new AmforeasError(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        }finally{
            u.addDynamic(p.end(), response.getStatus());
        }
        return response;
    }

    @POST
    @Path("{alias}/call/{query}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response storedProcedure(@PathParam("alias") String alias, @PathParam("query") String query, final String jsonRequest) {
        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.READ);
        
        Response response = null;
        try{
            response = new RestController(alias).executeStoredProcedure(query, jsonRequest).getResponse();
        }catch(IllegalArgumentException e){
            response = new AmforeasError(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        }finally{
            u.addQuery(p.end(), response.getStatus());
        }
        return response;
    }
    
    @Override
    @GET @Path("stats") @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getJongoStatistics() {
        Response response = u.getUsageData().getResponse();
        return response;
    }
}