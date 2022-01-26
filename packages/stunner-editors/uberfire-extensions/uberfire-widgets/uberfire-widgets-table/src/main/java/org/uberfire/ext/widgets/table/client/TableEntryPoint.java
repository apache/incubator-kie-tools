/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.widgets.table.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.gwtproject.resources.client.GWT3Resources;
import org.uberfire.client.views.pfly.sys.PatternFlyBootstrapper;
import org.uberfire.ext.widgets.table.client.resources.UFTableResources;

@ApplicationScoped
@GWT3Resources(
        cssResource = @GWT3Resources.CssResource(
                conversionMode = "strict"
        )
)
public class TableEntryPoint {

    @PostConstruct
    public void startApp() {
        UFTableResources.INSTANCE.CSS().ensureInjected();
        PatternFlyBootstrapper.ensurejQueryIsAvailable();
    }
}
