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

import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.home.model.HomeShortcut;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;

import static org.uberfire.workbench.model.ActivityResourceType.PERSPECTIVE;

public class ShortcutHelper {

    private AuthorizationManager authorizationManager;

    private User user;

    private PlaceManager placeManager;

    public ShortcutHelper() {
    }

    @Inject
    public ShortcutHelper(final AuthorizationManager authorizationManager,
                          final User user,
                          final PlaceManager placeManager) {
        this.authorizationManager = authorizationManager;
        this.user = user;
        this.placeManager = placeManager;
    }

    public String getPart(final String text,
                          final int part) {
        if (part < 1) {
            throw new RuntimeException("The first part is the number one.");
        }

        int beginIndex = 0;

        if (part > 1) {
            final int i = part - 2;
            final String s = "{" + i + "}";
            beginIndex = text.indexOf(s);
            if (beginIndex < 0) {
                throw new RuntimeException("The translation " + text + " is missing parameters.");
            } else {
                beginIndex += String.valueOf(i).length() + 2;
            }
        }

        final int i2 = part - 1;
        final String s2 = "{" + i2 + "}";
        int endIndex = text.indexOf(s2);
        if (endIndex < 0) {
            endIndex = text.length();
        }

        return text.substring(beginIndex,
                              endIndex);
    }

    public void goTo(final String perspectiveIdentifier) {
        if (authorize(perspectiveIdentifier)) {
            placeManager.goTo(perspectiveIdentifier);
        }
    }

    public boolean authorize(final String perspective) {
        return authorizationManager.authorize(new ResourceRef(perspective,
                                                              PERSPECTIVE),
                                              user);
    }

    public boolean authorize(final HomeShortcut shortcut) {
        if (shortcut.getResource() != null) {
            return authorizationManager.authorize(shortcut.getResource(),
                                                  shortcut.getResourceAction(),
                                                  user);
        } else if (shortcut.getPermission() != null) {
            return authorizationManager.authorize(shortcut.getPermission(),
                                                  user);
        } else {
            return true;
        }
    }
}
