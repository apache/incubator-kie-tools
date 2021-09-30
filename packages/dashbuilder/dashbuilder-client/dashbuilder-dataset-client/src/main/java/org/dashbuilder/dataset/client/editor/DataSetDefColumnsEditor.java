package org.dashbuilder.dataset.client.editor;

import com.google.gwt.editor.client.ValueAwareEditor;
import org.dashbuilder.common.client.editor.HasConstrainedValue;
import org.dashbuilder.common.client.editor.HasRestrictedValue;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;

import java.util.List;

/**
 * <p>The GWT editor contract for the data set definition columns.</p>
 * <p>It takes care about the allColumnsEnabled flag.</p>
 *
 * @since 0.4.0
 */
public interface DataSetDefColumnsEditor extends ValueAwareEditor<DataSetDef>,
        HasConstrainedValue<List<DataColumnDef>>, HasRestrictedValue<String> {

    ColumnListEditor columns();

}
