package org.dashbuilder.dataset.client.editor;

import com.google.gwt.editor.client.ValueAwareEditor;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.filter.DataSetFilter;

/**
 * <p>The GWT editor contract for beans of type <code>org.dashbuilder.dataset.filter.DataSetFilter</code>.</p>
 *
 * @since 0.4.0
 */
public interface DataSetDefFilterEditor extends ValueAwareEditor<DataSetFilter> {
    
    void init(DataSetMetadata metadata);
    
}
