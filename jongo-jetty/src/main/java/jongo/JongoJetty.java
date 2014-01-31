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

package jongo;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Main class of Jongo. Reads configuration file and starts jetty embedded.
 * @author Alejandro Ayuso
 */
public class JongoJetty{
    
    private static final Logger l = LoggerFactory.getLogger(JongoJetty.class);
    
    public static void main(String[] args) throws Exception {
        l.debug("Load Jetty Configuration");
        XmlConfiguration jettyConf = getJettyConfiguration();
        
        if(jettyConf == null){
            l.error("Failed to load Jetty Configuration. Quitting.");
            System.exit(1);
        }
        
        Server server = (Server)jettyConf.configure();
        
        l.info("Starting Jongo in Jetty Embedded mode");
        server.start();
        server.join();
    }
    
    private static XmlConfiguration getJettyConfiguration(){
        // First we try from the command line
        String jettyFile = System.getProperty("jetty.configuration");
        
        if(StringUtils.isEmpty(jettyFile)){
            l.info("No jetty.configuration option given. Using etc/jetty.xml");
            jettyFile = "etc/jetty.xml";
        }
        
        Resource jettyXml = null;
        try {
            jettyXml = Resource.newSystemResource(jettyFile);
        } catch (IOException ex) {
            l.warn("Failed to read Jetty Configuration file: {}. Trying with jetty.xml", jettyFile);
        }
        
        if(jettyXml == null){
            jettyFile = "jetty.xml";
            try {
                jettyXml = Resource.newSystemResource(jettyFile);
            } catch (IOException ex1) {
                l.error("Failed to read jetty configuration.", ex1);
            }
        }
        
        XmlConfiguration configuration = null;
        if(jettyXml != null){
            try {
                configuration = new XmlConfiguration(jettyXml.getInputStream());
            } catch (SAXException ex) {
                l.error("Failed to parse Jetty configuration");
                l.error(ex.getMessage(), ex);
            } catch (IOException ex) {
                l.error("Failed to read Jetty configuration");
                l.error(ex.getMessage(), ex);
            }
        }
        return configuration;
    }
}
