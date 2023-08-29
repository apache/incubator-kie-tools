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


package org.appformer.kogito.bridge.client.context;

import org.appformer.client.context.Channel;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChannelTest {

    @Test
    public void withNameTest() {
        assertEquals(Channel.GITHUB, Channel.withName("GitHub"));
        assertEquals(Channel.DEFAULT, Channel.withName("dEfAuLt"));
        assertEquals(Channel.ONLINE, Channel.withName("ONLine"));
        assertEquals(Channel.VSCODE_DESKTOP, Channel.withName("VSCode_Desktop"));
        assertEquals(Channel.VSCODE_WEB, Channel.withName("VScode_WEb"));
        assertEquals(Channel.EMBEDDED, Channel.withName("emBedDED"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void withWrongNameTest() {
        Channel.withName("foo");
    }
}
