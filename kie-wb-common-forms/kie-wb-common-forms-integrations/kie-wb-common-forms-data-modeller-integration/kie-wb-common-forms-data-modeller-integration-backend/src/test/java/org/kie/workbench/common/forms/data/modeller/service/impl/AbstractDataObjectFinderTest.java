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

package org.kie.workbench.common.forms.data.modeller.service.impl;

import java.util.Date;

import org.junit.Before;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationDefinitionImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.DataModelImpl;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public abstract class AbstractDataObjectFinderTest extends AbstractDataObjectTest {

    public static final String PERSISTENCE_ID_PROPERTY = "id";
    public static final String NAME_PROPERTY = "name";
    public static final String LAST_NAME_PROPERTY = "lastName";
    public static final String AGE_PROPERTY = "age";
    public static final String BIRTHDATE_PROPERTY = "birthdate";
    public static final String MARRIED_PROPERTY = "married";

    public static final String PACKAGE = "org.kie.workbench.common.forms.test";
    public static final String DATA_OBJECT_NAME = "Person";

    public static final String TYPE_NAME = PACKAGE + "." + DATA_OBJECT_NAME;

    public static final int DATA_OBJECT_VALID_FIELDS = 5;

    @Mock
    protected KieProjectService projectService;

    @Mock
    protected DataModelerService dataModelerService;

    @Mock
    protected Path path;

    protected DataModel dataModel = new DataModelImpl();

    protected DataObject dataObject;

    protected DataObjectFinderServiceImpl service;

    @Before
    public void init() {

        dataObject = dataModel.addDataObject(PACKAGE,
                                             DATA_OBJECT_NAME);

        addProperty(dataObject,
                    DataModellerFieldGenerator.SERIAL_VERSION_UID,
                    Long.class.getName(),
                    false,
                    false);

        ObjectProperty property = addProperty(dataObject,
                                              PERSISTENCE_ID_PROPERTY,
                                              Long.class.getName(),
                                              false,
                                              false);
        property.addAnnotation(new AnnotationImpl(new AnnotationDefinitionImpl(DataModellerFieldGenerator.PERSISTENCE_ANNOTATION)));

        addProperty(dataObject,
                    NAME_PROPERTY,
                    String.class.getName(),
                    false,
                    true);
        addProperty(dataObject,
                    LAST_NAME_PROPERTY,
                    String.class.getName(),
                    false,
                    true);
        addProperty(dataObject,
                    AGE_PROPERTY,
                    Integer.class.getName(),
                    false,
                    true);
        addProperty(dataObject,
                    BIRTHDATE_PROPERTY,
                    Date.class.getName(),
                    false,
                    true);
        addProperty(dataObject,
                    MARRIED_PROPERTY,
                    Boolean.class.getName(),
                    false,
                    true);

        when(dataModelerService.loadModel(any())).thenReturn(dataModel);

        service = new DataObjectFinderServiceImpl(projectService,
                                                  dataModelerService);
    }
}
