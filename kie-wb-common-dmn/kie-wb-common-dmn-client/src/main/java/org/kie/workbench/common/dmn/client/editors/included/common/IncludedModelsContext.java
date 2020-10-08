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

package org.kie.workbench.common.dmn.client.editors.included.common;

import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.appformer.client.context.Channel;
import org.appformer.client.context.EditorContextProvider;

import static java.util.Arrays.asList;
import static org.appformer.client.context.Channel.DEFAULT;
import static org.appformer.client.context.Channel.EMBEDDED;
import static org.appformer.client.context.Channel.VSCODE;

@ApplicationScoped
public class IncludedModelsContext {

    private final EditorContextProvider contextProvider;

    private Boolean isIncludedModelChannel;

    private final List<Channel> INCLUDED_MODEL_CHANNELS = asList(DEFAULT, VSCODE, EMBEDDED);

    public IncludedModelsContext() {
        this(null); // CDI proxy
    }

    @Inject
    public IncludedModelsContext(final EditorContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public boolean isIncludedModelChannel() {

        if (isIncludedModelChannel == null) {
            isIncludedModelChannel = INCLUDED_MODEL_CHANNELS.stream().anyMatch(channel -> {
                final Channel currentChannel = contextProvider.getChannel();
                return Objects.equals(currentChannel, channel);
            });
        }

        return isIncludedModelChannel;
    }
}
