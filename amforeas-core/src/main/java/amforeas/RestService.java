/**
 * Copyright (C) Alejandro Ayuso
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

package amforeas;

import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * Wrapper for AmforeasWS
 */
public interface RestService {

    public Response dbMeta (String alias);

    public Response resourceMeta (String alias, String resource);

    public Response get (String alias, String resource, String pk, String id, MultivaluedMap<String, String> queryParams);

    public Response getAll (String alias, String resource, String pk, MultivaluedMap<String, String> queryParams);

    public Response find (String alias, String resource, String col, String arg, MultivaluedMap<String, String> queryParams);

    public Response findBy (String alias, String resource, String query, List<String> args, MultivaluedMap<String, String> queryParams);

    public Response insert (String alias, String resource, String pk, String jsonRequest);

    public Response insert (String alias, String resource, String pk, MultivaluedMap<String, String> formParams);

    public Response update (String alias, String resource, String pk, String id, String jsonRequest);

    public Response delete (String alias, String resource, String pk, String id);

    public Response storedProcedure (String alias, String query, String jsonRequest);

    public Response getStatistics ();

}
