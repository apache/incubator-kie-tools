package org.dashbuilder.client.widgets.dataset.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import javax.enterprise.context.Dependent;

/**
 * <p>Data Set filter editor view.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class DataSetDefFilterEditorView extends Composite implements DataSetDefFilterEditor.View {

    interface Binder extends UiBinder<Widget, DataSetDefFilterEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    DataSetDefFilterEditor presenter;

    @UiField
    FlowPanel mainPanel;

    @Override
    public void init(final DataSetDefFilterEditor presenter) {
        this.presenter = presenter;
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    @Override
    public DataSetDefFilterEditor.View setWidget(final IsWidget filterView) {
        mainPanel.clear();
        mainPanel.add(filterView);
        return this;
    }
}
