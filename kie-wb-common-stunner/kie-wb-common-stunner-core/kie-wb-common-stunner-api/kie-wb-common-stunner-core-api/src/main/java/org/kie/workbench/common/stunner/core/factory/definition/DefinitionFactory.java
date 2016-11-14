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

package org.kie.workbench.common.stunner.core.factory.definition;

import org.kie.workbench.common.stunner.core.factory.Factory;

/**
 * Factory for Definition domain objects.
 * The <code>identifier</code> argument for <code>accepts</code> and <code>build</code> methods
 * corresponds with the definition type identifier. ( Eg: Task, Rectangle ).
 */
public interface DefinitionFactory<T> extends Factory<String> {

    boolean accepts( String identifier );

    T build( String identifier );

}
