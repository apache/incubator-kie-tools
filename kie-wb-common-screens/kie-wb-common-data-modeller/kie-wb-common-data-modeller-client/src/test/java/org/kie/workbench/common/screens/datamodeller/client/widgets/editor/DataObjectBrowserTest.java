/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.context.DataModelerWorkbenchContextChangeEvent;
import org.kie.workbench.common.screens.datamodeller.client.widgets.DomainEditorBaseTest;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldDeletedEvent;
import org.kie.workbench.common.screens.datamodeller.validation.DataObjectValidationService;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.DataObjectImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ObjectPropertyImpl;
import org.kie.workbench.common.services.refactoring.client.usages.ShowAssetUsagesDisplayer;
import org.kie.workbench.common.services.refactoring.client.usages.ShowAssetUsagesDisplayerView;
import org.kie.workbench.common.services.refactoring.service.AssetsUsageService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataObjectBrowserTest
        extends DomainEditorBaseTest {

    @Mock
    private DataObjectValidationService dataObjectValidationService;

    @Mock
    DataObjectBrowserView view;

    Event<DataModelerWorkbenchContextChangeEvent> dataModelerWBContextEvent = mock(EventSourceMock.class);

    Event<LockRequiredEvent> lockRequiredEvent = mock(EventSourceMock.class);

    @Mock
    PlaceManager placeManager;

    @Mock
    NewFieldPopupView newFieldPopupView;

    NewFieldPopup newFieldPopup;

    @Mock
    Path dummyPath;

    Event<DataModelerEvent> dataModelerEvent = mock(EventSourceMock.class);

    @Mock
    AssetsUsageService assetsUsageService;

    @Mock
    ShowAssetUsagesDisplayerView assetUsagesDisplayerView;

    @Mock
    TranslationService translationService;

    ShowAssetUsagesDisplayer showAssetUsagesDisplayer;

    protected DataObjectBrowser createBrowser() {

        newFieldPopup = new NewFieldPopup(newFieldPopupView);

        when(assetUsagesDisplayerView.getDefaultMessageContainer()).thenReturn(mock(HTMLElement.class));

        showAssetUsagesDisplayer = spy(new ShowAssetUsagesDisplayer(assetUsagesDisplayerView,
                                                                    translationService,
                                                                    new CallerMock<>(assetsUsageService)));

        DataObjectBrowser objectBrowser = new DataObjectBrowser(handlerRegistry,
                                                                commandBuilder,
                                                                modelerServiceCaller,
                                                                new CallerMock<>(dataObjectValidationService),
                                                                validatorService,
                                                                dataModelerEvent,
                                                                dataModelerWBContextEvent,
                                                                lockRequiredEvent,
                                                                placeManager,
                                                                newFieldPopup,
                                                                view,
                                                                showAssetUsagesDisplayer);

        //emulate the @PostConstruct method invocation.
        objectBrowser.init();

        return objectBrowser;
    }

    @Test
    public void loadContextTest() {

        DataObjectBrowser objectBrowser = createBrowser();
        DataModelerContext context = createContext();

        objectBrowser.setContext(context);

        verify(view,
               times(1)).setReadonly(context.isReadonly());
        verify(view,
               times(1)).setObjectSelectorLabel("TestObject1Label (TestObject1)",
                                                context.getDataObject().getClassName());
    }

    @Test
    public void removePropertyTest() {

        DataObjectBrowser objectBrowser = createBrowser();
        DataModelerContext context = createContext();

        //the dataObject has fields: field1, field2 and field3
        DataObject dataObject = context.getDataObject();
        ObjectProperty objectProperty = dataObject.getProperty("field3");
        int count = dataObject.getProperties().size();

        context.getEditorModelContent().setPath(dummyPath);

        objectBrowser.setContext(context);

        when(assetsUsageService.getAssetPartUsages(any(),
                                                   any(),
                                                   any(),
                                                   any())).thenReturn(new ArrayList<>());

        //field3 is on position 2 by construction.
        objectBrowser.onDeleteProperty(objectProperty,
                                       2);

        verify(showAssetUsagesDisplayer).showAssetPartUsages(anyString(),
                                                             any(),
                                                             any(),
                                                             any(),
                                                             any(),
                                                             any(),
                                                             any());

        //if field3 was removed, then field2 should have been selected.
        verify(view).setSelectedRow(dataObject.getProperty("field2"),
                                    true);
        //an even should have been fired with the notification of the just removed property.
        verify(dataModelerEvent,
               times(1)).fire(any(DataModelerEvent.class));
        verify(view,
               times(1)).setTableHeight(
                DataObjectBrowser.DataObjectBrowserHelper.calculateTableHeight(count));
        verify(view,
               times(2)).setTableHeight(
                DataObjectBrowser.DataObjectBrowserHelper.calculateTableHeight(count - 1));
        //the dataObject should now have one less property.

        assertEquals((count - 1),
                     dataObject.getProperties().size());
    }

    @Test
    public void addValidPropertyAndContinueTest() {
        addValidPropertyTest(true);
    }

    @Test
    public void addValidPropertyAndCloseTest() {
        addValidPropertyTest(false);
    }

    private void addValidPropertyTest(boolean createAndContinue) {

        DataObjectBrowser objectBrowser = createBrowser();
        DataModelerContext context = createContext();
        objectBrowser.setContext(context);

        //the dataObject has fields: field1, field2 and field3
        DataObject dataObject = context.getDataObject();

        //open the new property dialog.
        objectBrowser.onNewProperty();

        //check the new field popup is shown
        verify(newFieldPopupView,
               times(1)).show();

        //emulate the user data entering in the new field popup
        when(newFieldPopupView.getFieldName()).thenReturn("field4");
        when(newFieldPopupView.getSelectedType()).thenReturn("java.lang.String");
        when(newFieldPopupView.getIsMultiple()).thenReturn(false);

        //emulate that the provided field name is correct
        Map<String, Boolean> validationResult = new HashMap<String, Boolean>();
        validationResult.put("field4",
                             true);
        when(validationService.evaluateJavaIdentifiers(new String[]{"field4"})).thenReturn(validationResult);

        //emulate the user pressing the create button in the new field popup
        newFieldPopup.onCreate();

        //the new field popup should have been closed and the new property shoud have been added o the data object.
        ObjectProperty expectedProperty = new ObjectPropertyImpl("field4",
                                                                 "java.lang.String",
                                                                 false);

        if (createAndContinue) {
            verify(newFieldPopupView,
                   times(1)).clear();
        } else {
            verify(newFieldPopupView,
                   times(1)).hide();
        }
        verify(view,
               times(1)).setTableHeight(
                DataObjectBrowser.DataObjectBrowserHelper.calculateTableHeight(3));
        verify(view,
               times(1)).setTableHeight(
                DataObjectBrowser.DataObjectBrowserHelper.calculateTableHeight(4));
        assertEquals(4,
                     dataObject.getProperties().size());
        assertEquals(expectedProperty,
                     dataObject.getProperties().get(3));
    }

    @Test
    public void addInvalidPropertyTest() {

        DataObjectBrowser objectBrowser = createBrowser();
        DataModelerContext context = createContext();
        objectBrowser.setContext(context);

        //the dataObject has fields: field1, field2 and field3
        DataObject dataObject = context.getDataObject();

        //open the new property dialog.
        objectBrowser.onNewProperty();

        //check the new field popup is shown
        verify(newFieldPopupView,
               times(1)).show();

        //emulate the user data entering in the new field popup
        when(newFieldPopupView.getFieldName()).thenReturn("field4");
        when(newFieldPopupView.getSelectedType()).thenReturn("java.lang.String");
        when(newFieldPopupView.getIsMultiple()).thenReturn(false);

        //emulate that the provided field name is NOT correct
        Map<String, Boolean> validationResult = new HashMap<String, Boolean>();
        validationResult.put("field4",
                             false);
        when(validationService.evaluateJavaIdentifiers(new String[]{"field4"})).thenReturn(validationResult);

        //emulate the user pressing the create button in the new field popup
        newFieldPopup.onCreate();

        //the error message should have been set
        verify(newFieldPopupView,
               times(1)).setErrorMessage(anyString());
        verify(view,
               times(1)).setTableHeight(
                DataObjectBrowser.DataObjectBrowserHelper.calculateTableHeight(3));
        //no property should have been added.
        assertEquals(3,
                     dataObject.getProperties().size());
    }

    @Test
    public void onDataObjectFieldDeleted() {
        DataObjectBrowser dataObjectBrowser = spy(createBrowser());
        DataModelerContext context = createContext();
        context.setContextId("contextId");
        dataObjectBrowser.setContext(context);

        DataObject dataObject = mock(DataObject.class);
        dataObjectBrowser.setDataObject(dataObject);

        DataObjectFieldDeletedEvent event = new DataObjectFieldDeletedEvent();
        event.setContextId("contextId");

        Mockito.reset(dataObjectBrowser);

        dataObjectBrowser.onDataObjectFieldDeleted(event);

        verify(dataObjectBrowser,
               times(1)).setDataObject(dataObject);
    }

    @Test
    public void safeObjectPropertyDeleteEmptyValidationMessages() {
        DataObjectBrowser dataObjectBrowser = createBrowser();

        DataObjectImpl dataObject = new DataObjectImpl("test",
                                                       "DataObject");
        dataObjectBrowser.setDataObject(dataObject);

        ObjectProperty objectProperty = new ObjectPropertyImpl("safeField",
                                                               Integer.class.getName(),
                                                               false);

        when(dataObjectValidationService.validateObjectPropertyDeletion(dataObject,
                                                                        objectProperty)).thenReturn(Collections.emptyList());

        dataObjectBrowser.onDeleteProperty(objectProperty,
                                           0);

        verify(view,
               never()).showValidationPopupForDeletion(anyListOf(ValidationMessage.class),
                                                       any(Command.class),
                                                       any(Command.class));
    }

    @Test
    public void safeObjectPropertyDeleteHasValidationMessages() {
        DataObjectBrowser dataObjectBrowser = createBrowser();

        DataObjectImpl dataObject = new DataObjectImpl("test",
                                                       "DataObject");
        dataObjectBrowser.setDataObject(dataObject);

        ObjectProperty objectProperty = new ObjectPropertyImpl("safeField",
                                                               Integer.class.getName(),
                                                               false);

        List<ValidationMessage> validationMessages = Arrays.asList(new ValidationMessage());
        when(dataObjectValidationService.validateObjectPropertyDeletion(dataObject,
                                                                        objectProperty)).thenReturn(validationMessages);

        dataObjectBrowser.onDeleteProperty(objectProperty,
                                           0);

        verify(view,
               times(1)).showValidationPopupForDeletion(anyListOf(ValidationMessage.class),
                                                        any(Command.class),
                                                        any(Command.class)
        );
    }
}
