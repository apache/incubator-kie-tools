package org.uberfire.ext.layout.editor.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;

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
        LayoutTemplate layoutTemplate = createLayoutEditor();
        String stringLayout = layoutServices.convertLayoutToString(layoutTemplate);
        LayoutTemplate extracted = layoutServices.convertLayoutFromString( stringLayout );
        assertEquals(layoutTemplate, extracted );
    }

    private LayoutTemplate createLayoutEditor() {
        LayoutTemplate layoutTemplate = new LayoutTemplate();
        List<String> rowSpam = new ArrayList<String>();
        rowSpam.add( "12" );

        LayoutColumn layoutColumn = new LayoutColumn( "12" );
        layoutColumn.addLayoutComponent( new LayoutComponent( "CLASS" ) );

        LayoutRow layoutRow = new LayoutRow( rowSpam );
        layoutRow.add(layoutColumn);

        layoutTemplate.addRow(layoutRow);

        return layoutTemplate;
    }

}