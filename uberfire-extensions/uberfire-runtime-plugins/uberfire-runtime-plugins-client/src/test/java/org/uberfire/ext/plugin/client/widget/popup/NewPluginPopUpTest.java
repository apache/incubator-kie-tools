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

package org.uberfire.ext.plugin.client.widget.popup;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.plugin.client.info.PluginsInfo;
import org.uberfire.ext.plugin.model.Activity;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NewPluginPopUpTest {

    private NewPluginPopUp presenter;
    private NewPluginPopUpView view;

    @Before
    public void setup() {
        view = mock( NewPluginPopUpView.class );
        presenter = createNewPluginPopUp();
    }

    @Test
    public void validateEmptyName() {
        assertFalse( presenter.validName( "", null ) );
        assertTrue( presenter.validName( "filled", null ) );
    }

    @Test
    public void validateInvalidName() {
        assertFalse( presenter.validName( "invalid*", null ) );
        assertTrue( presenter.validName( "valid", null ) );
    }

    @Test
    public void validateDuplicatedName() {
        assertFalse( presenter.validName( "existingPerspectiveLayout", null ) );
        assertFalse( presenter.validName( "existingScreen", null ) );
        assertFalse( presenter.validName( "existingEditor", null ) );
        assertFalse( presenter.validName( "existingSplashScreen", null ) );
        assertFalse( presenter.validName( "existingDynamicMenu", null ) );

        assertTrue( presenter.validName( "nonExistingPerspectiveLayout", null ) );
        assertTrue( presenter.validName( "nonExistingScreen", null ) );
        assertTrue( presenter.validName( "nonExistingEditor", null ) );
        assertTrue( presenter.validName( "nonExistingSplashScreen", null ) );
        assertTrue( presenter.validName( "nonExistingDynamicMenu", null ) );
    }

    private NewPluginPopUp createNewPluginPopUp() {
        return new NewPluginPopUp( view ) {

            @Override
            protected PluginsInfo getPluginsInfo() {

                return new PluginsInfo() {

                    @Override
                    public Set<Activity> getAllPlugins( final Collection<Plugin> plugins ) {
                        Set<Activity> activities = new HashSet<Activity>();
                        activities.add( new Plugin( "existingPerspectiveLayout", PluginType.PERSPECTIVE_LAYOUT, PathFactory.newPath( "test1", "/tmp/test1" ) ) );
                        activities.add( new Plugin( "existingScreen", PluginType.SCREEN, PathFactory.newPath( "test2", "/tmp/test2" ) ) );
                        activities.add( new Plugin( "existingEditor", PluginType.EDITOR, PathFactory.newPath( "test3", "/tmp/test3" ) ) );
                        activities.add( new Plugin( "existingSplashScreen", PluginType.SPLASH, PathFactory.newPath( "test4", "/tmp/test4" ) ) );
                        activities.add( new Plugin( "existingDynamicMenu", PluginType.DYNAMIC_MENU, PathFactory.newPath( "test5", "/tmp/test5" ) ) );

                        return activities;
                    }
                };
            }
        };
    }
}
