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

import java.util.Collection;
import java.util.Iterator;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreen;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorPropertiesScreen;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;

import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDockSupplierImplTest {

    private static final String PERSPECTIVE_ID = "perspectiveId";

    private DMNDockSupplierImpl supplier;

    @Before
    public void setup() {
        this.supplier = new DMNDockSupplierImpl();
    }

    @Test
    public void testDocks() {
        final Collection<UberfireDock> docks = supplier.getDocks(PERSPECTIVE_ID);

        assertEquals(2, docks.size());

        final Iterator<UberfireDock> docksIterator = docks.iterator();
        final UberfireDock dock1 = docksIterator.next();
        final UberfireDock dock2 = docksIterator.next();

        assertDock(dock1,
                   DMNDockSupplierImpl.PROPERTIES_DOCK_ICON,
                   DiagramEditorPropertiesScreen.SCREEN_ID,
                   DefaultWorkbenchConstants.INSTANCE.DocksStunnerPropertiesTitle());

        assertDock(dock2,
                   DMNDockSupplierImpl.PREVIEW_DOCK_ICON,
                   PreviewDiagramScreen.SCREEN_ID,
                   DefaultWorkbenchConstants.INSTANCE.DocksStunnerExplorerTitle());
    }

    private void assertDock(final UberfireDock dock,
                            final String iconType,
                            final String identifier,
                            final String label) {
        assertEquals(UberfireDockPosition.EAST, dock.getDockPosition());
        assertEquals(iconType, dock.getIconType());
        assertEquals(identifier, dock.getPlaceRequest().getIdentifier());
        assertEquals(PERSPECTIVE_ID, dock.getAssociatedPerspective());
        assertEquals(label, dock.getLabel());
    }
}
