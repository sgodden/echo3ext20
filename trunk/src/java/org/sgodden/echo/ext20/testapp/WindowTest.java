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
package org.sgodden.echo.ext20.testapp;

import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sgodden.echo.ext20.Button;
import org.sgodden.echo.ext20.Panel;
import org.sgodden.echo.ext20.Window;
import org.sgodden.echo.ext20.WindowListener;
import org.sgodden.echo.ext20.layout.FitLayout;

/**
 * Tests windows.
 * @author sgodden
 */
public class WindowTest 
        extends Panel {
    
    private static final transient Log log = LogFactory.getLog(WindowTest.class);
    
    public WindowTest(){
        super("Window test");
        add(makeWindowTestButton());
        add(makeModalWindowTestButton());
    }
    
    private Button makeWindowTestButton(){
        Button ret = new Button("Open window");
        
        ret.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                WindowTest.this.add(makeTestWindow());
            }
        });
        
        return ret;
    }
    
    private Button makeModalWindowTestButton(){
        Button ret = new Button("Open modal window");
        
        ret.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                WindowTest.this.add(makeModalTestWindow());
            }
        });
        
        return ret;
    }
    
    private TestWindow makeTestWindow() {
        final TestWindow ret = new TestWindow();
        
        ret.addWindowListener(new WindowListener() {
            public void windowClosing(ActionEvent event) {
                WindowTest.this.remove(ret);
            }
        });
        
        return ret;
    }
    
    private TestWindow makeModalTestWindow() {
        final TestWindow ret = new TestWindow();
        ret.setModal(true);
        
        ret.addWindowListener(new WindowListener() {
            public void windowClosing(ActionEvent event) {
                WindowTest.this.remove(ret);
            }
        });
        
        return ret;
    }

    private static class TestWindow extends Window {
        private TestWindow(){
            super(new FitLayout(), "Test window");
            setWidth(350);
            setHeight(400);
            UserPanel up = new UserPanel();
            up.createUI();
            add(up);
        }
    }

}