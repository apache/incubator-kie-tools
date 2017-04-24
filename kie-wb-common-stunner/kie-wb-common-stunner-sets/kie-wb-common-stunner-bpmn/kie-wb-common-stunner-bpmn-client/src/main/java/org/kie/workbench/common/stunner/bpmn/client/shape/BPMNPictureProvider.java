/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.shape;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.safehtml.shared.SafeUri;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNImageResources;
import org.kie.workbench.common.stunner.bpmn.shape.def.BPMNPictures;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureProvider;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@ApplicationScoped
public class BPMNPictureProvider implements PictureProvider<BPMNPictures> {

    private static final Map<BPMNPictures, SafeUri> PICTURE_URIS =
            new HashMap<BPMNPictures, SafeUri>(11) {{
                put(BPMNPictures.TASK_USER,
                    BPMNImageResources.INSTANCE.taskUser().getSafeUri());
                put(BPMNPictures.TASK_SCRIPT,
                    BPMNImageResources.INSTANCE.taskScript().getSafeUri());
                put(BPMNPictures.TASK_BUSINESS_RULE,
                    BPMNImageResources.INSTANCE.taskBusinessRule().getSafeUri());
                put(BPMNPictures.CANCEL,
                    BPMNImageResources.INSTANCE.cancel().getSafeUri());
                put(BPMNPictures.CIRCLE,
                    BPMNImageResources.INSTANCE.circle().getSafeUri());
                put(BPMNPictures.CLOCK_O,
                    BPMNImageResources.INSTANCE.clockO().getSafeUri());
                put(BPMNPictures.EVENT_END,
                    BPMNImageResources.INSTANCE.eventEnd().getSafeUri());
                put(BPMNPictures.EVENT_END_TERMINATE,
                    BPMNImageResources.INSTANCE.eventEndTerminate().getSafeUri());
                put(BPMNPictures.EVENT_INTERMEDIATE,
                    BPMNImageResources.INSTANCE.eventIntermediate().getSafeUri());
                put(BPMNPictures.EVENT_INTERMEDIATE_TIMER,
                    BPMNImageResources.INSTANCE.eventIntermediateTimer().getSafeUri());
                put(BPMNPictures.EVENT_START,
                    BPMNImageResources.INSTANCE.eventStart().getSafeUri());
                put(BPMNPictures.EVENT_START_SIGNAL,
                    BPMNImageResources.INSTANCE.eventStartSignal().getSafeUri());
                put(BPMNPictures.EVENT_START_TIMER,
                    BPMNImageResources.INSTANCE.eventStartTimer().getSafeUri());
                put(BPMNPictures.LANE,
                    BPMNImageResources.INSTANCE.lane().getSafeUri());
                put(BPMNPictures.PLUS_QUARE,
                    BPMNImageResources.INSTANCE.plusSquare().getSafeUri());
                put(BPMNPictures.SUB_PROCESS_REUSABLE,
                    BPMNImageResources.INSTANCE.subProcessReusable().getSafeUri());
                put(BPMNPictures.SUB_PROCESS_EMBEDDED,
                    BPMNImageResources.INSTANCE.subProcessEmbedded().getSafeUri());
                put(BPMNPictures.PARALLEL_EVENT,
                    BPMNImageResources.INSTANCE.gatewayParallelEvent().getSafeUri());
                put(BPMNPictures.PARALLEL_MULTIPLE,
                    BPMNImageResources.INSTANCE.gatewayParallelMultiple().getSafeUri());
                put(BPMNPictures.EXCLUSIVE,
                    BPMNImageResources.INSTANCE.gatewayExclusive().getSafeUri());
                put(BPMNPictures.GLYPH_OOME_HACK,
                    BPMNImageResources.INSTANCE.glyphOOMEHack().getSafeUri());
            }};

    @Override
    public Class<BPMNPictures> getSourceType() {
        return BPMNPictures.class;
    }

    @Override
    public boolean thumbFor(final BPMNPictures source) {
        return null != get(source);
    }

    @Override
    public SafeUri getThumbnailUri(final BPMNPictures source) {
        return get(source);
    }

    private SafeUri get(final BPMNPictures source) {
        checkNotNull("source",
                     source);
        return PICTURE_URIS.get(source);
    }
}
