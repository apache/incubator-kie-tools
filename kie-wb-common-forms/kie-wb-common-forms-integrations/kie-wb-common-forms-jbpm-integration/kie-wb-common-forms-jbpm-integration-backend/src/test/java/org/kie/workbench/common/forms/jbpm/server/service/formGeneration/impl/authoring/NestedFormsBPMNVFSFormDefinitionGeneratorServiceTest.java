/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.authoring;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.model.Client;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.model.Expense;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.model.Line;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.impl.DataObjectImpl;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NestedFormsBPMNVFSFormDefinitionGeneratorServiceTest extends BPMNVFSFormDefinitionGeneratorServiceTest {

    @Override
    public void setup() {
        super.setup();

        when(dataObjectFinderService.getDataObject(anyString(),
                                                   any())).then(new Answer<DataObject>() {
            @Override
            public DataObject answer(InvocationOnMock invocationOnMock) throws Throwable {
                String className = invocationOnMock.getArguments()[0].toString();

                if (Expense.class.getName().equals(className)) {
                    return getExpenseDataObject();
                }

                if (Client.class.getName().equals(className)) {
                    return getClientDataObject();
                }

                if (Line.class.getName().equals(className)) {
                    return getLineDataObject();
                }
                return null;
            }
        });
    }

    @Test
    public void testCreateNewProcessFormNestedForms() {

        when(ioService.exists(any())).thenReturn(false);

        launchNestedFormsTest();

        verify(ioService,
               times(3)).write(any(),
                               anyString(),
                               any());
    }

    protected DataObject getExpenseDataObject() {
        DataObject expense = new DataObjectImpl(Expense.class.getPackage().toString(),
                                                Expense.class.getSimpleName());

        expense.addProperty("id",
                            Long.class.getName(),
                            false,
                            null);
        expense.addProperty("date",
                            Date.class.getName(),
                            false,
                            null);
        expense.addProperty("client",
                            Client.class.getName(),
                            false,
                            null);
        expense.addProperty("lines",
                            Line.class.getName(),
                            true,
                            List.class.getName());
        return expense;
    }

    protected DataObject getLineDataObject() {
        DataObject expense = new DataObjectImpl(Line.class.getPackage().toString(),
                                                Line.class.getSimpleName());

        expense.addProperty("id",
                            Long.class.getName(),
                            false,
                            null);
        expense.addProperty("date",
                            Date.class.getName(),
                            false,
                            null);
        expense.addProperty("product",
                            String.class.getName(),
                            false,
                            null);
        expense.addProperty("price",
                            Double.class.getName(),
                            false,
                            null);
        return expense;
    }

    protected DataObject getClientDataObject() {
        DataObject client = new DataObjectImpl(Client.class.getPackage().toString(),
                                               Client.class.getSimpleName());

        client.addProperty("id",
                           Long.class.getName(),
                           false,
                           null);
        client.addProperty("name",
                           String.class.getName(),
                           false,
                           null);
        client.addProperty("lastName",
                           String.class.getName(),
                           false,
                           null);
        return client;
    }
}
