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

package amforeas.enums;

/**
 * Deprecated. Use instead the Response.Status enum
 * @deprecated 
 * @author Alejandro Ayuso 
 */
public enum ErrorCode {
    E200 ("No results"),
    E201 ("Invalid Session"),
    E202 ("Authentication Error"),
    E203 ("Invalid Operator"),
    E204 ("Failed to insert new registry"),
    E500 ("Application Error");
    
    private final String message;
    
    private ErrorCode(final String message){
        this.message = message;
    }
    
    public String getMessage(){
        return this.message;
    }
    
}
