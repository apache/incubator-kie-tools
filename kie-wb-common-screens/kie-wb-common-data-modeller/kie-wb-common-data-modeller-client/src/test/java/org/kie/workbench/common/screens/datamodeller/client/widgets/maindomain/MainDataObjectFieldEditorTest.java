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
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.refactoring.client.usages.ShowAssetUsagesDisplayer;
import org.kie.workbench.common.services.refactoring.client.usages.ShowAssetUsagesDisplayerView;
import org.kie.workbench.common.services.refactoring.service.AssetsUsageService;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.kie.workbench.common.screens.datamodeller.client.widgets.DataModelerEditorsTestHelper.NEW_DESCRIPTION;
import static org.kie.workbench.common.screens.datamodeller.client.widgets.DataModelerEditorsTestHelper.NEW_FIELD_NAME;
import static org.kie.workbench.common.screens.datamodeller.client.widgets.DataModelerEditorsTestHelper.NEW_LABEL;
import static org.kie.workbench.common.screens.datamodeller.client.widgets.DataModelerEditorsTestHelper.NEW_TYPE;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class MainDataObjectFieldEditorTest
        extends DomainEditorBaseTest {

    @Mock
    private MainDataObjectFieldEditorView view;

    @Mock
    private AssetsUsageService assetsUsageService;

    @Mock
    private ShowAssetUsagesDisplayerView assetUsagesDisplayerView;

    @Mock
    private TranslationService translationService;

    private ShowAssetUsagesDisplayer showAssetUsagesDisplayer;

    protected MainDataObjectFieldEditor createFieldEditor() {

        when(assetUsagesDisplayerView.getDefaultMessageContainer()).thenReturn(mock(HTMLElement.class));

        showAssetUsagesDisplayer = spy(new ShowAssetUsagesDisplayer(assetUsagesDisplayerView,
                                                                    translationService,
                                                                    new CallerMock<>(assetsUsageService)));

        MainDataObjectFieldEditor fieldEditor = new MainDataObjectFieldEditor(view,
                                                                              handlerRegistry,
                                                                              dataModelerEvent,
                                                                              commandBuilder,
                                                                              validatorService,
                                                                              modelerServiceCaller,
                                                                              showAssetUsagesDisplayer);
        return fieldEditor;
    }

    @Test
    public void loadDataObjectFieldTest() {

        MainDataObjectFieldEditor fieldEditor = createFieldEditor();

        DataObject dataObject = context.getDataObject();
        ObjectProperty field = dataObject.getProperty("field1");
        //emulates selection of field1 in current context.
        context.setObjectProperty(field);

        //The domain editors typically reacts upon DataModelerContext changes.
        //when the context changes the editor will typically be reloaded.
        fieldEditor.onContextChange(context);

        //the view should be populated with the values from the field.

        verify(view,
               times(1)).setName(field.getName());
        verify(view,
               times(1)).setLabel(AnnotationValueHandler.getStringValue(field,
                                                                        Label.class.getName(),
                                                                        "value"));
        verify(view,
               times(1)).setDescription(AnnotationValueHandler.getStringValue(field,
                                                                              Description.class.getName(),
                                                                              "value"));
        verify(view,
               times(1)).initTypeList(anyList(),
                                      eq(field.getClassName()),
                                      eq(false));

        assertFalse(fieldEditor.isReadonly());
    }

    @Test
    public void valuesChangeTest() {

        MainDataObjectFieldEditor fieldEditor = createFieldEditor();

        DataObject dataObject = context.getDataObject();
        ObjectProperty field = dataObject.getProperty("field1");
        //emulates selection of field1 in current context.
        context.setObjectProperty(field);

        //The domain editors typically reacts upon DataModelerContext changes.
        //when the context changes the editor will typically be reloaded.
        fieldEditor.onContextChange(context);

        context.getEditorModelContent().setOriginalClassName(context.getDataObject().getClassName());
        context.getEditorModelContent().setPath(mock(Path.class));

        //emulate the user input
        when(view.getName()).thenReturn(NEW_FIELD_NAME);
        when(view.getDescription()).thenReturn(NEW_DESCRIPTION);
        when(view.getLabel()).thenReturn(NEW_LABEL);
        when(view.getType()).thenReturn(NEW_TYPE);

        Map<String, Boolean> validationResult = new HashMap<String, Boolean>();
        validationResult.put(NEW_FIELD_NAME,
                             true);
        when(validationService.evaluateJavaIdentifiers(any(String[].class))).thenReturn(validationResult);

        //notify the presenter about the changes in the UI
        fieldEditor.onNameChange();
        fieldEditor.onLabelChange();
        fieldEditor.onDescriptionChange();
        fieldEditor.onTypeChange();

        verify(showAssetUsagesDisplayer).showAssetPartUsages(anyString(),
                                                             any(),
                                                             anyString(),
                                                             anyString(),
                                                             any(),
                                                             any(),
                                                             any());

        //After the changes has been processed by the presenter the field should have been populated with the new values.
        field = context.getObjectProperty();

        assertEquals(NEW_FIELD_NAME,
                     field.getName());
        assertEquals(NEW_LABEL,
                     AnnotationValueHandler.getStringValue(field,
                                                           Label.class.getName()));
        assertEquals(NEW_DESCRIPTION,
                     AnnotationValueHandler.getStringValue(field,
                                                           Description.class.getName()));
        assertEquals(NEW_TYPE,
                     field.getClassName());

        verify(dataModelerEvent,
               times(4)).fire(any(DataModelerEvent.class));
    }
}
