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

import java.util.List;

/**
 * Represents a generic Pipeline with a List of stages.
 */
public interface Pipeline {

    /**
     * Get the pipeline name
     * @return the pipeline name
     */
    String getName();

    /**
     * Get the list of stages for this pipeline
     * @return List<Stages>
     * @see Stage
     */
    List<Stage> getStages();
}
