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
package org.jongo.filter;

import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.server.impl.application.WebApplicationImpl;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import jongo.filter.DefaultFormatFilter;

import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Alejandro Ayuso 
 */
public class DefaultFormatFilterTest {
    
    DefaultFormatFilter f = new DefaultFormatFilter();
    
    public DefaultFormatFilterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testFilter() {
        ContainerResponse cr = applyFilter("application/json", "xxxxxxxxxxxxxxxxx");
        assertEquals("PvgoOWefBe8mDjrJgt6TzQ==", cr.getHttpHeaders().getFirst("Content-MD5"));
        assertEquals(17, cr.getHttpHeaders().getFirst("Content-Length"));
        
        
        cr = applyFilter("application/xml;", "xxxxxxxxxxxxxxxxx");
        System.out.println(cr.getResponse().getEntity());
        cr = applyFilter("application/json;format=jax", "xxxxxxxxxxxxxxxxx");
        cr = applyFilter("application/xml;format=jax", "xxxxxxxxxxxxxxxxx");
        cr = applyFilter("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", "xxxxxxxxxxxxxxxxx");
        
    }

    @Test
    public void testFormat() {
    }

    @Test
    public void testFormatSuccessResponse() {
    }

    @Test
    public void testFormatErrorResponse() {
    }

    @Test
    public void testFormatHeadResponse() {
    }
    
    private ContainerResponse applyFilter(String mime, String response){
        InBoundHeaders reqHeaders = new InBoundHeaders();
        reqHeaders.add(HttpHeaders.ACCEPT, mime);
        ContainerRequest req = new ContainerRequest(new WebApplicationImpl(), null, null, null, reqHeaders, null);
        ContainerResponse cr = new ContainerResponse(null, null, null);
        cr.setResponse(Response.ok(response).build());
        cr = f.filter(req, cr);
        System.out.println(HttpHeaders.CONTENT_TYPE + ": " +cr.getHttpHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        System.out.println(HttpHeaders.CONTENT_LENGTH + ": " +cr.getHttpHeaders().getFirst(HttpHeaders.CONTENT_LENGTH));
        System.out.println(HttpHeaders.DATE + ": " +cr.getHttpHeaders().getFirst(HttpHeaders.DATE));
        System.out.println("Content-MD5" + ": " +cr.getHttpHeaders().getFirst("Content-MD5"));
        return cr;
    }
}
