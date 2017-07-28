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

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.home.client.widgets.shortcut.ShortcutPresenter;
import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.screens.home.model.HomeModelProvider;
import org.kie.workbench.common.screens.home.model.ModelUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.AuthorizationManager;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HomePresenterTest {

    @Mock
    private HomePresenter.View view;

    @Mock
    private TranslationService translationService;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private User user;

    @Mock
    private ManagedInstance<ShortcutPresenter> shortcutPresenters;

    private HomeModelProvider modelProvider;

    private HomePresenter presenter;

    @Before
    public void setup() {
        doReturn(mock(ShortcutPresenter.class)).when(shortcutPresenters).get();

        modelProvider = () -> {
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
                                      authorizationManager,
                                      user,
                                      shortcutPresenters);
    }

    @Test
    public void setupWithPermissionsTest() {
        doReturn(true).when(authorizationManager).authorize(any(Resource.class),
                                                            any(ResourceAction.class),
                                                            any(User.class));
        doReturn(true).when(authorizationManager).authorize(anyString(),
                                                            any(User.class));

        presenter.setup();

        verify(view).setWelcome("welcome");
        verify(view).setDescription("description");
        verify(view).setBackgroundImageUrl("backgroundImageUrl");
        verify(view,
               times(3)).addShortcut(any());
    }

    @Test
    public void setupWithoutPermissionsTest() {
        doReturn(false).when(authorizationManager).authorize(any(Resource.class),
                                                             any(ResourceAction.class),
                                                             any(User.class));
        doReturn(false).when(authorizationManager).authorize(anyString(),
                                                             any(User.class));

        presenter.setup();

        verify(view).setWelcome("welcome");
        verify(view).setDescription("description");
        verify(view).setBackgroundImageUrl("backgroundImageUrl");
        verify(view,
               times(1)).addShortcut(any());
    }
}
