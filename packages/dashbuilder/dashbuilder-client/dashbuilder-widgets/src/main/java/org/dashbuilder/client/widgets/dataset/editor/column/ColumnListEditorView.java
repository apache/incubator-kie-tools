package org.dashbuilder.client.widgets.dataset.editor.column;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.Row;

import javax.enterprise.context.Dependent;

/**
 * <p>Data Set column list editor view.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class ColumnListEditorView extends Composite implements ColumnListEditor.View {

    interface Binder extends UiBinder<Widget, ColumnListEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    ColumnListEditor presenter;

    @UiField
    FlowPanel container;

    @UiConstructor
    public ColumnListEditorView() {
        initWidget(Binder.BINDER.createAndBindUi(this));
    }
    
    @Override
    public void init(final ColumnListEditor presenter) {
        this.presenter = presenter;
    }

    @Override
    public ColumnListEditor.View insert(final int index,
                                        final DataColumnDefEditor.View columnEditorView,
                                        final boolean selected, final boolean enabled,
                                        final String altText) {
        final CheckBox selectedInput = new CheckBox();
        selectedInput.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        selectedInput.getElement().getStyle().setTop(-7, Style.Unit.PX);
        selectedInput.setEnabled(enabled);
        selectedInput.setValue(selected);
        selectedInput.setTitle(altText != null ? altText : "");
        selectedInput.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Boolean> event) {
                presenter.onColumnSelect(index, event.getValue());
            }
        });

        final HorizontalPanel panel = new HorizontalPanel();
        panel.setWidth("100%");
        panel.add(selectedInput);
        panel.add(columnEditorView.asWidget());
        container.insert(panel, index);
        return this;
    }

    @Override
    public ColumnListEditor.View remove(final int index) {
        container.remove(index);
        return this;
    }

    @Override
    public ColumnListEditor.View clear() {
        container.clear();
        return this;
    }
}
