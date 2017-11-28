package org.dashbuilder.common.client.editor;

import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.HasEditorErrors;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.List;

/**
 * <p>Provides an editor for a ValueBoxBase widget of type T.</p>
 * <p>It displays the editor errors using a bootstrap tooltip.</p>
 * 
 * @since 0.4.0
 */
@Dependent
public class ValueBoxEditor<T> implements IsWidget, LeafAttributeEditor<T> {

    public interface View<T> extends UberView<ValueBoxEditor<T>> {

        @UiChild(limit = 1, tagname = "valuebox")
        void setValueBox(final ValueBoxBase<T> widget);

        View<T> addHelpContent(final String title, final String content, final Placement placement);
        
        View<T> setValue(T value);
        
        View<T> showError(final SafeHtml message);
        
        View<T> clearError();

    }
    
    Event<org.dashbuilder.common.client.event.ValueChangeEvent<T>> valueChangeEvent;
    public View<T> view;
    
    T value;
    
    @Inject
    public ValueBoxEditor(final View<T> view,
                          final Event<org.dashbuilder.common.client.event.ValueChangeEvent<T>> valueChangeEvent) {
        this.view = view;
        this.valueChangeEvent = valueChangeEvent;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void addHelpContent(final String title, final String content, final Placement placement) {
        view.addHelpContent(title, content, placement);    
    }
    
    @Override
    public void showErrors(List<EditorError> errors) {
        StringBuilder sb = new StringBuilder();
        for (final EditorError error : errors) {

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
    public void setValue(T value) {
        this.value = value;
        view.setValue(value);
    }

    @Override
    public T getValue() {
        return value;
    }
    
    void onValueChanged(final T value) {
        // Check value is not same one as current.
        if (this.value != null && this.value.equals(value)) return;
        
        // Clear error messages on the view.
        view.clearError();
                
        // Set the new value.
        T before = this.value;
        this.value = value;

        // Fire the value change event.
        valueChangeEvent.fire(new org.dashbuilder.common.client.event.ValueChangeEvent<T>(this, before, this.value));
        
    }
}
