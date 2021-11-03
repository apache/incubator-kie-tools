package org.dashbuilder.client.widgets.dataset.editor.sql;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.dashbuilder.common.client.editor.list.DropDownEditor;
import org.gwtbootstrap3.client.ui.RadioButton;

import javax.enterprise.context.Dependent;

/**
 * <p>The SQL Data Set attributes editor view.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class SQLDataSetDefAttributesEditorView extends Composite implements SQLDataSetDefAttributesEditor.View {

    interface Binder extends UiBinder<Widget, SQLDataSetDefAttributesEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    SQLDataSetDefAttributesEditor presenter;

    @UiField(provided = true)
    DropDownEditor.View dataSource;

    @UiField(provided = true)
    ValueBoxEditor.View dbSchema;

    @UiField
    @Editor.Ignore
    RadioButton tableButton;

    @UiField
    @Editor.Ignore
    RadioButton queryButton;

    @UiField
    FlowPanel dbTablePanel;
    
    @UiField(provided = true)
    ValueBoxEditor.View dbTable;

    @UiField
    FlowPanel dbSQLPanel;

    @UiField(provided = true)
    ValueBoxEditor.View dbSQL;

    @Override
    public void init(final SQLDataSetDefAttributesEditor presenter) {
        this.presenter = presenter;
    }
    
    @Override
    public void initWidgets(final DropDownEditor.View dataSource, final ValueBoxEditor.View dbSchema,
                            final ValueBoxEditor.View dbTable, final ValueBoxEditor.View dbSQL) {
        this.dataSource = dataSource;
        this.dbSchema = dbSchema;
        this.dbTable = dbTable;
        this.dbSQL = dbSQL;
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    public void table() {
        dbTablePanel.setVisible(true);
        dbSQLPanel.setVisible(false);
        tableButton.setValue(true);
        queryButton.setValue(false);
    }

    public void query() {
        dbTablePanel.setVisible(false);
        dbSQLPanel.setVisible(true);
        tableButton.setValue(false);
        queryButton.setValue(true);
    }
    
    @UiHandler("tableButton")
    void handleTableRadioClick(ClickEvent e) {
        presenter.onSelectTable();
    }

    @UiHandler("queryButton")
    void handleQueryRadioClick(ClickEvent e) {
        presenter.onSelectQuery();
    }
    
}
