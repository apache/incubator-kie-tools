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

package org.kie.workbench.common.stunner.core.definition.property;

/**
 * It gives semantics to model definition properties.for different Stunner behaviors.
 * Stunner provides some built-in features that could require model updates,
 * so this meta-property values are used for this bindings.
 */
public enum PropertyMetaTypes {
    /**
     * No semantics.
     */
    NONE,
    /**
     * Use it for the Definition's property used as name for this bean.
     */
    NAME
}
