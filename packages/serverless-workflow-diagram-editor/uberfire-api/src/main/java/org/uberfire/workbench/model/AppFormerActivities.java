/*
 *
 *   Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.uberfire.workbench.model;

import java.util.List;

/**
 * Defines the list of entities used in the workbench
 * If no backend implementation for this class exists, a default one will be provided.
 */
public interface AppFormerActivities {

    /**
     * Returns list of all the editors IDs used in the workbench.
     *
     * @return List of all editors.
     */
    List<String> getAllEditorIds();

    /**
     * Returns list of all the perspectives used in the workbench.
     *
     * @return List of all perspectives.
     */
    List<String> getAllPerpectivesIds();

}
