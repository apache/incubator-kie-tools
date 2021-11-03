package org.dashbuilder.client.widgets.dataset.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import javax.enterprise.context.Dependent;

/**
 * <p>Data Set table preview view.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class DataSetDefPreviewTableView extends Composite implements DataSetDefPreviewTable.View {

    interface Binder extends UiBinder<Widget, DataSetDefPreviewTableView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    DataSetDefPreviewTable presenter;

    @UiField
    FlowPanel mainPanel;

    @UiConstructor
    public DataSetDefPreviewTableView() {
        initWidget(Binder.BINDER.createAndBindUi(this));

        // Set id for selenium tests
        mainPanel.getElement()
                .setId("ds-preview-editor-" + Document.get().createUniqueId());
    }

    @Override
    public void init(final DataSetDefPreviewTable presenter) {
        this.presenter = presenter;
    }

    @Override
    public DataSetDefPreviewTable.View setDisplayer(final IsWidget widget) {
        mainPanel.add(widget);
        return this;
    }

    @Override
    public DataSetDefPreviewTable.View clear() {
        mainPanel.clear();
        return this;
    }
}
