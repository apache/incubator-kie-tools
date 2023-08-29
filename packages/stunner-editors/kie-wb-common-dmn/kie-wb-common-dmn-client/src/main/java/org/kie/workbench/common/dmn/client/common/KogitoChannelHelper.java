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

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import org.appformer.client.context.Channel;
import org.appformer.client.context.EditorContextProvider;

import static java.util.Arrays.asList;
import static org.appformer.client.context.Channel.DEFAULT;
import static org.appformer.client.context.Channel.EMBEDDED;
import static org.appformer.client.context.Channel.ONLINE_MULTI_FILE;
import static org.appformer.client.context.Channel.VSCODE_DESKTOP;
import static org.appformer.client.context.Channel.VSCODE_WEB;

/**
 * Scope of this Helper class is to retrieve and check the current Channel where the editor lives.
 */
public class KogitoChannelHelper {

    private final EditorContextProvider contextProvider;

    public KogitoChannelHelper() {
        this(null); // CDI proxy
    }

    @Inject
    public KogitoChannelHelper(final EditorContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public boolean isCurrentChannelEnabled(Channel channel) {
        return Objects.equals(contextProvider.getChannel(), channel);
    }

    public boolean isCurrentChannelEnabled(List<Channel> enabledChannels) {
        return enabledChannels.stream().anyMatch(this::isCurrentChannelEnabled);
    }

    public boolean isIncludedModelEnabled() {
        return isCurrentChannelEnabled(asList(DEFAULT, EMBEDDED, ONLINE_MULTI_FILE, VSCODE_DESKTOP, VSCODE_WEB));
    }

    public boolean isIncludedModelLinkEnabled() {
        return isCurrentChannelEnabled(asList(ONLINE_MULTI_FILE, VSCODE_DESKTOP, VSCODE_WEB));
    }
}
