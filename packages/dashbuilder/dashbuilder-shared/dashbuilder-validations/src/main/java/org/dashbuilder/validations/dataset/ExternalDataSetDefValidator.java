/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.validations.dataset;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.def.ExternalDataSetDef;
import org.dashbuilder.dataset.validation.groups.ExternalDataSetDefValidation;

/**
 * <p>The singleton application External data set definition validator.</p>
 *
 */
@Dependent
public class ExternalDataSetDefValidator extends AbstractDataSetDefValidator<ExternalDataSetDef> {

    @Inject
    public ExternalDataSetDefValidator(Validator validator) {
        super(validator);
    }

    @Override
    public DataSetProviderType getSupportedProvider() {
        return DataSetProviderType.EXTERNAL;
    }

    @Override
    public Iterable<ConstraintViolation<?>> validateCustomAttributes(ExternalDataSetDef dataSetDef, Object... params) {
        var _violations = validator.validate(dataSetDef, ExternalDataSetDefValidation.class);
        return toIterable(_violations);
    }

    @Override
    public Iterable<ConstraintViolation<?>> validate(ExternalDataSetDef dataSetDef,
                                                     boolean isCacheEnabled,
                                                     boolean isPushEnabled,
                                                     boolean isRefreshEnabled,
                                                     Object... params) {
        var _violations = validator.validate(dataSetDef, getValidationGroups(isCacheEnabled, isPushEnabled,
                isRefreshEnabled,
                ExternalDataSetDefValidation.class));
        return toIterable(_violations);
    }

}
