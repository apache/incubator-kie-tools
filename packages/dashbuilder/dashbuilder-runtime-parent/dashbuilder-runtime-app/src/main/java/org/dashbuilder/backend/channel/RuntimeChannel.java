/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.backend.channel;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import org.dashbuilder.backend.RuntimeOptions;
import org.dashbuilder.shared.event.RemovedRuntimeModelEvent;
import org.dashbuilder.shared.event.SSEType;
import org.dashbuilder.shared.event.UpdatedRuntimeModelEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Channel with client for runtime events.
 *
 */
@Path("runtime-channel")
@ApplicationScoped
public class RuntimeChannel {

    Logger logger = LoggerFactory.getLogger(RuntimeChannel.class);

    private Sse sse;
    private SseBroadcaster sseBroadcaster;
    private OutboundSseEvent.Builder eventBuilder;

    @Inject
    RuntimeOptions runtimeOptions;


    @GET
    @Path("subscribe")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void listen(@Context SseEventSink sseEventSink) {
        if (runtimeOptions.isWatchModels() && sseBroadcaster != null) {
            sseBroadcaster.register(sseEventSink);
            sseEventSink.send(sse.newEvent(SSEType.SUBSCRIBED.name(), ""));
        } else {
            sseEventSink.send(sse.newEvent(SSEType.NOT_SUBSCRIBED.name(), ""));
        }
    }

    public void onRuntimeModelUpdated(@Observes UpdatedRuntimeModelEvent updatedRuntimeModel) {
        broadcastEvent(SSEType.MODEL_UPDATED, updatedRuntimeModel.getRuntimeModelId());
    }

    public void onRuntimeModelRemoved(@Observes RemovedRuntimeModelEvent removedRuntimeModel) {
        broadcastEvent(SSEType.MODEL_REMOVED, removedRuntimeModel.getRuntimeModelId());
    }

    private void broadcastEvent(SSEType type, String data) {
        if (sseBroadcaster != null) {
            var sseEvent = eventBuilder.name(type.name())
                                       .mediaType(MediaType.TEXT_PLAIN_TYPE)
                                       .data(data)
                                       .reconnectDelay(3000)
                                       .build();
            sseBroadcaster.broadcast(sseEvent);
        }
    }

    @Context
    public void setSse(Sse sse) {
        this.sse = sse;
        this.eventBuilder = sse.newEventBuilder();
        this.sseBroadcaster = sse.newBroadcaster();
    }

}
