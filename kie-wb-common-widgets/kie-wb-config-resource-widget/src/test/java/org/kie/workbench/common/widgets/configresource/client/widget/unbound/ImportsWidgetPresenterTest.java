/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.configresource.client.widget.unbound;

import java.util.List;

import org.guvnor.common.services.project.model.ProjectImports;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.imports.Import;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ImportsWidgetPresenterTest {

    @Mock
    private ImportsWidgetView view;

    @Captor
    private ArgumentCaptor<List<Import>> importsArgumentCaptor;

    private ProjectImports imports = new ProjectImports();

    private ImportsWidgetPresenter presenter;

    @Before
    public void setup() {
        this.presenter = new ImportsWidgetPresenter(view);

        imports.getImports().addImport(new Import("import1"));
        imports.getImports().addImport(new Import("import2"));
    }

    @Test
    public void testSetup() {
        verify(view,
               times(1)).init(presenter);
    }

    @Test
    public void testSetContent() {
        presenter.setContent(imports,
                             false);

        verify(view,
               times(1)).setContent(importsArgumentCaptor.capture(),
                                    eq(false));
        verify(view).updateRenderedColumns();

        final List<Import> importsArgument = importsArgumentCaptor.getValue();

        assertEquals(2,
                     importsArgument.size());
        assertContains("import1",
                       importsArgument);
        assertContains("import2",
                       importsArgument);
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
}
