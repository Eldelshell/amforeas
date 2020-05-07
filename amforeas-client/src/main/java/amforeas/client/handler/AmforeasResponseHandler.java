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

package amforeas.client.handler;

import java.io.IOException;
import javax.ws.rs.core.Response;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import amforeas.client.model.AmforeasResponse;
import amforeas.client.model.ErrorResponse;
import amforeas.client.model.SuccessResponse;

public class AmforeasResponseHandler implements ResponseHandler<AmforeasResponse> {

    private static final Logger l = LoggerFactory.getLogger(AmforeasResponseHandler.class);

    @Override
    public AmforeasResponse handleResponse (HttpResponse response) throws ClientProtocolException, IOException {
        final String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        final int code = response.getStatusLine().getStatusCode();

        l.debug("Got response ({}): {}", code, body);

        Class<? extends AmforeasResponse> marshall = null;
        if (code == Response.Status.OK.getStatusCode() || code == Response.Status.CREATED.getStatusCode()) {
            marshall = SuccessResponse.class;
        } else {
            marshall = ErrorResponse.class;
        }

        return new ObjectMapper().readValue(body, marshall);
    }

}
