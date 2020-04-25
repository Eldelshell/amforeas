/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amforeas;

import amforeas.PerformanceLogger;

import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Alejandro Ayuso 
 */
public class PerformanceLoggerTest {
    
    public PerformanceLoggerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Test
    public void testLogger(){
        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.UNK);
        Long t = p.end();
        assertTrue(t.equals(0L));
        
        p = PerformanceLogger.start(PerformanceLogger.Code.UNK, "Test");
        t = p.end();
        assertTrue(t.equals(0L));
    }
}
