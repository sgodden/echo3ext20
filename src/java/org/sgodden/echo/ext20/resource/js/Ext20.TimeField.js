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
EchoExt20.TimeField = Core.extend(EchoExt20.ExtComponent, {
    
    $load: function() {
        Echo.ComponentFactory.registerType("Ext20TimeField", this);
        Echo.ComponentFactory.registerType("E2TMF", this);
    },

    focusable: true,
    
    componentType: "Ext20TimeField"
    
});

EchoExt20.TimeFieldSync = Core.extend(EchoExt20.ExtComponentSync, {
    
    $load: function() {
        Echo.Render.registerPeer("Ext20TimeField", this);
    },
    
    _handleBlurEventRef: null,
    
    $construct: function() {
        this._handleBlurEventRef = Core.method(this, this._handleBlurEvent);
    },
    
    createExtComponent: function(update, options) {
        
        var timeFormat = this.component.get("timeFormat");
        if (timeFormat == null) {
            throw new Error("property 'timeFormat' must be specified on time fields");
        }
        options['format'] = timeFormat;

        options['fieldLabel'] = this.component.get("fieldLabel");
        if (this.component.get("allowBlank") != null) {
            options['allowBlank'] = this.component.get("allowBlank");
        }
        if ( !(this.component.isEnabled()) ) {
            options['disabled'] = true;
        }
        
        var extComponent = new Ext.form.TimeField(options);

        var time = this.component.get("time");
        if (time != null) {
            extComponent.setValue(time);
        }
        
        extComponent.on('blur', this._handleBlurEventRef);
        
        return extComponent;
    },
    
    _handleBlurEvent: function() {
        var value = this.extComponent.getValue();
        this.component.set("time", value);
    },
    
    renderUpdate: function(update){
        EchoExt20.ExtComponentSync.prototype.renderUpdate.call(this, update);
        var time = this.component.get("time");
        if (time != null) {
            extComponent.setValue(time);
        }
    }
    
});
