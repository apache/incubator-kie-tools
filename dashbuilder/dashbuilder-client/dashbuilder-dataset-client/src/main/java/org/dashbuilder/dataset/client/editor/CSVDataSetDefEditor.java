package org.dashbuilder.dataset.client.editor;

import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.dataset.def.CSVDataSetDef;

/**
 * <p>The GWT editor contract for beans of type <code>org.dashbuilder.dataset.def.CSVDataSetDef</code>.</p>
 *
 * TODO 
 * All csv specific attributes should be not directly referenced here, here should be referenced just the org.dashbuilder.dataset.client.editor.BeanDataSetDefAttributesEditor.
 * But gwt editor inheritance is not working in this situation, due to https://github.com/gwtproject/gwt/issues/6340
 * Please refactor it when bug from gwt is fixed.
 * 
 * @since 0.4.0
 */
public interface CSVDataSetDefEditor extends DataSetDefEditor<CSVDataSetDef> {

    LeafAttributeEditor<String> fileURL();

    LeafAttributeEditor<String> filePath();

    LeafAttributeEditor<Character> separatorChar();

    LeafAttributeEditor<Character> quoteChar();

    LeafAttributeEditor<Character> escapeChar();

    LeafAttributeEditor<String> datePattern();

    LeafAttributeEditor<String> numberPattern();

    @Ignore
    boolean isUsingFilePath();
}
