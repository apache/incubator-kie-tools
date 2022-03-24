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

import org.kie.workbench.common.stunner.core.diagram.Diagram;

/**
 * Base validator type for diagram instances.
 * <p>
 * A Diagram validator type, at least, must evaluate both domain model beans
 * and the graph structure.
 * @param <D> The diagram type.
 * @param <V> The graph element violation type.
 */
public interface DiagramValidator<D extends Diagram, V extends ElementViolation>
        extends Validator<D, DiagramElementViolation<V>> {

}
