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

package org.dashbuilder.validations;

import javax.validation.ConstraintViolation;

import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.def.DataSetDef;

/**
 * Validator for {@link DataSetDef}
 */
public interface DataSetDefValidator<T extends DataSetDef> {

    /**
     * Retrieves the supported {@link DataSetDef} type
     */
    DataSetProviderType getSupportedProvider();

    /**
     * Validates the {@link DataSetDef} basic attributes
     */
    Iterable<ConstraintViolation<?>> validateBasicAttributes( DataSetDef dataSetDef );

    /**
     * Validates the {@link DataSetDef} specific attributes
     */
    Iterable<ConstraintViolation<?>> validateCustomAttributes( T dataSetDef, Object... params );

    /**
     * Validates a {@link DataSetDef}
     */
    Iterable<ConstraintViolation<?>> validate( T dataSetDef,
                                               boolean isCacheEnabled,
                                               boolean isPushEnabled,
                                               boolean isRefreshEnabled,
                                               Object... params );

    Iterable<ConstraintViolation<?>> validateProviderType( DataSetDef dataSetDef );
}
