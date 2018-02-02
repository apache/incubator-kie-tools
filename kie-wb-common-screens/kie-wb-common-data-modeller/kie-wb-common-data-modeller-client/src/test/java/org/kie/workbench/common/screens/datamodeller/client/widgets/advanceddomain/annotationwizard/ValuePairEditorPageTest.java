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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationwizard;

import javax.enterprise.event.Event;
import javax.persistence.Entity;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditorProvider;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.Mock;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ValuePairEditorPageTest {

    @GwtMock
    ValuePairEditorPageView view;

    @Mock
    ValuePairEditorProvider valuePairEditorProvider;

    @Mock
    ValuePairEditor valuePairEditor;

    @Mock
    protected DataModelerService modelerService;

    protected CallerMock<DataModelerService> modelerServiceCaller;

    protected Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent = mock(EventSourceMock.class);

    @Mock
    protected KieModule kieModule;

    @Test
    public void testPageLoad() {

        modelerServiceCaller = new CallerMock<DataModelerService>(modelerService);
        ValuePairEditorPage editorPage = new ValuePairEditorPage(view,
                                                                 valuePairEditorProvider,
                                                                 modelerServiceCaller,
                                                                 wizardPageStatusChangeEvent);

        editorPage.prepareView();
        WizardTestUtil.assertPageComplete(false, editorPage);
    }

    @Test
    public void testPageInitialization() {

        modelerServiceCaller = new CallerMock<DataModelerService>(modelerService);
        ValuePairEditorPage editorPage = new ValuePairEditorPage(view,
                                                                 valuePairEditorProvider,
                                                                 modelerServiceCaller,
                                                                 wizardPageStatusChangeEvent);

        editorPage.prepareView();

        AnnotationDefinition annotationDefinition = DriverUtils.buildAnnotationDefinition(Entity.class);
        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair("name");

        when(valuePairEditorProvider.getValuePairEditor(valuePairDefinition)).thenReturn(valuePairEditor);

        editorPage.init(annotationDefinition, valuePairDefinition, ElementType.FIELD,
                        kieModule);

        //the view should be properly initialized with the corresponding editor.
        verify(view, times(1)).setValuePairEditor(valuePairEditor);
        WizardTestUtil.assertPageComplete(true, editorPage); //the "name" value pair is not mandatory, so the page is completed.
    }

    @Test
    public void testValidValueChange() {

        modelerServiceCaller = new CallerMock<DataModelerService>(modelerService);
        ValuePairEditorPage editorPage = new ValuePairEditorPage(view,
                                                                 valuePairEditorProvider,
                                                                 modelerServiceCaller,
                                                                 wizardPageStatusChangeEvent);

        editorPage.prepareView();

        AnnotationDefinition annotationDefinition = DriverUtils.buildAnnotationDefinition(Entity.class);
        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair("name");

        when(valuePairEditorProvider.getValuePairEditor(valuePairDefinition)).thenReturn(valuePairEditor);

        editorPage.init(annotationDefinition, valuePairDefinition, ElementType.FIELD,
                        kieModule);

        //emulate a change in the internal ValuePairEditor with a valid value.
        when(view.getValuePairEditor()).thenReturn(valuePairEditor);
        when(valuePairEditor.getValue()).thenReturn("TheEntityName");
        when(valuePairEditor.isValid()).thenReturn(true);

        editorPage.onValueChange();

        //the view should be properly initialized with the corresponding editor.
        verify(view, times(1)).setValuePairEditor(valuePairEditor);
        verify(valuePairEditor, times(1)).getValue();
        verify(valuePairEditor, times(1)).isValid();
        assertEquals("TheEntityName", editorPage.getCurrentValue());
        WizardTestUtil.assertPageComplete(true, editorPage);
    }

    @Test
    public void testInvalidValueChange() {

        modelerServiceCaller = new CallerMock<DataModelerService>(modelerService);
        ValuePairEditorPage editorPage = new ValuePairEditorPage(view,
                                                                 valuePairEditorProvider,
                                                                 modelerServiceCaller,
                                                                 wizardPageStatusChangeEvent);

        editorPage.prepareView();

        AnnotationDefinition annotationDefinition = DriverUtils.buildAnnotationDefinition(Entity.class);
        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair("name");

        when(valuePairEditorProvider.getValuePairEditor(valuePairDefinition)).thenReturn(valuePairEditor);

        editorPage.init(annotationDefinition, valuePairDefinition, ElementType.FIELD,
                        kieModule);

        //emulate a change in the internal ValuePairEditor with a valid value.
        when(view.getValuePairEditor()).thenReturn(valuePairEditor);
        when(valuePairEditor.getValue()).thenReturn(null);
        when(valuePairEditor.isValid()).thenReturn(false);

        editorPage.onValueChange();

        //the view should be properly initialized with the corresponding editor.
        verify(view, times(1)).setValuePairEditor(valuePairEditor);
        verify(valuePairEditor, times(1)).getValue();
        verify(valuePairEditor, times(1)).isValid();
        WizardTestUtil.assertPageComplete(false, editorPage);
    }
}
