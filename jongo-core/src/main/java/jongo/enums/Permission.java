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

package jongo.enums;

/**
 *
 * @author Alejandro Ayuso
 * @deprecated 
 */
public enum Permission {
    NONE,
    READ,
    WRITE,
    READWRITE;
    
    public static Permission valueOf(int value){
        switch(value){
            case 1:
                return READ;
            case 2:
                return WRITE;
            case 3:
                return READWRITE;
            default:
                return NONE;
        }
    }
    
    public int getValue(){
        return this.ordinal();
    }
    
    public boolean isReadable(){
        return (this == READ || this == READWRITE);
    }
    
    public boolean isWritable(){
        return (this == WRITE || this == READWRITE);
    }
}
