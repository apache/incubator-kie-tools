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

package org.kie.workbench.common.dmn.client.common;

import java.util.function.Supplier;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.appformer.client.context.Channel;
import org.appformer.client.context.EditorContextProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.appformer.client.context.Channel.DEFAULT;
import static org.appformer.client.context.Channel.EMBEDDED;
import static org.appformer.client.context.Channel.GITHUB;
import static org.appformer.client.context.Channel.ONLINE;
import static org.appformer.client.context.Channel.ONLINE_MULTI_FILE;
import static org.appformer.client.context.Channel.VSCODE_DESKTOP;
import static org.appformer.client.context.Channel.VSCODE_WEB;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class KogitoChannelHelperTest {

    @Mock
    private EditorContextProvider contextProvider;

    private KogitoChannelHelper kogitoChannelHelper;

    @Before
    public void setup() {
        kogitoChannelHelper = new KogitoChannelHelper(contextProvider);
    }

    @Test
    public void testIsIncludedModelEnabled() {
        Channel[] channels = {DEFAULT, EMBEDDED, GITHUB, ONLINE, ONLINE_MULTI_FILE, VSCODE_DESKTOP, VSCODE_WEB};
        boolean[] expected = {   true,     true,  false,  false,              true,           true,       true};

        for(int i = 0; i < channels.length; i++) {
            testGenericChannelCheckFunction(channels[i], expected[i], kogitoChannelHelper::isIncludedModelEnabled);
        }
    }

    @Test
    public void testIsIncludedModelLinkEnabled() {
        Channel[] channels = {DEFAULT, EMBEDDED, GITHUB, ONLINE, ONLINE_MULTI_FILE, VSCODE_DESKTOP, VSCODE_WEB};
        boolean[] expected = {  false,    false,  false,  false,              true,           true,       true};

        for(int i = 0; i < channels.length; i++) {
            testGenericChannelCheckFunction(channels[i], expected[i], kogitoChannelHelper::isIncludedModelLinkEnabled);
        }
    }


    private void testGenericChannelCheckFunction(Channel channel, boolean expectedEnabled, Supplier<Boolean> function) {
        when(contextProvider.getChannel()).thenReturn(channel);
        assertEquals(expectedEnabled, function.get());
    }

}
