/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amforeas;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import amforeas.PerformanceLogger;

/**
 *
 * @author Alejandro Ayuso
 */
@Tag("offline-tests")
public class PerformanceLoggerTest {

    public PerformanceLoggerTest() {}

    @BeforeAll
    public static void setUpClass () throws Exception {}

    @AfterAll
    public static void tearDownClass () throws Exception {}

    @Test
    public void testLogger () {
        PerformanceLogger p = PerformanceLogger.start(PerformanceLogger.Code.UNK);
        Long t = p.end();
        assertTrue(t.equals(0L));

        p = PerformanceLogger.start(PerformanceLogger.Code.UNK, "Test");
        t = p.end();
        assertTrue(t.equals(0L));
    }
}
