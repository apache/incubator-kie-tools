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

package org.kie.workbench.common.dmn.client.editors.types.listview.confirmation;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeManager_Structure;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeConfirmationTest {

    private static final String STRUCTURE = "Structure";

    @Mock
    private DataTypeStore dataTypeStore;

    @Mock
    private ItemDefinitionStore itemDefinitionStore;

    @Mock
    private EventSourceMock<FlashMessage> flashMessageEvent;

    @Mock
    private DataTypeHasFieldsWarningMessage dataTypeHasFieldsWarningMessage;

    @Mock
    private ReferencedDataTypeWarningMessage referencedDataTypeWarningMessage;

    @Mock
    private TranslationService translationService;

    private DataTypeConfirmation confirmation;

    @Before
    public void setup() {
        confirmation = new DataTypeConfirmation(dataTypeStore, itemDefinitionStore, flashMessageEvent, dataTypeHasFieldsWarningMessage, referencedDataTypeWarningMessage, translationService);

        when(translationService.format(DataTypeManager_Structure)).thenReturn(STRUCTURE);
    }

    @Test
    public void testIfDataTypeDoesNotHaveLostSubDataTypesWhenSuccessCallbackIsExecuted() {

        final DataType dataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final Command onSuccess = mock(Command.class);
        final Command onError = mock(Command.class);
        final String uuid = "uuid";

        when(dataType.getUUID()).thenReturn(uuid);
        when(dataType.getType()).thenReturn(STRUCTURE);
        when(itemDefinitionStore.get(uuid)).thenReturn(itemDefinition);

        confirmation.ifDataTypeDoesNotHaveLostSubDataTypes(dataType, onSuccess, onError);

        verify(onSuccess).execute();
    }

    @Test
    public void testIfDataTypeDoesNotHaveLostSubDataTypesWhenItemDefinitionItemComponentIsEmpty() {

        final DataType dataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final List<ItemDefinition> itemComponent = singletonList(mock(ItemDefinition.class));
        final Command onSuccess = mock(Command.class);
        final Command onError = mock(Command.class);
        final String uuid = "uuid";

        when(dataType.getUUID()).thenReturn(uuid);
        when(dataType.getType()).thenReturn(STRUCTURE);
        when(itemDefinitionStore.get(uuid)).thenReturn(itemDefinition);
        when(itemDefinition.getItemComponent()).thenReturn(itemComponent);

        confirmation.ifDataTypeDoesNotHaveLostSubDataTypes(dataType, onSuccess, onError);

        verify(onSuccess).execute();
    }

    @Test
    public void testIfDataTypeDoesNotHaveLostSubDataTypesWhenItemDefinitionItemComponentIsNotEmpty() {

        final DataType dataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final List<ItemDefinition> itemComponent = singletonList(mock(ItemDefinition.class));
        final Command onSuccess = mock(Command.class);
        final Command onError = mock(Command.class);
        final FlashMessage warningMessage = mock(FlashMessage.class);
        final String uuid = "uuid";

        when(dataType.getUUID()).thenReturn(uuid);
        when(dataType.getType()).thenReturn("tCity");
        when(itemDefinitionStore.get(uuid)).thenReturn(itemDefinition);
        when(itemDefinition.getItemComponent()).thenReturn(itemComponent);
        when(dataTypeHasFieldsWarningMessage.getFlashMessage(dataType, onSuccess, onError)).thenReturn(warningMessage);

        confirmation.ifDataTypeDoesNotHaveLostSubDataTypes(dataType, onSuccess, onError);

        verify(onSuccess, never()).execute();
        verify(flashMessageEvent).fire(warningMessage);
    }

    @Test
    public void testIfIsNotReferencedDataTypeWhenCallbackIsExecuted() {

        final DataType dataType = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final List<DataType> allDataTypes = asList(dataType1, dataType2);
        final Command callback = mock(Command.class);
        final String tCity = "tCity";
        final String tPerson = "tPerson";
        final String tDocument = "tDocument";

        when(dataType.getName()).thenReturn(tCity);
        when(dataType1.getType()).thenReturn(tDocument);
        when(dataType2.getType()).thenReturn(tPerson);

        when(dataTypeStore.all()).thenReturn(allDataTypes);

        confirmation.ifIsNotReferencedDataType(dataType, callback);

        verify(callback).execute();
    }

    @Test
    public void testIfIsNotReferencedDataTypeWhenCallbackIsNotExecuted() {

        final DataType dataType = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final List<DataType> allDataTypes = asList(dataType1, dataType2);
        final FlashMessage warningMessage = mock(FlashMessage.class);
        final Command onSuccess = mock(Command.class);
        final String tCity = "tCity";
        final String tPerson = "tPerson";

        when(dataType.getName()).thenReturn(tCity);
        when(dataType1.getType()).thenReturn(tCity);
        when(dataType2.getType()).thenReturn(tPerson);
        when(referencedDataTypeWarningMessage.getFlashMessage(eq(dataType), eq(onSuccess), any())).thenReturn(warningMessage);

        when(dataTypeStore.all()).thenReturn(allDataTypes);

        confirmation.ifIsNotReferencedDataType(dataType, onSuccess);

        verify(onSuccess, never()).execute();
        verify(flashMessageEvent).fire(warningMessage);
    }
}
