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
package org.sgodden.echo.ext20.grid;

import java.util.EventListener;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.event.ChangeEvent;
import nextapp.echo.app.event.ChangeListener;
import nextapp.echo.app.list.DefaultListSelectionModel;
import nextapp.echo.app.list.ListSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sgodden.echo.ext20.Panel;
import org.sgodden.echo.ext20.data.DefaultSimpleStore;
import org.sgodden.echo.ext20.data.SimpleStore;
import org.sgodden.echo.ext20.data.TableModelAdapter;

/**
 * An ext GridPanel.  It uses swing table models, since these provide a complete
 * model API, and it is portable since it is shipped with the JVM.  An adapter takes
 * care of converting these into ext stores.
 * <p/>
 * Code example:
 * <pre class="code">
List<ColumnConfiguration> cols = new ArrayList<ColumnConfiguration>();
cols.add(new ColumnConfiguration("User ID", "userid"));
cols.add(new ColumnConfiguration("Name", "name"));
ColumnModel columnModel = new ColumnModel(cols);

TableModel model = new DefaultTableModel(
    data, // simple Object[][] of your data
    new String[]{"id", "userid", "name", "date"});

gridPanel = new GridPanel(columnModel, model);

gridPanel.addActionListener(new ActionListener(){
    public void actionPerformed(ActionEvent e) {
    	Object[] data = gridPanel.getSelectedRow();
    	...
    }
});
 * </pre>
 * 
 * @author sgodden
 *
 */
public class GridPanel
        extends Panel 
        implements TableModelListener {

    private static final transient Log log = LogFactory.getLog(GridPanel.class);
    public static final String COLUMN_MODEL_PROPERTY = "columnModel";
    public static final String ACTION_COMMAND_PROPERTY = "actionCommand";
    public static final String INPUT_ACTION = "action";
    public static final String ACTION_LISTENERS_CHANGED_PROPERTY = "actionListeners";
    public static final String SELECTION_CHANGED_PROPERTY = "selection";
    public static final String SELECTION_MODEL_CHANGED_PROPERTY = "selectionModel";
    
    public static final String MODEL_CHANGED_PROPERTY="model";
    
    private TableModel tableModel;
    private ListSelectionModel selectionModel;
    private boolean suppressChangeNotifications;
    
    /**
     * Constructs a new grid panel.
     * @param columnModel the column model.
     */
    public GridPanel(ColumnModel columnModel) {
    	super();
        setColumnModel(columnModel);
        setSelectionModel(new DefaultListSelectionModel());
    }

    /**
     * Constructs a new grid panel.
     * @param columnModel the column model.
     * @param tableModel the table model.
     */
    public GridPanel(ColumnModel columnModel, TableModel tableModel) {
        this(columnModel);
        setTableModel(tableModel);
    }

    /**
     * Sets the column model for the table.
     * @param columnModel the column model for the table.
     */
    public void setColumnModel(ColumnModel columnModel) {
        setProperty(COLUMN_MODEL_PROPERTY, columnModel);
    }
    
    /**
     * Sets the data store from a Swing {@link TableModel}.
     * @param tableModel
     */
    public void setTableModel(TableModel tableModel) {
    	if (tableModel == null) {
    		throw new IllegalArgumentException("table model may not be null");
    	}
    	
    	TableModel oldValue = this.tableModel;
    	
    	this.tableModel = tableModel;
    	tableModel.removeTableModelListener(this); // just in case they set the same table model
    	tableModel.addTableModelListener(this);
    	
    	firePropertyChange(MODEL_CHANGED_PROPERTY, oldValue, tableModel);
    }
    
    /**
     * Returns the grid's table model.
     * @return the table model.
     */
    public TableModel getTableModel() {
    	return tableModel;
    }

    /**
     * Returns the action command which will be provided in 
     * <code>ActionEvent</code>s fired by this 
     * <code>Table</code>.
     * 
     * @return the action command
     */
    public String getActionCommand() {
        return (String) getProperty(ACTION_COMMAND_PROPERTY);
    }

    /**
     * Adds an <code>ActionListener</code> to the <code>Table</code>.
     * <code>ActionListener</code>s will be invoked when the user
     * selects a row.
     * 
     * @param l the <code>ActionListener</code> to add
     */
    public void addActionListener(ActionListener l) {
        getEventListenerList().addListener(ActionListener.class, l);
        // Notification of action listener changes is provided due to 
        // existence of hasActionListeners() method. 
        firePropertyChange(ACTION_LISTENERS_CHANGED_PROPERTY, null, l);
    }

    /**
     * Returns the row selection model.
     * 
     * @return the selection model
     */
    public ListSelectionModel getSelectionModel() {
        return selectionModel;
    }

    /**
     * Sets the row selection model.
     * The selection model may not be null.
     * 
     * @param newValue the new selection model
     */
    public void setSelectionModel(ListSelectionModel newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException("Selection model may not be null.");
        }
        ListSelectionModel oldValue = selectionModel;
        if (oldValue != null) {
            oldValue.removeChangeListener(changeHandler);
        }
        newValue.addChangeListener(changeHandler);
        selectionModel = newValue;
        firePropertyChange(SELECTION_MODEL_CHANGED_PROPERTY, oldValue, newValue);
    }

    /**
     * Determines the any <code>ActionListener</code>s are registered.
     * 
     * @return true if any action listeners are registered
     */
    public boolean hasActionListeners() {
        return getEventListenerList().getListenerCount(ActionListener.class) != 0;
    }

    /**
     * @see nextapp.echo.app.Component#processInput(java.lang.String, java.lang.Object)
     */
    @Override
    public void processInput(String inputName, Object inputValue) {
        super.processInput(inputName, inputValue);
        if (inputName.equals(SELECTION_CHANGED_PROPERTY)) {
            setSelectedIndices((int[]) inputValue);
        } else if (INPUT_ACTION.equals(inputName)) {
            fireActionEvent();
        }
    }

    /**
     * Selects only the specified row indices.
     * 
     * @param selectedIndices the indices to select
     */
    private void setSelectedIndices(int[] selectedIndices) {
        // Temporarily suppress the Tables selection event notifier.
        suppressChangeNotifications = true;
        ListSelectionModel selectionModel = getSelectionModel();
        selectionModel.clearSelection();
        for (int i = 0; i < selectedIndices.length; ++i) {
            selectionModel.setSelectedIndex(selectedIndices[i], true);
        }
        // End temporary suppression.
        suppressChangeNotifications = false;
        firePropertyChange(SELECTION_CHANGED_PROPERTY, null, selectedIndices);
    }

    /**
     * Fires an action event to all listeners.
     */
    private void fireActionEvent() {
        if (!hasEventListenerList()) {
            return;
        }
        EventListener[] listeners = getEventListenerList().getListeners(ActionListener.class);
        ActionEvent e = null;
        for (int i = 0; i < listeners.length; ++i) {
            if (e == null) {
                e = new ActionEvent(this, (String) getRenderProperty(ACTION_COMMAND_PROPERTY));
            }
            ((ActionListener) listeners[i]).actionPerformed(e);
        }
    }
    /**
     * Local handler for list selection events.
     */
    private ChangeListener changeHandler = new ChangeListener() {

        /** Serial Version UID. */
        private static final long serialVersionUID = 20070101L;

        /**
         * @see nextapp.echo.app.event.ChangeListener#stateChanged(nextapp.echo.app.event.ChangeEvent)
         */
        public void stateChanged(ChangeEvent e) {
            if (!suppressChangeNotifications) {
                firePropertyChange(SELECTION_CHANGED_PROPERTY, null, null);
            }
        }
    };

    /**
     * Forces a client-side refresh of the table when the table model changes.
     */
    public void tableChanged(TableModelEvent e) {
    	log.info("Table model has changed, updating");
		firePropertyChange(MODEL_CHANGED_PROPERTY, tableModel, tableModel); // a bodge but we're not interested in the old and new values anyway
	}
}
