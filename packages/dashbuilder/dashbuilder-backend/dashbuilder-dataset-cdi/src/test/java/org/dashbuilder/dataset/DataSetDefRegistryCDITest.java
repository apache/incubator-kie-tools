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
package org.dashbuilder.dataset;

import javax.enterprise.event.Event;

import org.dashbuilder.dataprovider.DataSetProviderRegistryCDI;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.dashbuilder.dataset.events.DataSetDefModifiedEvent;
import org.dashbuilder.dataset.events.DataSetDefRegisteredEvent;
import org.dashbuilder.dataset.events.DataSetDefRemovedEvent;
import org.dashbuilder.dataset.events.DataSetStaleEvent;
import org.dashbuilder.exception.ExceptionManager;
import org.dashbuilder.project.storage.impl.ProjectStorageServicesImpl;
import org.dashbuilder.scheduler.SchedulerCDI;
import org.dashbuilder.test.BaseCDITest;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(Arquillian.class)
@Ignore("see https://issues.jboss.org/browse/RHPAM-832")
public class DataSetDefRegistryCDITest extends BaseCDITest {

    @Mock
    DataSetProviderRegistryCDI dataSetProviderRegistry;

    @Mock
    SchedulerCDI scheduler;

    @Mock
    ExceptionManager exceptionManager;

    @Mock
    Event<DataSetDefModifiedEvent> dataSetDefModifiedEvent;

    @Mock
    Event<DataSetDefRegisteredEvent> dataSetDefRegisteredEvent;

    @Mock
    Event<DataSetDefRemovedEvent> dataSetDefRemovedEvent;

    @Mock
    Event<DataSetStaleEvent> dataSetStaleEvent;

    DataSetDefRegistryCDI dataSetDefRegistry;

    public DataSetDef dataSetDef = DataSetDefFactory
            .newCSVDataSetDef()
            .uuid("testDset")
            .buildDef();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        dataSetDefRegistry = spy(new DataSetDefRegistryCDI(
                10485760,
                new ProjectStorageServicesImpl(),
                dataSetProviderRegistry,
                scheduler,
                exceptionManager,
                dataSetDefModifiedEvent,
                dataSetDefRegisteredEvent,
                dataSetDefRemovedEvent,
                dataSetStaleEvent));

        dataSetDefRegistry.init();

    }

    @Test
    public void testRegistryDataSetDef() throws Exception {
        dataSetDefRegistry.registerDataSetDef(dataSetDef);

        verify(dataSetDefRegisteredEvent).fire(any(DataSetDefRegisteredEvent.class));
    }

    @Test
    public void testDeleteDataSetDef() throws Exception {
        dataSetDefRegistry.registerDataSetDef(dataSetDef);
        dataSetDefRegistry.removeDataSetDef(dataSetDef.getUUID());

        verify(dataSetDefRemovedEvent).fire(any(DataSetDefRemovedEvent.class));
    }
}
