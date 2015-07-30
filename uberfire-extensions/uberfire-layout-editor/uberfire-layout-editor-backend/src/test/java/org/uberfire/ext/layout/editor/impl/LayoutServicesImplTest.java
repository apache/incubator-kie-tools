package org.uberfire.ext.layout.editor.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.impl.old.perspective.editor.PerspectiveEditor;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class LayoutServicesImplTest {

    private LayoutServicesImpl layoutServices;

    @Before
    public void setup() {
        layoutServices = new LayoutServicesImpl();
        layoutServices.init();
    }

    @Test
    public void layoutMarshallerDefaultLayout() {
        LayoutTemplate layoutTemplate = LayoutTemplate.defaultLayout("teste");
        String stringLayout = layoutServices.convertLayoutToString(layoutTemplate);
        LayoutTemplate extracted = layoutServices.convertLayoutFromString(stringLayout);
        assertEquals(layoutTemplate, extracted);
    }

    @Test
    public void layoutMarshaller12withHTMLComponent() {
        String expected = loadSample("12withHTMLComponent.txt");
        LayoutTemplate template = layoutServices.convertLayoutFromString(expected);
        String actual = layoutServices.convertLayoutToString(template);
        assertEquals(expected, actual);
    }

    @Test
    public void layoutMarshallerBigLayout() {
        String expected = loadSample("BigLayout.txt");
        LayoutTemplate template = layoutServices.convertLayoutFromString(expected);
        String actual = layoutServices.convertLayoutToString(template);
        assertEquals(expected, actual);
    }

    @Test
    public void layoutMarshallerSubColumns() {
        String expected = loadSample("SubColumnsLayout.txt");
        LayoutTemplate template = layoutServices.convertLayoutFromString(expected);
        String actual = layoutServices.convertLayoutToString(template);
        assertEquals(expected, actual);
    }


    private static String loadSample( String file ) {
        try {
            return IOUtils.toString(new LayoutServicesImplTest().getClass().getResourceAsStream(file),
                    "UTF-8");
        } catch ( IOException e ) {
            return "";
        }
    }

}