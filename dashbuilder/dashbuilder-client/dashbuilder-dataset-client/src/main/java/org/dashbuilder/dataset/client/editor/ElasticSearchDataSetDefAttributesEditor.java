package org.dashbuilder.dataset.client.editor;

import com.google.gwt.editor.client.Editor;
import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.dataset.def.ElasticSearchDataSetDef;

/**
 * <p>The GWT editor contract for the specific attributes of type <code>org.dashbuilder.dataset.def.ElasticSearchDataSetDef</code>.</p>
 * <p>Used to to edit the following sub-set of attributes:</p>
 * <ul>
 *     <li>serverURL</li>
 *     <li>clusterName</li>
 *     <li>index</li>
 *     <li>type</li>
 * </ul>
 * 
 * @since 0.4.0
 */
public interface ElasticSearchDataSetDefAttributesEditor extends Editor<ElasticSearchDataSetDef> {

    LeafAttributeEditor<String> serverURL();

    LeafAttributeEditor<String> clusterName();

    LeafAttributeEditor<String> index();

    LeafAttributeEditor<String> type();
    
}
