/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.client.mvp.ActivityBeansInfo;
import org.uberfire.ext.plugin.event.NewPluginRegistered;
import org.uberfire.ext.plugin.event.PluginUnregistered;
import org.uberfire.ext.plugin.model.PluginType;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Mockito.*;

public class ScreenLayoutDragComponentTest {

    private ScreenLayoutDragComponent screenLayoutDragComponent;

    private ActivityBeansInfo activityBeansInfo;

    @Before
    public void setup() {
        screenLayoutDragComponent = spy(new ScreenLayoutDragComponent());

        activityBeansInfo = spy(new ActivityBeansInfo());

        List<String> availableWorkbenchScreensIds = new ArrayList<String>();
        availableWorkbenchScreensIds.add("screen1");
        availableWorkbenchScreensIds.add("screen2");
        availableWorkbenchScreensIds.add("screen3");

        doReturn(availableWorkbenchScreensIds).when(activityBeansInfo).getAvailableWorkbenchScreensIds();
        doReturn(activityBeansInfo).when(screenLayoutDragComponent).getActivityBeansInfo();

        screenLayoutDragComponent.setup();
    }

    @Test
    public void newNotScreenPluginRegisteredTest() {
        screenLayoutDragComponent.onNewPluginRegistered(new NewPluginRegistered("newPlugin",
                                                                                PluginType.EDITOR));
        assertEquals(3,
                     screenLayoutDragComponent.getAvailableWorkbenchScreensIds().size());
    }

    @Test
    public void existingScreenRegisteredTest() {
        screenLayoutDragComponent.onNewPluginRegistered(new NewPluginRegistered("screen1",
                                                                                PluginType.SCREEN));
        assertEquals(3,
                     screenLayoutDragComponent.getAvailableWorkbenchScreensIds().size());
    }

    @Test
    public void newScreenRegisteredTest() {
        screenLayoutDragComponent.onNewPluginRegistered(new NewPluginRegistered("newScreen",
                                                                                PluginType.SCREEN));
        assertEquals(4,
                     screenLayoutDragComponent.getAvailableWorkbenchScreensIds().size());
    }

    @Test
    public void notScreenPluginUnregisteredTest() {
        screenLayoutDragComponent.onPluginUnregistered(new PluginUnregistered("screen1",
                                                                              PluginType.EDITOR));
        assertEquals(3,
                     screenLayoutDragComponent.getAvailableWorkbenchScreensIds().size());
    }

    @Test
    public void existingScreenUnregisteredTest() {
        screenLayoutDragComponent.onPluginUnregistered(new PluginUnregistered("screen1",
                                                                              PluginType.SCREEN));
        assertEquals(2,
                     screenLayoutDragComponent.getAvailableWorkbenchScreensIds().size());
    }

    @Test
    public void unexistingScreenUnregisteredTest() {
        screenLayoutDragComponent.onPluginUnregistered(new PluginUnregistered("unexistingPlugin",
                                                                              PluginType.SCREEN));
        assertEquals(3,
                     screenLayoutDragComponent.getAvailableWorkbenchScreensIds().size());
    }
}
