package org.dashbuilder.dataset.client.editor;

import com.google.gwt.editor.client.ValueAwareEditor;
import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.dataset.def.DataSetDef;

/**
 * <p>The GWT editor contract for beans of type <code>org.dashbuilder.dataset.def.DataSetDef</code>.</p>
 * <p>Used to to edit the following sub-set of attributes:</p>
 * <ul>
 *     <li>pushEnabled</li>
 *     <li>pushMaxSize</li>
 * </ul>
 * 
 * @since 0.4.0
 */
public interface DataSetDefClientCacheAttributesEditor extends ValueAwareEditor<DataSetDef> {

    LeafAttributeEditor<Boolean> pushEnabled();
    LeafAttributeEditor<Integer> pushMaxSize();
    
}
