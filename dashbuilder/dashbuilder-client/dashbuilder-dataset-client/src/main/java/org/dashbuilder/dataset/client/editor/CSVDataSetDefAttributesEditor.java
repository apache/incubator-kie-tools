package org.dashbuilder.dataset.client.editor;

import com.google.gwt.editor.client.ValueAwareEditor;
import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.dataset.def.CSVDataSetDef;

/**
 * <p>The GWT editor contract for the specific attributes of type <code>org.dashbuilder.dataset.def.CSVDataSetDef</code>.</p>
 * <p>Used to to edit the following sub-set of attributes:</p>
 * <ul>
 *     <li>fileURL</li>
 *     <li>filePath</li>
 *     <li>separatorChar</li>
 *     <li>quoteChar</li>
 *     <li>escapeChar</li>
 *     <li>datePattern</li>
 *     <li>numberPattern</li>
 * </ul>
 * <p>It is a ValueAwareEditor as must nullify filePath or fileURL editors at runtime depending on the instance value.</p>
 * 
 * @since 0.4.0
 */
public interface CSVDataSetDefAttributesEditor extends ValueAwareEditor<CSVDataSetDef> {

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
