package org.dashbuilder.common.client.editor;

import com.google.gwt.editor.client.Editor;

/**
 * <p>Editor that accepts a set of predefined values.</p>
 * 
 * @since 0.4.0
 */
public interface HasConstrainedValue<T> {
    /**
     * <p>Sets acceptable values for the editor.</p>
     * <p>If no acceptable values set BEFORE editing a T instance, the editor can either disable /remove/modify the editor's value/s or just not edit any value.</p>
     *
     * @param acceptableValues Acceptable values for the editor.
     */
    @Editor.Ignore
    void setAcceptableValues(T acceptableValues);
}
