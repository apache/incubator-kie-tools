/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.validation;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * A validator type.
 * @param <T> The entity type.
 * @param <V> The evaulation violation type.
 */
public interface Validator<T, V extends Violation> {

    /**
     * Validates the instance <code>entity</code>
     * and provides the resulting validation violations to
     * the <code>resultConsumer</code> consumer
     */
    void validate(T entity,
                  Consumer<Collection<V>> resultConsumer);
}
