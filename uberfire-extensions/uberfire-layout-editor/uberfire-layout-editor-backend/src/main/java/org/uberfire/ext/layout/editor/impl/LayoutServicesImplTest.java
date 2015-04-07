package org.uberfire.ext.layout.editor.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.layout.editor.api.editor.ColumnEditor;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutEditor;
import org.uberfire.ext.layout.editor.api.editor.RowEditor;

import static org.junit.Assert.*;

public class LayoutServicesImplTest {

    private LayoutServicesImpl layoutServices;

    @Before
    public void setup() {
        layoutServices = new LayoutServicesImpl();
        layoutServices.init();
    }

    @Test
    public void layoutMarshaller() {
        LayoutEditor layoutEditor = createLayoutEditor();
        String stringLayout = layoutServices.convertLayoutToString( layoutEditor );
        LayoutEditor extracted = layoutServices.convertLayoutFromString( stringLayout );
        assertEquals( layoutEditor, extracted );
    }

    private LayoutEditor createLayoutEditor() {
        LayoutEditor layoutEditor = new LayoutEditor();
        List<String> rowSpam = new ArrayList<String>();
        rowSpam.add( "12" );

        ColumnEditor columnEditor = new ColumnEditor( "12" );
        columnEditor.addLayoutComponent( new LayoutComponent( "CLASS" ) );

        RowEditor rowEditor = new RowEditor( rowSpam );
        rowEditor.add( columnEditor );

        layoutEditor.addRow( rowEditor );

        return layoutEditor;
    }

}