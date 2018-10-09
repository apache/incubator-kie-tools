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

package org.kie.workbench.common.screens.datamodeller.backend.server.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceUnitModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.TransactionType;
import org.kie.workbench.common.screens.datamodeller.validation.PersistenceDescriptorValidator;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.vfs.Path;

import static org.kie.workbench.common.screens.datamodeller.backend.server.validation.PersistenceDescriptorValidationMessages.newErrorMessage;

@ApplicationScoped
public class PersistenceDescriptorValidatorImpl
        implements PersistenceDescriptorValidator {

    private KieModuleService moduleService;

    private ModuleClassLoaderHelper moduleClassLoaderHelper;

    private PersistableClassValidator classValidator = new PersistableClassValidator();

    private PropertyValidator propertyValidator = new PropertyValidator();

    public PersistenceDescriptorValidatorImpl() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public PersistenceDescriptorValidatorImpl(KieModuleService moduleService,
                                              ModuleClassLoaderHelper moduleClassLoaderHelper) {
        this.moduleService = moduleService;
        this.moduleClassLoaderHelper = moduleClassLoaderHelper;
    }

    @Override
    public List<ValidationMessage> validate(Path path, PersistenceDescriptorModel model) {

        final List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
        final KieModule module = moduleService.resolveModule(path);

        if (module == null) {
            //uncommon scenario, since by construction, the same as with other wb assets, a persistence descriptor
            // belongs to a module
            messages.add(newErrorMessage(PersistenceDescriptorValidationMessages.DESCRIPTOR_NOT_BELONG_TO_PROJECT_ID,
                                         PersistenceDescriptorValidationMessages.DESCRIPTOR_NOT_BELONG_TO_PROJECT));
            return messages;
        }

        if (model.getPersistenceUnit() == null) {
            messages.add(newErrorMessage(PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_NOT_FOUND_ID,
                                         PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_NOT_FOUND));
            return messages;
        }

        final PersistenceUnitModel unitModel = model.getPersistenceUnit();
        if (unitModel.getName() == null || unitModel.getName().trim().isEmpty()) {
            messages.add(newErrorMessage(PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_NAME_EMPTY_ID,
                                         PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_NAME_EMPTY));
        }

        if (unitModel.getProvider() == null || unitModel.getProvider().trim().isEmpty()) {
            messages.add(newErrorMessage(PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_PROVIDER_ID,
                                         PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_PROVIDER_EMPTY));
        }

        if (unitModel.getTransactionType() == null) {
            messages.add(newErrorMessage(PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_TRANSACTION_TYPE_EMPTY_ID,
                                         PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_TRANSACTION_TYPE_EMPTY));
        } else if (unitModel.getTransactionType() == TransactionType.JTA &&
                (unitModel.getJtaDataSource() == null || unitModel.getJtaDataSource().trim().isEmpty())) {
            messages.add(newErrorMessage(PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_JTA_DATASOURCE_EMPTY_ID,
                                         PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_JTA_DATASOURCE_EMPTY));
        } else if (unitModel.getTransactionType() == TransactionType.RESOURCE_LOCAL &&
                (unitModel.getNonJtaDataSource() == null || unitModel.getNonJtaDataSource().trim().isEmpty())) {
            messages.add(newErrorMessage(PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_NON_JTA_DATASOURCE_EMPTY_ID,
                                         PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_NON_JTA_DATASOURCE_EMPTY));
        }

        if (unitModel.getClasses() != null && !unitModel.getClasses().isEmpty()) {
            ClassLoader moduleClassLoader = moduleClassLoaderHelper.getModuleClassLoader(module);
            unitModel.getClasses().forEach(clazz -> Optional.ofNullable(classValidator.validate( clazz.getValue(), moduleClassLoader)).ifPresent(messages::addAll));
        }

        if (unitModel.getProperties() != null) {
            int[] index = {1};
            unitModel.getProperties().forEach(property ->
                                                      messages.addAll(Optional.ofNullable(propertyValidator.validate(property.getName(), property.getValue(), index[0]++)).orElse(Collections.emptyList()))
            );
        }
        return messages;
    }
}