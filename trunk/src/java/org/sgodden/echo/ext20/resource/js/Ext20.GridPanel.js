/* =================================================================
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
#
# ================================================================= */
EchoExt20.GridPanel = Core.extend(EchoExt20.ExtComponent, {
    
    $load: function() {
        Echo.ComponentFactory.registerType("Ext20GridPanel", this);
        Echo.ComponentFactory.registerType("E2GP", this);
    },

    componentType: "Ext20GridPanel",
    focusable: true,

    /**
     * Programatically performs a row click.
     */
    doAction: function() {
        this.fireEvent({type: "action", source: this, actionCommand: this.get("actionCommand")});
    },

    doSort: function() {
        this.fireEvent({type: "sort", source: this});
    }
    
});

/*
 * TODO - write a custom proxy which handles all the load and sort 
 * operations, raising events to the server side when e.g. sort events
 * occur.
 * 
 * So, configure a Store with an ArrayReader, and a custom Proxy
 * which simply returns the array of current data, and which
 * passed back sort events to this sync peer, which raises
 * an event to the server side, which reconstructs the store
 * appropriately.
 * 
 * TODO - ensure that multiple selections across different
 * pages are handled and not lost.
 */
EchoExt20.GridPanelSync = Core.extend(EchoExt20.PanelSync, {

    $load: function() {
        Echo.Render.registerPeer("Ext20GridPanel", this);
    },

    _ctrlKeyDown: false,
    _selectedRows: null,
    
    /*
     * The server-side model is already correctly sorted when we are
     * initially displayed, and we need to prevent a bogus trip back to
     * the server to sort it.
     * This will be set to true after the first sort event is received by
     * the data proxy.
     */
    handleSortEvents: false,
    
    createExtComponent: function(update, options) {
        
        this._selectedRows = {};

        this.handleSortEvents = false; // avoind infinite sort loop
        options["store"] = this._makeStore();
        this.handleSortEvents = true;
        
        var view;
        if (this.component.get("groupField")) {
            view = new Ext.grid.GroupingView({
                groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})'
            });
        }
        if (view) {
            options["view"] = view;
        }
		
        options["cm"] = this.component.get("columnModel");
        // TODO - multi and single select
        var sm = new Ext.grid.RowSelectionModel();
        sm.on("rowselect", this._handleRowSelectEvent, this);
        sm.on("rowdeselect", this._handleRowDeselectEvent, this);
        options["sm"] = sm;
        
        options["border"] = true;
    
        /* should be done server side (?)
        options["bbar"] = new Ext.PagingToolbar({
            pageSize: 25,
            store: store,
            displayInfo: true,
            displayMsg: 'Displaying records {0} - {1} of {2}',
            emptyMsg: "No records to display"
        });
         */
        
        Ext.Element.get("approot").on("keydown",
                this._handleKeyDownEvent, this);
        Ext.Element.get("approot").on("keyup",
                this._handleKeyUpEvent, this);

        return EchoExt20.PanelSync.prototype
                .createExtComponent.call(this, update, options);
    },
    
    doSort: function(fieldName, sortDirection) {
        this.component.set("sortField", fieldName);
        this.component.set("sortDirection", sortDirection);
        this.component.doSort();
    },

    _handleKeyDownEvent: function(evt) {
        if (evt.keyCode == 17) {
            //alert("Control pressed");
            this._ctrlKeyDown = true;
        }
    },

    _handleKeyUpEvent: function(evt) {
        this._ctrlKeyDown = false;
    },
    
    _handleRowActivation: function() {
        this.component.doAction();
    },
    
    _handleRowDeselectEvent: function(selectionModel, rowIndex, record) {
        var pageOffset = this.component.get("pageOffset");
        var index = pageOffset + rowIndex;
        if (this._selectedRows[index] != null) {
            delete this._selectedRows[index];
        }
        this._updateRowSelection();
    },
    
    _handleRowSelectEvent: function(selectionModel, rowIndex, record) {
        var pageOffset = this.component.get("pageOffset");
        var index = pageOffset + rowIndex;
        if (!this._ctrlKeyDown) {
            this._selectedRows = {};
        }
        this._selectedRows[index] = true;
        this._updateRowSelection();
    },
    
    /**
     * Creates an ext store from the component's model representation.
     */
    _makeStore: function() {
        var model = this.component.get("model");

        /*
         * Create an ArrayReader using the fields
         * provided in the model.
         */
        var recordMappings = [];
        for (var i = 0; i < model.fields.length; i++) {
            recordMappings.push({
                name: model.fields[i],
                mapping: i
            });
        }
        var record = Ext.data.Record.create(recordMappings);
        var reader = new Ext.data.ArrayReader({}, record);
        var proxy = new EchoExt20.GridPanelDataProxy(model.data, this);

        var store;

        /*
         * TODO - defensive checks on sort field name and
         * group field name.
         */
        var sortInfo = null;
        var sortField = this.component.get("sortField");
        if (sortField) {
            var sortDirection = this.component.get("sortDirection");
            if (sortDirection == null) {
                sortDirection = "ASC";
            }
            sortInfo = {
                field: sortField,
                direction: sortDirection
            }
        }

        var groupField = this.component.get("groupField");
        if (groupField) {
            store = new Ext.data.GroupingStore({
                groupField: groupField,
                proxy: proxy,
                reader: reader,
                sortInfo: sortInfo,
                remoteSort: true
            });
        }
        else {
            store = new Ext.data.Store({
                reader: reader,
                proxy: proxy,
                sortInfo: sortInfo,
                remoteSort: true
            });
        }

        store.load();
        
        return store;
    },
    
    newExtComponentInstance: function(options) {
        var ret = new Ext.grid.GridPanel(options);
        ret.on("rowdblclick", this._handleRowActivation, this);
        return ret;
    },

    renderDispose: function(update) {
        EchoExt20.PanelSync.prototype.renderDispose.call(this, update);
        Ext.Element.get("approot").un("keydown",
                this._handleKeyDownEvent);
        Ext.Element.get("approot").un("keyup",
                this._handleKeyUpEvent);
    },
	
    renderUpdate: function(update) {
        EchoExt20.PanelSync.prototype.renderUpdate.call(this, update);
		
        var updatedStore = update.getUpdatedProperty("model");
        if (updatedStore != null) {
            // avoid infinite loop on sorting
            this.handleSortEvents = false;
            this.extComponent.reconfigure(
              this._makeStore(),
              this.component.get("columnModel")
            );
            this.handleSortEvents = true;
        }
    },

    _updateRowSelection: function() {
        var selectionString = "";
        var first = true;
        for (var row in this._selectedRows) {
            if (!first) {
                selectionString += ",";
            }
            first = false;

            selectionString += row;
        }
        this.component.set("selection", selectionString);
    }

});

/**
 * Custom ext DataProxy implementation which notifies
 * the sync peer of sort change events.
 */
EchoExt20.GridPanelDataProxy = function(data, syncPeer) {
    Ext.data.MemoryProxy.superclass.constructor.call(this);
    this.data = data;
    this.syncPeer = syncPeer;
}

Ext.extend(EchoExt20.GridPanelDataProxy, Ext.data.DataProxy, {
    
    syncPeer: null,
    
    $construct: function(syncPeer) {
        this.syncPeer = syncPeer;
    },

    load : function(params, reader, callback, scope, arg){
        params = params || {};
		
        var result;
		
        if (params.sort) {
            var sortField = params.sort;
            var direction = params.dir;
            if (this.syncPeer.handleSortEvents) {
                this.syncPeer.doSort(sortField, direction);
            }
        }
		
        try {
            result = reader.readRecords(this.data);
        }
        catch(e) {
            this.fireEvent("loadexception", this, arg, null, e);
            callback.call(scope, null, arg, false);
            return;
        }
        callback.call(scope, result, arg, true);
    }
	
});
