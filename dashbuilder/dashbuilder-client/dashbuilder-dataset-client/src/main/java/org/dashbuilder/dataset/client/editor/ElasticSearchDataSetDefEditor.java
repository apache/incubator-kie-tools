package org.dashbuilder.dataset.client.editor;

import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.dataset.def.ElasticSearchDataSetDef;

/**
 * <p>The GWT editor contract for beans of type <code>org.dashbuilder.dataset.def.ElasticSearchDataSetDef</code>.</p>
 *
 * TODO 
 * The attributes serverURL, clusterName, index and type should be not directly referenced here, here should be referenced just the org.dashbuilder.dataset.client.editor.BeanDataSetDefAttributesEditor.
 * But gwt editor inheritance is not working in this situation, due to https://github.com/gwtproject/gwt/issues/6340
 * Please refactor it when bug from gwt is fixed.
 * 
 * @since 0.4.0
 */
public interface ElasticSearchDataSetDefEditor extends DataSetDefEditor<ElasticSearchDataSetDef> {

    LeafAttributeEditor<String> serverURL();

    LeafAttributeEditor<String> clusterName();

    LeafAttributeEditor<String> index();

    LeafAttributeEditor<String> type();
    
}
