/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.widgets.configresource.client.widget.bound;

import java.util.List;
import javax.enterprise.event.Event;

import org.appformer.project.datamodel.imports.Import;
import org.appformer.project.datamodel.imports.Imports;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.ImportAddedEvent;
import org.kie.workbench.common.widgets.client.datamodel.ImportRemovedEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ImportsWidgetPresenterTest {

    @Mock
    ImportsWidgetView view;

    @Mock
    Event<ImportAddedEvent> importAddedEvent;

    @Mock
    Event<ImportRemovedEvent> importRemovedEvent;

    @Mock
    AsyncPackageDataModelOracle dmo;

    @Captor
    private ArgumentCaptor<List<Import>> internalFactTypesCaptor;

    @Captor
    private ArgumentCaptor<List<Import>> externalFactTypesCaptor;

    @Captor
    private ArgumentCaptor<List<Import>> importsFactTypesCaptor;

    @Captor
    private ArgumentCaptor<ImportAddedEvent> importAddedEventCaptor;

    @Captor
    private ArgumentCaptor<ImportRemovedEvent> importRemovedEventCaptor;

    @Before
    public void setup() {
        when(dmo.getInternalFactTypes()).thenReturn(new String[]{"Internal1", "Internal2", "Internal3"});
        when(dmo.getExternalFactTypes()).thenReturn(new String[]{"org.pkg1.External1", "org.pkg1.External2", "org.pkg1.External3"});
    }

    @Test
    public void testSetup() {
        final ImportsWidgetPresenter presenter = new ImportsWidgetPresenter(view,
                                                                            importAddedEvent,
                                                                            importRemovedEvent);
        verify(view,
               times(1)).init(presenter);
    }

    @Test
    public void testSetContent() {
        final ImportsWidgetPresenter presenter = new ImportsWidgetPresenter(view,
                                                                            importAddedEvent,
                                                                            importRemovedEvent);
        final Imports imports = new Imports();
        imports.addImport(new Import("Internal1"));

        presenter.setContent(dmo,
                             imports,
                             false);

        verify(view,
               times(1)).setContent(internalFactTypesCaptor.capture(),
                                    externalFactTypesCaptor.capture(),
                                    importsFactTypesCaptor.capture(),
                                    eq(false));

        assertEquals(3,
                     internalFactTypesCaptor.getValue().size());
        assertContains("Internal1",
                       internalFactTypesCaptor.getValue());
        assertContains("Internal2",
                       internalFactTypesCaptor.getValue());
        assertContains("Internal3",
                       internalFactTypesCaptor.getValue());

        assertEquals(3,
                     externalFactTypesCaptor.getValue().size());
        assertContains("org.pkg1.External1",
                       externalFactTypesCaptor.getValue());
        assertContains("org.pkg1.External2",
                       externalFactTypesCaptor.getValue());
        assertContains("org.pkg1.External3",
                       externalFactTypesCaptor.getValue());

        assertEquals(0,
                     importsFactTypesCaptor.getValue().size());

        assertEquals(1,
                     imports.getImports().size());
        assertContains("Internal1",
                       imports.getImports());
    }

    @Test
    public void isInternalImportWithoutSetup() {
        final ImportsWidgetPresenter presenter = new ImportsWidgetPresenter(view,
                                                                            importAddedEvent,
                                                                            importRemovedEvent);
        assertFalse(presenter.isInternalImport(mock(Import.class)));
    }

    @Test
    public void isInternalImportWithoutSetupNullImportType() {
        final ImportsWidgetPresenter presenter = new ImportsWidgetPresenter(view,
                                                                            importAddedEvent,
                                                                            importRemovedEvent);
        assertFalse(presenter.isInternalImport(null));
    }

    @Test
    public void isInternalImportInternalImportType() {
        final ImportsWidgetPresenter presenter = new ImportsWidgetPresenter(view,
                                                                            importAddedEvent,
                                                                            importRemovedEvent);
        final Imports imports = new Imports();
        final Import importType = new Import("Internal1");
        imports.addImport(importType);

        presenter.setContent(dmo,
                             imports,
                             false);

        assertTrue(presenter.isInternalImport(importType));
    }

    @Test
    public void isInternalImportExternalImportType() {
        final ImportsWidgetPresenter presenter = new ImportsWidgetPresenter(view,
                                                                            importAddedEvent,
                                                                            importRemovedEvent);
        final Imports imports = new Imports();
        final Import importType = new Import("External1");
        imports.addImport(importType);

        presenter.setContent(dmo,
                             imports,
                             false);

        assertFalse(presenter.isInternalImport(mock(Import.class)));
    }

    @Test
    public void testOnImportAdded() {
        final ImportsWidgetPresenter presenter = new ImportsWidgetPresenter(view,
                                                                            importAddedEvent,
                                                                            importRemovedEvent);
        final Imports imports = new Imports();
        imports.addImport(new Import("Internal1"));

        presenter.setContent(dmo,
                             imports,
                             false);

        presenter.onAddImport(new Import("NewImport1"));

        assertEquals(2,
                     imports.getImports().size());
        assertContains("Internal1",
                       imports.getImports());
        assertContains("NewImport1",
                       imports.getImports());

        verify(importAddedEvent,
               times(1)).fire(importAddedEventCaptor.capture());
        assertEquals("NewImport1",
                     importAddedEventCaptor.getValue().getImport().getType());
    }

    @Test
    public void testOnImportRemoved() {
        final ImportsWidgetPresenter presenter = new ImportsWidgetPresenter(view,
                                                                            importAddedEvent,
                                                                            importRemovedEvent);
        final Imports imports = new Imports();
        imports.addImport(new Import("Internal1"));

        presenter.setContent(dmo,
                             imports,
                             false);

        presenter.onRemoveImport(new Import("Internal1"));

        assertEquals(0,
                     imports.getImports().size());

        verify(importRemovedEvent,
               times(1)).fire(importRemovedEventCaptor.capture());
        assertEquals("Internal1",
                     importRemovedEventCaptor.getValue().getImport().getType());
    }

    private static void assertContains(final String factType,
                                       final List<Import> factTypes) {
        for (Import i : factTypes) {
            if (i.getType().equals(factType)) {
                return;
            }
        }
        fail("Expected Fact Type '" + factType + "' was not found.");
    }
}
