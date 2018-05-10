/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.resources;

import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;

public interface BPMNSVGGlyphFactory {

    ImageDataUriGlyph NONE_TASK_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.task().getSafeUri());

    ImageDataUriGlyph USER_TASK_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.taskUser().getSafeUri());

    ImageDataUriGlyph SCRIPT_TASK_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.taskScript().getSafeUri());

    ImageDataUriGlyph BUSINESS_RULE_TASK_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.taskBusinessRule().getSafeUri());

    ImageDataUriGlyph PARALLEL_MULTIPLE_GATEWAY_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.gatewayParallelMultiple().getSafeUri());

    ImageDataUriGlyph EXCLUSIVE_GATEWAY_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.gatewayExclusive().getSafeUri());

    ImageDataUriGlyph INCLUSIVE_GATEWAY_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.gatewayInclusive().getSafeUri());

    ImageDataUriGlyph START_NONE_EVENT_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.eventStartNone().getSafeUri());

    ImageDataUriGlyph START_SIGNAL_EVENT_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.eventStartSignal().getSafeUri());

    ImageDataUriGlyph START_TIMER_EVENT_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.eventStartTimer().getSafeUri());

    ImageDataUriGlyph START_MESSAGE_EVENT_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.eventStartMessage().getSafeUri());

    ImageDataUriGlyph START_ERROR_EVENT_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.eventStartError().getSafeUri());

    ImageDataUriGlyph END_NONE_EVENT_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.eventEndNone().getSafeUri());

    ImageDataUriGlyph END_SIGNAL_EVENT_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.eventEndSignal().getSafeUri());

    ImageDataUriGlyph END_MESSAGE_EVENT_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.eventEndMessage().getSafeUri());

    ImageDataUriGlyph END_TERMINATE_EVENT_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.eventEndTerminate().getSafeUri());

    ImageDataUriGlyph END_ERROR_EVENT_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.eventEndError().getSafeUri());

    ImageDataUriGlyph INTERMEDIATE_MESSAGE_EVENT_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.eventIntermediateMessage().getSafeUri());

    ImageDataUriGlyph INTERMEDIATE_SIGNAL_EVENT_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.eventIntermediateSignal().getSafeUri());

    ImageDataUriGlyph INTERMEDIATE_TIMER_EVENT_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.eventIntermediateTimer().getSafeUri());

    ImageDataUriGlyph INTERMEDIATE_ERROR_EVENT_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.eventIntermediateError().getSafeUri());

    ImageDataUriGlyph INTERMEDIATE_SIGNAL_EVENT_THROWING_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.eventIntermediateSignalThrowing().getSafeUri());

    ImageDataUriGlyph INTERMEDIATE_MESSAGE_EVENT_THROWING_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.eventIntermediateMessageThrowing().getSafeUri());

    ImageDataUriGlyph LANE_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.lane().getSafeUri());

    ImageDataUriGlyph REUSABLE_SUBPROCESS_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.subProcessReusable().getSafeUri());

    ImageDataUriGlyph EMBEDDED_SUBPROCESS_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.subProcessEmbedded().getSafeUri());

    ImageDataUriGlyph ADHOC_SUBPROCESS_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.subProcessAdHoc().getSafeUri());

    ImageDataUriGlyph EVENT_SUBPROCESS_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.subProcessEvent().getSafeUri());

    ImageDataUriGlyph MULTIPLE_INSTANCE_SUBPROCESS_GLYPH = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.subProcessMultipleInstance().getSafeUri());
}
