package org.dashbuilder.dataset.client.editor;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.ValueAwareEditor;
import org.dashbuilder.common.client.editor.HasEditMode;
import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.def.DataColumnDef;

/**
 * <p>The GWT editor contract for beans of type <code>org.dashbuilder.dataset.ColumnType</code> of a given data column definition.</p>
 *
 * <p>It's a value aware editor as the available column types to change depend on the current column type value.</p>
 * @since 0.4.0
 */
public interface ColumnTypeEditor extends ValueAwareEditor<DataColumnDef>, HasEditMode {

    /**
     * Column editor requires the original column type in oder to allow changing column types to the types given by the original data set column type.   
     * @param columnType The original data set column type
     */
    @Editor.Ignore
    void setOriginalColumnType(ColumnType columnType);
    
    LeafAttributeEditor<ColumnType> columnType();
}