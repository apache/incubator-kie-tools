/*
 * Copyright 2017 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.plugin.client.perspective.editor.layout.editor;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static org.junit.Assert.*;

public class TargetDivListTest {

    public static final String SAMPLE_LAYOUT = "org/uberfire/ext/plugin/client/perspective/editor/layout/editor/fullLayoutPage.txt";

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void getAllTargetDivsTest() throws Exception {

        LayoutTemplate layout = loadLayout(SAMPLE_LAYOUT);

        List<String> divs = TargetDivList.list(layout);

        assertEquals(3,
                     divs.size());
        assertTrue(divs.contains("dora_div_id"));
        assertTrue(divs.contains("bento_div_id"));
        assertTrue(divs.contains("another_div_id"));
    }

    public LayoutTemplate loadLayout(String templateURL) throws Exception {
        URL resource = getClass().getClassLoader()
                .getResource(templateURL);
        String layoutEditorModel = new String(Files.readAllBytes(Paths.get(resource.toURI())));

        LayoutTemplate layoutTemplate = gson.fromJson(layoutEditorModel,
                                                      LayoutTemplate.class);

        return layoutTemplate;
    }
}