/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.client.security;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import org.jboss.errai.security.client.local.api.SecurityContext;
import org.jboss.errai.security.shared.api.UserCookieEncoder;
import org.uberfire.client.mvp.PlaceManager;

/**
 * Screen that shows details about the User object obtained via the security context cache.
 */
@Dependent
@Named("org.uberfire.wbtest.client.security.SecurityContextInfoScreen")
public class SecurityContextInfoScreen extends AbstractUserInfoScreen {

    Label cookieLabel = new Label(Cookies.getCookie(UserCookieEncoder.USER_COOKIE_NAME));
    Button refreshButton = new Button("Refresh Security Status");
    @Inject
    SecurityContext securityContext;

    @Inject
    public SecurityContextInfoScreen(PlaceManager placeManager) {
        super(placeManager);
    }

    @PostConstruct
    public void setup() {
        panel.add(cookieLabel);

        cookieLabel.ensureDebugId("SecurityStatusScreen-cookieLabel");
        refreshButton.ensureDebugId("SecurityStatusScreen-refreshButton");

        refreshButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateLabels();
            }
        });

        updateLabels();
    }

    public void updateLabels() {
        cookieLabel.setText(Cookies.getCookie(UserCookieEncoder.USER_COOKIE_NAME));
        super.updateLabels(securityContext.getCachedUser());
    }
}
