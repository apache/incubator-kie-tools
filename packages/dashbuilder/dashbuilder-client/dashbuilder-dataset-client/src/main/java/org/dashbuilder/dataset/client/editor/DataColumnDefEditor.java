package org.dashbuilder.dataset.client.editor;

import com.google.gwt.editor.client.Editor;
import org.dashbuilder.common.client.editor.HasEditMode;
import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.def.DataColumnDef;

/**
 * <p>The GWT editor contract for beans of type <code>org.dashbuilder.dataset.def.DataColumnDef</code>.</p>
 *
 * @since 0.4.0
 */
public interface DataColumnDefEditor extends Editor<DataColumnDef>, HasEditMode {


    /**
     * Columns edition constrains are different depending on the data set's provider type.
     *
     * @param type The data set's provider type.
     */
    @Editor.Ignore
    void setProviderType(DataSetProviderType type);

    /**
     * Column editor requires the original column type in oder to allow changing column types to the types given by the original data set column type.   
     * @param columnType The original data set column type
     */
    @Editor.Ignore
    void setOriginalColumnType(ColumnType columnType);

    /**
     * <p>Called when editor is detached from the editors chain. Remove view from parent element here.</p>
     */
    @Editor.Ignore
    void removeFromParent();
    
    LeafAttributeEditor<String> id();

    @Editor.Path(".")
    ColumnTypeEditor columnType();
    
}
