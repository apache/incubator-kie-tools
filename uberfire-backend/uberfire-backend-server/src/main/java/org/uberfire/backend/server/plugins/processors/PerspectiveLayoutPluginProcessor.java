/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.backend.server.plugins.processors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.workbench.events.PluginAddedEvent;
import org.uberfire.workbench.events.PluginUpdatedEvent;

@ApplicationScoped
public class PerspectiveLayoutPluginProcessor extends AbstractRuntimePluginProcessor {

    public PerspectiveLayoutPluginProcessor() {
    }

    @Inject
    public PerspectiveLayoutPluginProcessor(final Event<PluginAddedEvent> pluginAddedEvent,
                                            final Event<PluginUpdatedEvent> pluginUpdatedEvent) {
        super(pluginAddedEvent,
              pluginUpdatedEvent);
    }

    @Override
    PluginProcessorType getType() {
        return PluginProcessorType.PERSPECTIVE_EDITOR;
    }
}
