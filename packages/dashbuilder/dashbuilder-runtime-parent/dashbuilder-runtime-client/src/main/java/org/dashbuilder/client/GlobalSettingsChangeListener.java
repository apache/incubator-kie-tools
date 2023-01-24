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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.dom.client.StyleInjector;
import elemental2.dom.DomGlobal;
import org.dashbuilder.client.resources.NativeLibraryResources;
import org.dashbuilder.displayer.GlobalDisplayerSettings;
import org.dashbuilder.shared.event.UpdatedGlobalSettingsEvent;

@ApplicationScoped
public class GlobalSettingsChangeListener {

    @Inject
    GlobalDisplayerSettings globalDisplayerSettings;

    @PostConstruct
    void injectDarkModeCss() {
        final var darkModeCss = NativeLibraryResources.INSTANCE.cssDarkMode().getText();
        StyleInjector.inject(darkModeCss);
    }

    void onNewGlobalSettings(@Observes UpdatedGlobalSettingsEvent event) {
        var settings = event.getGlobalSettings();

        globalDisplayerSettings.setDisplayerSettings(settings.getSettings());

        DomGlobal.document.body.setAttribute("dashbuilder-mode", settings.getMode().name().toLowerCase());
    }

}
