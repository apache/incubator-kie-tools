package org.dashbuilder.common.client.editor;

import com.google.gwt.editor.client.HasEditorErrors;
import com.google.gwt.editor.client.LeafValueEditor;

/**
 * <p>Editor contract for generic attributes.</p>
 * 
 * @since 0.4.0
 */
public interface LeafAttributeEditor<T> extends HasEditorErrors<T>, LeafValueEditor<T> {
}
