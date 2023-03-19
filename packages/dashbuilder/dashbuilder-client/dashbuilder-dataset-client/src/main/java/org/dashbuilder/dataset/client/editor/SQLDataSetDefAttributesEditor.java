package org.dashbuilder.dataset.client.editor;

import com.google.gwt.editor.client.ValueAwareEditor;
import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.dataset.def.SQLDataSetDef;

/**
 * <p>The GWT editor contract for the specific attributes of type <code>org.dashbuilder.dataset.def.SQLDataSetDef</code>.</p>
 * <p>Used to to edit the following sub-set of attributes:</p>
 * <ul>
 *     <li>dataSource</li>
 *     <li>dbSchema</li>
 *     <li>dbTable</li>
 *     <li>dbSQL</li>
 * </ul>
 * <p>It is a ValueAwareEditor as must nullify dbTable or dbSQL editors at runtime depending on instance value.</p>
 * @since 0.4.0
 */
public interface SQLDataSetDefAttributesEditor extends ValueAwareEditor<SQLDataSetDef> {

    LeafAttributeEditor<String> dataSource();
    LeafAttributeEditor<String> dbSchema();
    LeafAttributeEditor<String> dbTable();
    LeafAttributeEditor<String> dbSQL();
    
    @Ignore
    boolean isUsingQuery();
    
}
