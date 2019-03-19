/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.data.modeller.service.ext;

/**
 * Component that provides a module {@link ModelReader} based on a given {@link SOURCE}
 * @param <SOURCE> Source represents any source that can be used to resolve a module
 */
public interface ModelReaderService<SOURCE> {

    /**
     * Retrieves the  {@link ModelReader} for the given {@link SOURCE}
     * @param source the module source
     * @return a valid {@link ModelReader} able to read the module models.
     */
    ModelReader getModelReader(SOURCE source);
}
