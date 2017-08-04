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
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditorProvider;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationDefinitionRequest;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationDefinitionResponse;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.mockito.Mock;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class CreateAnnotationWizardTest {

    protected SearchAnnotationPage searchPage;

    @GwtMock
    protected SearchAnnotationPageView searchView;

    @Mock
    protected SearchAnnotationPageView.SearchAnnotationHandler searchAnnotationHandler;

    @GwtMock
    protected WizardView view;

    protected CreateAnnotationWizard createAnnotationWizard;

    @Mock
    protected DataModelerService modelerService;

    protected CallerMock<DataModelerService> modelerServiceCaller;

    protected Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent = mock(EventSourceMock.class);

    @Mock
    protected KieProject kieProject;

    @Mock
    protected ValuePairEditorProvider valuePairEditorProvider;

    @GwtMock
    protected ValuePairEditorPageView editorView;

    protected ValuePairEditorPage editorPage;

    @Mock
    protected ValuePairEditor valuePairEditor;

    @Mock
    protected SyncBeanManager iocManager;

    @Mock
    protected SyncBeanDef<ValuePairEditorPage> beanDef;

    protected Annotation createdAnnotation;

    @Before
    public void init() {

        modelerServiceCaller = new CallerMock<>(modelerService);
        searchPage = new SearchAnnotationPage(searchView,
                                              modelerServiceCaller,
                                              wizardPageStatusChangeEvent);

        editorPage = new ValuePairEditorPage(editorView,
                                             valuePairEditorProvider,
                                             modelerServiceCaller,
                                             wizardPageStatusChangeEvent);

        createAnnotationWizard = new CreateAnnotationWizardExtended(searchPage,
                                                                    iocManager,
                                                                    view);

        createAnnotationWizard.init(kieProject,
                                    ElementType.TYPE);
        createAnnotationWizard.onCloseCallback(result -> {
            //collect the created annotation when the wizard is finished
            createdAnnotation = result;
        });
        createAnnotationWizard.start();
    }

    @Test
    public void testAnnotationCreated() {

        //emulate the user is searching the javax.persistence.Entity annotation.
        AnnotationDefinitionRequest request = new AnnotationDefinitionRequest(Entity.class.getName());
        //the response has a definition
        AnnotationDefinitionResponse response = new AnnotationDefinitionResponse(
                DriverUtils.buildAnnotationDefinition(Entity.class));

        when(searchView.getClassName()).thenReturn(Entity.class.getName());
        when(modelerService.resolveDefinitionRequest(request,
                                                     kieProject)).thenReturn(response);

        //when the search is performed the ValuePairEditor pages will be automatically created
        //so we also emulate the corresponding ValuePairEditors for the given value pairs.
        AnnotationValuePairDefinition valuePairDefinition = response.getAnnotationDefinition().getValuePair("name");
        when(valuePairEditorProvider.getValuePairEditor(valuePairDefinition)).thenReturn(valuePairEditor);

        //the wizard pages corresponding to the value pairs are also created dynamically, se we need also emulate
        //the ValuePairEditorPage instantiation
        when(iocManager.lookupBean(ValuePairEditorPage.class)).thenReturn(beanDef);
        when(beanDef.getInstance()).thenReturn(editorPage);

        //emulate the user click on the search button
        searchPage.onSearchClass();

        //the page should have been completed, since the modelerService returned the annotation definition as expected
        WizardTestUtil.assertPageComplete(true,
                                          searchPage);

        //now emulate the parameter completion in the value pair page.
        //emulate a change in the internal ValuePairEditor with a valid value.
        when(editorView.getValuePairEditor()).thenReturn(valuePairEditor);
        when(valuePairEditor.getValue()).thenReturn("TheEntityName");
        when(valuePairEditor.isValid()).thenReturn(true);

        editorPage.onValueChange();

        //the value pair editor page shoud have been completed.
        WizardTestUtil.assertPageComplete(true,
                                          searchPage);

        //emulates the user clicking on the finish button.
        createAnnotationWizard.complete();

        //finally if the Wizard has been completed successfuly an annotation should be created.
        Annotation expectedAnnotation = new AnnotationImpl(DriverUtils.buildAnnotationDefinition(Entity.class));
        expectedAnnotation.setValue("name",
                                    "TheEntityName");

        assertEquals(expectedAnnotation,
                     createdAnnotation);
    }

    public static class CreateAnnotationWizardExtended extends CreateAnnotationWizard {

        public CreateAnnotationWizardExtended(SearchAnnotationPage searchAnnotationPage,
                                              SyncBeanManager iocManager,
                                              WizardView view
        ) {
            super(searchAnnotationPage,
                  iocManager);
            super.view = view;
            //emulates the execution for the @PostConstruct annotated method
            init();
        }
    }
}
