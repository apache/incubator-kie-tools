/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.kogito.api.docks;

public interface DiagramEditorDock {

    /**
     * Initialise dock registering it with AppFormer's docks configuration.
     * @param owningPerspectiveId PerspectiveId for the perspective on which the dock should appear.
     */
    void init(final String owningPerspectiveId);

    /**
     * Destroy the dock removing it from AppFormer's docks configuration.
     */
    void destroy();

    /**
     * Open the dock showing its content.
     */
    void open();

    /**
     * Close the dock hiding its content.
     */
    void close();
}
