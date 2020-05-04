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

package org.amforeas.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import amforeas.exceptions.AmforeasBadRequestException;
import amforeas.jdbc.LimitParam;
import amforeas.jdbc.OrderParam;
import amforeas.sql.DynamicFinder;
import amforeas.sql.dialect.MySQLDialect;
import amforeas.sql.dialect.OracleDialect;

/**
 * Test that dynamic finders work as expected
 */
@Tag("dialect-tests")
public class DynamicFinderTest {

    @Test
    public void test_findByName () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE name = ?";
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void testSpecialCharsInTableNames () {
        assertTrue(doTest("findByUser_id", "SELECT * FROM sometable WHERE user_id = ?"));
        assertTrue(doTest("findBy09_id", "SELECT * FROM sometable WHERE 09_id = ?"));
        assertTrue(doTest("findByid_09", "SELECT * FROM sometable WHERE id_09 = ?"));
        assertTrue(doTest("findByid09", "SELECT * FROM sometable WHERE id09 = ?"));
    }

    @Test
    public void test_findByNameAndAgeGreaterThanEquals () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE name = ? AND age >= ?";
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void test_findByNameEqualsAndAgeIsNotNull () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE name = ? AND age IS NOT NULL";
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void test_findByAgeEqualsAndNameIsNull () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE age = ? AND name IS NULL";
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void test_findByNameAndAgeEquals () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE name = ? AND age = ?";
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void test_findByNameLessThanAndAgeGreaterThanEquals () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE name < ? AND age >= ?";
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void test_findAllByAgeBetweenAndNameEquals () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE age BETWEEN ? AND ? AND name = ?";
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void test_findByNameAndAgeIsNull () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE name = ? AND age IS NULL";
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void test_findAllByAgeBetween () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE age BETWEEN ? AND ?";
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void test_findByNameAndAgeIsNotNull () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE name = ? AND age IS NOT NULL";
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void test_findByNameEquals () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE name = ?";
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void test_findAllByNameAndAge () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE name = ? AND age = ?";
        doTest(dynamicQuery, query);
    }

    @Test
    public void test_findByNameIsNotNull () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE name IS NOT NULL";
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void test_findByNameIsNotNullAndAgeIsNotNull () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE name IS NOT NULL AND age IS NOT NULL";
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void test_findByNameIsNullAndAgeIsNotNull () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE name IS NULL AND age IS NOT NULL";
        doTest(dynamicQuery, query);
    }

    @Test
    public void test_findByNameAndAgeGreaterThan () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE name = ? AND age > ?";
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void test_findByNameIsNull () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE name IS NULL";
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void test_findByNameGreaterThanAndAgeIsNull () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE name > ? AND age IS NULL";
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void test_findByNameGreaterThanEqualsAndAgeIsNotNull () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE name >= ? AND age IS NOT NULL";
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void test_findByAgeAndNameIsNull () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE age = ? AND name IS NULL";
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void test_findAllByNameLike () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE name LIKE ?";
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void test_findLastByNameLike () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE name LIKE ?";
        // In the future this shouldn't fail
        assertFalse(doTest(dynamicQuery, query));
    }

    @Test
    public void test_findAllByMarketEqualsAndDateBetween () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE date BETWEEN ? AND ? AND market = ?";
        // In the future this shouldn't fail
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void test_findAllByDateBetweenAndMarketEquals () {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        String query = "SELECT * FROM sometable WHERE date BETWEEN ? AND ? AND market = ?";
        assertTrue(doTest(dynamicQuery, query));
    }

    @Test
    public void testLimitAndOrder_findAllByDateBetweenAndMarketEquals () throws AmforeasBadRequestException {
        String dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split("_")[1];
        LimitParam l = new LimitParam();
        OrderParam o = new OrderParam();
        MySQLDialect my = new MySQLDialect();
        OracleDialect ora = new OracleDialect();
        String my_result = my.toStatementString(DynamicFinder.valueOf("sometable", dynamicQuery), l, o);
        String ora_result = ora.toStatementString(DynamicFinder.valueOf("sometable", dynamicQuery), l, o);
        String my_query =
            "SELECT * FROM sometable WHERE date BETWEEN ? AND ? AND market = ? ORDER BY id ASC LIMIT 25 OFFSET 0";
        String ora_query =
            "SELECT * FROM ( SELECT ROW_NUMBER() OVER ( ORDER BY sometable.id ASC ) AS ROW_NUMBER, sometable.* FROM sometable WHERE  date BETWEEN ? AND ? AND market = ? ) WHERE ROW_NUMBER BETWEEN 0 AND 25";
        assertEquals(my_query, my_result);
        assertEquals(ora_query, ora_result);
    }

    @Test
    public void test_findAllByAgeGreaterTahnEquals () {
        assertThrows(AmforeasBadRequestException.class, () -> doTest(getCurrentMethodName()));
    }

    @Test
    public void test_findAllByAgeGreateroThanEqualos () throws AmforeasBadRequestException {
        assertThrows(AmforeasBadRequestException.class, () -> doTest(getCurrentMethodName()));
    }

    @Test
    public void test_findAllByAgeGreateroThanEquals () throws AmforeasBadRequestException {
        assertThrows(AmforeasBadRequestException.class, () -> doTest(getCurrentMethodName()));
    }

    @Test
    public void test_findAllByCreditIsNall () throws AmforeasBadRequestException {
        assertThrows(AmforeasBadRequestException.class, () -> doTest(getCurrentMethodName()));
    }

    @Test
    public void test_findAllByCreditIsNatNull () throws AmforeasBadRequestException {
        assertThrows(AmforeasBadRequestException.class, () -> doTest(getCurrentMethodName()));
    }

    @Test
    public void test_findAllByCreditAndoAgeEquals () throws AmforeasBadRequestException {
        assertThrows(AmforeasBadRequestException.class, () -> doTest(getCurrentMethodName()));
    }

    @Test
    public void test_findAllByCreditEqualsAndoAgeEquals () throws AmforeasBadRequestException {
        assertThrows(AmforeasBadRequestException.class, () -> doTest(getCurrentMethodName()));
    }

    @Test
    public void test_findAllByCreditOxAgeEquals () throws AmforeasBadRequestException {
        assertThrows(AmforeasBadRequestException.class, () -> doTest(getCurrentMethodName()));
    }

    @Test
    public void test_findAllByCreditEqualsOxAgeEquals () throws AmforeasBadRequestException {
        assertThrows(AmforeasBadRequestException.class, () -> doTest(getCurrentMethodName()));
    }

    private boolean doTest (String dynamicQuery, String query) {
        try {
            DynamicFinder d = DynamicFinder.valueOf("sometable", dynamicQuery);
            return d.getSql().equalsIgnoreCase(query);
        } catch (AmforeasBadRequestException ex) {
            System.out.print(ex.getMessage());
        }
        return false;
    }

    private void doTest (String dynamicQuery) throws AmforeasBadRequestException {
        DynamicFinder.valueOf("sometable", dynamicQuery);
    }

    private void generateDynamicFindersTests () {
        Map<String, String> tests = new HashMap<String, String>();
        tests.put("findByName", "SELECT * FROM sometable WHERE name = ?");
        tests.put("findByNameEquals", "SELECT * FROM sometable WHERE name = ?");
        tests.put("findByNameIsNull", "SELECT * FROM sometable WHERE name IS NULL");
        tests.put("findByNameIsNotNull", "SELECT * FROM sometable WHERE name IS NOT NULL");
        tests.put("findAllByNameAndAge", "SELECT * FROM sometable WHERE name = ? AND age = ?");
        tests.put("findByNameAndAgeEquals", "SELECT * FROM sometable WHERE name = ? AND age = ?");
        tests.put("findByNameAndAgeGreaterThan", "SELECT * FROM sometable WHERE name = ? AND age > ?");
        tests.put("findByNameAndAgeGreaterThanEquals", "SELECT * FROM sometable WHERE name = ? AND age >= ?");
        tests.put("findByNameLessThanAndAgeGreaterThanEquals", "SELECT * FROM sometable WHERE name < ? AND age >= ?");
        tests.put("findByNameAndAgeIsNull", "SELECT * FROM sometable WHERE name = ? AND age IS NULL");
        tests.put("findByNameAndAgeIsNotNull", "SELECT * FROM sometable WHERE name = ? AND age IS NOT NULL");
        tests.put("findByNameEqualsAndAgeIsNotNull", "SELECT * FROM sometable WHERE name = ? AND age IS NOT NULL");
        tests.put("findByNameIsNullAndAgeIsNotNull", "SELECT * FROM sometable WHERE name IS NULL AND age IS NOT NULL");
        tests.put("findByAgeAndNameIsNull", "SELECT * FROM sometable WHERE age = ? AND name IS NULL");
        tests.put("findByAgeEqualsAndNameIsNull", "SELECT * FROM sometable WHERE age = ? AND name IS NULL");
        tests.put("findByNameIsNotNullAndAgeIsNotNull",
            "SELECT * FROM sometable WHERE name IS NOT NULL AND age IS NOT NULL");
        tests.put("findByNameGreaterThanAndAgeIsNull", "SELECT * FROM sometable WHERE name > ? AND age IS NULL");
        tests.put("findByNameGreaterThanEqualsAndAgeIsNotNull",
            "SELECT * FROM sometable WHERE name >= ? AND age IS NOT NULL");
        tests.put("findAllByAgeBetween", "SELECT * FROM sometable WHERE age BETWEEN ? AND ?");
        tests.put("findAllByAgeBetweenAndNameEquals", "SELECT * FROM sometable WHERE age BETWEEN ? AND ? AND name = ?");

        for (String str : tests.keySet()) {
            String result = tests.get(str);
            StringBuilder b = new StringBuilder("@Test\npublic void test_");
            b.append(str);
            b.append(
                "(){\nString dynamicQuery = new Exception().getStackTrace()[0].getMethodName().split(\"_\")[1];\nString query = \"");
            b.append(result);
            b.append("\";\n");
            b.append("assertTrue(doTest(dynamicQuery, query));\n}");
            // System.out.println(b.toString());
        }

    }

    private String getCurrentMethodName () {
        return Arrays.asList(
            new Exception().getStackTrace()).stream()
            .filter(ste -> ste.getMethodName().startsWith("test_find"))
            .findFirst()
            .get()
            .getMethodName()
            .split("_")[1];
    }
}
