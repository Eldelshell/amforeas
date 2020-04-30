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

package amforeas.filter;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import amforeas.AmforeasUtils;

/**
 * Jersey filter to add some headers
 */
@Provider
public class DefaultFormatFilter implements ContainerResponseFilter {

    @Override
    public void filter (ContainerRequestContext req, ContainerResponseContext res) throws IOException {
        this.setHeadersToResponse(res);
    }

    /**
     * Adds the DATE and Content-MD5 headers to the given 
     * {@linkplain com.sun.jersey.spi.container.ContainerResponse}
     * @param cr1 a {@linkplain com.sun.jersey.spi.container.ContainerResponse}.
     * @param entity the response body. Used to generate the Content-Length, Content-MD5 headers.
     * @param mime a {@linkplain javax.ws.rs.core.MediaType} used to normalize the Content-Type header.
     */
    private void setHeadersToResponse (ContainerResponseContext cr) {
        Object entity = cr.getEntity();
        if (entity == null) {
            return;
        }

        cr.getHeaders().add("Content-MD5", AmforeasUtils.getMD5Base64(entity.toString()));
    }

}
