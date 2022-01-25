/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.graph.processing.traverse.content;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public abstract class ContentTraverseProcessorBaseTest<T extends ContentTraverseProcessor, C extends ContentTraverseCallback> {

    protected TestingGraphMockHandler graphTestHandler;
    protected T tested;
    protected C callback;

    @Before
    public void setup() {
        this.graphTestHandler = new TestingGraphMockHandler();
        this.tested = newTraverseProcessor();
        this.callback = newTraverseCallback();
    }

    protected abstract T newTraverseProcessor();

    protected abstract C newTraverseCallback();
}
