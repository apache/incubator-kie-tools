/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.project.client.docks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.project.client.docks.screens.PreviewDiagramScreen;
import org.kie.workbench.common.stunner.project.client.docks.StunnerDockSupplier;
import org.kie.workbench.common.stunner.project.client.screens.ProjectDiagramPropertiesScreen;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@DMNEditor
@Dependent
public class DMNDockSupplierImpl implements StunnerDockSupplier {

    static final String PROPERTIES_DOCK_ICON = "PENCIL_SQUARE_O";

    static final String PREVIEW_DOCK_ICON = "EYE";

    private DefaultWorkbenchConstants constants = DefaultWorkbenchConstants.INSTANCE;

    @Override
    public Collection<UberfireDock> getDocks(String perspectiveIdentifier) {
        List<UberfireDock> result = new ArrayList<>();

        result.add(new UberfireDock(UberfireDockPosition.EAST,
                                    PROPERTIES_DOCK_ICON,
                                    new DefaultPlaceRequest(ProjectDiagramPropertiesScreen.SCREEN_ID),
                                    perspectiveIdentifier).withSize(450).withLabel(constants.DocksStunnerPropertiesTitle()));
        result.add(new UberfireDock(UberfireDockPosition.EAST,
                                    PREVIEW_DOCK_ICON,
                                    new DefaultPlaceRequest(PreviewDiagramScreen.SCREEN_ID),
                                    perspectiveIdentifier).withSize(450).withLabel(constants.DocksStunnerExplorerTitle()));
        return result;
    }
}
