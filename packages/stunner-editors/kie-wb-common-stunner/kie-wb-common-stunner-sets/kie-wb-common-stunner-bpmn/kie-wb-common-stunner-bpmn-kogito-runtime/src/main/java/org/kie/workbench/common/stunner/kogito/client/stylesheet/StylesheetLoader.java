/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.kogito.client.stylesheet;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.inject.Singleton;

import io.crysknife.ui.templates.client.StyleInjector;

@Startup
@Singleton
public class StylesheetLoader {

    private final Stylesheets stylesheets = new StylesheetsImpl();

    @PostConstruct
    public void onStartup() {

        StyleInjector.fromString(stylesheets.patternfly().getText()).inject();
        StyleInjector.fromString(stylesheets.uberfirePatternfly().getText()).inject();
        StyleInjector.fromString(stylesheets.patternflyAdditions().getText()).inject();
        StyleInjector.fromString(stylesheets.card().getText()).inject();
        StyleInjector.fromString(stylesheets.animate().getText()).inject();
        StyleInjector.fromString(stylesheets.uberfireSimplePager().getText()).inject();
        StyleInjector.fromString(stylesheets.uftable().getText()).inject();
    }

}
