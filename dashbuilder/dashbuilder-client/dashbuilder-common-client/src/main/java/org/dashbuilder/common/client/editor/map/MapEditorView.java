package org.dashbuilder.common.client.editor.map;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import javax.enterprise.context.Dependent;
import java.util.List;
import java.util.Map;

/**
 * <p>The MapEditor view that uses a DataGrid widget.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class MapEditorView extends Composite implements MapEditor.View {

    interface Binder extends UiBinder<Widget, MapEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    @Editor.Ignore
    HTMLPanel mainPanel;

    @UiField
    @Editor.Ignore
    ScrollPanel gridPanel;

    @UiField
    DataGrid<Map.Entry<String, String>> grid;

    @UiField
    @Editor.Ignore
    org.gwtbootstrap3.client.ui.Button addButton;

    @UiField
    @Editor.Ignore
    org.gwtbootstrap3.client.ui.Label errorLabel;
    
    private MapEditor presenter;

    @UiConstructor
    public MapEditorView() {
        initWidget(Binder.BINDER.createAndBindUi(this));
        addButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                presenter.addEntry();
            }
        });
    }
    
    @Override
    public void init(final MapEditor presenter) {
        this.presenter = presenter;
    }


    @Override
    public MapEditor.View setEmptyText(final String text) {
        grid.setEmptyTableWidget(new org.gwtbootstrap3.client.ui.Label(text));
        return this;
    }

    @Override
    public MapEditor.View setAddText(final String text) {
        addButton.setText(text);
        addButton.setTitle(text);
        return this;
    }

    @Override
    public MapEditor.View addTextColumn(final int columnIndex, final String heading, boolean isSortable, int width) {
        final com.google.gwt.user.cellview.client.Column<Map.Entry<String, String>, String> keyColumn =
                new com.google.gwt.user.cellview.client.Column<Map.Entry<String, String>, String>(new EditTextCell()) {
                    @Override
                    public String getValue(final Map.Entry<String, String> object) {
                        return presenter.getValue(columnIndex, object);
                    }
                };
        addColumn(keyColumn, columnIndex, heading, isSortable, width);
        return this;
    }

    @Override
    public MapEditor.View addButtonColumn(final int columnIndex, final String header, final int width) {
        final com.google.gwt.user.cellview.client.Column<Map.Entry<String, String>, String> removeColumn =
                new com.google.gwt.user.cellview.client.Column<Map.Entry<String, String>, String>(new ButtonCell( IconType.MINUS, ButtonSize.EXTRA_SMALL)) {

                    @Override
                    public String getValue(Map.Entry<String, String> object) {
                        return presenter.getValue(columnIndex, object);
                    }
                };
        addColumn(removeColumn, columnIndex, header, false, width);
        return this;
    }
    
    private void addColumn(final com.google.gwt.user.cellview.client.Column<Map.Entry<String, String>, String> column,
                           final int columnIndex, final String header, final boolean isSortable, final int width) {
        column.setSortable(isSortable);
        column.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        grid.addColumn(column, header);
        grid.setColumnWidth(column, width, Style.Unit.PCT);
        column.setFieldUpdater(new FieldUpdater<Map.Entry<String, String>, String>() {
            @Override
            public void update(final int index, final Map.Entry<String, String> object, final String value) {
                presenter.update(columnIndex, index, object, value);
            }
        });
    }

    @Override
    public MapEditor.View removeColumn(final int index) {
        grid.removeColumn(index);
        return this;
    }

    @Override
    public MapEditor.View setRowCount(final int count) {
        grid.setRowCount(count);
        return this;
    }

    @Override
    public MapEditor.View setData(final List<Map.Entry<String, String>> data) {
        grid.setRowData(0, data);
        return this;
    }

    @Override
    public MapEditor.View showError(final SafeHtml message) {
        final Element element = errorLabel.getElement();
        element.setInnerText(message.asString());
        element.getStyle().setDisplay(Style.Display.INLINE);
        element.getStyle().setBorderColor("red");
        element.getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        element.getStyle().setBorderWidth(1, Style.Unit.PX);
        errorLabel.setVisible(true);
        return this;
    }

    @Override
    public MapEditor.View clearError() {
        final Element element = errorLabel.getElement();
        element.setInnerText("");
        element.getStyle().setDisplay(Style.Display.NONE);
        element.getStyle().setBorderWidth(0, Style.Unit.PX);
        errorLabel.setVisible(false);
        return this;
    }

}
