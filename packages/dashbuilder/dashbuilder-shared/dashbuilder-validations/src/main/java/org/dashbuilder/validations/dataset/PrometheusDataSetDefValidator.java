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
package org.dashbuilder.validations.dataset;

import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.def.PrometheusDataSetDef;
import org.dashbuilder.dataset.validation.groups.PrometheusDataSetDefValidation;

@Dependent
public class PrometheusDataSetDefValidator extends AbstractDataSetDefValidator<PrometheusDataSetDef> {

    @Inject
    public PrometheusDataSetDefValidator(Validator validator) {
        super(validator);
    }

    @Override
    public DataSetProviderType getSupportedProvider() {
        return DataSetProviderType.PROMETHEUS;
    }

    @Override
    public Iterable<ConstraintViolation<?>> validateCustomAttributes(PrometheusDataSetDef dataSetDef, Object... params) {
        Set<ConstraintViolation<PrometheusDataSetDef>> violations = validator.validate(dataSetDef,
                                                                                       PrometheusDataSetDefValidation.class);
        return toIterable(violations);
    }

    @Override
    public Iterable<ConstraintViolation<?>> validate(PrometheusDataSetDef dataSetDef, boolean isCacheEnabled, boolean isPushEnabled, boolean isRefreshEnabled, Object... params) {
        Set<ConstraintViolation<PrometheusDataSetDef>> violations = validator.validate(dataSetDef,
                                                                                       getValidationGroups(isCacheEnabled,
                                                                                                           isPushEnabled,
                                                                                                           isRefreshEnabled,
                                                                                                           PrometheusDataSetDefValidation.class));
        return toIterable(violations);
    }

}
