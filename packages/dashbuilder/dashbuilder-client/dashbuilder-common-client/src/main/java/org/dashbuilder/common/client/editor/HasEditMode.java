package org.dashbuilder.common.client.editor;

import com.google.gwt.editor.client.Editor;

/**
 * <p>Indicates that an editor can be used as read-only or as edit mode.</p>
 * 
 * @since 0.4.0
 */
public interface HasEditMode {

    /**
     * <p>Enables or disables the edition.</p>
     */
    @Editor.Ignore
    void isEditMode(boolean isEdit);
    
}
