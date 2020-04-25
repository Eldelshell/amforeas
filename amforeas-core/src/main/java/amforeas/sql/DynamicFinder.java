/**
 * Copyright (C) 2011, 2012 Alejandro Ayuso
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

package amforeas.sql;

import java.util.ArrayList;
import java.util.List;

import amforeas.AmforeasUtils;
import amforeas.enums.Operator;
import amforeas.exceptions.AmforeasBadRequestException;
import amforeas.jdbc.LimitParam;
import amforeas.jdbc.OrderParam;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A DynamicFinder object which is an implementation to replicate this functionality as provided
 * by Grails. The idea is that given a string like findAllByNameAndAge generate the appropriate
 * DynamicFinder and use this to generate the appropriate SQL statement.
 * A DynamicFinder  is made up of the prefix findBy or findAllBy followed by 
 * an expression that combines one or more properties, i.e.
 * findAllBy - FirstColumn - [FirstOperator] - [BooleanOperator] - [SecondColumn] - [SecondOperator] 
 * Then the values for the columns are provided separately.
 * @author Alejandro Ayuso 
 */
public class DynamicFinder {

    private static final Logger l = LoggerFactory.getLogger(DynamicFinder.class);
    public static final String FINDBY = "findBy";
    public static final String FINDALLBY = "findAllBy";
    private String table;
    private String command;
    private String firstColumn;
    private Operator firstOperator;
    private Operator booleanOperator;
    private String secondColumn;
    private Operator secondOperator;
    private final String sql;
    
    private OrderParam orderParam;
    private LimitParam limitParam;

    /**
     * Creates a dynamic finder for the given arguments.
     * @param resource the resource where to apply the statement
     * @param query the query to parse
     * @param values the values for the query
     * @return a DynamicFinder
     * @throws AmforeasBadRequestException if we're unable to parse the query or values.
     */
    public static DynamicFinder valueOf(String resource, final String query, final String... values) throws AmforeasBadRequestException {
        l.debug("Generating dynamic finder for " + query + " with values: [ " + StringUtils.join(values, ",") + "]");
        String str = query;
        String cmd = null;
        if (str.contains(FINDBY)) {
            str = str.substring(FINDBY.length());
            cmd = FINDBY;
        } else if (str.contains(FINDALLBY)) {
            str = str.substring(FINDALLBY.length());
            cmd = FINDALLBY;
        } else {
            throw new AmforeasBadRequestException("Invalid Command " + str, resource);
        }

        String[] strs = AmforeasUtils.splitCamelCase(str).split("\\ ");
        List<String> columns = new ArrayList<String>();
        List<String> ops = new ArrayList<String>();
        for (String word : strs) {
            if (!Operator.keywords().contains(word)) {
                columns.add(word.toLowerCase());
            } else {
                ops.add(word);
            }
        }
        List<Operator> operators = new ArrayList<Operator>();
        String tmp = "";
        for (int i = 0; i < ops.size(); i++) {
            tmp += ops.get(i);
            try {
                Operator op = Operator.valueOf(tmp.toUpperCase());
                if ((op == Operator.GREATERTHAN || op == Operator.LESSTHAN) && ((i + 1) < ops.size())) {
                    Operator ope = Operator.valueOf(ops.get(i + 1).toUpperCase());
                    if (ope == Operator.EQUALS) {
                        op = Operator.valueOf(tmp.toUpperCase() + "EQUALS");
                        ops.remove(i + 1);
                    }
                }
                operators.add(op);
                tmp = "";
            } catch (IllegalArgumentException e) {}
        }
        
        DynamicFinder finder = null;
        if (operators.isEmpty() && ops.isEmpty()) {
            finder = new DynamicFinder(resource, cmd, columns.get(0));
        } else if (operators.isEmpty() && !ops.isEmpty()) {
            throw new AmforeasBadRequestException("Invalid Operator", resource);
        } else {
            if (columns.size() == 1) {
                finder = new DynamicFinder(resource, cmd, columns.get(0), operators.get(0));
            } else if (columns.size() == 2) {
                if (operators.size() == 1) {
                    finder = new DynamicFinder(resource, cmd, columns.get(0), operators.get(0), columns.get(1));
                } else if (operators.size() == 2) {
                    finder = new DynamicFinder(resource, cmd, columns.get(0), operators.get(0), columns.get(1), operators.get(1));
                } else if (operators.size() == 3) {
                    finder = new DynamicFinder(resource, cmd, columns.get(0), operators.get(0), operators.get(1), columns.get(1), operators.get(2));
                } else {
                    throw new AmforeasBadRequestException("Too many operators: " + operators.size(), resource);
                }

            } else {
                throw new AmforeasBadRequestException("Too many columns: " + columns.size(), resource);
            }
        }
        l.debug(finder.getSql());
        return finder;
    }
    
    /**
     * Creates a dynamic finder for findByName queries.
     * @param resource where to apply the statement
     * @param command command either findBy or findAllBy
     * @param firstColumn firstColumn the name of an existing column
     */
    public DynamicFinder(String resource, String command, String firstColumn) {
        this.table = resource;
        this.command = command;
        this.firstColumn = firstColumn;
        this.firstOperator = Operator.EQUALS;
        this.sql = generateOneColumnQuery(this.table, this.firstColumn, this.firstOperator);
    }

    /**
     * Creates a dynamic finder for findByNameIsNotNull or findByNameIsNull queries.
     * @param resource where to apply the statement
     * @param command either findBy or findAllBy
     * @param firstColumn  the name of an existing column
     * @param firstOperator only unary operators IsNull or IsNotNull
     */
    public DynamicFinder(String resource, String command, String firstColumn, Operator firstOperator) {
        this.table = resource;
        this.command = command;
        this.firstColumn = firstColumn;
        this.firstOperator = firstOperator;
        if (this.firstOperator.isUnary()) {
            this.sql = generateNullColumnQuery(this.table, this.firstColumn, this.firstOperator);
        } else if (this.firstOperator == Operator.BETWEEN) {
            this.sql = generateBetweenQuery(this.table, this.firstColumn);
        } else {
            this.sql = generateOneColumnQuery(this.table, this.firstColumn, this.firstOperator);
        }
    }

    /**
     * Creates a dynamic finder for findByNameAndAge
     * @param resource where to apply the statement
     * @param command command either findBy or findAllBy
     * @param firstColumn the name of an existing column
     * @param booleanOperator an operator AND or OR
     * @param secondColumn  the name of an existing column
     */
    public DynamicFinder(String resource, String command, String firstColumn, Operator booleanOperator, String secondColumn) throws AmforeasBadRequestException {
        if (!booleanOperator.isBoolean()) {
            throw new AmforeasBadRequestException("Invalid Operator " + booleanOperator);
        }
        this.table = resource;
        this.command = command;
        this.firstColumn = firstColumn;
        this.firstOperator = Operator.EQUALS;
        this.booleanOperator = booleanOperator;
        this.secondColumn = secondColumn;
        this.secondOperator = Operator.EQUALS;
        this.sql = generateTwoColumnQuery(this.table, this.firstColumn, this.firstOperator, this.booleanOperator, this.secondColumn, this.secondOperator);
    }

    /**
     * Creates a dynamic finder for findByNameAndAgeGreaterThan
     * @param resource where to apply the statement
     * @param command command either findBy or findAllBy
     * @param firstColumnthe name of an existing column
     * @param booleanOperator an operator AND or OR
     * @param secondColumn the name of an existing column
     * @param secondOperator a binary operator for the second column
     */
    public DynamicFinder(String resource, String command, String firstColumn, Operator booleanOperator, String secondColumn, Operator secondOperator) throws AmforeasBadRequestException {
        if (!booleanOperator.isBoolean()) {
            throw new AmforeasBadRequestException("Invalid Operator");
        }
        this.table = resource;
        this.command = command;
        this.firstColumn = firstColumn;
        this.firstOperator = Operator.EQUALS;
        this.booleanOperator = booleanOperator;
        this.secondColumn = secondColumn;
        this.secondOperator = secondOperator;
        if (this.firstOperator == Operator.BETWEEN) {
            this.sql = generateBetweenQuery(this.table, this.firstColumn, this.booleanOperator, this.secondColumn, this.secondOperator);
        }else{
            this.sql = generateTwoColumnQuery(this.table, this.firstColumn, this.firstOperator, this.booleanOperator, this.secondColumn, this.secondOperator);
        }
    }

    /**
     * Creates a dynamic finder for findByNameNotEqualsAndAgeGreaterThan
     * @param resource where to apply the statement
     * @param command command either findBy or findAllBy
     * @param firstColumn the name of an existing column
     * @param firstOperator operator for the first column
     * @param booleanOperator an operator AND or OR
     * @param secondColumn the name of an existing column
     * @param secondOperator a binary operator for the second column
     */
    public DynamicFinder(String resource, String command, String firstColumn, Operator firstOperator, Operator booleanOperator, String secondColumn, Operator secondOperator) throws AmforeasBadRequestException {
        if (!booleanOperator.isBoolean()) {
            throw new AmforeasBadRequestException("Invalid Operator");
        }
        this.table = resource;
        this.command = command;
        this.firstColumn = firstColumn;
        this.firstOperator = firstOperator;
        this.booleanOperator = booleanOperator;
        this.secondColumn = secondColumn;
        this.secondOperator = secondOperator;
        if (this.firstOperator == Operator.BETWEEN) {
            this.sql = generateBetweenQuery(this.table, this.firstColumn, this.booleanOperator, this.secondColumn, this.secondOperator);
        }else{
            if(this.secondOperator == Operator.BETWEEN){
                this.sql = generateBetweenQuery(this.table, this.secondColumn, this.booleanOperator, this.firstColumn, this.firstOperator);
            }else{
                this.sql = generateTwoColumnQuery(this.table, this.firstColumn, this.firstOperator, this.booleanOperator, this.secondColumn, this.secondOperator);
            }
        }
    }

    /**
     * Generates a select query for a NULL or NOT NULL operators, i.e. SELECT * FROM resource WHERE firstColumn IS NULL
     * @param resource where to apply the statement
     * @param firstColumn the name of an existing column
     * @param firstOperator operator for the first column (IS NULL or IS NOT NULL)
     * @return an SQL select query
     */
    private static String generateNullColumnQuery(String resource, String firstColumn, Operator firstOperator) {
        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(resource);
        sb.append(" WHERE ");
        sb.append(firstColumn);
        sb.append(" ");
        sb.append(firstOperator.sql());
        return sb.toString();
    }

    /**
     * Generates a select query for any operator, i.e SELECT * FROM resource WHERE firstColumn >= ?
     * @param resource where to apply the statement
     * @param firstColumn the name of an existing column
     * @param firstOperator operator for the first column
     * @return an SQL select query
     */
    private static String generateOneColumnQuery(String resource, String firstColumn, Operator firstOperator) {
        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(resource);
        sb.append(" WHERE ");
        sb.append(firstColumn.toLowerCase());
        sb.append(" ");
        sb.append(firstOperator.sql());
        if (!firstOperator.isUnary()) {
            sb.append(" ?");
        }
        return sb.toString();
    }

    /**
     * Generates a select query for two columns and two operators, i.e. SELECT * FROM resource WHERE col1 >= ? OR col2 IS NOT NULL
     * @param resource where to apply the statement
     * @param firstColumn the name of an existing column
     * @param firstOperator operator for the first column
     * @param booleanOperator an operator AND or OR
     * @param secondColumn the name of an existing column
     * @param secondOperator operator for the second Column
     * @return an SQL select query
     */
    private static String generateTwoColumnQuery(String resource, String firstColumn, Operator firstOperator, Operator booleanOperator, String secondColumn, Operator secondOperator) {
        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(resource);
        sb.append(" WHERE ");
        sb.append(firstColumn);
        sb.append(" ");
        sb.append(firstOperator.sql());
        if (!firstOperator.isUnary()) {
            sb.append(" ? ");
        }else{
            sb.append(" ");
        }
        sb.append(booleanOperator.sql());
        sb.append(" ");
        sb.append(secondColumn);
        sb.append(" ");
        sb.append(secondOperator.sql());
        if (!secondOperator.isUnary()) {
            sb.append(" ?");
        }
        return sb.toString();
    }

    /**
     * Generates a select query for a between operator on one column, i.e. SELECT * FROM foo WHERE col BETWEEN ? AND ?
     * @param resource where to apply the statement
     * @param firstColumn the name of an existing column
     * @return an SQL select query
     */
    private static String generateBetweenQuery(String resource, String firstColumn) {
        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(resource);
        sb.append(" WHERE ");
        sb.append(firstColumn.toLowerCase());
        sb.append(" BETWEEN ? AND ?");
        return sb.toString();
    }
    
    /**
     * Generates a select query for a between operator on one column and another operator
     * i.e. SELECT * FROM foo WHERE col1 BETWEEN ? AND ? OR col2 = ?
     * @param resource where to apply the statement
     * @param firstColumn the name of an existing column
     * @param booleanOperator an operator AND or OR
     * @param secondColumn the name of an existing column
     * @param secondOperator operator for the second Column
     * @return an SQL select query
     */
    private static String generateBetweenQuery(String resource, String firstColumn, Operator booleanOperator, String secondColumn, Operator secondOperator) {
        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(resource);
        sb.append(" WHERE ");
        sb.append(firstColumn.toLowerCase());
        sb.append(" BETWEEN ? AND ? ");
        sb.append(booleanOperator.sql());
        sb.append(" ");
        sb.append(secondColumn);
        sb.append(" ");
        sb.append(secondOperator.sql());
        if (!secondOperator.isUnary()) {
            sb.append(" ?");
        }
        return sb.toString();
    }

    public String getCommand() {
        return command;
    }

    public String getSql() {
        return sql;
    }

    public String getTable() {
        return table;
    }

    public boolean findAll() {
        return this.command.equalsIgnoreCase(FINDALLBY);
    }
    
    public DynamicFinder setOrderParam(OrderParam param){
        this.orderParam = param;
        return this;
    }
    
    public DynamicFinder setLimitParam(LimitParam param){
        this.limitParam = param;
        return this;
    }
    
    public LimitParam getLimitParam() {
        return limitParam;
    }

    public OrderParam getOrderParam() {
        return orderParam;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("DynamicFinder ");
        b.append("{ table : ");
        b.append(table);
        b.append("{ command : ");
        b.append(command);
        b.append("}");
        b.append("{ firstColumn : ");
        b.append(firstColumn);
        b.append(" }");
        b.append("{ firstOperator : ");
        b.append(firstOperator);
        b.append(" }");
        b.append("{ booleanOperator : ");
        b.append(booleanOperator);
        b.append(" }");
        b.append("{ secondColumn : ");
        b.append(secondColumn);
        b.append(" }");
        b.append("{ secondOperator : ");
        b.append(secondOperator);
        b.append(" }");
        return b.toString();
    }
}
