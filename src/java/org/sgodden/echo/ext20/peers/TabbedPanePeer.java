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
package org.sgodden.echo.ext20.peers;

import nextapp.echo.app.Component;
import nextapp.echo.app.update.ClientUpdateManager;
import nextapp.echo.app.util.Context;
import nextapp.echo.webcontainer.AbstractComponentSynchronizePeer;

import org.sgodden.echo.ext20.TabbedPane;

@SuppressWarnings({"unchecked"})
public class TabbedPanePeer 
extends ExtComponentPeer {
    
//    protected static final Service TABBED_PANE_SERVICE = JavaScriptService.forResource("EchoExt20.TabbedPane", 
//            "org/sgodden/echo/ext20/resource/js/Ext20.TabbedPane.js");
//    
//    static {
//        WebContainerServlet.getServiceRegistry().add(TABBED_PANE_SERVICE);
//    }
    
    public TabbedPanePeer() {
        super();
        addEvent(new AbstractComponentSynchronizePeer.EventPeer(
                TabbedPane.ACTIVE_TAB_CHANGE_EVENT, 
                TabbedPane.TAB_CHANGE_LISTENERS_CHANGED_PROPERTY) {
            @Override
            public boolean hasListeners(Context context, Component component) {
                return ((TabbedPane) component).hasTabChangeListeners();
            }
        });
        addEvent(new AbstractComponentSynchronizePeer.EventPeer(
                TabbedPane.TAB_CLOSE_EVENT, TabbedPane.TAB_CLOSE_LISTENERS_CHANGED) {
            @Override
            public boolean hasListeners(Context context, Component component) {
                return true;
            }
        });
    }

    public Class getComponentClass() {
        return TabbedPane.class;
    }

    public String getClientComponentType(boolean shortType) {
        return shortType ? "E2TP" : "Ext20TabbedPane";
    }

    /**
     * @see nextapp.echo.webcontainer.AbstractComponentSynchronizePeer#getInputPropertyClass(java.lang.String)
     */
    public Class getInputPropertyClass(String propertyName) {
        if (TabbedPane.PROPERTY_ACTIVE_TAB_INDEX.equals(propertyName)) {
            return Integer.class;
        }
        return null;
    }
    

    @Override
    public Class getEventDataClass(String eventType) {
        if (TabbedPane.TAB_CLOSE_EVENT.equals(eventType)){
            return Integer.class;
        } else if (TabbedPane.ACTIVE_TAB_CHANGE_EVENT.equals(eventType)){
            return Integer.class;
        } else {
            return super.getEventDataClass(eventType);
        }
    }

    /**
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#storeInputProperty(Context, Component, String, int, Object)
     */
    public void storeInputProperty(Context context, Component component, String propertyName, int propertyIndex, Object newValue) {
        if (propertyName.equals(TabbedPane.PROPERTY_ACTIVE_TAB_INDEX)) {
            ClientUpdateManager clientUpdateManager = (ClientUpdateManager) context.get(ClientUpdateManager.class);
            clientUpdateManager.setComponentProperty(component, TabbedPane.PROPERTY_ACTIVE_TAB_INDEX, newValue);    
        }
    }

    /**
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#init(Context)
     */
    public void init(Context context, Component c) {
        super.init(context, c);
        //ServerMessage serverMessage = (ServerMessage) context.get(ServerMessage.class);
        //serverMessage.addLibrary(TABBED_PANE_SERVICE.getId());
    }


}
