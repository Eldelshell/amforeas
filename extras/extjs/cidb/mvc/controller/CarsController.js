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

Ext.define('cidb.controller.CarsController', {
    extend: 'Ext.app.Controller',
    
    stores: [
        'Cars',
        'Comments',
        'Pictures'
    ],
    
    models: [
        'Car',
        'Comment',
        'Picture'
    ],
    
    views: [
        'cars.List',
        'cars.Show'
    ],
    
    init: function() {
        console.log('Controller');
        this.control({
            'viewport > panel': {
                render: this.onPanelRendered
            }
        })
    },
    
    onPanelRendered: function() {
        console.log('The panel was rendered');
    }
});
