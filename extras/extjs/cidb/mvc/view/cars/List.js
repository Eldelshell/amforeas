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

Ext.define('cidb.view.cars.List' ,{
    extend: 'Ext.grid.Panel',
    alias : 'widget.carsList',
    store : 'Cars',
    frame: true,
    margin: '10 10 10 10',
    listeners: {
        'selectionchange': function(view, record) {
            var win = this.up('window');
            if(record){
                this.down('#deleteCarButton').enable()
                this.down('#showCarButton').enable()
            }else{
                this.down('#deleteCarButton').disable()
                this.down('#showCarButton').disable()
            }
        }
    },
    
    

    initComponent: function() {
        var editingPlugin = Ext.create('Ext.grid.plugin.RowEditing', {
            clicksToMoveEditor: 1,
            autoCancel: false
        })
        
        var brands = Ext.create('cidb.store.Brands')
        var fuels = Ext.create('Ext.data.Store',{
            fields: ['fuel'],
            data:[
                {"fuel":"Gasoline"},
                {"fuel":"Diesel"},
                {"fuel":"Hybrid"},
                {"fuel":"Electric"},
                {"fuel":"Hydrogen"}
            ]
        });
        
        var trs = Ext.create('Ext.data.Store',{
            fields: ['trs'],
            data:[
                {"trs":"Automatic"},
                {"trs":"Manual"},
                {"trs":"Semi-Automatic"}
            ]
        });
        
        Ext.apply(this,{
            plugins: [ editingPlugin ],
            
            columns: [
                {header: 'Model',  dataIndex: 'model',  flex: 1, editor:{allowBlank: false}},
                {header: 'Maker', dataIndex: 'maker', flex: 1, editor:{
                    xtype: 'combo',
                    name : 'brand',
                    store: brands,
                    queryMode: 'local',
                    displayField: 'name',
                    valueField: 'name',
                    editable: false
                }},
                {header: 'Year', dataIndex: 'year', flex: 1, editor:{
                        xtype: 'numberfield',
                        value: 2011,
                        minValue: 2008,
                        maxValue: 2011, allowBlank: false
                }},
                {header: 'Fuel', dataIndex: 'fuel', flex: 1, editor:{
                    xtype: 'combo',
                    name : 'fuel',
                    store: fuels,
                    queryMode: 'local',
                    displayField: 'fuel',
                    valueField: 'fuel',
                    editable: false
                }},
                {header: 'Transmission', dataIndex: 'transmission', flex: 1, editor:{
                    xtype: 'combo',
                    name : 'trs',
                    store: trs,
                    queryMode: 'local',
                    displayField: 'trs',
                    valueField: 'trs',
                    editable: false
                }},
                //current recommended price
                {header: 'CRP(€)', dataIndex: 'currentmarketvalue', editor:{xtype: 'numberfield', minValue: 0, step: 5000, allowBlank: false}},
                //proposed sale price
                {header: 'PSP(€)', dataIndex: 'newvalue', editor:{xtype: 'numberfield', minValue: 0, step: 5000, allowBlank: false}},
            ],

            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    items: [
                        {
                            text: 'Save',
                            action: 'saveCars',
                            scope: this,
                            handler: this.onSaveClick
                        },{    
                            text: 'Reload',
                            action: 'reloadCars',
                            scope: this,
                            handler: this.onReloadClick
                        }
                    ]
                },{
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            text: 'Add',
                            scope: this,
                            handler: this.onAddClick
                        },{
                            text: 'Details',
                            disabled: true,
                            action: 'showCar',
                            itemId: 'showCarButton',
                            scope: this,
                            handler: this.onShowClick
                        },{
                            text: 'Delete',
                            disabled: true,
                            action: 'removeCar',
                            itemId: 'deleteCarButton',
                            scope: this,
                            handler: this.onDeleteClick
                        }
                    ]
                }
            ]
        })
        
        this.callParent(arguments);
    },
    
    onAddClick: function(){
        console.log("adding car")
        var rec = Ext.create('cidb.model.Car',{
            year: 2011,
            phantom: true
        });
        var edit = this.editingPlugin;
        edit.cancelEdit();
        this.store.insert(0, rec);
        edit.startEdit(0, 1);
    },
    
    onDeleteClick: function(){
        var selection = this.getView().getSelectionModel().getSelection()[0];
        if (selection) {
            this.store.remove(selection);
        }
    },
    
    onSaveClick: function(){
//        this.store.sync()
//        this.store.load()
        var store = this.store
        Ext.MessageBox.confirm('Update Records','Are you sure you want to save your changes?',function(btn){
            if(btn == 'yes'){
                store.sync();
                store.load();
            }
        })
    },
    
    onReloadClick: function(){
        this.store.load();
    },
    
    onShowClick: function(){
        var selection = this.getView().getSelectionModel().getSelection()[0];
        if (selection) {
            Ext.create('cidb.view.cars.Show',{
                car: selection
            })
        }
    }
});
