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
import java.util.Collections;
import java.util.Iterator;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMaximizedEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.mockito.Mock;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class StunnerDocksHandlerTest {

    private StunnerDocksHandler handler;

    @Mock
    private Command command;

    @Mock
    private ManagedInstance<StunnerDockSupplier> dockSuppliers;

    @Mock
    private ManagedInstance<StunnerDockSupplier> defaultDockSupplier;

    @Mock
    private UberfireDock uberfireDock;

    @Mock
    private StunnerDockSupplier customDock;

    @Mock
    private ManagedInstance<StunnerDockSupplier> customDockSupplier;

    private interface CustomQualifier extends Annotation {

    }

    private Annotation CUSTOM_QUALIFIER = new Default() {
        @Override
        public Class<? extends Annotation> annotationType() {
            return CustomQualifier.class;
        }
    };

    @Before
    public void init() {
        handler = new StunnerDocksHandler(dockSuppliers);

        when(dockSuppliers.select(StunnerDockSupplier.class, DefinitionManager.DEFAULT_QUALIFIER)).thenReturn(defaultDockSupplier);
        when(dockSuppliers.select(StunnerDockSupplier.class, CUSTOM_QUALIFIER)).thenReturn(customDockSupplier);
        when(defaultDockSupplier.get()).thenReturn(new DefaultStunnerDockSupplierImpl());
        when(customDockSupplier.get()).thenReturn(customDock);
        when(customDock.getDocks(anyString())).thenReturn(Collections.singleton(uberfireDock));

        handler.init(command);
    }

    @Test
    public void testOnDiagramFocusEventDefaultQualifier() {
        handler.onDiagramFocusEvent(new OnDiagramFocusEvent(DefinitionManager.DEFAULT_QUALIFIER));

        assertTrue(handler.shouldRefreshDocks());
        assertFalse(handler.shouldDisableDocks());

        verify(command).execute();

        final Collection<UberfireDock> docks = handler.provideDocks("");

        assertEquals(2,
                     docks.size());
        final Iterator<UberfireDock> dockIterator = docks.iterator();
        assertEquals(DefaultWorkbenchConstants.INSTANCE.DocksStunnerPropertiesTitle(), dockIterator.next().getLabel());
        assertEquals(DefaultWorkbenchConstants.INSTANCE.DocksStunnerExplorerTitle(), dockIterator.next().getLabel());
    }

    @Test
    public void testOnDiagramFocusEventCustomQualifier() {
        handler.onDiagramFocusEvent(new OnDiagramFocusEvent(CUSTOM_QUALIFIER));

        assertTrue(handler.shouldRefreshDocks());
        assertFalse(handler.shouldDisableDocks());

        verify(command).execute();

        final Collection<UberfireDock> docks = handler.provideDocks("");

        assertEquals(1,
                     docks.size());
        assertEquals(uberfireDock, docks.iterator().next());
    }

    @Test
    public void testOnDiagramFocusEventCustomQualifierUnsatisfied() {
        handler.onDiagramFocusEvent(new OnDiagramFocusEvent(CUSTOM_QUALIFIER));

        assertTrue(handler.shouldRefreshDocks());
        assertFalse(handler.shouldDisableDocks());

        verify(command).execute();

        when(customDockSupplier.isUnsatisfied()).thenReturn(true);

        final Collection<UberfireDock> docks = handler.provideDocks("");

        assertEquals(2,
                     docks.size());
        final Iterator<UberfireDock> dockIterator = docks.iterator();
        assertEquals(DefaultWorkbenchConstants.INSTANCE.DocksStunnerPropertiesTitle(), dockIterator.next().getLabel());
        assertEquals(DefaultWorkbenchConstants.INSTANCE.DocksStunnerExplorerTitle(), dockIterator.next().getLabel());
    }

    @Test
    public void testOnDiagramLoseFocusEvent() {
        handler.onDiagramLoseFocusEvent(new OnDiagramLoseFocusEvent());
    }

    @Test
    public void testOnDiagramEditorMaximized() {
        handler.onDiagramEditorMaximized(new ScreenMaximizedEvent(true));

        assertTrue(handler.shouldRefreshDocks());
        assertFalse(handler.shouldDisableDocks());
    }

    @Test
    public void testOnOtherEditorMaximized() {
        handler.onDiagramEditorMaximized(new ScreenMaximizedEvent(false));

        assertFalse(handler.shouldRefreshDocks());
        assertFalse(handler.shouldDisableDocks());
    }

    public void onDiagramLoseFocusEvent(@Observes OnDiagramLoseFocusEvent event) {
        assertTrue(handler.shouldRefreshDocks());

        assertTrue(handler.shouldDisableDocks());

        verify(command).execute();
    }
}
