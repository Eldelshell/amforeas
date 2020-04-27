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

Ext.define('cidb.store.Brands',{
    extend: 'Ext.data.Store',
    fields: ['id', 'name', 'realname'],
    autoLoad: true,
    proxy:{
        type: 'rest',
        url: '/amforeas/demo1/maker',
        headers: {
            Accept: 'application/json'
        },
        reader:{
            type: 'json',
            success: true,
            root: 'cells'
        }
    }
});


