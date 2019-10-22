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

package org.kie.workbench.common.stunner.project.client.docks;

import java.lang.annotation.Annotation;
import java.util.Collection;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMaximizedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.InstanceUtils;
import org.kie.workbench.common.stunner.kogito.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.kogito.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.widgets.client.docks.AbstractWorkbenchDocksHandler;
import org.uberfire.client.workbench.docks.UberfireDock;

@Dependent
public class StunnerDocksHandler extends AbstractWorkbenchDocksHandler {

    private ManagedInstance<StunnerDockSupplier> dockSuppliers;
    private Annotation[] qualifiers = new Annotation[]{DefinitionManager.DEFAULT_QUALIFIER};

    public StunnerDocksHandler() {
        //CDI proxy
    }

    @Inject
    public StunnerDocksHandler(final @Any ManagedInstance<StunnerDockSupplier> dockSuppliers) {
        this.dockSuppliers = dockSuppliers;
    }

    @Override
    public Collection<UberfireDock> provideDocks(final String perspectiveIdentifier) {
        final StunnerDockSupplier dockSupplier = InstanceUtils.lookup(dockSuppliers, StunnerDockSupplier.class, qualifiers);
        return dockSupplier.getDocks(perspectiveIdentifier);
    }

    public void onDiagramFocusEvent(final @Observes OnDiagramFocusEvent event) {
        qualifiers = event.getQualifiers();
        refreshDocks(true,
                     false);
    }

    public void onDiagramLoseFocusEvent(final @Observes OnDiagramLoseFocusEvent event) {
        qualifiers = new Annotation[]{DefinitionManager.DEFAULT_QUALIFIER};
        refreshDocks(true,
                     true);
    }

    public void onDiagramEditorMaximized(final @Observes ScreenMaximizedEvent event) {
        if (event.isDiagramScreen()) {
            refreshDocks(true,
                         false);
        }
    }
}
