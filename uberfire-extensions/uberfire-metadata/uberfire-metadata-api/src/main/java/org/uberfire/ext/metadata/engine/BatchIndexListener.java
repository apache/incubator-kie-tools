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

package org.uberfire.ext.metadata.engine;

import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.java.nio.file.Path;

/**
 * Listener that will be notified when the indexing of a project starts or finishes
 */
public interface BatchIndexListener {

    /**
     * Method that will be called to notify that the indexing has started.
     * @param kCluster the KCluster that's going to be indexed
     * @param path the path of the project to be indexed
     */
    void notifyIndexIngStarted(KCluster kCluster, Path path);

    /**
     * Method that will be called to notify that the indexing has finished.
     * @param kCluster the KCluster that's been indexed
     * @param path the path of the indexed project
     */
    void notifyIndexIngFinished(KCluster kCluster, Path path);

}
