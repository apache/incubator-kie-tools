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
import java.util.Collection;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.datamodeller.validation.ObjectPropertyDeleteValidator;
import org.kie.workbench.common.screens.datamodeller.validation.DataObjectValidationService;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;

@Service
@ApplicationScoped
public class DataObjectValidationServiceImpl implements DataObjectValidationService {

    private Collection<ObjectPropertyDeleteValidator> objectPropertyDeleteValidators = new ArrayList<>();

    public DataObjectValidationServiceImpl() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public DataObjectValidationServiceImpl(final Instance<ObjectPropertyDeleteValidator> objectPropertyDeleteValidatorInstance) {
        objectPropertyDeleteValidatorInstance.iterator().forEachRemaining(objectPropertyDeleteValidators::add);
    }

    public Collection<ValidationMessage> validateObjectPropertyDeletion(final DataObject dataObject,
                                                                        final ObjectProperty objectProperty) {
        return objectPropertyDeleteValidators
                .stream()
                .flatMap(c -> c.validate(dataObject,
                                         objectProperty).stream())
                .collect(Collectors.toList());
    }
}
