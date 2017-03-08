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

package org.uberfire.ext.preferences.client.admin.item;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.client.admin.page.AdminTool;
import org.uberfire.ext.preferences.client.event.PreferencesCentralActionsConfigurationEvent;

public class AdminPageItemPresenter {

    private final View view;
    private final PlaceManager placeManager;
    private final Event<PreferencesCentralActionsConfigurationEvent> adminPageConfigurationEvent;
    private AdminTool adminTool;
    private PreferencesCentralActionsConfigurationEvent preferencesCentralActionsConfigurationEventToFire;

    @Inject
    public AdminPageItemPresenter(final View view,
                                  final PlaceManager placeManager,
                                  final Event<PreferencesCentralActionsConfigurationEvent> adminPageConfigurationEvent) {
        this.view = view;
        this.placeManager = placeManager;
        this.adminPageConfigurationEvent = adminPageConfigurationEvent;
    }

    public void setup(final AdminTool adminTool,
                      final String screen,
                      final String perspectiveIdentifierToGoBackTo) {
        this.adminTool = adminTool;
        this.preferencesCentralActionsConfigurationEventToFire = new PreferencesCentralActionsConfigurationEvent(screen,
                                                                                                                 perspectiveIdentifierToGoBackTo);

        view.init(this);
    }

    public void enter() {
        adminTool.getOnClickCommand().execute();
        adminPageConfigurationEvent.fire(preferencesCentralActionsConfigurationEventToFire);
    }

    public AdminTool getAdminTool() {
        return adminTool;
    }

    public View getView() {
        return view;
    }

    public interface View extends UberElement<AdminPageItemPresenter> {

    }
}
