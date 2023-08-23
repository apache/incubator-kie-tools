/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.webapp.kogito.common.client.editors.documentation;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.appformer.client.context.Channel;
import org.appformer.client.context.EditorContextProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.appformer.client.context.Channel.VSCODE_DESKTOP;
import static org.appformer.client.context.Channel.VSCODE_WEB;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDocumentationViewButtonsVisibilitySupplierTest {

    @Mock
    private EditorContextProvider contextProvider;

    private DMNDocumentationViewButtonsVisibilitySupplier supplier;

    @Before
    public void setup() {
        supplier = new DMNDocumentationViewButtonsVisibilitySupplier(contextProvider);
    }

    @Test
    public void testIsButtonsVisibleWhenIsVSCodeDesktop() {

        when(contextProvider.getChannel()).thenReturn(VSCODE_DESKTOP);

        final boolean isButtonsVisible = supplier.isButtonsVisible();

        assertFalse(isButtonsVisible);
    }

    @Test
    public void testIsButtonsVisibleWhenIsVSCodeWeb() {

        when(contextProvider.getChannel()).thenReturn(VSCODE_WEB);

        final boolean isButtonsVisible = supplier.isButtonsVisible();

        assertFalse(isButtonsVisible);
    }

    @Test
    public void testIsButtonsVisibleWhenIsNotVSCode() {

        final Channel channel = mock(Channel.class);
        when(contextProvider.getChannel()).thenReturn(channel);

        final boolean isButtonsVisible = supplier.isButtonsVisible();

        assertTrue(isButtonsVisible);
    }
}