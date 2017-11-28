package org.dashbuilder.common.client.editor.list;

import org.dashbuilder.common.client.event.ValueChangeEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * <p>Images are shown in an single row container.</p>
 *
 * @param <T> The edited type.
 *
 * @since 0.4.0
 */
@Dependent
public class HorizImageListEditor<T> extends ImageListEditor<T> {
    
    @Inject
    public HorizImageListEditor(HorizImageListEditorView<T> horizImageListEditorView, Event<ValueChangeEvent<T>> valueChangeEvent) {
        super(horizImageListEditorView, valueChangeEvent);
    }

    protected HorizImageListEditor(ImageListEditorView<T> imageListEditorView, Event<ValueChangeEvent<T>> valueChangeEvent) {
        super(imageListEditorView, valueChangeEvent);
    }
}
