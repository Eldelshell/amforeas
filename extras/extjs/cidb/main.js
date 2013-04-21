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

sessionUser = null

Ext.application({
    name: 'cidb',

    appFolder: 'mvc',
    
    controllers: [
        'CarsController'
    ],

    launch: drawViewPort
});


function drawViewPort(){
    var carsGrid = Ext.create('cidb.view.cars.List');
    
    Ext.create('Ext.container.Viewport', {
        layout: 'border',
        items: [
            {
                region: 'center',
                items:[
                    carsGrid
                ]
            },{
                region: 'west',
                xtype: 'toolbar',
                layout: 'vbox',
                items: [{
                    text: 'Users',
                    menu:{
                        items:[
                            { text: 'Show Users', action: 'showUsersWindow' },
                            { text: 'Add User', disabled: true, action: 'addUsersWindow' }
                        ]
                    }
                }, {
                    text: 'Statistics',
                    menu:{
                        items:[
                            {text: 'Car Sales by year', action:'show'},
                            {text: 'Car Sales by brand', action:'show'}
                        ]
                    }
                }, {
                    text: 'Pictures', action:'show'
                }, {
                    text: 'Comments', action:'show'
                }]
            }
        ]
    });
}