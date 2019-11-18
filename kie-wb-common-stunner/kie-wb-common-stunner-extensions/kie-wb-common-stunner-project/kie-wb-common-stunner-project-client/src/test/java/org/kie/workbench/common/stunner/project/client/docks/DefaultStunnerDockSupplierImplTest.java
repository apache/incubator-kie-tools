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

import java.util.Collection;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.project.client.docks.DefaultStunnerDockSupplierImpl.DOCK_POSITION;
import static org.kie.workbench.common.stunner.project.client.docks.DefaultStunnerDockSupplierImpl.EXPLORER_DOCK_SCREEN_ID;
import static org.kie.workbench.common.stunner.project.client.docks.DefaultStunnerDockSupplierImpl.EXPLORER_ICON_TYPE;
import static org.kie.workbench.common.stunner.project.client.docks.DefaultStunnerDockSupplierImpl.EXPLORER_LABEL;
import static org.kie.workbench.common.stunner.project.client.docks.DefaultStunnerDockSupplierImpl.PROPERTIES_DOCK_SCREEN_ID;
import static org.kie.workbench.common.stunner.project.client.docks.DefaultStunnerDockSupplierImpl.PROPERTIES_ICON_TYPE;
import static org.kie.workbench.common.stunner.project.client.docks.DefaultStunnerDockSupplierImpl.PROPERTIES_LABEL;
import static org.kie.workbench.common.stunner.project.client.docks.DefaultStunnerDockSupplierImpl.SIZE;

@RunWith(GwtMockitoTestRunner.class)
public class DefaultStunnerDockSupplierImplTest {

    private static final String PERSPECTIVE_IDENTIFIER = "Test Perspective Identifier";

    @Test
    public void getDocks() {
        DefaultStunnerDockSupplierImpl defaultStunnerDockSupplier = new DefaultStunnerDockSupplierImpl();
        Collection<UberfireDock> docks = defaultStunnerDockSupplier.getDocks(PERSPECTIVE_IDENTIFIER);

        assertEquals(2, docks.size(), 0);

        Optional<UberfireDock> propertiesDock = docks.stream()
                .filter(dock -> dock.getPlaceRequest().getIdentifier().compareTo(PROPERTIES_DOCK_SCREEN_ID) == 0)
                .findFirst();
        assertDock(propertiesDock.get(),
                   DOCK_POSITION,
                   PROPERTIES_ICON_TYPE,
                   PROPERTIES_DOCK_SCREEN_ID,
                   PERSPECTIVE_IDENTIFIER,
                   SIZE,
                   PROPERTIES_LABEL,
                   null);

        Optional<UberfireDock> explorerDock = docks.stream()
                .filter(dock -> dock.getPlaceRequest().getIdentifier().compareTo(DefaultStunnerDockSupplierImpl.EXPLORER_DOCK_SCREEN_ID) == 0)
                .findFirst();
        assertDock(explorerDock.get(),
                   DOCK_POSITION,
                   EXPLORER_ICON_TYPE,
                   EXPLORER_DOCK_SCREEN_ID,
                   PERSPECTIVE_IDENTIFIER,
                   SIZE,
                   EXPLORER_LABEL,
                   null);
    }

    private void assertDock(final UberfireDock dock,
                            final UberfireDockPosition position,
                            final String iconType,
                            final String placeRequestIdentifier,
                            final String perspectiveIdentifier,
                            final Double size,
                            final String label,
                            final String tooltip) {

        assertNotNull(dock);

        assertEquals(position, dock.getDockPosition());
        assertTrue(dock.getIconType().compareTo(iconType) == 0);
        assertTrue(dock.getIdentifier().compareTo(placeRequestIdentifier) == 0);
        assertTrue(dock.getAssociatedPerspective().compareTo(perspectiveIdentifier) == 0);

        assertEquals(size, dock.getSize());
        if (label == null) {
            assertNull(dock.getLabel());
        } else {
            assertTrue(dock.getLabel().compareTo(label) == 0);
        }

        if (tooltip == null) {
            assertNull(dock.getTooltip());
        } else {
            assertTrue(dock.getTooltip().compareTo(tooltip) == 0);
        }
    }
}