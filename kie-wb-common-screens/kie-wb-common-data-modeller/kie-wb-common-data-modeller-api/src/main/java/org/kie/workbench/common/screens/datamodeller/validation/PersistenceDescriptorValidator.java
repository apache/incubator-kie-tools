/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.validation;

import java.util.List;

import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.uberfire.backend.vfs.Path;

/**
 * Provides validation for persistence descriptors.
 */
public interface PersistenceDescriptorValidator {

    /**
     * Validates a persistence descriptor.
     *
     * @param path path to the persistence descriptor.
     * @param model the persistence descriptor model to validate.
     * @return a list of validation message with the results.
     */
    List<ValidationMessage> validate( final Path path, final PersistenceDescriptorModel model );

}