/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.factory.graph;

import org.kie.workbench.common.stunner.core.factory.Factory;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

/**
 * Factory for different types of graph elements, such as nodes or edges.
 * The <code>identifier</code> argument for <code>build</code> method
 * corresponds with the UUID of the resulting element.
 */
public interface ElementFactory<C, D extends Definition<C>, T extends Element<D>>
        extends Factory<C> {

    Class<? extends ElementFactory> getFactoryType();

    T build( String uuid, C definition );

}
