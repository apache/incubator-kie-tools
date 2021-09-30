package org.dashbuilder.common.client.editor;

import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.HasEditorErrors;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.List;

/**
 * <p>Provides an editor for Boolean values using a toggle switch for the view.</p>
 * <p>It displays the editor errors using a bootstrap tooltip.</p>
 * 
 * @since 0.4.0
 */
@Dependent
public class ToggleSwitchEditor implements IsWidget, LeafAttributeEditor<Boolean> {

    public interface View extends UberView<ToggleSwitchEditor> {

        View setValue(Boolean value);

        View setEnabled(Boolean value);

        View showError(final SafeHtml message);

        View clearError();
        
    }
    
    Event<org.dashbuilder.common.client.event.ValueChangeEvent<Boolean>> valueChangeEvent;
    public View view;
    
    Boolean value;
    
    @Inject
    public ToggleSwitchEditor(final View view,
                              final Event<org.dashbuilder.common.client.event.ValueChangeEvent<Boolean>> valueChangeEvent) {
        this.view = view;
        this.valueChangeEvent = valueChangeEvent;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setEnabled(final boolean isEnabled) {
        view.setEnabled(isEnabled);
    }
    
    @Override
    public void showErrors(List<EditorError> errors) {
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
    public void setValue(Boolean value) {
        this.value = value;
        view.setValue(value);
    }

    @Override
    public Boolean getValue() {
        return value;
    }
    
    void onValueChanged(final Boolean value) {
        // Check value is not same one as current.
        if (this.value != null && this.value.equals(value)) return;
        
        // Clear error messages on the view.
        view.clearError();
                
        // Set the new value.
        Boolean before = this.value;
        this.value = value;

        // Fire the value change event.
        valueChangeEvent.fire(new org.dashbuilder.common.client.event.ValueChangeEvent<Boolean>(this, before, this.value));
        
    }
}
