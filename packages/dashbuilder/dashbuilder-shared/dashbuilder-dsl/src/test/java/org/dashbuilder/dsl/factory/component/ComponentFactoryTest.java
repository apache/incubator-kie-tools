/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dsl.factory.component;

import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.dsl.model.Component;
import org.junit.Test;

import static org.dashbuilder.dsl.factory.component.HtmlComponentBuilder.HTML_CODE_PROP;
import static org.dashbuilder.dsl.factory.component.LogoBuilder.LOGO_ID;
import static org.dashbuilder.dsl.factory.component.LogoBuilder.LOGO_URL_PROP;
import static org.dashbuilder.dsl.factory.component.ProcessHeatmapBuilder.COMPONENT_ID;
import static org.dashbuilder.dsl.factory.component.ProcessHeatmapBuilder.CONTAINER_ID_PARAM;
import static org.dashbuilder.dsl.factory.component.ProcessHeatmapBuilder.PROCESS_ID_PARAM;
import static org.dashbuilder.dsl.factory.component.ProcessHeatmapBuilder.SERVER_TEMPLATE_PARAM;
import static org.dashbuilder.external.model.ExternalComponent.COMPONENT_ID_KEY;
import static org.junit.Assert.assertEquals;

public class ComponentFactoryTest {

    @Test
    public void testExternal() {
        Component external = ComponentFactory.external("test");
        assertEquals("test", external.getLayoutComponent().getProperties().get(COMPONENT_ID_KEY));
    }

    @Test
    public void testHtml() {
        Component html = ComponentFactory.html("test");
        assertEquals("test", html.getLayoutComponent().getProperties().get(HTML_CODE_PROP));
    }

    @Test
    public void testLogo() {
        Component logo = ComponentFactory.logo("src");
        assertEquals(LOGO_ID, logo.getLayoutComponent().getProperties().get(COMPONENT_ID_KEY));
        assertEquals("src", logo.getLayoutComponent().getProperties().get(LOGO_ID + "." + LOGO_URL_PROP));
    }

    @Test
    public void testHeatmap() {
        DisplayerSettings settings = DisplayerSettingsFactory.newExternalDisplayerSettings().buildSettings();
        Component heatmap = ComponentFactory.processHeatmap("server", "container", "process", settings);
        assertEquals(COMPONENT_ID, heatmap.getLayoutComponent().getProperties().get(COMPONENT_ID_KEY));
        assertEquals("server", settings.getComponentProperty(SERVER_TEMPLATE_PARAM));
        assertEquals("container", settings.getComponentProperty(CONTAINER_ID_PARAM));
        assertEquals("process", settings.getComponentProperty(PROCESS_ID_PARAM));
    }
    
    
    @Test
    public void testAllProcessesHeatmap() {
        DisplayerSettings settings = DisplayerSettingsFactory.newExternalDisplayerSettings().buildSettings();
        Component allProcessHeatmap = ComponentFactory.allProcessesHeatmap("server", settings);
        assertEquals(AllProcessesHeatmapBuilder.COMPONENT_ID, allProcessHeatmap.getLayoutComponent().getProperties().get(COMPONENT_ID_KEY));
        assertEquals("server", settings.getComponentProperty(AllProcessesHeatmapBuilder.SERVER_TEMPLATE_PARAM));
    }
    
}
