package org.dashbuilder.dataset.client.editor;

import com.google.gwt.editor.client.Editor;
import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.dataset.def.DataSetDef;

/**
 * <p>The GWT editor contract for beans of type <code>org.dashbuilder.dataset.def.DataSetDef</code>.</p>
 * <p>Used to to edit the following sub-set of attributes:</p>
 * <ul>
 *     <li>uuid</li>
 *     <li>name</li>
 * </ul>
 * 
 * @since 0.4.0
 */
public interface DataSetDefBasicAttributesEditor extends Editor<DataSetDef> {

    LeafAttributeEditor<String> UUID();
    LeafAttributeEditor<String> name();
    
}
