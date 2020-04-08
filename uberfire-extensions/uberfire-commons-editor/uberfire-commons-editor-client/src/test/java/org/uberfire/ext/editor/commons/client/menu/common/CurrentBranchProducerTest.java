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
 *
 */

package org.uberfire.ext.editor.commons.client.menu.common;

import javax.enterprise.inject.Instance;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.editor.commons.version.CurrentBranch;
import org.uberfire.mocks.MockInstanceImpl;

@RunWith(MockitoJUnitRunner.class)
public class CurrentBranchProducerTest {

    @Mock
    private Instance<CurrentBranch> currentBranch = new MockInstanceImpl<>();
    @InjectMocks
    private CurrentBranchProducer currentBranchProducer = new CurrentBranchProducer();

    @Test
    public void testCurrentBranchProducerWhenUnsatisfied() {
        Mockito.when(currentBranch.isUnsatisfied()).thenReturn(true);
        CurrentBranch currentBranch = currentBranchProducer.currentBranchProducer();
        Assert.assertNotNull(currentBranch);
        Assert.assertEquals(new DefaultCurrentBranch().getName(),
                            currentBranch.getName());
    }
}
