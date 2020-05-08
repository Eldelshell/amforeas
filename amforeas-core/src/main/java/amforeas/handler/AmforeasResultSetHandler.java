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

package amforeas.handler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amforeas.rest.xstream.Row;

import org.apache.commons.dbutils.ResultSetHandler;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles a ResultSet and converts it to a List of {@link amforeas.rest.xstream.Row}
 */
public class AmforeasResultSetHandler implements ResultSetHandler<List<Row>> {

    private final boolean all;

    private static final Logger l = LoggerFactory.getLogger(AmforeasResultSetHandler.class);
    private static final DateTimeFormatter dateTimeFTR = ISODateTimeFormat.dateTime();
    private static final DateTimeFormatter dateFTR = ISODateTimeFormat.date();
    private static final DateTimeFormatter timeFTR = ISODateTimeFormat.time();

    /**
     * Ignored SQL Types.
     */
    private static final int[] ignore = new int[] {
        Types.ARRAY, Types.BINARY, Types.BLOB, Types.CLOB,
        Types.DATALINK, Types.DISTINCT, Types.JAVA_OBJECT,
        Types.LONGVARBINARY, Types.NCHAR, Types.NCLOB, Types.REF,
        Types.REF_CURSOR, Types.SQLXML, Types.STRUCT, Types.VARBINARY
    };

    /**
     * Constructor of the handler.
     * @param all if true, the handler will process all results in the {@linkplain java.sql.ResultSet}
     * if false, it will only process the first result.
     */
    public AmforeasResultSetHandler(final boolean all) {
        super();
        this.all = all;
    }

    /**
     * Method in charge of the conversion. Depending on the argument given to the contructor, it
     * will process all results or only the first one.
     * @param rs the {@linkplain java.sql.ResultSet}
     * @return a List of {@link amforeas.rest.xstream.Row}
     * @throws SQLException if we fail to handle the {@linkplain java.sql.ResultSet}
     */
    @Override
    public List<Row> handle (ResultSet rs) throws SQLException {
        List<Row> results = new ArrayList<Row>();
        int rowId = 0;
        if (all) {
            while (rs.next()) {
                Map<String, Object> map = resultSetToMap(rs);
                if (map != null)
                    results.add(new Row(rowId++, map));
            }
        } else {
            rs.next();
            Map<String, Object> map = resultSetToMap(rs);
            if (map != null)
                results.add(new Row(rowId++, map));
        }
        return results;
    }

    /**
     * Converts a ResultSet to a Map. Important to note that DATE, TIMESTAMP & TIME objects generate
     * a {@linkplain org.joda.time.DateTime} object using {@linkplain org.joda.time.format.ISODateTimeFormat}.
     * @param resultSet a {@linkplain java.sql.ResultSet}
     * @return a Map with the column names as keys and the values. null if something goes wrong.
     */
    public static Map<String, Object> resultSetToMap (ResultSet resultSet) {
        Map<String, Object> map = new HashMap<>();
        try {
            int columnCount = resultSet.getMetaData().getColumnCount();

            l.trace("Mapping a result set with {} columns to a Map", columnCount);

            final ResultSetMetaData meta = resultSet.getMetaData();
            for (int i = 1; i < columnCount + 1; i++) {
                final String colName = meta.getColumnName(i).toLowerCase();
                final int colType = meta.getColumnType(i);

                if (Arrays.stream(ignore).anyMatch(sqlType -> colType == sqlType)) {
                    l.trace("Ignoring column {} with type {}. Unsupported SQL Type.", colName, colType);
                    continue;
                }

                Object v = null;

                if (resultSet.getObject(i) == null) {
                    l.trace("Mapped {} column {} with value : {}", meta.getColumnTypeName(i), colName, v);
                    map.put(colName, v);
                    continue;
                }

                if (colType == Types.DATE) {
                    v = new DateTime(resultSet.getDate(i)).toString(dateFTR);
                    l.trace("Mapped DATE column {} with value : {}", colName, v);
                } else if (colType == Types.TIMESTAMP) {
                    v = new DateTime(resultSet.getTimestamp(i)).toString(dateTimeFTR);
                    l.trace("Mapped TIMESTAMP column {} with value : {}", colName, v);
                } else if (colType == Types.TIME) {
                    v = new DateTime(resultSet.getTimestamp(i)).toString(timeFTR);
                    l.trace("Mapped TIME column {} with value : {}", colName, v);
                } else if (colType == Types.DECIMAL) {
                    v = resultSet.getBigDecimal(i);
                    l.trace("Mapped DECIMAL column {} with value : {}", colName, v);
                } else if (colType == Types.FLOAT) {
                    v = resultSet.getFloat(i);
                    l.trace("Mapped FLOAT column {} with value : {}", colName, v);
                } else if (colType == Types.DOUBLE) {
                    v = resultSet.getDouble(i);
                    l.trace("Mapped DOUBLE column {} with value : {}", colName, v);
                } else if (colType == Types.TINYINT) {
                    v = resultSet.getInt(i);
                    l.trace("Mapped TINYINT column {} with value : {}", colName, v);
                } else if (colType == Types.SMALLINT) {
                    v = resultSet.getInt(i);
                    l.trace("Mapped SMALLINT column {} with value : {}", colName, v);
                } else if (colType == Types.INTEGER) {
                    v = resultSet.getInt(i);
                    l.trace("Mapped INTEGER column {} with value : {}", colName, v);
                } else if (colType == Types.BIGINT) {
                    v = resultSet.getInt(i);
                    l.trace("Mapped BIGINT column {} with value : {}", colName, v);
                } else {
                    v = resultSet.getString(i);
                    l.trace("Mapped {} column {} with value : {}", meta.getColumnTypeName(i), colName, v);
                }

                map.put(colName, v);
            }
        } catch (SQLException e) {
            l.error("Failed to map ResultSet");
            l.error(e.getMessage());
            return null;
        }

        return map;
    }

}
