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
package org.kie.workbench.common.stunner.project.client.docks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;

import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreen;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorPropertiesScreen;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Default
@Dependent
public class DefaultStunnerDockSupplierImpl implements StunnerDockSupplier {

    protected DefaultWorkbenchConstants constants = DefaultWorkbenchConstants.INSTANCE;

    @Override
    public Collection<UberfireDock> getDocks(String perspectiveIdentifier) {
        List<UberfireDock> result = new ArrayList<>();

        result.add(new UberfireDock(UberfireDockPosition.EAST,
                                    "PENCIL_SQUARE_O",
                                    new DefaultPlaceRequest(DiagramEditorPropertiesScreen.SCREEN_ID),
                                    perspectiveIdentifier).withSize(450).withLabel(constants.DocksStunnerPropertiesTitle()));
        result.add(new UberfireDock(UberfireDockPosition.EAST,
                                    "EYE",
                                    new DefaultPlaceRequest(DiagramEditorExplorerScreen.SCREEN_ID),
                                    perspectiveIdentifier).withSize(450).withLabel(constants.DocksStunnerExplorerTitle()));
        return result;
    }
}
