package org.dashbuilder.client.widgets.dataset.editor.column;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.editor.ValueBoxEditor;

import javax.enterprise.context.Dependent;

/**
 * <p>Data Column Definition editor view.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class DataColumnDefEditorView extends Composite implements DataColumnDefEditor.View {

    interface Binder extends UiBinder<Widget, DataColumnDefEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    DataColumnDefEditor presenter;

    @UiField(provided = true)
    ValueBoxEditor.View idView;

    @UiField(provided = true)
    IsWidget columnTypeView;
    
    @Override
    public void init(final DataColumnDefEditor presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initWidgets(final ValueBoxEditor.View idView, final IsWidget columnTypeView) {
        this.idView = idView;
        this.columnTypeView = columnTypeView;
        initWidget(Binder.BINDER.createAndBindUi(this));
    }
}
