/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.compiler.impl.pomprocessor;

/***
 * Used to signals the plugins present after the scan of the poms
 */
public interface PluginPresents {

    /***
     * Signals if the Default Maven compiler is explicitely declared
     * @return
     */
    Boolean isDefaultCompilerPresent();

    /***
     * Signals if the alternative incremental compiler (takari) is present
     * @return
     */
    Boolean isAlternativeCompilerPresent();

    /***
     * Signals ff the kie plugin is declared in the pom
     * @return
     */
    Boolean isKiePluginPresent();

    /***
     * Signals if needed overwrite the pom
     * @return
     */
    Boolean pomOverwriteRequired();
}
