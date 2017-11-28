package org.dashbuilder.dataset.client.editor;

import com.google.gwt.editor.client.CompositeEditor;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.HasEditorErrors;
import org.dashbuilder.common.client.editor.HasConstrainedValue;
import org.dashbuilder.common.client.editor.HasEditMode;
import org.dashbuilder.common.client.editor.HasRestrictedValue;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.def.DataColumnDef;

import java.util.List;

/**
 * <p>The GWT editor contract for a collection of the data set definition columns, which type is <code>org.dashbuilder.dataset.def.DataColumnDef</code>.</p>
 *
 * @since 0.4.0
 */
public interface ColumnListEditor extends CompositeEditor<List<DataColumnDef>, DataColumnDef, DataColumnDefEditor>, 
        HasEditorErrors<List<DataColumnDef>>, HasConstrainedValue<List<DataColumnDef>>,
        HasRestrictedValue<String> {

    /**
     * Columns edition constrains are different depending on the data set's provider type.
     * 
     * @param type The data set's provider type.
     */
    @Editor.Ignore
    void setProviderType(DataSetProviderType type);
    
}
