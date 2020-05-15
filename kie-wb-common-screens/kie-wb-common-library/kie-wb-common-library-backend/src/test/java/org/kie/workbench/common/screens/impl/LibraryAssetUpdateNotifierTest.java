/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.impl;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.metadata.event.BatchIndexEvent;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.api.FileSystemUtils;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LibraryAssetUpdateNotifierTest {

    @Spy
    private LibraryAssetUpdateNotifier notifier = new LibraryAssetUpdateNotifier();

    @Mock
    private BatchIndexEvent event;

    @Before
    public void setUp() {
        doReturn(new ArrayList<>()).when(event).getIndexEvents();
        doReturn(true).when(notifier).isUpdateNotifierEnabled();
    }

    @Test
    public void testMonitoringEnabled() {
        doReturn(false).when(notifier).isUpdateNotifierEnabled();
        notifier.notifyOnUpdatedAssets(event);
        verify(event, never()).getIndexEvents();
    }

    @Test
    public void testMonitoringDisabled() {
        {
            notifier.notifyOnUpdatedAssets(event);
            verify(event, times(1)).getIndexEvents();
        }
    }
}