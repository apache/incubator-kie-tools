/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import org.dashbuilder.dataset.def.CSVDataSetDef;
import org.dashbuilder.dataset.validation.groups.CSVDataSetDefFilePathValidation;
import org.dashbuilder.dataset.validation.groups.CSVDataSetDefFileURLValidation;
import org.dashbuilder.dataset.validation.groups.CSVDataSetDefValidation;

/**
 * <p>The singleton application CSV data set definition validator.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class CSVDataSetDefValidator extends AbstractDataSetDefValidator<CSVDataSetDef> {

    @Inject
    public CSVDataSetDefValidator( Validator validator ) {
        super( validator );
    }

    @Override
    public DataSetProviderType getSupportedProvider() {
        return DataSetProviderType.CSV;
    }

    @Override
    public Iterable<ConstraintViolation<?>> validateCustomAttributes( CSVDataSetDef dataSetDef, Object... params) {
        assert params != null && params.length == 1;
        final Boolean isFilePath = (Boolean) params[0];
        Set<ConstraintViolation<CSVDataSetDef>> _violations = validator.validate( dataSetDef,
                                                                                  CSVDataSetDefValidation.class, isFilePath ? CSVDataSetDefFilePathValidation.class : CSVDataSetDefFileURLValidation.class);
        return toIterable(_violations);
    }

    @Override
    public Iterable<ConstraintViolation<?>> validate(CSVDataSetDef dataSetDef, boolean isCacheEnabled, boolean isPushEnabled, boolean isRefreshEnabled, Object... params) {
        assert params != null && params.length == 1;
        final Boolean isFilePath = (Boolean) params[0];
        Set<ConstraintViolation<CSVDataSetDef>> _violations = validator.validate(dataSetDef,
                getValidationGroups(isCacheEnabled, isPushEnabled, isRefreshEnabled, CSVDataSetDefValidation.class,
                        isFilePath ? CSVDataSetDefFilePathValidation.class : CSVDataSetDefFileURLValidation.class));
        return toIterable(_violations);
    }

}
