/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.panel;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

@ApplicationScoped
public class DMNGridPanelProducer {

    private DMNGridPanel panel;

    public DMNGridPanelProducer() {
        //CDI proxy
    }

    @Inject
    public DMNGridPanelProducer(final @DMNEditor DMNGridLayer gridLayer,
                                final @DMNEditor RestrictedMousePanMediator mousePanMediator) {
        this.panel = new DMNGridPanel(gridLayer,
                                      mousePanMediator);
    }

    @Produces
    @DMNEditor
    public DMNGridPanel getGridPanel() {
        return panel;
    }
}
