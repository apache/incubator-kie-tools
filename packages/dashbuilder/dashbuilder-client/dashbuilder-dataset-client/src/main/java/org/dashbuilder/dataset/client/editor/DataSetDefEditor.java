package org.dashbuilder.dataset.client.editor;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.ValueAwareEditor;
import org.dashbuilder.common.client.editor.HasConstrainedValue;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;

import java.util.List;

/**
 * <p>The GWT editor contract for beans of type <code>org.dashbuilder.dataset.def.DataSetDef</code>.</p>
 * <p>It delegates to sub-editors the different attributes edition:</p>
 *
 * @since 0.4.0
 */
public interface DataSetDefEditor<T extends DataSetDef> extends ValueAwareEditor<T>, HasConstrainedValue<List<DataColumnDef>> {

    @Editor.Path(".")
    DataSetDefBasicAttributesEditor basicAttributesEditor();

    @Editor.Path(".")
    DataSetDefColumnsFilterEditor columnsAndFilterEditor();
    
    @Editor.Path(".")
    DataSetDefBackendCacheAttributesEditor backendCacheEditor();

    @Editor.Path(".")
    DataSetDefClientCacheAttributesEditor clientCacheEditor();

    @Editor.Path(".")
    DataSetDefRefreshAttributesEditor refreshEditor();
}
