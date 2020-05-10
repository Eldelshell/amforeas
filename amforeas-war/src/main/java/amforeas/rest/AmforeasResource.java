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

package amforeas.rest;

import java.util.List;
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
import amforeas.AmforeasWS;
import amforeas.DefaultRestService;
import amforeas.RestService;

@Path("/")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class AmforeasResource implements AmforeasWS {

    @Context
    UriInfo ui;

    private final RestService restService = new DefaultRestService();

    @GET
    @Path("{alias}")
    @Override
    public Response dbMeta (@PathParam("alias") String alias) {
        return restService.dbMeta(alias);
    }

    @HEAD
    @Path("{alias}/{resource}")
    @Override
    public Response resourceMeta (@PathParam("alias") String alias, @PathParam("resource") String resource) {
        return restService.resourceMeta(alias, resource);
    }

    @GET
    @Path("{alias}/{resource}")
    @Override
    public Response getAll (
        @PathParam("alias") String alias,
        @PathParam("resource") String resource,
        @DefaultValue("id") @HeaderParam("Primary-Key") String pk) {

        return restService.getAll(alias, resource, pk, ui.getQueryParameters());
    }

    @GET
    @Path("{alias}/{resource}/{id}")
    @Override
    public Response get (
        @PathParam("alias") String alias,
        @PathParam("resource") String resource,
        @DefaultValue("id") @HeaderParam("Primary-Key") String pk,
        @PathParam("id") String id) {

        return restService.get(alias, resource, pk, id, ui.getQueryParameters());
    }

    @POST
    @Path("{alias}/{resource}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response insert (
        @PathParam("alias") String alias,
        @PathParam("resource") final String resource,
        @DefaultValue("id") @HeaderParam("Primary-Key") String pk,
        final String jsonRequest) {

        return restService.insert(alias, resource, pk, jsonRequest);
    }

    @POST
    @Path("{alias}/{resource}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Override
    public Response insert (
        @PathParam("alias") String alias,
        @PathParam("resource") final String resource,
        @DefaultValue("id") @HeaderParam("Primary-Key") String pk,
        final MultivaluedMap<String, String> formParams) {

        return restService.insert(alias, resource, pk, formParams);
    }

    @PUT
    @Path("{alias}/{resource}/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response update (
        @PathParam("alias") String alias,
        @PathParam("resource") final String resource,
        @DefaultValue("id") @HeaderParam("Primary-Key") String pk,
        @PathParam("id") final String id,
        final String jsonRequest) {

        return restService.update(alias, resource, pk, id, jsonRequest);
    }

    @DELETE
    @Path("{alias}/{resource}/{id}")
    @Override
    public Response delete (
        @PathParam("alias") String alias,
        @PathParam("resource") final String resource,
        @DefaultValue("id") @HeaderParam("Primary-Key") String pk,
        @PathParam("id") final String id) {

        return restService.delete(alias, resource, pk, id);
    }

    @GET
    @Path("{alias}/{resource}/{column}/{arg}")
    @Override
    public Response find (
        @PathParam("alias") String alias,
        @PathParam("resource") String resource,
        @DefaultValue("id") @HeaderParam("Primary-Key") String pk,
        @PathParam("column") final String col,
        @PathParam("arg") final String arg) {

        return restService.find(alias, resource, pk, col, arg, ui.getQueryParameters());
    }

    @GET
    @Path("{alias}/{resource}/dynamic/{query}")
    @Override
    public Response findBy (
        @PathParam("alias") String alias,
        @PathParam("resource") final String resource,
        @DefaultValue("id") @HeaderParam("Primary-Key") String pk,
        @PathParam("query") String query,
        @QueryParam("args") List<String> args) {

        return restService.findBy(alias, resource, pk, query, args, ui.getQueryParameters());
    }

    @POST
    @Path("{alias}/call/{query}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response storedProcedure (@PathParam("alias") String alias, @PathParam("query") String query, final String jsonRequest) {
        return restService.storedProcedure(alias, query, jsonRequest);
    }


    @GET
    @Path("stats")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public Response getStatistics () {
        return restService.getStatistics();
    }
}
