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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Tooltip;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

/**
 * <p>The ValueBoxEditor view.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class ToggleSwitchEditorView extends Composite implements ToggleSwitchEditor.View {

    private static final String STYLE_ERROR = " control-group has-error ";
    
    interface Binder extends UiBinder<Widget, ToggleSwitchEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    @Editor.Ignore
    HTMLPanel contents;
    
    @UiField
    @Editor.Ignore
    ToggleSwitch toggleSwitch;

    @UiField
    @Editor.Ignore
    Tooltip errorTooltip;

    ToggleSwitchEditor presenter;
    
    @Override
    public void init(final ToggleSwitchEditor presenter) {
        this.presenter = presenter;
    }
    
    @UiConstructor
    public ToggleSwitchEditorView() {
        initWidget(Binder.BINDER.createAndBindUi(this));
        toggleSwitch.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Boolean> event) {
                presenter.onValueChanged(event.getValue());
            }
        });
    }

    @Override
    public ToggleSwitchEditor.View setValue(final Boolean value) {
        toggleSwitch.setValue(value);
        return this;
    }

    @Override
    public ToggleSwitchEditor.View setEnabled(final Boolean isEnabled) {
        // Only set the boolean value is it's different from the current one, if not, the ToggleSwitch remains always disabled, probably due to a gwtboostrap bug.
        if (toggleSwitch.isEnabled() != isEnabled) {
            toggleSwitch.setEnabled(isEnabled);
        }
        return this;
    }

    @Override
    public ToggleSwitchEditor.View showError(final SafeHtml message) {
        contents.addStyleName(STYLE_ERROR);
        errorTooltip.setTitle(message.asString());
        return this;
    }

    @Override
    public ToggleSwitchEditor.View clearError() {
        contents.removeStyleName(STYLE_ERROR);
        errorTooltip.setTitle("");
        return this;
    }

}
