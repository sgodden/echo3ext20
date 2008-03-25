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
EchoExt20.GridPanel = Core.extend(EchoApp.Component, {
    
    $load: function() {
        EchoApp.ComponentFactory.registerType("Ext20GridPanel", this);
        EchoApp.ComponentFactory.registerType("E2GP", this);
    },

    componentType: "Ext20GridPanel",
    focusable: true,

	$virtual: {
		/**
         * Programatically performs a row click.
         */
        doAction: function() {
            this.fireEvent({type: "action", source: this, actionCommand: this.get("actionCommand")});
        }

	}
    
});

EchoExt20.GridPanelSync = Core.extend(EchoExt20.ExtComponentSync, {

    $load: function() {
        EchoRender.registerPeer("Ext20GridPanel", this);
    },

	_selectedRows: {},
    
    _handleRowSelectEventRef: null,
    _handleRowDeselectEventRef: null,
     
    $construct: function() {
    	this._handleRowSelectEventRef = Core.method(this, this._handleRowSelectEvent);
    	this._handleRowDeselectEventRef = Core.method(this, this._handleRowDeselectEvent);
    },

    createExtComponent: function(update, options) {

        options["store"] = this.component.get("simpleStore");
        options["cm"] = this.component.get("columnModel");
        var sm = new Ext.grid.RowSelectionModel({singleSelect:true});
		sm.on("rowselect", this._handleRowSelectEventRef);
		sm.on("rowdeselect", this._handleRowDeselectEventRef);
		options["sm"] = sm;
        options["title"] = this.component.get("title");
        options["border"] = true;

        var gridPanel = new Ext.grid.GridPanel(options);
            
        return gridPanel;
    },
    
    _handleRowSelectEvent: function(selectionModel, rowIndex, record) {
		// update the selection value
		this._selectedRows[rowIndex] = true;

		// and now update the selection in the component
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
		this.component.doAction();

    },
    
    _handleRowDeselectEvent: function(selectionModel, rowIndex, record) {
		// update the selection value
		if (this._selectedRows[rowIndex] != null) {
			delete this._selectedRows[rowIndex];
		}
    },
    
    renderUpdate: function(){}

});
