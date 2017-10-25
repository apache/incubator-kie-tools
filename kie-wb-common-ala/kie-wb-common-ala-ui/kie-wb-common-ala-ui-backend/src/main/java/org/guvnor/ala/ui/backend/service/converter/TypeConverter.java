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

package org.guvnor.ala.ui.backend.service.converter;

/**
 * Defines the conversion of types between domains.
 */
public interface TypeConverter<MODEL_TYPE, DOMAIN_TYPE> {

    Class<MODEL_TYPE> getModelType();

    Class<DOMAIN_TYPE> getDomainType();

    DOMAIN_TYPE toDomain(MODEL_TYPE modelValue);

    MODEL_TYPE toModel(DOMAIN_TYPE domainValue);
}
