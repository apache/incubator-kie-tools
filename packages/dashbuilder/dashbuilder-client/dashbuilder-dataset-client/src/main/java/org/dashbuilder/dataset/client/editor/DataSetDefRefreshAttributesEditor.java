package org.dashbuilder.dataset.client.editor;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.ValueAwareEditor;
import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.dataset.def.DataSetDef;

/**
 * <p>The GWT editor contract for beans of type <code>org.dashbuilder.dataset.def.DataSetDef</code>.</p>
 * <p>Used to to edit the following sub-set of attributes:</p>
 * <ul>
 *     <li>refreshTime</li>
 *     <li>refreshAlways</li>
 * </ul>
 * 
 * @since 0.4.0
 */
public interface DataSetDefRefreshAttributesEditor extends ValueAwareEditor<DataSetDef> {

    LeafAttributeEditor<Boolean> refreshAlways();
    DataSetDefRefreshIntervalEditor refreshTime();

    @Editor.Ignore
    boolean isRefreshEnabled();
}
