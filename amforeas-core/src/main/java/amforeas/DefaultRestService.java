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

package amforeas;

import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import amforeas.acl.ACLFilter;
import amforeas.acl.ACLManager;
import amforeas.jdbc.LimitParam;
import amforeas.jdbc.OrderParam;
import amforeas.rest.xstream.ErrorResponse;
import amforeas.rest.xstream.Usage;

public class DefaultRestService implements RestService {

    private static final Usage u = Usage.getInstance();

    private final ACLManager aclManager;

    private final SingletonFactory factory;

    public DefaultRestService() {
        this.factory = new SingletonFactoryImpl();
        this.aclManager = new ACLManager();
    }

    public DefaultRestService(SingletonFactory factory) {
        this.factory = factory;
        this.aclManager = new ACLManager(factory);
    }

    @Override
    public Response dbMeta (String alias) {
        if (!aclManager.validate(alias, ACLFilter.META)) {
            return new ErrorResponse(alias, Response.Status.METHOD_NOT_ALLOWED).getResponse();
        }

        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.DBMETA);

        try {
            return factory.getRESTController(alias).getDatabaseMetadata().getResponse();
        } catch (IllegalArgumentException e) {
            return new ErrorResponse(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        } finally {
            p.end();
        }
    }

    @Override
    public Response resourceMeta (String alias, String resource) {
        if (!aclManager.validate(alias, resource, ACLFilter.META)) {
            return new ErrorResponse(resource, Response.Status.METHOD_NOT_ALLOWED).getResponse();
        }

        var p = PerformanceLogger.start(PerformanceLogger.Code.RSMETA);

        try {
            return factory.getRESTController(alias).getResourceMetadata(resource).getResponse();
        } catch (IllegalArgumentException e) {
            return new ErrorResponse(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        } finally {
            p.end();
        }
    }

    public Response get (String alias, String resource, String pk, String id, MultivaluedMap<String, String> queryParams) {
        if (!aclManager.validate(alias, resource, ACLFilter.READ)) {
            return new ErrorResponse(resource, Response.Status.METHOD_NOT_ALLOWED).getResponse();
        }

        var p = PerformanceLogger.start(PerformanceLogger.Code.READ);

        var limit = LimitParam.valueOf(queryParams, this.getPageSize(queryParams));
        var order = OrderParam.valueOf(queryParams, pk);
        var columns = queryParams.getFirst("columns");

        Response response = null;
        try {
            response = factory.getRESTController(alias).getResource(resource, pk, id, limit, order, columns).getResponse();
        } catch (IllegalArgumentException e) {
            response = new ErrorResponse(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        } finally {
            if (response != null) {
                u.addRead(p.end(), response.getStatus());
            }
        }
        return response;
    }

    public Response getAll (String alias, String resource, String pk, MultivaluedMap<String, String> queryParams) {
        if (!aclManager.validate(alias, resource, ACLFilter.READ)) {
            return new ErrorResponse(resource, Response.Status.METHOD_NOT_ALLOWED).getResponse();
        }

        var p = PerformanceLogger.start(PerformanceLogger.Code.READALL);

        var limit = LimitParam.valueOf(queryParams, this.getPageSize(queryParams));
        var order = OrderParam.valueOf(queryParams, pk);
        var columns = queryParams.getFirst("columns");

        Response response = null;
        try {
            response = factory.getRESTController(alias).getAllResources(resource, limit, order, columns).getResponse();
        } catch (IllegalArgumentException e) {
            response = new ErrorResponse(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        } catch (Exception e) {
            response = new ErrorResponse(alias, Response.Status.INTERNAL_SERVER_ERROR, e.getMessage()).getResponse();
        } finally {
            if (response != null) {
                u.addRead(p.end(), response.getStatus());
            }
        }
        return response;
    }

    public Response find (String alias, String resource, String col, String arg, MultivaluedMap<String, String> queryParams) {
        if (!aclManager.validate(alias, resource, ACLFilter.READ)) {
            return new ErrorResponse(resource, Response.Status.METHOD_NOT_ALLOWED).getResponse();
        }

        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.READ);

        var limit = LimitParam.valueOf(queryParams, this.getPageSize(queryParams));
        var order = OrderParam.valueOf(queryParams);
        var columns = queryParams.getFirst("columns");

        Response response = null;
        try {
            response = factory.getRESTController(alias).findResources(resource, col, arg, limit, order, columns).getResponse();
        } catch (IllegalArgumentException e) {
            response = new ErrorResponse(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        } finally {
            if (response != null) {
                u.addRead(p.end(), response.getStatus());
            }
        }
        return response;
    }

    public Response findBy (String alias, String resource, String query, List<String> args, MultivaluedMap<String, String> queryParams) {
        if (!aclManager.validate(alias, resource, ACLFilter.READ)) {
            return new ErrorResponse(resource, Response.Status.METHOD_NOT_ALLOWED).getResponse();
        }

        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.READ);

        LimitParam limit = LimitParam.valueOf(queryParams, this.getPageSize(queryParams));
        OrderParam order = OrderParam.valueOf(queryParams);

        Response response = null;
        try {
            response = factory.getRESTController(alias).findByDynamicFinder(resource, query, args, limit, order).getResponse();
        } catch (IllegalArgumentException e) {
            response = new ErrorResponse(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        } finally {
            if (response != null) {
                u.addDynamic(p.end(), response.getStatus());
            }
        }
        return response;
    }

    @Override
    public Response insert (String alias, String resource, String pk, String jsonRequest) {
        if (!aclManager.validate(alias, resource, ACLFilter.INSERT)) {
            return new ErrorResponse(resource, Response.Status.METHOD_NOT_ALLOWED).getResponse();
        }

        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.CREATE);

        Response response = null;
        try {
            response = factory.getRESTController(alias).insertResource(resource, pk, jsonRequest).getResponse();
        } catch (IllegalArgumentException e) {
            response = new ErrorResponse(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        } finally {
            if (response != null) {
                u.addCreate(p.end(), response.getStatus());
            }
        }
        return response;
    }

    public Response insert (String alias, String resource, String pk, MultivaluedMap<String, String> formParams) {
        if (!aclManager.validate(alias, resource, ACLFilter.INSERT)) {
            return new ErrorResponse(resource, Response.Status.METHOD_NOT_ALLOWED).getResponse();
        }

        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.CREATE);

        Map<String, String> map = AmforeasUtils.hashMapOf(formParams);

        Response response = null;
        try {
            response = factory.getRESTController(alias).insertResource(resource, pk, map).getResponse();
        } catch (IllegalArgumentException e) {
            response = new ErrorResponse(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        } finally {
            if (response != null) {
                u.addCreate(p.end(), response.getStatus());
            }
        }
        return response;
    }

    public Response update (String alias, String resource, String pk, String id, String jsonRequest) {
        if (!aclManager.validate(alias, resource, ACLFilter.UPDATE)) {
            return new ErrorResponse(resource, Response.Status.METHOD_NOT_ALLOWED).getResponse();
        }

        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.UPDATE);

        Response response = null;
        try {
            response = factory.getRESTController(alias).updateResource(resource, pk, id, jsonRequest).getResponse();
        } catch (IllegalArgumentException e) {
            response = new ErrorResponse(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        } finally {
            if (response != null) {
                u.addUpdate(p.end(), response.getStatus());
            }
        }
        return response;
    }

    public Response delete (String alias, String resource, String pk, String id) {
        if (!aclManager.validate(alias, resource, ACLFilter.DELETE)) {
            return new ErrorResponse(resource, Response.Status.METHOD_NOT_ALLOWED).getResponse();
        }

        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.DELETE);

        Response response = null;
        try {
            response = factory.getRESTController(alias).deleteResource(resource, pk, id).getResponse();
        } catch (IllegalArgumentException e) {
            response = new ErrorResponse(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        } finally {
            if (response != null) {
                u.addDelete(p.end(), response.getStatus());
            }
        }
        return response;
    }

    public Response storedProcedure (String alias, String query, String jsonRequest) {
        if (!aclManager.validate(alias, ACLFilter.EXEC)) {
            return new ErrorResponse(alias, Response.Status.METHOD_NOT_ALLOWED).getResponse();
        }

        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.READ);

        Response response = null;
        try {
            response = factory.getRESTController(alias).executeStoredProcedure(query, jsonRequest).getResponse();
        } catch (IllegalArgumentException e) {
            response = new ErrorResponse(alias, Response.Status.BAD_REQUEST, e.getMessage()).getResponse();
        } finally {
            if (response != null) {
                u.addQuery(p.end(), response.getStatus());
            }
        }
        return response;
    }

    public Response getStatistics () {
        return u.getUsageData().getResponse();
    }

    private Integer getPageSize (MultivaluedMap<String, String> params) {
        if (!StringUtils.isNumeric(params.getFirst("pageSize"))) {
            return this.factory.getConfiguration().getPageSize();
        }

        Integer size = Integer.valueOf(params.getFirst("pageSize"));
        if (size > this.factory.getConfiguration().getMaxPageSize()) {
            return this.factory.getConfiguration().getPageSize();
        }
        return size;


    }

}
