/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.backend.server.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.enterprise.inject.Instance;

import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.validation.ObjectPropertyDeleteValidator;
import org.kie.workbench.common.services.datamodeller.core.impl.DataObjectImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ObjectPropertyImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataObjectValidationServiceImplTest {

    @Mock
    private Instance<ObjectPropertyDeleteValidator> objectPropertyDeleteValidatorInstance;

    private DataObjectValidationServiceImpl validationService;

    private ValidationMessage message1 = new ValidationMessage();

    private ValidationMessage message2 = new ValidationMessage();

    @Before
    public void setUp() {
        List<ObjectPropertyDeleteValidator> validators = new ArrayList<>(2);

        ObjectPropertyDeleteValidator validator1 = (dataObject, objectProperty) -> Arrays.asList(message1);
        ObjectPropertyDeleteValidator validator2 = (dataObject, objectProperty) -> Arrays.asList(message2);

        validators.add(validator1);
        validators.add(validator2);

        when(objectPropertyDeleteValidatorInstance.iterator()).thenReturn(validators.iterator());

        validationService = new DataObjectValidationServiceImpl(objectPropertyDeleteValidatorInstance);
    }

    @Test
    public void validateObjectPropertyDeletion() {
        Collection<ValidationMessage> result = validationService.validateObjectPropertyDeletion(new DataObjectImpl(),
                                                                                                new ObjectPropertyImpl());

        assertEquals(2,
                     result.size());
        assertTrue(result.contains(message1));
        assertTrue(result.contains(message2));
    }
}
