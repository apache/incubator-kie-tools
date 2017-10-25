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
package org.guvnor.ala.runtime;

import java.util.Optional;
import java.util.function.Function;

import org.guvnor.ala.config.RuntimeConfig;

/*
 * Runtime Builder provides a way to create new Runtimes for different providers. 
 * notice that this build extends the Functional interface Function. See also RuntimeDestroyer
 * @param <T extends RuntimeConfig> the configuration used to create the runtime
 * @param <R extends Runtime> to be created
 * @see Function
 * @see RuntimeDestroyer
*/
public interface RuntimeBuilder<T extends RuntimeConfig, R extends Runtime> extends Function<T, Optional<R>> {

    boolean supports(final RuntimeConfig config);
}
