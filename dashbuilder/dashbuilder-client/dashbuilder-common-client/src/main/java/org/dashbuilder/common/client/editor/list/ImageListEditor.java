package org.dashbuilder.common.client.editor.list;

import com.google.gwt.editor.client.EditorError;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.gwtbootstrap3.client.ui.constants.Placement;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>Presenter for a gwt editor component that accepts multiple values and display each one using a given image.</p>
 * <p>The validation error messages are displayed by changing border color to RED and showing the message using a tooltip.</p>
 * <p>This component is typically used for handling enums.</p>
 *
 * @param <T> The edited type.
 *           
 * @since 0.4.0
 */
public abstract class ImageListEditor<T> implements IsWidget, LeafAttributeEditor<T> {

    private static final String IMAGE_SIZE = "160px";

    public class Entry {
        private T value;
        private SafeUri uri;
        private SafeHtml heading;
        private SafeHtml text;

        private Entry(final T value, final SafeUri uri, final SafeHtml heading, final SafeHtml text) {
            this.value = value;
            this.uri = uri;
            this.heading = heading;
            this.text = text;
        }

        public T getValue() {
            return value;
        }

        public SafeUri getUri() {
            return uri;
        }

        public SafeHtml getHeading() {
            return heading;
        }

        public SafeHtml getText() {
            return text;
        }
    }
    
    
    Event<org.dashbuilder.common.client.event.ValueChangeEvent<T>> valueChangeEvent;
    public ImageListEditorView<T> view;
    
    final List<Entry> entries = new ArrayList<Entry>();
    String imageWidth = IMAGE_SIZE;
    String imageHeight = IMAGE_SIZE;
    T value;

    public ImageListEditor(final ImageListEditorView<T> view,
                           final Event<org.dashbuilder.common.client.event.ValueChangeEvent<T>> valueChangeEvent) {
        this.view = view;
        this.valueChangeEvent = valueChangeEvent;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public Entry newEntry(final T value, final SafeUri uri, final SafeHtml heading, final SafeHtml text) {
        return new Entry(value, uri, heading, text);
    }
    
    public void setEntries(final Collection<Entry> entries) {
        this.entries.clear();
        if (entries != null) {
            this.entries.addAll(entries);
        }
        showElements();
    }
    
    public void setImageSize(final String w, final String h) {
        this.imageWidth = w;
        this.imageHeight = h;
    }

    public void setHelpContent(final String title, final String content, final Placement placement) {
        view.setHelpContent(title, content, placement);
    }
    
    /*************************************************************
     ** GWT EDITOR CONTRACT METHODS **
     *************************************************************/

    @Override
    public void showErrors(final List<EditorError> errors) {
        StringBuilder sb = new StringBuilder();
        for (EditorError error : errors) {
            if (error.getEditor().equals(this)) {
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
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(final T value) {
        setValue(value, false);
    }

    public void setValue(final T value, final boolean fireEvents) {
        if (this.value != null && this.value.equals(value)) return;

        T before = this.value;
        this.value = value;

        showElements();

        if (fireEvents) {
            valueChangeEvent.fire(new org.dashbuilder.common.client.event.ValueChangeEvent<T>(this, before, this.value));
        }
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
    
    public void clear() {
        this.entries.clear();
        this.imageWidth = IMAGE_SIZE;
        this.imageHeight = IMAGE_SIZE;
        this.value = null;
        view.clear();
        
    }
    /*************************************************************
     ** PRIVATE EDITOR METHODS **
     *************************************************************/

    protected void showElements() {
        view.clear(); 
        for (final Entry entry : entries) {
            final boolean selected = this.value == null || entry.getValue().equals(this.value);
            view.add(entry.getUri(), this.imageWidth, this.imageHeight, entry.getHeading(), entry.getText(), selected, () -> {
                ImageListEditor.this.setValue(entry.getValue(), true);
            });
         }
    }
    
}
