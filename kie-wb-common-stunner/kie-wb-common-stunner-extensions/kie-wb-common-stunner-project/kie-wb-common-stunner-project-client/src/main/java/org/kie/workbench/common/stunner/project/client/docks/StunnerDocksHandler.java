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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMaximizedEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.workbench.client.docks.impl.AbstractWorkbenchDocksHandler;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Dependent
public class StunnerDocksHandler extends AbstractWorkbenchDocksHandler {

    protected DefaultWorkbenchConstants constants = DefaultWorkbenchConstants.INSTANCE;

    @Override
    public Collection<UberfireDock> provideDocks(String perspectiveIdentifier) {
        List<UberfireDock> result = new ArrayList<>();

        result.add(new UberfireDock(UberfireDockPosition.EAST,
                                    "PENCIL_SQUARE_O",
                                    new DefaultPlaceRequest("ProjectDiagramPropertiesScreen"),
                                    perspectiveIdentifier).withSize(450).withLabel(constants.DocksStunnerPropertiesTitle()));
        result.add(new UberfireDock(UberfireDockPosition.EAST,
                                    "EYE",
                                    new DefaultPlaceRequest("ProjectDiagramExplorerScreen"),
                                    perspectiveIdentifier).withSize(450).withLabel(constants.DocksStunnerExplorerTitle()));
        return result;
    }

    public void onDiagramFocusEvent(@Observes OnDiagramFocusEvent event) {
        refreshDocks(true,
                     false);
    }

    public void onDiagramLoseFocusEvent(@Observes OnDiagramLoseFocusEvent event) {
        refreshDocks(true,
                     true);
    }

    public void onDiagramEditorMaximized(@Observes ScreenMaximizedEvent event) {
        if (event.isDiagramScreen()) {
            refreshDocks(true,
                         false);
        }
    }
}
