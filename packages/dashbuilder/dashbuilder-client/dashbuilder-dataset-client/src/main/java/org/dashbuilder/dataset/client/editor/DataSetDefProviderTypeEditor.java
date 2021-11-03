package org.dashbuilder.dataset.client.editor;

import com.google.gwt.editor.client.HasEditorErrors;
import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.def.DataSetDef;

/**
 * <p>The GWT editor contract for beans of type <code>org.dashbuilder.dataset.def.DataSetDef</code>.</p>
 * <p>Used to to edit the following sub-set of attributes:</p>
 * <ul>
 *     <li>provider type</li>
 * </ul>
 * 
 * @since 0.4.0
 */
public interface DataSetDefProviderTypeEditor extends HasEditorErrors<DataSetDef> {

    LeafAttributeEditor<DataSetProviderType> provider();
    
}
