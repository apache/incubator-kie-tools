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
 *
 */

package org.kie.workbench.screens.workbench.backend.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.services.refactoring.model.index.events.IndexingFinishedEvent;
import org.kie.workbench.common.services.refactoring.model.index.events.IndexingStartedEvent;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.ext.metadata.engine.BatchIndexListener;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class DefaultBatchIndexListener implements BatchIndexListener {

    private final Event<IndexingStartedEvent> indexingStartedEventEvent;
    private final Event<IndexingFinishedEvent> indexingFinishedEventEvent;

    @Inject
    public DefaultBatchIndexListener(final Event<IndexingStartedEvent> indexingStartedEventEvent,
                                     final Event<IndexingFinishedEvent> indexingFinishedEventEvent) {
        this.indexingStartedEventEvent = indexingStartedEventEvent;
        this.indexingFinishedEventEvent = indexingFinishedEventEvent;
    }

    @Override
    public void notifyIndexIngStarted(KCluster kCluster, Path path) {
        indexingStartedEventEvent.fire(new IndexingStartedEvent(kCluster.getClusterId(), Paths.convert(path)));
    }

    @Override
    public void notifyIndexIngFinished(KCluster kCluster, Path path) {
        indexingFinishedEventEvent.fire(new IndexingFinishedEvent(kCluster.getClusterId(), Paths.convert(path)));
    }
}
