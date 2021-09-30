package org.dashbuilder.common.client.editor.list;

import org.dashbuilder.common.client.editor.HasEditMode;
import org.dashbuilder.common.client.event.ValueChangeEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * <p>Images are shown using a drop down selector.</p>
 *
 * @param <T> The edited type.
 *
 * @since 0.4.0
 */
@Dependent
public class DropDownImageListEditor<T> extends ImageListEditor<T> implements HasEditMode {

    boolean isEditMode = true;

    public interface View<T> extends ImageListEditorView<T> {
        void setDropDown(boolean isDropDown);
    }

    @Inject
    public DropDownImageListEditor(DropDownImageListEditorView<T> dropDownImageListEditorView, Event<ValueChangeEvent<T>> valueChangeEvent) {
        super(dropDownImageListEditorView, valueChangeEvent);
    }

    protected DropDownImageListEditor(ImageListEditorView<T> imageListEditorView, Event<ValueChangeEvent<T>> valueChangeEvent) {
        super(imageListEditorView, valueChangeEvent);
    }

    @Override
    public void isEditMode(final boolean isEdit) {
        this.isEditMode = isEdit;
        showElements();
    }
    
    @Override
    protected void showElements() {
        super.showElements();
        ((DropDownImageListEditor.View<T>)view).setDropDown(isEditMode && entries != null && entries.size() > 1);
    }
}
