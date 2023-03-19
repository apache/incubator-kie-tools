package org.dashbuilder.dataset.client.editor;

import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.dataset.def.ExternalDataSetDef;

/**
 * <p>The GWT editor contract for beans of type <code>org.dashbuilder.dataset.def.ExternalDataSetDef</code>.</p>
 *
 * TODO 
 * All external specific attributes should be not directly referenced here, here should be referenced just the org.dashbuilder.dataset.client.editor.ExternalDataSetDefAttributesEditor.
 * But gwt editor inheritance is not working in this situation, due to https://github.com/gwtproject/gwt/issues/6340
 * Please refactor it when bug from gwt is fixed.
 * 
 * @since 0.4.0
 */
public interface ExternalDataSetDefEditor extends DataSetDefEditor<ExternalDataSetDef> {

    LeafAttributeEditor<String> url();

   
}
