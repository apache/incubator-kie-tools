/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.home.client.widgets.home;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.uberfire.mocks.ParametrizedCommandMock.executeParametrizedCommandWith;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.home.client.widgets.shortcut.ShortcutPresenter;
import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.screens.home.model.HomeModelProvider;
import org.kie.workbench.common.screens.home.model.ModelUtils;
import org.kie.workbench.common.profile.api.preferences.ProfilePreferences;
import org.kie.workbench.common.profile.api.preferences.Profile;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceType;

@RunWith(MockitoJUnitRunner.class)
public class HomePresenterTest {

    @Mock
    private HomePresenter.View view;

    @Mock
    private TranslationService translationService;

    @Mock
    private ManagedInstance<ShortcutPresenter> shortcutPresenters;

    private HomeModelProvider modelProvider;

    private HomePresenter presenter;
    
    @Mock
    ProfilePreferences profilePreferences;

    @Before
    public void setup() {
        doReturn(mock(ShortcutPresenter.class)).when(shortcutPresenters).get();

        executeParametrizedCommandWith(0, new ProfilePreferences(Profile.FULL))
                .when(profilePreferences).load(any(ParameterizedCommand.class), 
                                                any(ParameterizedCommand.class)); 
        
        modelProvider = ProfilePreferences -> {
            final HomeModel homeModel = new HomeModel("welcome",
                                                      "description",
                                                      "backgroundImageUrl");
            homeModel.addShortcut(ModelUtils.makeShortcut("iconCss1",
                                                          "heading1",
                                                          "subHeading1",
                                                          mock(Command.class)));
            homeModel.addShortcut(ModelUtils.makeShortcut("iconCss2",
                                                          "heading2",
                                                          "subHeading2",
                                                          mock(Command.class),
                                                          "resourceId2",
                                                          ResourceType.UNKNOWN,
                                                          ResourceAction.READ));
            homeModel.addShortcut(ModelUtils.makeShortcut("iconCss3",
                                                          "heading3",
                                                          "subHeading3",
                                                          mock(Command.class),
                                                          "perspectiveId"));

            return homeModel;
        };

        presenter = new HomePresenter(view,
                                      translationService,
                                      modelProvider,
                                      shortcutPresenters,
                                      profilePreferences);
    }

    @Test
    public void setupTest() {
        presenter.setup();

        verify(view).setWelcome("welcome");
        verify(view).setDescription("description");
        verify(view).setBackgroundImageUrl("backgroundImageUrl");
        verify(view,
               times(3)).addShortcut(any());
    }
}
