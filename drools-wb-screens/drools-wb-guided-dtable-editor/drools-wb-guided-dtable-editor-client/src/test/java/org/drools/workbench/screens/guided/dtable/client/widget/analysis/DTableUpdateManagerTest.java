/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import java.util.ArrayList;

import org.drools.workbench.services.verifier.plugin.client.api.SortTable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DTableUpdateManagerTest {

    @Mock
    private Poster poster;

    @Mock
    private FieldTypeProducer fieldTypeProducer;

    @Captor
    ArgumentCaptor<SortTable> sortTableArgumentCaptor;

    private DTableUpdateManager dTableUpdateManager;

    @Before
    public void setUp() throws Exception {
        dTableUpdateManager = new DTableUpdateManager(poster,
                                                      fieldTypeProducer);
    }

    @Test
    public void name() {
        ArrayList<Integer> rowOrder = new ArrayList<>();
        dTableUpdateManager.sort(rowOrder);
        verify(poster).post(sortTableArgumentCaptor.capture());
    }
}
