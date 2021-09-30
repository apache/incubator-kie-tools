package org.dashbuilder.common.client.editor.map;

import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.HasEditorErrors;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.common.client.event.ValueChangeEvent;
import org.dashbuilder.common.client.resources.i18n.DashbuilderCommonConstants;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.*;

/**
 * <p>Provides an editor for a Map instance of String values.</p>
 * 
 * @since 0.4.0
 */
@Dependent
public class MapEditor implements IsWidget, LeafAttributeEditor<Map<String, String>> {

    public interface View extends UberView<MapEditor> {

        View setEmptyText(String text);

        View setAddText(String text);
        
        View addTextColumn(int columnIndex, String header, boolean isSortable, int width);

        View addButtonColumn(int columnIndex, String header, int width);

        View removeColumn(int index);

        View setRowCount(int count);
        
        View setData(List<Map.Entry<String, String>> data);
        
        View showError(SafeHtml message);

        View clearError();
        
    }
    
    Event<org.dashbuilder.common.client.event.ValueChangeEvent<Map<String, String>>> valueChangeEvent;
    public View view;

    Map<String, String> value;
    
    @Inject
    public MapEditor(final View view,
                     final Event<org.dashbuilder.common.client.event.ValueChangeEvent<Map<String, String>>> valueChangeEvent) {
        this.view = view;
        this.valueChangeEvent = valueChangeEvent;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.setAddText(DashbuilderCommonConstants.INSTANCE.add());
        initDataGrid();
    }
    
    /*************************************************************
     ** GWT EDITOR CONTRACT METHODS **
     *************************************************************/
    
    @Override
    public void showErrors(final List<EditorError> errors) {
        StringBuilder sb = new StringBuilder();
        for (EditorError error : errors) {

            if (error.getEditor() == this) {
                sb.append("\n").append(error.getMessage());
            }
        }

        boolean hasErrors = sb.length() > 0;
        if (!hasErrors) {
            view.clearError();
            return;
        }

        // Show the errors.
        view.showError(new SafeHtmlBuilder().appendEscaped(sb.substring(1)).toSafeHtml());
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void setValue(final Map<String, String> value) {
        setValue(value, false);
    }
    
    @Override
    public Map<String, String> getValue() {
        return value;
    }

    /*************************************************************
     ** VIEW CALLBACK METHODS **
     *************************************************************/

    void addEntry() {
        onAddEntry();
    }
    
    String getValue(final int columnIndex, final Map.Entry<String, String> object) {
        
        // Key updated.
        if (columnIndex == 0) {
            return object.getKey();
        }

        // Value updated.
        if (columnIndex == 1) {
            return object.getValue();
        }
        
        return null;
    }

    void update(final int columnIndex, final int index, final Map.Entry<String, String> object, final String value) {
        
        // Key updated.
        if (columnIndex == 0) {
            onKeyUpdated(index, object, value);
        
        // Value updated.
        } else if (columnIndex == 1) {
            onValueUpdated(index, object, value);
        
        // Remove action.
        } else if (columnIndex == 2) {
            onRemoveEntry(object);
        }
        
    }

    /*************************************************************
     ** PRIVATE EDITOR METHODS **
     *************************************************************/
    
    private void onKeyUpdated(final int index, final Map.Entry<String, String> object, final String value) {
        final HashMap<String, String> _m = new LinkedHashMap<String, String>(getValue());
        final String lastKeyValue = object.getKey();
        String mapValue = "";
        if (lastKeyValue != null) {
            mapValue = _m.remove(lastKeyValue);
        }
        _m.put(value, mapValue);
        setValue(_m, true);
    }

    private void onValueUpdated(final int index, final Map.Entry<String, String> object, final String value) {
        // Look for the entry.
        final String key = getKeyParameter(index);
        final HashMap<String, String> _m = new LinkedHashMap<String, String>(getValue());
        _m.put(key, value);
        setValue(_m, true);
    }
    
    private void onRemoveEntry(final Map.Entry<String, String> object) {
        if (getValue() != null) {
            final HashMap<String, String> _m = new LinkedHashMap<String, String>(getValue());
            _m.remove(object.getKey());
            setValue(_m, true);
        }
    }

    private void onAddEntry() {
        final String key = DashbuilderCommonConstants.INSTANCE.newValue();
        final String value = DashbuilderCommonConstants.INSTANCE.newValue();
        final HashMap<String, String> _m = new LinkedHashMap<String, String>();;
        _m.put(key, value);
        if (getValue() != null) {
          _m.putAll(getValue());  
        }

        // New value.
        setValue(_m, true);
    }
    
    private void setValue(final Map<String, String> value, final boolean fireEvents) {
        // Disable current error markers, if present.
        view.clearError();

        final Map<String, String> before = this.value;
        this.value = value;

        // Fill grid values.
        redraw();

        // Fire events, if necessary.
        if (fireEvents) {
            valueChangeEvent.fire(new ValueChangeEvent<Map<String, String>>(this, before, value));
        }
    }

    private void initDataGrid() {
        view.setEmptyText(DashbuilderCommonConstants.INSTANCE.noData());
        addColumns();

    }

    private void addColumns() {
        // Key column.
        view.addTextColumn(0, DashbuilderCommonConstants.INSTANCE.key(), false, 20);

        // Value column.
        view.addTextColumn(1, DashbuilderCommonConstants.INSTANCE.value(), false, 20);

        // Remove action column.
        view.addButtonColumn(2, DashbuilderCommonConstants.INSTANCE.actions(), 20);

    }

    private String getKeyParameter(final int index) {
        if (getValue() != null && !getValue().isEmpty() && index > -1) {
            int x = 0;
            for (Map.Entry<String, String> entry : getValue().entrySet()) {
                if (index == x) return entry.getKey();
                x++;
            }

        }
        return null;
    }
    
    private void redraw() {
        // NOTE: If not removing and re-adding columns, grid tow data refresh is not well done.
        view.removeColumn(0);
        view.removeColumn(0);
        view.removeColumn(0);

        // Rebuild grid columns.
        initDataGrid();

        // Set new data.
        final int count = value != null ? value.size() : 0;
        final List<Map.Entry<String, String>> list = value != null ? new LinkedList<Map.Entry<String, String>>(value.entrySet()) : new LinkedList<Map.Entry<String, String>>();
        view.setRowCount(count);
        view.setData(list);
    }
    
}
