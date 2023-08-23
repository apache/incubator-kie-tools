/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.types.imported;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.editors.types.DataObject;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ImportDataObjectModalTest {

    @Mock
    private ImportDataObjectModal.View view;

    @Mock
    private DMNClientServicesProxy client;

    private ImportDataObjectModal modal;

    @Before
    public void setup() {
        modal = spy(new ImportDataObjectModal(view, client));
    }

    @Test
    public void testShow() {

        final Consumer consumer = mock(Consumer.class);
        final ServiceCallback serviceCallback = mock(ServiceCallback.class);
        final List<String> existingDataTypes = mock(List.class);
        doReturn(serviceCallback).when(modal).wrap(consumer);
        doReturn(consumer).when(modal).getConsumer();
        doNothing().when(modal).superShow();

        modal.show(existingDataTypes);

        verify(modal).superShow();
        verify(client).loadDataObjects(serviceCallback);
        verify(view).hideDataTypeWithSameNameWarning();
    }

    @Test
    public void testWrap() {

        final Consumer consumer = mock(Consumer.class);
        final ServiceCallback service = modal.wrap(consumer);
        final List<DataObject> items = mock(List.class);
        service.onSuccess(items);

        verify(consumer).accept(items);
    }

    @Test
    public void testGetConsumer() {

        doNothing().when(modal).superShow();
        final Consumer<List<DataObject>> consumer = modal.getConsumer();
        final List<DataObject> items = mock(List.class);
        when(items.isEmpty()).thenReturn(false);
        consumer.accept(items);

        verify(view).clear();
        verify(view).addItems(items);
        verify(modal).superShow();
    }

    @Test
    public void testSetup() {

        final Consumer<List<DataObject>> consumer = mock(Consumer.class);
        final Consumer onDataObjectSelectionChanged = mock(Consumer.class);
        doNothing().when(modal).callSuperSetup();
        doReturn(onDataObjectSelectionChanged).when(modal).getOnDataObjectSelectionChanged();

        modal.setup(consumer);

        verify(modal).callSuperSetup();
        assertEquals(consumer, modal.getDataObjectsConsumer());
    }

    @Test
    public void testHide() {

        final List<DataObject> importedObjects = mock(List.class);
        final Consumer<List<DataObject>> consumer = mock(Consumer.class);

        doNothing().when(modal).callSuperSetup();
        doNothing().when(modal).superHide();

        modal.setup(consumer);

        modal.hide(importedObjects);

        verify(consumer).accept(importedObjects);
        verify(modal).superHide();
    }

    @Test
    public void testHideWhenThereIsNotConsumer() {

        final List<DataObject> importedObjects = mock(List.class);

        doNothing().when(modal).callSuperSetup();
        doNothing().when(modal).superHide();

        modal.hide(importedObjects);

        verify(modal).superHide();
    }

    @Test
    public void testOnDataObjectSelectionChangedAndHasDuplicatedName() {

        final String name1 = "name1";
        final String name2 = "name2";
        final String name3 = "name3";
        final List<String> existingDataTypes = Arrays.asList(name1, name2, name3);
        final DataObject do1 = createDataObject(name1);
        final DataObject do2 = createDataObject(name2);
        final DataObject do3 = createDataObject(name3);
        final List<DataObject> dataObjects = Arrays.asList(do1, do2, do3);
        doReturn(existingDataTypes).when(modal).getExistingDataTypes();

        modal.onDataObjectSelectionChanged(dataObjects);

        verify(view).showDataTypeWithSameNameWarning();
        verify(view, never()).hideDataTypeWithSameNameWarning();
    }

    @Test
    public void testOnDataObjectSelectionChangedAndDoesntHasDuplicatedName() {

        final String name1 = "name1";
        final String name2 = "name2";
        final String name3 = "name3";
        final List<String> existingDataTypes = Arrays.asList("unique1", "unique2", "unique3");
        final DataObject do1 = createDataObject(name1);
        final DataObject do2 = createDataObject(name2);
        final DataObject do3 = createDataObject(name3);
        final List<DataObject> dataObjects = Arrays.asList(do1, do2, do3);
        doReturn(existingDataTypes).when(modal).getExistingDataTypes();

        modal.onDataObjectSelectionChanged(dataObjects);

        verify(view, never()).showDataTypeWithSameNameWarning();
        verify(view).hideDataTypeWithSameNameWarning();
    }

    private DataObject createDataObject(final String name) {

        final DataObject dataObject = mock(DataObject.class);
        when(dataObject.getClassNameWithoutPackage()).thenReturn(name);
        return dataObject;
    }
}