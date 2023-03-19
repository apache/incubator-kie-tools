package org.dashbuilder.dataset.client.editor;

import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.dataset.def.SQLDataSetDef;

/**
 * <p>The GWT editor contract for beans of type <code>org.dashbuilder.dataset.def.SQLDataSetDef</code>.</p>
 *
 * TODO 
 * The attributes dataSource, dbSchema, dbTable and dbSQL should be not directly referenced here, here should be referenced just the org.dashbuilder.dataset.client.editor.SQLDataSetDefAttributesEditor.
 * But gwt editor inheritance is not working in this situation, due to https://github.com/gwtproject/gwt/issues/6340
 * Please refactor it when bug from gwt is fixed.
 * 
 * @since 0.4.0
 */
public interface SQLDataSetDefEditor extends DataSetDefEditor<SQLDataSetDef> {

    LeafAttributeEditor<String> dataSource();
    LeafAttributeEditor<String> dbSchema();
    LeafAttributeEditor<String> dbTable();
    LeafAttributeEditor<String> dbSQL();

    @Ignore
    boolean isUsingQuery();
    
}
