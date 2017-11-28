package org.dashbuilder.client.widgets.dataset.editor.attributes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.editor.file.FileUploadEditor;
import org.gwtbootstrap3.client.ui.IntegerBox;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Popover;
import org.gwtbootstrap3.client.ui.Tooltip;
import org.gwtbootstrap3.client.ui.constants.Placement;

import javax.enterprise.context.Dependent;

/**
 * <p>The DataSetDefRefreshIntervalEditor editor view.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class DataSetDefRefreshIntervalEditorView extends Composite implements DataSetDefRefreshIntervalEditor.View {

    interface Binder extends UiBinder<Widget, DataSetDefRefreshIntervalEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    HorizontalPanel mainPanel;
    
    @UiField
    IntegerBox valueBox;

    @UiField
    ListBox intervalType;
    
    DataSetDefRefreshIntervalEditor presenter;

    @UiConstructor
    public DataSetDefRefreshIntervalEditorView() {
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    @Override
    public void init(final DataSetDefRefreshIntervalEditor presenter) {
        this.presenter = presenter;
    }


    @Override
    public DataSetDefRefreshIntervalEditor.View addIntervalTypeItem(final String item) {
        intervalType.addItem(item);
        return this;
    }

    @Override
    public DataSetDefRefreshIntervalEditor.View setSelectedIntervalType(final int index) {
        intervalType.setSelectedIndex(index);
        return this;
    }

    @Override
    public int getSelectedIntervalTypeIndex() {
        return intervalType.getSelectedIndex();
    }

    @Override
    public DataSetDefRefreshIntervalEditor.View setQuantity(final double value) {
        valueBox.setValue((int) value);
        return this;
    }

    @Override
    public double getQuantity() {
        final Integer value = valueBox.getValue();
        return value != null ? value.doubleValue() : 0;
    }

    @Override
    public DataSetDefRefreshIntervalEditor.View setEnabled(final boolean isEnabled) {
        valueBox.setEnabled(isEnabled);
        intervalType.setEnabled(isEnabled);
        return this;
    }

    @Override
    public DataSetDefRefreshIntervalEditor.View addHelpContent(final String title, final String content, final Placement placement) {
        final Tooltip tooltip = new Tooltip(intervalType);
        tooltip.setContainer("body");
        tooltip.setShowDelayMs(1000);
        tooltip.setPlacement(placement);
        tooltip.setTitle(content);
        mainPanel.add(tooltip);
        return this;
    }
}
