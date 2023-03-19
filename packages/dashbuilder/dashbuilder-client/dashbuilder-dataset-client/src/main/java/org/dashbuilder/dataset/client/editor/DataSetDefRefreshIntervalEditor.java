package org.dashbuilder.dataset.client.editor;

import com.google.gwt.editor.client.LeafValueEditor;

/**
 * <p>The GWT editor contract for the refreshInterval attribute for a data set definition.</p>
 * 
 * @since 0.4.0
 */
public interface DataSetDefRefreshIntervalEditor extends LeafValueEditor<String> {

    void setEnabled(boolean isEnabled);
    
}
