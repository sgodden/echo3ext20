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
EchoExt20.SplitButton = Core.extend(EchoExt20.ExtComponent, {
	
    $load: function() {
        Echo.ComponentFactory.registerType("Ext20SplitButton", this);
        Echo.ComponentFactory.registerType("E2SB", this);
    },

    componentType: "Ext20SplitButton"
	
});

EchoExt20.SplitButtonSync = Core.extend(EchoExt20.ButtonSync, {

    $load: function() {
        Echo.Render.registerPeer("Ext20SplitButton", this);
    },
    
    _handleClickEventRef: null,
    
    $construct: function() {
    	this._handleClickEventRef = Core.method(this, this._handleClickEvent);
    },

    $virtual: {
        newExtComponentInstance: function(options) {
            return new Ext.Button(options);
        }
    },
    
    createExtComponent: function(update, options) {
    
    	options['text'] = this.component.get("text");
         
        // see if we have a menu child item
        if (this.component.getComponentCount() == 1) {
            var child = this.component.getComponent(0);
            if (child instanceof EchoExt20.Menu) {
                Echo.Render.renderComponentAdd(update, child, null);
                var menu = child.peer.extComponent;
                if (menu == null) {
                    throw new Error("Menu not created for button");
                }
                options['menu'] = menu;
            }
            else {
                throw new Error("Illegal child added to a button");
            }
        }
    
    	var extComponent = this.newExtComponentInstance(options);
    	extComponent.on('click', this._handleClickEventRef);
    	
    	return extComponent;
    },
    
    _handleClickEvent: function() {
    	this.component.doAction();
    }

});
