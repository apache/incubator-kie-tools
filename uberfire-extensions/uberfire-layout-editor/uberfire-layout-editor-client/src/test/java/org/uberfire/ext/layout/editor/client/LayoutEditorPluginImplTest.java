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

package org.uberfire.ext.layout.editor.client;

import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class LayoutEditorPluginImplTest {

    @Mock
    private LayoutEditorPresenter layoutEditorPresenter;

    @Spy
    @InjectMocks
    private LayoutEditorPluginImpl layoutEditorPlugin;

    @Captor
    private ArgumentCaptor<Supplier<Boolean>> lockSupplierCaptor;

    @Test
    public void testLock() {
        layoutEditorPlugin.lock();
        Assert.assertTrue(layoutEditorPlugin.isLocked());
    }

    @Test
    public void testUnlock() {
        layoutEditorPlugin.unlock();
        Assert.assertFalse(layoutEditorPlugin.isLocked());
    }

    @Test
    public void testSetup() {
        layoutEditorPlugin.setup();
        verify(layoutEditorPresenter, times(1)).setup(lockSupplierCaptor.capture());
        Assert.assertFalse(lockSupplierCaptor.getValue().get());
    }
}
