package org.dashbuilder.common.client.editor;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Tooltip;
import org.gwtbootstrap3.client.ui.constants.Placement;

/**
 * <p>The ValueBoxEditor view.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class ValueBoxEditorView<T> extends Composite implements ValueBoxEditor.View<T> {

    private static final String STYLE_ERROR = " control-group has-error ";
    
    interface Binder extends UiBinder<Widget, ValueBoxEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }
    
    @UiField
    SimplePanel contents;

    @UiField
    @Editor.Ignore
    Tooltip errorTooltip;
    
    ValueBoxEditor<T> presenter;
    ValueBoxBase<T> widget;
    
    @Override
    public void init(final ValueBoxEditor<T> presenter) {
        this.presenter = presenter;
    }
    
    @UiConstructor
    public ValueBoxEditorView() {
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    @Override
    public void setValueBox(final ValueBoxBase<T> widget) {
        this.widget = widget;
        widget.addValueChangeHandler(new ValueChangeHandler<T>() {
            @Override
            public void onValueChange(final ValueChangeEvent<T> event) {
                presenter.onValueChanged(event.getValue());
            }
        });
        contents.add(widget);
    }

    @Override
    public ValueBoxEditor.View<T> setValue(T value) {
        widget.setValue(value);
        return this;
    }

    @Override
    public ValueBoxEditor.View<T> showError(SafeHtml message) {
        contents.addStyleName(STYLE_ERROR);
        errorTooltip.setTitle(message.asString());
        return this;
    }

    @Override
    public ValueBoxEditor.View<T> clearError() {
        contents.removeStyleName(STYLE_ERROR);
        errorTooltip.setTitle("");
        return this;
    }

    public ValueBoxEditor.View<T> addHelpContent(final String title, final String content, final Placement placement) {
        final Tooltip tooltip = new Tooltip(widget);
        tooltip.setContainer("body");
        tooltip.setShowDelayMs(1000);
        tooltip.setPlacement(placement);
        tooltip.setTitle(content);
        contents.add(tooltip);
        return this;
    }

}
