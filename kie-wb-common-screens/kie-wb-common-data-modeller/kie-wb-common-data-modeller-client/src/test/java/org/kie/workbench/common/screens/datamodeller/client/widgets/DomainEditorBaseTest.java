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

package org.kie.workbench.common.screens.datamodeller.client.widgets;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelChangeNotifier;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommandBuilder;
import org.kie.workbench.common.screens.datamodeller.client.handlers.DomainHandlerRegistry;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.kie.workbench.common.screens.datamodeller.client.widgets.DataModelerEditorsTestHelper.*;
import static org.mockito.Mockito.*;

public class DomainEditorBaseTest {

    public enum PopupActions { YES, NO, CANCEL };

    @Mock
    protected DomainHandlerRegistry handlerRegistry;

    protected Event<DataModelerEvent> dataModelerEvent = mock( EventSourceMock.class );

    protected DataModelChangeNotifier changeNotifier;

    protected DataModelCommandBuilder commandBuilder;

    @Mock
    protected DataModelerService modelerService;

    protected CallerMock<DataModelerService> modelerServiceCaller;

    @Mock
    protected ValidationService validationService;

    protected CallerMock<ValidationService> validationServiceCallerMock;

    protected DataModelerContext context;

    protected ValidatorService validatorService;

    @Before
    public void initTest() {

        changeNotifier = new DataModelChangeNotifier( dataModelerEvent );
        commandBuilder = new DataModelCommandBuilder( changeNotifier );
        modelerServiceCaller = new CallerMock<DataModelerService>( modelerService );
        validationServiceCallerMock = new CallerMock<ValidationService>( validationService );
        validatorService = new ValidatorService( validationServiceCallerMock );
        context = createContext();
    }

    protected DataModelerContext createContext() {
        context = DataModelerEditorsTestHelper.createTestContext();
        DataObject dataObject = createTestObject1();
        context.setDataObject( dataObject );
        return context;
    }

}
