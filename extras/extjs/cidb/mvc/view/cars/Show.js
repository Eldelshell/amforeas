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

Ext.define('cidb.view.cars.Show' ,{
    extend: 'Ext.window.Window',
    alias : 'widget.carShowWindow',
    title: 'Car Details',
    autoShow: true,
    plain: true,
    border: '0 0 0 0',
    constrain: true,
    layout: 'border',
    maximizable: true,
    width: 700,
    height: 400,
    car: null,
    
    initComponent: function() {
        if(this.car == null) return
        var comments = Ext.create('cidb.store.Comments',{
            car: this.car,
            filters: [
                {
                    filterFn: function(record){
                        return record.get('car_id') == this.car.get('cid')
                    },
                    scope: this
                }
            ]
        })
        comments.load()
        
        var carDetailsForm = Ext.create('cidb.view.cars.CarDetails',{
            car: this.car
        })
        
        var pictures = Ext.create('cidb.store.Pictures',{
            car: this.car,
            filters: [
                {
                    filterFn: function(record){
                        console.log(record.get('car_id') == this.car.get('cid'))
                        return record.get('car_id') == this.car.get('cid')
                    },
                    scope: this
                }
            ]
        })
        
        pictures.load({
            scope   : this,
            callback: function(records, operation, success) {
                var image = this.down('#carImage');
                for(var i = 0 ; i < records.length; i++){
                    var record = records[i];
                    if(record.get('car_id') == this.car.get('cid')){
                        image.setSrc(record.get('picture'))
                        break
                    }
                }
            }
        })
        
        var commentsList = Ext.create('cidb.view.cars.CommentsList',{
            store: comments
        })
        
        Ext.apply(this,{
            items:[
                {
                    region: 'south',
                    split: true,
                    items:[
                        commentsList
                    ]
                },{
                    region: 'center',
                    items:[
                        carDetailsForm
                    ]
                }
            ]
        })
        
        this.callParent(arguments);
    }
});

Ext.define('cidb.view.cars.CarDetails',{
    extend: 'Ext.form.Panel',
    alias : 'widget.macEditForm',
    bodyPadding: 5,
    frame: false,
    margin: '10 10 10 10',
    layout: 'anchor',
    car: null,
    defaults: {
        anchor: '100%'
    },
    initComponent: function() {
        if(this.car == null) return
        
        Ext.apply(this,{
            title: this.car.get('model'),
            items: [{
                layout:'column',
                border:false,
                items:[{
                    columnWidth:.5,
                    border:false,
                    layout: 'anchor',
                    defaultType: 'textfield',
                    items: [
                        {value: this.car.get('model'), name: 'model', fieldLabel: '<b>Model</b>', anchor:'95%', editable: false},
                        {value: this.car.get('maker'), name: 'maker', fieldLabel: '<b>Maker</b>', anchor:'95%'}
                    ]
                },{
                    columnWidth:.5,
                    border: false,
                    frame: false,
                    layout: 'anchor',
                    items: [{
                        xtype: 'image',
                        src: 'extjs/resources/themes/images/default/shared/large-loading.gif',
                        itemId: 'carImage',
                        maxWidth: 400,
                        maxHeight: 330,
                        anchor:'100%'
                    }]
                }]
            }]
        })
        this.callParent(arguments);
    }
})

Ext.define('cidb.view.cars.CommentsList' ,{
    extend: 'Ext.grid.Panel',
    alias : 'widget.commentsList',
    frame: false,
    margin: '10 10 10 10',
    listeners: {
        'selectionchange': function(view, record) {
            var win = this.up('window');
            if(record){
                this.down('#deleteCommentButton').enable()
            }else{
                this.down('#deleteCommentButton').disable()
            }
        }
    },
    
    

    initComponent: function() {
        Ext.apply(this,{
            columns: [
                {header: 'Comment',  dataIndex: 'comment',  flex: 1}
            ],

            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    items: [
                        {
                            text: 'Delete',
                            disabled: true,
                            action: 'removeCar',
                            itemId: 'deleteCommentButton',
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
        this.store.sync()
        this.store.load()
    },
    
    onReloadClick: function(){
        this.store.load();
    }
});