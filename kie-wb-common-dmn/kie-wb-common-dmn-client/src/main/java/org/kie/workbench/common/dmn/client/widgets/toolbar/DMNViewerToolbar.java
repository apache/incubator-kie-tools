/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.toolbar;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToJpgToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPdfToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPngToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.VisitGraphToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ManagedToolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ManagedToolbarDelegate;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ViewerToolbar;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;

@Default
@DMNEditor
public class DMNViewerToolbar
        extends ManagedToolbarDelegate<ViewerSession>
        implements ViewerToolbar {

    private final ManagedToolbar<ViewerSession> toolbar;

    @Inject
    public DMNViewerToolbar(final ManagedToolbar<ViewerSession> toolbar) {
        this.toolbar = toolbar;
    }

    @PostConstruct
    public void init() {
        toolbar.register(VisitGraphToolbarCommand.class)
                .register(ExportToPngToolbarCommand.class)
                .register(ExportToJpgToolbarCommand.class)
                .register(ExportToPdfToolbarCommand.class);
    }

    @Override
    protected ManagedToolbar<ViewerSession> getDelegate() {
        return toolbar;
    }
}
