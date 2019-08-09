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

import java.io.IOException;
import java.net.URISyntaxException;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.refactoring.model.index.events.IndexingFinishedEvent;
import org.kie.workbench.common.services.refactoring.model.index.events.IndexingStartedEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.java.nio.file.Path;
import org.uberfire.mocks.FileSystemTestingUtils;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultBatchIndexListenerTest {

    private static final String KCLUSTER_ID = "id";

    private FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    @Mock
    private Event<IndexingStartedEvent> indexingStartedEventEvent;

    @Mock
    private Event<IndexingFinishedEvent> indexingFinishedEventEvent;

    private DefaultBatchIndexListener indexListener;

    @Mock
    private KCluster kCluster;

    private Path path;

    @Before
    public void init() throws IOException {

        fileSystemTestingUtils.setup();

        path = fileSystemTestingUtils.getIoService().get("");

        when(kCluster.getClusterId()).thenReturn(KCLUSTER_ID);

        indexListener = new DefaultBatchIndexListener(indexingStartedEventEvent, indexingFinishedEventEvent);
    }

    @Test
    public void testFireEvents() {
        indexListener.notifyIndexIngStarted(kCluster, path);

        verify(kCluster).getClusterId();
        verify(indexingStartedEventEvent).fire(any());

        indexListener.notifyIndexIngFinished(kCluster, path);

        verify(kCluster, times(2)).getClusterId();
        verify(indexingFinishedEventEvent).fire(any());
    }
}
