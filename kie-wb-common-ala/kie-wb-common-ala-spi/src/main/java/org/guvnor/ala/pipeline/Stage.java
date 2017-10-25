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

package org.guvnor.ala.pipeline;

import java.util.function.Consumer;

/*
 * Represent a Stage in a Pipeline. Different implementaions can provide different beheavior, 
 * which can be chained in different ways by a concrete Pipeline.
 */
public interface Stage<INPUT, OUTPUT> {

    /*
     * Execute the current stage based on the Input 
     *   and execute the consumer callback after the execution.
     */
    void execute(final INPUT input,
                 final Consumer<OUTPUT> callback);

    /*
     * Get the Stage name
     * @return String the name for the stage.
     */
    String getName();
}
