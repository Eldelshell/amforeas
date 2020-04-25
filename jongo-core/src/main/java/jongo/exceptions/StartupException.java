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
package amforeas.exceptions;

/**
 * A fatal exception which indicates that Amforeas can't bootup.
 * @author Alejandro Ayuso 
 */
public class StartupException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    private final boolean fatal;
    
    public StartupException (final String msg){
        super(msg);
        this.fatal = false;
    }
    
    public StartupException (final String msg, final boolean fatal){
        super(msg);
        this.fatal = fatal;
    }
    
    public boolean isFatal(){
        return fatal;
    }
    
}
