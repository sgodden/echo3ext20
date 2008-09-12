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

import nextapp.echo.app.ApplicationInstance;
import org.sgodden.echo.ext20.AbstractExtAppServlet;

/**
 * Application servlet for the test application.
 * 
 * @author goddens
 *
 */
public class AppServlet extends AbstractExtAppServlet {
	
	public AppServlet(){
		super();
        addCssFileName("resources/ext/css/xtheme-slate.css");
		addCssFileName("resources/local/localstyle.css");
	}

    public ApplicationInstance newApplicationInstance() {
        return new AppInstance();
    }
}