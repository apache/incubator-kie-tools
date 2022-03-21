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

package org.kie.workbench.common.stunner.client.widgets.toolbar.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToJpgToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPdfToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPngToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToSvgToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.VisitGraphToolbarCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;

@Default
public class ManagedViewerToolbar
        extends ManagedToolbarDelegate<ViewerSession>
        implements ViewerToolbar {

    private final ManagedToolbar<ViewerSession> toolbar;

    @Inject
    public ManagedViewerToolbar(final ManagedToolbar<ViewerSession> toolbar) {
        this.toolbar = toolbar;
    }

    @PostConstruct
    public void init() {
        toolbar.register(VisitGraphToolbarCommand.class)
                .register(ExportToPngToolbarCommand.class)
                .register(ExportToJpgToolbarCommand.class)
                .register(ExportToSvgToolbarCommand.class)
                .register(ExportToPdfToolbarCommand.class);
    }

    @Override
    protected ManagedToolbar<ViewerSession> getDelegate() {
        return toolbar;
    }
}
