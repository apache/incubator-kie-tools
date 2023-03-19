/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

/**
 * A violation type that related to an element in a diagram.
 */
public interface DiagramElementViolation<V extends ElementViolation> extends Violation {

    /**
     * The violation's root element UUID.
     */
    String getUUID();

    /**
     * Returns the resulting violations produced by the graph structure validation, if any.
     */
    Collection<V> getGraphViolations();

    /**
     * Returns the resulting violations produced by the validation for the different beans
     * present in the graph structure, if any.
     */
    Collection<ModelBeanViolation> getModelViolations();

    /**
     * Returns the domain (BPMN, DMN, CM...) violations produced by a diagram validation.
     */
    Collection<DomainViolation> getDomainViolations();
}
