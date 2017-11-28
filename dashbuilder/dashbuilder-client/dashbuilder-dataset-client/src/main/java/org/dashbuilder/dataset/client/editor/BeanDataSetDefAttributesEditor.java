package org.dashbuilder.dataset.client.editor;

import com.google.gwt.editor.client.Editor;
import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.dataset.def.BeanDataSetDef;

import java.util.Map;

/**
 * <p>The GWT editor contract for the specific attributes of type <code>org.dashbuilder.dataset.def.BeanDataSetDef</code>.</p>
 * <p>Used to to edit the following sub-set of attributes:</p>
 * <ul>
 *     <li>generatorClass</li>
 *     <li>paramaterMap</li>
 * </ul>
 * 
 * @since 0.4.0
 */
public interface BeanDataSetDefAttributesEditor extends Editor<BeanDataSetDef> {

    LeafAttributeEditor<String> generatorClass();

    LeafAttributeEditor<Map<String, String>> paramaterMap();
    
}
