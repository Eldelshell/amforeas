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

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A special logger used to calculate the time a request takes to be performed and log it to a file. To
 * use this, call the static start method, get a hold of the returned instance
 * and perform the operations. After your operations are done, call the end method of the instance and
 * the log message should be produced with the time in milliseconds it took for the operation to complete.
 * For this logs to appear in a given appender, it must allow DEBUG messages.
 * @author Alejandro Ayuso
 */
public class PerformanceLogger {
    private static final Logger l = LoggerFactory.getLogger(PerformanceLogger.class);
    
    private final Code code;
    private final String msg;
    private Long start;
    private Long end;
    
    private PerformanceLogger(final Code code, final String msg){
        this.code = code;
        this.msg = msg;
    }
    
    /**
     * Starts the performance logger for the given code.
     * @param code the code of the operation.
     * @return an instance to be ended.
     */
    public static PerformanceLogger start(final Code code){
        PerformanceLogger instance = new PerformanceLogger(code, "");
        instance.start = System.nanoTime();
        return instance;
    }
    
    /**
     * Starts the performance logger for the given code.
     * @param code the code of the operation.
     * @param msg a custom message to be logged.
     * @return an instance to be ended.
     */
    public static PerformanceLogger start(final Code code, final String msg){
        PerformanceLogger instance = new PerformanceLogger(code, msg);
        instance.start = System.nanoTime();
        return instance;
    }
    
    /**
     * Ends the current logger, logs the time in milliseconds since the start
     * method was called and logs a custom message if given.
     * @return 
     */
    public long end(){
        this.end = System.nanoTime();
        final long dur = TimeUnit.NANOSECONDS.toMillis(this.end - this.start);
        StringBuilder b = new StringBuilder("");
        if(msg.length() > 0){
            b.append(msg);
            b.append(" ");
        }
        b.append(code);
        b.append(" ");
        b.append(dur);
        l.debug(b.toString());
        return dur;
    }
    
    /**
     * Enum of codes supported. Each operation has its own.
     */
    public enum Code{
        UNK,
        DBMETA,
        RSMETA,
        READ,
        READALL,
        CREATE,
        UPDATE,
        DELETE;
    }
}
