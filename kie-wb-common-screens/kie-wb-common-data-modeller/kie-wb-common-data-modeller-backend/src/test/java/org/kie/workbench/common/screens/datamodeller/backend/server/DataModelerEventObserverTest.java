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

package org.kie.workbench.common.screens.datamodeller.backend.server;

import javax.persistence.Entity;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectCreatedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectDeletedEvent;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistableDataObject;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceUnitModel;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorService;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.DataObjectImpl;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataModelerEventObserverTest {

    @Mock
    PersistenceDescriptorService descriptorService;

    @Mock
    IOService ioService;

    @Mock
    public Module module;

    @Mock
    private Path descriptorPath;

    private static final String DESCRIPTOR_PATH = "file://dummy-repo/dummy-project/src/main/resources/META-INF/persistence.xml";

    private DataModelerEventObserver eventObserver;
    private PersistenceDescriptorModel descriptorModel;
    private DataObject dataObject;

    @Before
    public void init() {

        eventObserver = createObserver();
        descriptorModel = createModel();
        dataObject = new DataObjectImpl("package1", "PersistableObject");
        dataObject.addAnnotation(new AnnotationImpl(DriverUtils.buildAnnotationDefinition(Entity.class)));

        when(descriptorPath.toURI()).thenReturn(DESCRIPTOR_PATH);
        when(descriptorService.calculatePersistenceDescriptorPath(any(Module.class))).thenReturn(descriptorPath);
        when(descriptorService.load(descriptorPath)).thenReturn(descriptorModel);
        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(true);
    }

    @Test
    public void onPersistableDataObjectCreatedTest() {

        DataObjectCreatedEvent createdEvent = new DataObjectCreatedEvent(module, dataObject);
        eventObserver.onDataObjectCreated(createdEvent);

        verify(descriptorService, times(1)).save(eq(descriptorPath), eq(descriptorModel), any(Metadata.class), anyString());
        assertTrue(descriptorModel.getPersistenceUnit().getClasses().contains(new PersistableDataObject(dataObject.getClassName())));
    }

    @Test
    public void onPersistableDataObjectDeletedTest() {

        DataObjectDeletedEvent deletedEvent = new DataObjectDeletedEvent(module, dataObject);
        descriptorModel.getPersistenceUnit().getClasses().add(new PersistableDataObject(deletedEvent.getCurrentDataObject().getClassName()));
        eventObserver.onDataObjectDeleted(deletedEvent);

        verify(descriptorService, times(1)).save(eq(descriptorPath), eq(descriptorModel), any(Metadata.class), anyString());
        assertFalse(descriptorModel.getPersistenceUnit().getClasses().contains(new PersistableDataObject(dataObject.getClassName())));
    }

    @Test
    public void onNonPersistableDataObjectCreatedTest() {

        descriptorModel.getPersistenceUnit().getClasses().add(new PersistableDataObject("package1.PersistableObject"));

        DataObjectCreatedEvent createdEvent = new DataObjectCreatedEvent(module, dataObject);
        eventObserver.onDataObjectCreated(createdEvent);

        verify(descriptorService, times(0)).save(eq(descriptorPath), eq(descriptorModel), any(Metadata.class), anyString());
        assertEquals(1, descriptorModel.getPersistenceUnit().getClasses().size());
    }

    @Test
    public void onNonPersistableDataObjectDeletedTest() {

        DataObject dataObject = new DataObjectImpl("package1", "NonPersistableObject");
        DataObjectCreatedEvent createdEvent = new DataObjectCreatedEvent(module, dataObject);
        eventObserver.onDataObjectCreated(createdEvent);

        descriptorModel.getPersistenceUnit().getClasses().add(new PersistableDataObject(createdEvent.getCurrentDataObject().getClassName()));
        verify(descriptorService, times(0)).save(eq(descriptorPath), eq(descriptorModel), any(Metadata.class), anyString());
        assertEquals(1, descriptorModel.getPersistenceUnit().getClasses().size());
    }

    private PersistenceDescriptorModel createModel() {
        PersistenceDescriptorModel descriptorModel = new PersistenceDescriptorModel();
        descriptorModel.setPersistenceUnit(new PersistenceUnitModel());
        return descriptorModel;
    }

    private DataModelerEventObserver createObserver() {
        return new DataModelerEventObserver(descriptorService, ioService);
    }
}
