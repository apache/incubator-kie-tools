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

package org.kie.workbench.common.screens.home.client.widgets.shortcut.utils;

import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ShortcutHelperTest {

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private User user;

    @Mock
    private PlaceManager placeManager;

    @InjectMocks
    private ShortcutHelper shortcutHelper;

    @Test(expected = RuntimeException.class)
    public void partLessThanOneTest() {
        shortcutHelper.getPart("text",
                               0);
    }

    @Test(expected = RuntimeException.class)
    public void nonexistentSecondPartTest() {
        shortcutHelper.getPart("text",
                               2);
    }

    @Test(expected = RuntimeException.class)
    public void nonexistentThirdPartTest() {
        shortcutHelper.getPart("text{0}more-text",
                               3);
    }

    @Test
    public void onlyOnePartTest() {
        final String text = "first-part";

        final String firstPart = shortcutHelper.getPart(text,
                                                        1);
        assertEquals("first-part",
                     firstPart);
    }

    @Test
    public void threeValidPartsTest() {
        final String text = "first-part{0}second-part{1}third-part";

        final String firstPart = shortcutHelper.getPart(text,
                                                        1);
        assertEquals("first-part",
                     firstPart);

        final String secondPart = shortcutHelper.getPart(text,
                                                         2);
        assertEquals("second-part",
                     secondPart);

        final String thirdPart = shortcutHelper.getPart(text,
                                                        3);
        assertEquals("third-part",
                     thirdPart);
    }

    @Test
    public void goToWithPermission() {
        doReturn(true).when(authorizationManager).authorize(any(ResourceRef.class),
                                                            eq(user));
        final String perspectiveIdentifier = "perspectiveIdentifier";

        shortcutHelper.goTo(perspectiveIdentifier);

        verify(placeManager).goTo(perspectiveIdentifier);
    }

    @Test
    public void goToWithoutPermission() {
        doReturn(false).when(authorizationManager).authorize(any(ResourceRef.class),
                                                             eq(user));
        final String perspectiveIdentifier = "perspectiveIdentifier";

        shortcutHelper.goTo(perspectiveIdentifier);

        verify(placeManager,
               never()).goTo(perspectiveIdentifier);
    }
}
