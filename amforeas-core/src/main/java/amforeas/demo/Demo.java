/**
 * Copyright (C) 2011, 2012 Alejandro Ayuso
 *
 * This file is part of Amforeas. Amforeas is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * Amforeas is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Amforeas. If not, see <http://www.gnu.org/licenses/>.
 */

package amforeas.demo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import amforeas.AmforeasUtils;
import amforeas.config.DatabaseConfiguration;
import amforeas.enums.JDBCDriver;
import amforeas.jdbc.JDBCConnectionFactory;

import org.apache.commons.dbutils.QueryRunner;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create the database resources for the demo
 * @author Alejandro Ayuso 
 */
public class Demo {

    private static final Logger l = LoggerFactory.getLogger(Demo.class);

    public static void generateDemoDatabases (final List<DatabaseConfiguration> databases) {
        for (DatabaseConfiguration k : databases)
            generateDemoDatabase(k);
    }

    public static void destroyDemoDatabases (final List<DatabaseConfiguration> databases) {
        for (DatabaseConfiguration k : databases)
            destroyDemoDatabase(k);
    }

    private static void generateDemoDatabase (final DatabaseConfiguration dbcfg) {
        final String database = dbcfg.getDatabase();
        QueryRunner run = new QueryRunner(JDBCConnectionFactory.getDataSource(dbcfg));

        l.info("Generating Demo resources in database {}", database);
        update(run, getCreateAuthTable());
        update(run, getCreateUserTable());
        update(run, getCreateMakersTable());
        update(run, getCreateCarsTable());
        update(run, getCreateCommentsTable());
        update(run, getCreatePicturesTable());
        update(run, getCreateSalesStatsTable());
        update(run, getCreateSalesByMakerAndModelStatsTable());
        update(run, getCreateEmptyTable());

        l.info("Generating Demo Data in database {}", database);

        final String insertAuthQuery = "INSERT INTO amforeas_auth (email, password) VALUES (?,?)";
        update(run, insertAuthQuery, "a@a.com", AmforeasUtils.getHashedPassword("123456"));
        update(run, insertAuthQuery, "a@b.com", AmforeasUtils.getHashedPassword("This is a test"));

        final String insertUserQuery = "INSERT INTO users (name, age, birthday, credit) VALUES (?,?,?,?)";
        update(run, insertUserQuery, "foo", 30, "1982-12-13", 32.5);
        update(run, insertUserQuery, "bar", 33, "1992-01-15", 0);
        update(run, insertUserQuery, "bar2", 24, "1934-01-15", 0);
        update(run, insertUserQuery, "bar3", 35, "1990-01-15", 0);
        update(run, insertUserQuery, "bar4", 40, "1980-01-15", 0);
        update(run, insertUserQuery, "bar5", 20, "1991-01-15", 0);

        for (CarMaker maker : CarMaker.values()) {
            update(run, "INSERT INTO maker (name, realname) VALUES (?,?)", maker.name(), maker.getRealName());
        }

        final String insertCar = "INSERT INTO car (maker, model, year, fuel, transmission, currentMarketValue, newValue) VALUES (?,?,?,?,?,?,?)";
        update(run, insertCar, "CITROEN", "C2", 2008, "Gasoline", "Manual", 9000, 13000);
        update(run, "INSERT INTO car (maker, model, year, transmission, currentMarketValue, newValue) VALUES (?,?,?,?,?,?)", "FIAT", "500", 2010, "Manual", 19000, 23.000);
        update(run, insertCar, "BMW", "X5", 2011, "Diesel", "Automatic", 59000, 77000);

        final String insertComment = "INSERT INTO comments (car_id, car_comment) VALUES (?,?)";
        update(run, insertComment, 0, "The Citroen C2 is a small car with a great attitude");
        update(run, insertComment, 0, "I Love my C2");
        update(run, insertComment, 2, "BMW's X5 costs too much for what it's worth. Checkout http://www.youtube.com/watch?v=Bg1TB4dRobY");

        final String insertPicture = "INSERT INTO pictures (car_id, picture) VALUES (?,?)";
        update(run, insertPicture, 0, "http://www.babez.de/citroen/c2/picth01.jpg");
        update(run, insertPicture, 0, "http://www.babez.de/citroen/c2/pic02.jpg");
        update(run, insertPicture, 0, "http://www.babez.de/citroen/c2/picth03.jpg");

        update(run, insertPicture, 1, "http://www.dwsauto.com/wp-content/uploads/2008/07/fiat-500-photo.jpg");
        update(run, insertPicture, 1, "http://www.cochesadictos.com/coches/fiat-500/imagenes/index1.jpg");
        update(run, insertPicture, 1, "http://www.cochesadictos.com/coches/fiat-500/imagenes/index4.jpg");

        update(run, insertPicture, 2, "http://www.coches21.com/fotos/100/bmw_x5_457.jpg");
        update(run, insertPicture, 2, "http://www.coches21.com/fotos/100/bmw_x5_460.jpg");
        update(run, insertPicture, 2, "http://www.coches21.com/modelos/250/bmw_x5_65.jpg");

        // generate some random data for the stats page
        DateTimeFormatter isofmt = ISODateTimeFormat.dateTime();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
        DateTime dt;// = isofmt.parseDateTime("2012-01-16T13:34:00.000Z");
        for (int year = 2000; year < 2012; year++) {
            for (int month = 1; month <= 12; month++) {
                int val = 1910 + new Random().nextInt(100);
                dt = isofmt.parseDateTime(year + "-" + month + "-01T01:00:00.000Z");
                update(run, "INSERT INTO sales_stats (year, month, sales, last_update) VALUES (?,?,?,?)", year, month, val, fmt.print(dt));
                for (CarMaker maker : CarMaker.values()) {
                    val = new Random().nextInt(100);
                    update(run, "INSERT INTO maker_stats (year, month, sales, maker, last_update) VALUES (?,?,?,?,?)", year, month, val, maker.name(), fmt.print(dt));
                }
            }
        }

        update(run, "SET TABLE maker READONLY TRUE");

        // load the sp
        update(run, "CREATE FUNCTION simpleStoredProcedure () RETURNS TINYINT RETURN 1");
        update(run, "CREATE PROCEDURE insert_comment (IN car_id INTEGER, IN car_comment VARCHAR(255)) MODIFIES SQL DATA INSERT INTO comments VALUES (DEFAULT, car_id, car_comment)");
        update(run, "CREATE PROCEDURE get_year_sales (IN in_year INTEGER, OUT out_total INTEGER) READS SQL DATA SELECT COUNT(sales) INTO out_total FROM sales_stats WHERE year = in_year");
        update(run, getCreateView());

    }

    private static void destroyDemoDatabase (final DatabaseConfiguration dbcfg) {
        final String database = dbcfg.getDatabase();
        QueryRunner run = new QueryRunner(JDBCConnectionFactory.getDataSource(dbcfg));
        l.info("Destroying Demo Tables in database " + database);
        try {
            run.update("DROP FUNCTION simpleStoredProcedure");
            run.update("DROP PROCEDURE insert_comment");
            run.update("DROP PROCEDURE get_year_sales");
            run.update("DROP VIEW MAKER_STATS_2010");
            run.update("DROP TABLE maker_stats");
            run.update("DROP TABLE sales_stats");
            run.update("DROP TABLE comments");
            run.update("DROP TABLE pictures");
            run.update("DROP TABLE car");
            run.update("DROP TABLE users");
            run.update("DROP TABLE maker");
            run.update("DROP TABLE empty");
        } catch (SQLException ex) {
            l.error("Failed to destroy demo tables " + ex.getMessage());
        } finally {
            try {
                run.getDataSource().getConnection().close();
            } catch (SQLException ex) {
                l.error("Failed to close demo database " + ex.getMessage());
            }
        }
    }

    private static String getCreateSalesByMakerAndModelStatsTable () {
        StringBuilder b = new StringBuilder();
        b.append("CREATE TABLE maker_stats (");
        b.append("id INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 0 INCREMENT BY 1) PRIMARY KEY, ");
        b.append("year INTEGER, month INTEGER, maker VARCHAR(50), sales INTEGER, last_update DATETIME NOT NULL)");
        return b.toString();
    }

    private static String getCreateSalesStatsTable () {
        StringBuilder b = new StringBuilder();
        b.append("CREATE TABLE sales_stats (");
        b.append("id INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 0 INCREMENT BY 1) PRIMARY KEY, ");
        b.append("year INTEGER, month INTEGER, sales INTEGER, last_update DATETIME NOT NULL)");
        return b.toString();
    }

    private static String getCreatePicturesTable () {
        StringBuilder b = new StringBuilder();
        b.append("CREATE TABLE pictures (");
        b.append("id INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 0 INCREMENT BY 1) PRIMARY KEY, ");
        b.append("car_id INTEGER, picture VARCHAR(255))");
        return b.toString();
    }

    private static String getCreateCommentsTable () {
        StringBuilder b = new StringBuilder();
        b.append("CREATE TABLE comments (");
        b.append("id INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 0 INCREMENT BY 1) PRIMARY KEY, ");
        b.append("car_id INTEGER, car_comment VARCHAR(255))");
        return b.toString();
    }

    private static String getCreateMakersTable () {
        StringBuilder b = new StringBuilder();
        b.append("CREATE TABLE maker (");
        b.append("id INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 0 INCREMENT BY 1) PRIMARY KEY, ");
        b.append("name VARCHAR(50), realname VARCHAR(50))");
        return b.toString();
    }

    private static String getCreateCarsTable () {
        StringBuilder b = new StringBuilder();
        b.append("CREATE TABLE car (");
        b.append("cid INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 0 INCREMENT BY 1) PRIMARY KEY, ");
        b.append("maker VARCHAR(50), model VARCHAR(25), year INTEGER, fuel VARCHAR(25), transmission VARCHAR(25), ");
        b.append("created DATE, lastupdate TIMESTAMP, currentMarketValue DECIMAL(10,2), newValue DECIMAL(10,2)) ");
        return b.toString();
    }

    private static String getCreateUserTable () {
        StringBuilder b = new StringBuilder();
        b.append("CREATE TABLE users (");
        b.append("id INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 0 INCREMENT BY 1) PRIMARY KEY, ");
        b.append("name VARCHAR(25) NOT NULL, age INTEGER, birthday DATE, lastupdate TIMESTAMP, credit DECIMAL(6,2)) ");
        return b.toString();
    }

    private static String getCreateEmptyTable () {
        StringBuilder b = new StringBuilder();
        b.append("CREATE TABLE empty (");
        b.append("id INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 0 INCREMENT BY 1) PRIMARY KEY, ");
        b.append("name VARCHAR(50), realname VARCHAR(50))");
        return b.toString();
    }

    private static String getCreateView () {
        return "CREATE VIEW MAKER_STATS_2010 AS SELECT month, sales, maker FROM maker_stats WHERE year = 2010";
    }

    private static String getCreateAuthTable () {
        StringBuilder b = new StringBuilder();
        b.append("CREATE TABLE amforeas_auth (");
        b.append("id INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 0 INCREMENT BY 1) PRIMARY KEY, ");
        b.append("email VARCHAR(100) NOT NULL, password VARCHAR(255) NOT NULL) ");
        return b.toString();
    }

    public static List<DatabaseConfiguration> getDemoDatabasesConfiguration () {
        List<DatabaseConfiguration> demos = new ArrayList<DatabaseConfiguration>();
        demos.add(DatabaseConfiguration.instanceOf("demo1", JDBCDriver.HSQLDB_MEM, "demo", "demo", "my_demo_db", null, null, 1, false));
        // demos.put("demo2", AbstractDatabaseConfiguration.instanceOf("demo2", JDBCDriver.HSQLDB, "demo", "demo", "jdbc:hsqldb:mem:demo2"));
        return demos;
    }

    private static void update (QueryRunner run, String stmt, Object... args) {
        if (args.length == 0) {
            // System.out.println(stmt); // uncomment to print SQL
        } else {
            String k = stmt;
            for (Object o : args) {
                String p = "";
                if (o instanceof java.lang.Number)
                    p = String.valueOf(o);
                else
                    p = "'" + String.valueOf(o) + "'";
                k = k.replaceFirst("\\?", p);
            }
            // System.out.println(k); // uncomment to print SQL
        }
        try {
            run.update(stmt, args);
        } catch (SQLException ex) {
            l.error("Failed to update database", ex.getMessage());
        }
    }
}
