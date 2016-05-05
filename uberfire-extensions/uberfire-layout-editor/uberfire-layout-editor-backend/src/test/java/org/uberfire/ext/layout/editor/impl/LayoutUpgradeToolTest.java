/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.impl.old.perspective.editor.PerspectiveEditor;
import org.uberfire.ext.plugin.type.TagsConverterUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LayoutUpgradeToolTest {


    @Test
    public void testVersion1() throws Exception {
        String model = loadSample("12withHTMLComponent.txt");
        assertTrue(LayoutUpgradeTool.isVersion1(model));
    }

    @Test
    public void convertEmptyPerspectiveEditor() {
        String model = loadSample("DeprecatedEmptyPerspectiveEditor.txt");
        assertEquals(LayoutTemplate.defaultLayout("Empty"), LayoutUpgradeTool.convert(model));
    }

    @Test
    public void convertEmptyMoreColumnsPerspectiveEditor() {
        String model = loadSample("DeprecatedMoreColumnsPerspectiveEditor.txt");
        LayoutTemplate expected = generateEmptyMoreColumnsPerspectiveEditor();
        assertEquals(expected, LayoutUpgradeTool.convert(model));
    }

    @Test
    public void convertComplexPerspectiveEditor() {
        String model = loadSample("DeprecatedComplexPerspectiveEditor.txt");
        LayoutTemplate expected = generateComplexPerspectiveEditor();
        LayoutTemplate actual = LayoutUpgradeTool.convert(model);
        assertEquals(expected, actual);
    }


    private static String loadSample(String file) {
        try {
            return IOUtils.toString(new LayoutServicesImplTest().getClass().getResourceAsStream(file),
                    "UTF-8");
        } catch (IOException e) {
            return "";
        }
    }


    private LayoutTemplate generateComplexPerspectiveEditor() {
        Map<String, String> property = new HashMap<String, String>();
        String tags = "tg1|tg2|tg3|";
        property.put(TagsConverterUtil.LAYOUT_PROPERTY, tags);

        LayoutTemplate template = new LayoutTemplate("Complex", property);
        final LayoutRow layoutRow444 = new LayoutRow( new ArrayList<String>() {{
            add( "4" );
            add( "4" );
            add( "4" );
        }} );

        LayoutColumn column1 = new LayoutColumn("4");

        LayoutComponent layoutComponent = new LayoutComponent(LayoutUpgradeTool.HTML_DRAG_TYPE);
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("HTML_CODE", "\u003ch1\u003eeder\u003c/h1\u003e");
        layoutComponent.addProperties(properties);

        column1.addLayoutComponent(layoutComponent);

        layoutRow444.add(column1);


        LayoutColumn column2 = new LayoutColumn("4");

        LayoutComponent layoutComponent2 = new LayoutComponent(LayoutUpgradeTool.SCREEN_DRAG_TYPE);
        Map<String, String> properties2 = new HashMap<String, String>();
        properties2.put("Place Name", "screen1");
        layoutComponent2.addProperties(properties2);

        column2.addLayoutComponent(layoutComponent2);

        layoutRow444.add(column2);

        LayoutColumn column3 = new LayoutColumn("4");

        LayoutComponent layoutComponent3 = new LayoutComponent(LayoutUpgradeTool.DASHBUILDER_DRAG_TYPE);
        Map<String, String> properties3 = new HashMap<String, String>();
        properties3.put("Place Name", "screen2");
        layoutComponent3.addProperties(properties3);

        column3.addLayoutComponent(layoutComponent3);

        layoutRow444.add(column3);


        template.addRow(layoutRow444);

        return template;
    }

    private LayoutTemplate generateEmptyMoreColumnsPerspectiveEditor() {
        LayoutTemplate template = new LayoutTemplate("23");
        final LayoutRow layoutRow12 = new LayoutRow( new ArrayList<String>() {{
            add( "12" );
        }} );
        layoutRow12.add(new LayoutColumn("12"));
        template.addRow(layoutRow12);

        final LayoutRow layoutRow6 = new LayoutRow( new ArrayList<String>() {{
            add( "6" );
            add( "6" );
        }} );
        layoutRow6.add(new LayoutColumn("6"));
        layoutRow6.add(new LayoutColumn("6"));
        template.addRow(layoutRow6);

        final LayoutRow layoutRow4 = new LayoutRow( new ArrayList<String>() {{
            add( "4" );
            add( "4" );
            add( "4" );
        }} );
        layoutRow4.add(new LayoutColumn("4"));
        layoutRow4.add(new LayoutColumn("4"));
        layoutRow4.add(new LayoutColumn("4"));
        template.addRow(layoutRow4);
        return template;
    }
}