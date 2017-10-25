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

import org.guvnor.ala.config.Config;

/*
 * Represents a configuration executor which will be executed only for 
 *  the configuration returned by the executeFor() method.
 */
public interface ConfigExecutor {

    /*
     * Returns the configuration type which this executor can be executed for
     * @return a configuration type for which this ConfigExecutor can be executed.
     * @see Config
    */
    Class<? extends Config> executeFor();

    /*
     * Returns the outputId for the execution results
     * @return the outputId
    */
    String outputId();

    /*
     * Returns the intputId for the execution 
     * @return the inputId 
    */
    default String inputId() {
        return "none";
    }
}
