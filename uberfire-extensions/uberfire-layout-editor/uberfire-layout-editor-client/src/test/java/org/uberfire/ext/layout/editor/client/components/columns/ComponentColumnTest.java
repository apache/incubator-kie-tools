package org.uberfire.ext.layout.editor.client.components.columns;

import org.junit.Test;
import org.uberfire.ext.layout.editor.client.AbstractLayoutEditorTest;


public class ComponentColumnTest extends AbstractLayoutEditorTest {

    @Test
    public void assertThereIsNoGWTDepInComponentColumn() throws Exception {
        loadLayout( SINGLE_ROW_TWO_COMPONENTS_LAYOUT );
    }

}