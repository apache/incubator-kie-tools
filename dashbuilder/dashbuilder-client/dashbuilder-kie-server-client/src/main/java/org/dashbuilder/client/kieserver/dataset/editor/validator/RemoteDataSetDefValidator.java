/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.kieserver.dataset.editor.validator;

import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.validation.groups.SQLDataSetDefDbSQLValidation;
import org.dashbuilder.dataset.validation.groups.SQLDataSetDefValidation;
import org.dashbuilder.kieserver.RemoteDataSetDef;
import org.dashbuilder.kieserver.RemoteDataSetDefValidation;
import org.dashbuilder.kieserver.RuntimeKieServerDataSetProviderType;
import org.dashbuilder.validations.dataset.AbstractDataSetDefValidator;

/**
 * <p>The singleton application Remote data set definition validator.</p>
 */
@Dependent
public class RemoteDataSetDefValidator extends AbstractDataSetDefValidator<RemoteDataSetDef> {

    @Inject
    public RemoteDataSetDefValidator(Validator validator) {
        super(validator);
    }

    @Override
    public DataSetProviderType getSupportedProvider() {
        return new RuntimeKieServerDataSetProviderType();
    }

    @Override
    public Iterable<ConstraintViolation<?>> validateCustomAttributes(RemoteDataSetDef dataSetDef, Object... params) {

        Set<ConstraintViolation<RemoteDataSetDef>> violations = validator.validate(dataSetDef,
                                                                                    RemoteDataSetDefValidation.class,
                                                                                    SQLDataSetDefValidation.class,
                                                                                    SQLDataSetDefDbSQLValidation.class);
        return toIterable(violations);
    }

    @Override
    public Iterable<ConstraintViolation<?>> validate(RemoteDataSetDef dataSetDef,
                                                     boolean isCacheEnabled,
                                                     boolean isPushEnabled,
                                                     boolean isRefreshEnabled,
                                                     Object... params) {

        Set<ConstraintViolation<RemoteDataSetDef>> _violations = validator.validate(dataSetDef,
                                                                                    getValidationGroups(isCacheEnabled,
                                                                                                        isPushEnabled,
                                                                                                        isRefreshEnabled,
                                                                                                        RemoteDataSetDefValidation.class,
                                                                                                        SQLDataSetDefValidation.class,
                                                                                                        SQLDataSetDefDbSQLValidation.class));
        return toIterable(_violations);
    }

}
