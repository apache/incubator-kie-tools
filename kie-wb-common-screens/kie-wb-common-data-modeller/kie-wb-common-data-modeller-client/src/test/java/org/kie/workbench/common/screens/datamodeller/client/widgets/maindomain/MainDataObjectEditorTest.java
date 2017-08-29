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

package org.kie.workbench.common.screens.datamodeller.client.widgets.maindomain;

import java.util.HashMap;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.definition.type.Description;
import org.kie.api.definition.type.Label;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.widgets.DomainEditorBaseTest;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.refactoring.client.usages.ShowAssetUsagesDisplayer;
import org.kie.workbench.common.services.refactoring.client.usages.ShowAssetUsagesDisplayerView;
import org.kie.workbench.common.services.refactoring.service.AssetsUsageService;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.kie.workbench.common.screens.datamodeller.client.widgets.DataModelerEditorsTestHelper.NEW_DESCRIPTION;
import static org.kie.workbench.common.screens.datamodeller.client.widgets.DataModelerEditorsTestHelper.NEW_LABEL;
import static org.kie.workbench.common.screens.datamodeller.client.widgets.DataModelerEditorsTestHelper.NEW_NAME;
import static org.kie.workbench.common.screens.datamodeller.client.widgets.DataModelerEditorsTestHelper.NEW_PACKAGE;
import static org.kie.workbench.common.screens.datamodeller.client.widgets.DataModelerEditorsTestHelper.NEW_SUPERCLASS;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class MainDataObjectEditorTest
        extends DomainEditorBaseTest {

    @Mock
    private MainDataObjectEditorView view;

    @Mock
    private AssetsUsageService assetsUsageService;

    @Mock
    private ShowAssetUsagesDisplayerView assetUsagesDisplayerView;

    @Mock
    private TranslationService translationService;

    private ShowAssetUsagesDisplayer showAssetUsagesDisplayer;

    protected MainDataObjectEditor createObjectEditor() {

        when(assetUsagesDisplayerView.getDefaultMessageContainer()).thenReturn(mock(HTMLElement.class));

        showAssetUsagesDisplayer = spy(new ShowAssetUsagesDisplayer(assetUsagesDisplayerView,
                                                                    translationService,
                                                                    new CallerMock<>(assetsUsageService)));

        MainDataObjectEditor objectEditor = new MainDataObjectEditor(view,
                                                                     handlerRegistry,
                                                                     dataModelerEvent,
                                                                     commandBuilder,
                                                                     validatorService,
                                                                     modelerServiceCaller,
                                                                     showAssetUsagesDisplayer);
        return objectEditor;
    }

    @Test
    public void loadDataObjectTest() {

        MainDataObjectEditor objectEditor = createObjectEditor();

        //The domain editors typically reacts upon DataModelerContext changes.
        //when the context changes the editor will typically be reloaded.
        objectEditor.onContextChange(context);

        DataObject dataObject = context.getDataObject();

        //the view should be populated with the values from the dataObject.
        verify(view,
               times(1)).setName(dataObject.getName());
        verify(view,
               times(1)).setPackageName(dataObject.getPackageName());
        verify(view,
               times(1)).initSuperClassList(anyList(),
                                            eq(dataObject.getSuperClassName()));
        verify(view,
               times(1)).setLabel(AnnotationValueHandler.getStringValue(dataObject,
                                                                        Label.class.getName(),
                                                                        "value"));
        verify(view,
               times(1)).setDescription(AnnotationValueHandler.getStringValue(dataObject,
                                                                              Description.class.getName(),
                                                                              "value"));

        assertFalse(objectEditor.isReadonly());
    }

    @Test
    public void valuesChangeTest() {

        MainDataObjectEditor objectEditor = createObjectEditor();

        //load the editor
        objectEditor.onContextChange(context);

        context.getEditorModelContent().setOriginalClassName(context.getDataObject().getClassName());
        context.getEditorModelContent().setPath(mock(Path.class));

        //emulate the user input
        when(view.getName()).thenReturn(NEW_NAME);
        when(view.getPackageName()).thenReturn(NEW_PACKAGE);
        when(view.getSuperClass()).thenReturn(NEW_SUPERCLASS);
        when(view.getDescription()).thenReturn(NEW_DESCRIPTION);
        when(view.getLabel()).thenReturn(NEW_LABEL);

        Map<String, Boolean> validationResult = new HashMap<String, Boolean>();
        validationResult.put(NEW_NAME,
                             true);
        when(validationService.evaluateJavaIdentifiers(any(String[].class))).thenReturn(validationResult);

        //notify the presenter about the changes in the UI
        objectEditor.onNameChange();
        objectEditor.onPackageChange();
        objectEditor.onSuperClassChange();
        objectEditor.onLabelChange();
        objectEditor.onDescriptionChange();

        verify(showAssetUsagesDisplayer,
               times(2)).showAssetUsages(anyString(),
                                         any(),
                                         any(),
                                         any(),
                                         any(),
                                         any());

        //After the changes has been processed by the presenter the dataObject should have been populated with the new values.
        DataObject dataObject = context.getDataObject();

        assertEquals(NEW_NAME,
                     dataObject.getName());
        assertEquals(NEW_PACKAGE,
                     dataObject.getPackageName());
        assertEquals(NEW_SUPERCLASS,
                     dataObject.getSuperClassName());
        assertEquals(NEW_LABEL,
                     AnnotationValueHandler.getStringValue(dataObject,
                                                           Label.class.getName()));
        assertEquals(NEW_DESCRIPTION,
                     AnnotationValueHandler.getStringValue(dataObject,
                                                           Description.class.getName()));

        verify(dataModelerEvent,
               times(5)).fire(any(DataModelerEvent.class));
    }
}
