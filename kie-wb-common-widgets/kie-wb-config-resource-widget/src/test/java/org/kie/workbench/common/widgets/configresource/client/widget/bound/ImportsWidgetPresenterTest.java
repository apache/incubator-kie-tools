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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.ImportAddedEvent;
import org.kie.workbench.common.widgets.client.datamodel.ImportRemovedEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ImportsWidgetPresenterTest {

    @Mock
    private ImportsWidgetView view;

    @Mock
    private Event<ImportAddedEvent> importAddedEvent;

    @Mock
    private Event<ImportRemovedEvent> importRemovedEvent;

    @Mock
    private AsyncPackageDataModelOracle dmo;

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

    private ImportsWidgetPresenter presenter;

    @Before
    public void setup() {
        when(dmo.getInternalFactTypes()).thenReturn(new String[]{"Internal1", "Internal2", "Internal3"});
        when(dmo.getExternalFactTypes()).thenReturn(new String[]{"org.pkg1.External1", "org.pkg1.External2", "org.pkg1.External3"});

        this.presenter = new ImportsWidgetPresenter(view,
                                                    importAddedEvent,
                                                    importRemovedEvent);
    }

    @Test
    public void testSetup() {
        verify(view,
               times(1)).init(presenter);
    }

    @Test
    public void testSetContent() {
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

        verify(view).updateRenderedColumns();

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
    public void testSetContentWithExternalImport() {
        final Imports imports = new Imports();
        imports.addImport(new Import("org.pkg1.External1"));

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

        assertEquals(2,
                     externalFactTypesCaptor.getValue().size());
        assertContains("org.pkg1.External2",
                       externalFactTypesCaptor.getValue());
        assertContains("org.pkg1.External3",
                       externalFactTypesCaptor.getValue());

        assertEquals(1,
                     importsFactTypesCaptor.getValue().size());
        assertContains("org.pkg1.External1",
                       importsFactTypesCaptor.getValue());

        assertEquals(1,
                     imports.getImports().size());
        assertContains("org.pkg1.External1",
                       imports.getImports());
    }

    @Test
    public void isInternalImportWithoutSetup() {
        assertFalse(presenter.isInternalImport(mock(Import.class)));
    }

    @Test
    public void isInternalImportWithoutSetupNullImportType() {
        assertFalse(presenter.isInternalImport(null));
    }

    @Test
    public void isInternalImportInternalImportType() {
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
        final Imports imports = new Imports();
        final Import importType = new Import("External1");
        imports.addImport(importType);

        presenter.setContent(dmo,
                             imports,
                             false);

        assertFalse(presenter.isInternalImport(importType));
    }

    @Test
    public void testOnImportAdded() {
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

        verify(view, times(2)).updateRenderedColumns();
    }

    @Test
    public void testSetContentRemovesOldImportsOnImportAdded() {
        when(dmo.getInternalFactTypes()).thenReturn(new String[]{"a"});
        when(dmo.getExternalFactTypes()).thenReturn(new String[]{"b"});
        final Imports imports = new Imports();
        imports.addImport(new Import("c"));

        presenter.setContent(dmo,
                             imports,
                             false);

        when(dmo.getInternalFactTypes()).thenReturn(new String[]{"A"});
        when(dmo.getExternalFactTypes()).thenReturn(new String[]{"B"});
        final Imports importsNew = new Imports();
        importsNew.addImport(new Import("C"));

        presenter.setContent(dmo,
                             importsNew,
                             false);

        verify(view, times(2)).updateRenderedColumns();

        assertEquals(1, presenter.getInternalFactTypes().size());
        assertEquals("A", presenter.getInternalFactTypes().get(0).getType());

        assertEquals(1, presenter.getExternalFactTypes().size());
        assertEquals("B", presenter.getExternalFactTypes().get(0).getType());

        assertEquals(1, presenter.getModelFactTypes().size());
        assertEquals("C", presenter.getModelFactTypes().get(0).getType());
    }

    @Test
    public void testOnImportAddedExternal() {
        final Imports imports = new Imports();

        presenter.setContent(dmo,
                             imports,
                             false);

        presenter.onAddImport(new Import("org.pkg1.External1"));

        verify(view, times(2)).updateRenderedColumns();

        assertEquals(1,
                     imports.getImports().size());
        assertContains("org.pkg1.External1",
                       imports.getImports());
        assertNotContains("org.pkg1.External1",
                          presenter.getExternalFactTypes());

        verify(dmo).filter();
    }

    @Test
    public void testOnImportRemoved() {
        final Imports imports = new Imports();
        imports.addImport(new Import("Internal1"));

        presenter.setContent(dmo,
                             imports,
                             false);

        presenter.onRemoveImport(new Import("Internal1"));

        verify(view, times(2)).updateRenderedColumns();

        assertEquals(0,
                     imports.getImports().size());

        verify(dmo).filter();

        verify(importRemovedEvent,
               times(1)).fire(importRemovedEventCaptor.capture());
        assertEquals("Internal1",
                     importRemovedEventCaptor.getValue().getImport().getType());
    }

    @Test
    public void testOnImportRemovedExternal() {
        final Imports imports = new Imports();
        imports.addImport(new Import("org.pkg1.External1"));

        presenter.setContent(dmo,
                             imports,
                             false);

        presenter.onRemoveImport(new Import("org.pkg1.External1"));

        verify(view, times(2)).updateRenderedColumns();

        assertEquals(0,
                     imports.getImports().size());

        assertContains("org.pkg1.External1",
                       presenter.getExternalFactTypes());
    }

    private static void assertContains(final String factType,
                                       final List<Import> factTypes) {
        assertTrue("Expected Fact Type '" + factType + "' was not found.",
                   factTypes
                           .stream()
                           .map(Import::getType)
                           .filter(t -> t.equals(factType))
                           .findAny()
                           .isPresent());
    }

    private static void assertNotContains(final String factType,
                                          final List<Import> factTypes) {
        assertFalse("Fact Type '" + factType + "' was found but was not expected.",
                    factTypes
                            .stream()
                            .map(Import::getType)
                            .filter(t -> t.equals(factType))
                            .findAny()
                            .isPresent());
    }
}
