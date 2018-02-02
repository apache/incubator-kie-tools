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
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationDefinitionRequest;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationDefinitionResponse;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.Mock;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class SearchAnnotationPageTest {

    @GwtMock
    SearchAnnotationPageView view;

    @Mock
    SearchAnnotationPageView.SearchAnnotationHandler searchAnnotationHandler;

    @Mock
    protected DataModelerService modelerService;

    protected CallerMock<DataModelerService> modelerServiceCaller;

    protected Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent = mock(EventSourceMock.class);

    @Mock
    protected KieModule kieModule;

    @Test
    public void testPageLoad() {

        modelerServiceCaller = new CallerMock<DataModelerService>(modelerService);
        SearchAnnotationPage searchPage = new SearchAnnotationPage(view,
                                                                   modelerServiceCaller,
                                                                   wizardPageStatusChangeEvent);

        searchPage.prepareView();

        WizardTestUtil.assertPageComplete(false, searchPage);
    }

    @Test
    public void testSearchAnnotationFound() {

        modelerServiceCaller = new CallerMock<DataModelerService>(modelerService);
        SearchAnnotationPage searchPage = new SearchAnnotationPage(view,
                                                                   modelerServiceCaller,
                                                                   wizardPageStatusChangeEvent);

        searchPage.init(kieModule, ElementType.FIELD);
        searchPage.prepareView();
        searchPage.addSearchAnnotationHandler(searchAnnotationHandler);
        //emulates the user is typing
        searchPage.onSearchClassChanged();

        //the wizard page should be automatically invalidated since the annotation class name to search
        //has changed.
        verify(wizardPageStatusChangeEvent, times(1)).fire(any(WizardPageStatusChangeEvent.class));
        WizardTestUtil.assertPageComplete(false, searchPage);
        assertEquals(CreateAnnotationWizardPage.PageStatus.NOT_VALIDATED, searchPage.getStatus());
        verify(searchAnnotationHandler, times(1)).onSearchClassChanged(); //the handler should also have been invocked.

        //emulate the user is searching the javax.persistence.Entity annotation.
        AnnotationDefinitionRequest request = new AnnotationDefinitionRequest(Entity.class.getName());
        //the response has a definition
        AnnotationDefinitionResponse response = new AnnotationDefinitionResponse(
                DriverUtils.buildAnnotationDefinition(Entity.class));

        when(view.getClassName()).thenReturn(Entity.class.getName());
        when(modelerService.resolveDefinitionRequest(request,
                                                     kieModule)).thenReturn(response);

        //emulate the user click on the search button
        searchPage.onSearchClass();

        //now the page should be completed
        WizardTestUtil.assertPageComplete(true, searchPage);
        verify(wizardPageStatusChangeEvent, times(2)).fire(any(WizardPageStatusChangeEvent.class));
        //the handler should also have been invoked with the expected annotation definition.
        verify(searchAnnotationHandler, times(1)).onAnnotationDefinitionChange(response.getAnnotationDefinition());
    }

    @Test
    public void testSearchAnnotationNotFound() {

        modelerServiceCaller = new CallerMock<DataModelerService>(modelerService);
        SearchAnnotationPage searchPage = new SearchAnnotationPage(view,
                                                                   modelerServiceCaller,
                                                                   wizardPageStatusChangeEvent);

        searchPage.init(kieModule, ElementType.FIELD);
        searchPage.prepareView();
        searchPage.addSearchAnnotationHandler(searchAnnotationHandler);
        //emulates the user is typing
        searchPage.onSearchClassChanged();

        //the wizard page should be automatically invalidated since the annotation class name to search
        //has changed.
        verify(wizardPageStatusChangeEvent, times(1)).fire(any(WizardPageStatusChangeEvent.class));
        WizardTestUtil.assertPageComplete(false, searchPage);
        assertEquals(CreateAnnotationWizardPage.PageStatus.NOT_VALIDATED, searchPage.getStatus());
        verify(searchAnnotationHandler, times(1)).onSearchClassChanged(); //the handler should also have been invocked.

        //emulate the user is searching the javax.persistence.Entity annotation.
        AnnotationDefinitionRequest request = new AnnotationDefinitionRequest(Entity.class.getName());
        //empty response was returned
        AnnotationDefinitionResponse response = new AnnotationDefinitionResponse(null);

        when(view.getClassName()).thenReturn(Entity.class.getName());
        when(modelerService.resolveDefinitionRequest(request,
                                                     kieModule)).thenReturn(response);

        //emulate the user click on the search button
        searchPage.onSearchClass();

        //now the page should be completed
        WizardTestUtil.assertPageComplete(false, searchPage);
        verify(wizardPageStatusChangeEvent, times(2)).fire(any(WizardPageStatusChangeEvent.class));
        //the handler should also have been invoked with the expected annotation definition.
        verify(searchAnnotationHandler, times(1)).onAnnotationDefinitionChange(null);
    }
}
