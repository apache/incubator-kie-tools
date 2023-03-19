package org.dashbuilder.common.client.editor;

import com.google.gwt.editor.client.Editor;

/**
 * <p>Editor that restrict a set of predefined values to use.</p>
 * 
 * @since 0.4.0
 */
public interface HasRestrictedValue<T> {
   
    /**
     * Specify the value that is restricted to use. 
     * @param value
     */
    @Editor.Ignore
    void onValueRestricted(T value);

    /**
     * Specify the value that was restricted for using but it can be used again. 
     * @param value
     */
    @Editor.Ignore
    void onValueUnRestricted(T value);
}
