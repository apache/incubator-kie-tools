package org.dashbuilder.dataset.client.editor;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.ValueAwareEditor;
import org.dashbuilder.common.client.editor.HasConstrainedValue;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;

import java.util.List;

/**
 * <p>The GWT editor contract for beans of type <code>org.dashbuilder.dataset.def.DataSetDef</code>.</p>
 * <p>Used to to edit the following sub-set of attributes:</p>
 * <ul>
 *     <li>data set columns</li>
 *     <li>data set filter</li>
 * </ul>
 * 
 * @since 0.4.0
 */
public interface DataSetDefColumnsFilterEditor extends ValueAwareEditor<DataSetDef>, HasConstrainedValue<List<DataColumnDef>> {

    @Editor.Path(".")
    DataSetDefColumnsEditor columnListEditor();

    DataSetDefFilterEditor dataSetFilter();
    
}
