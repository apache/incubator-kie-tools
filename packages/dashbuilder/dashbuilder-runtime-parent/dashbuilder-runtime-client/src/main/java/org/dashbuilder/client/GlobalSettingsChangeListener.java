/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import org.dashbuilder.displayer.GlobalDisplayerSettings;
import org.dashbuilder.displayer.Mode;
import org.dashbuilder.shared.event.UpdatedGlobalSettingsEvent;

@ApplicationScoped
public class GlobalSettingsChangeListener {

    private static final String PF5_DARK_MODE = "pf-v5-theme-dark";
    @Inject
    GlobalDisplayerSettings globalDisplayerSettings;

    void onNewGlobalSettings(@Observes UpdatedGlobalSettingsEvent event) {
        var settings = event.getGlobalSettings();

        globalDisplayerSettings.setDisplayerSettings(settings.getSettings());

        var html = DomGlobal.document.getElementsByTagName("html");
        if (html.length > 0) {
            if (settings.getMode() == Mode.DARK) {
                html.getAt(0).classList.add(PF5_DARK_MODE);
            } else {
                html.getAt(0).classList.remove(PF5_DARK_MODE);
            }
        }
    }

}
