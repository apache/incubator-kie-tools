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

import org.kie.workbench.common.stunner.core.client.shape.SvgDataUriGlyph;

public interface BPMNSVGGlyphFactory {

    SvgDataUriGlyph NONE_TASK_GLYPH =
            SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.task().getSafeUri());

    SvgDataUriGlyph USER_TASK_GLYPH =
            SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.taskUser().getSafeUri());

    SvgDataUriGlyph SCRIPT_TASK_GLYPH =
            SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.taskScript().getSafeUri());

    SvgDataUriGlyph BUSINESS_RULE_TASK_GLYPH =
            SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.taskBusinessRule().getSafeUri());

    SvgDataUriGlyph PARALLEL_MULTIPLE_GATEWAY_GLYPH =
            SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.gatewayParallelMultiple().getSafeUri());

    SvgDataUriGlyph EXCLUSIVE_GATEWAY_GLYPH =
            SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.gatewayExclusive().getSafeUri());

    SvgDataUriGlyph INCLUSIVE_GATEWAY_GLYPH =
            SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.gatewayInclusive().getSafeUri());

    SvgDataUriGlyph START_NONE_EVENT_GLYPH =
            SvgDataUriGlyph.Builder
                    .create()
                    .setUri(BPMNImageResources.INSTANCE.eventStart().getSafeUri())
                    .build();

    SvgDataUriGlyph START_SIGNAL_EVENT_GLYPH =
            SvgDataUriGlyph.Builder
                    .create()
                    .setUri(BPMNImageResources.INSTANCE.eventStart().getSafeUri())
                    .addUri("eventSignal",
                            BPMNImageResources.INSTANCE.eventSignal().getSafeUri())
                    .build("eventSignal");

    SvgDataUriGlyph START_TIMER_EVENT_GLYPH =
            SvgDataUriGlyph.Builder
                    .create()
                    .setUri(BPMNImageResources.INSTANCE.eventStart().getSafeUri())
                    .addUri("eventTimer",
                            BPMNImageResources.INSTANCE.eventTimer().getSafeUri())
                    .build("eventTimer");

    SvgDataUriGlyph START_MESSAGE_EVENT_GLYPH =
            SvgDataUriGlyph.Builder
                    .create()
                    .setUri(BPMNImageResources.INSTANCE.eventStart().getSafeUri())
                    .addUri("eventMessage",
                            BPMNImageResources.INSTANCE.eventMessage().getSafeUri())
                    .build("eventMessage");

    SvgDataUriGlyph START_ERROR_EVENT_GLYPH =
            SvgDataUriGlyph.Builder
                    .create()
                    .setUri(BPMNImageResources.INSTANCE.eventStart().getSafeUri())
                    .addUri("eventStartError",
                            BPMNImageResources.INSTANCE.eventStartError().getSafeUri())
                    .build("eventStartError");

    SvgDataUriGlyph END_NONE_EVENT_GLYPH =
            SvgDataUriGlyph.Builder
                    .create()
                    .setUri(BPMNImageResources.INSTANCE.eventEnd().getSafeUri())
                    .build();

    SvgDataUriGlyph END_SIGNAL_EVENT_GLYPH =
            SvgDataUriGlyph.Builder
                    .create()
                    .setUri(BPMNImageResources.INSTANCE.eventEnd().getSafeUri())
                    .addUri("eventSignal",
                            BPMNImageResources.INSTANCE.eventSignal().getSafeUri())
                    .build("eventSignal");

    SvgDataUriGlyph END_MESSAGE_EVENT_GLYPH =
            SvgDataUriGlyph.Builder
                    .create()
                    .setUri(BPMNImageResources.INSTANCE.eventEnd().getSafeUri())
                    .addUri("eventMessage",
                            BPMNImageResources.INSTANCE.eventMessage().getSafeUri())
                    .build("eventMessage");

    SvgDataUriGlyph END_TERMINATE_EVENT_GLYPH =
            SvgDataUriGlyph.Builder
                    .create()
                    .setUri(BPMNImageResources.INSTANCE.eventEnd().getSafeUri())
                    .addUri("eventEndTerminate",
                            BPMNImageResources.INSTANCE.eventEndTerminate().getSafeUri())
                    .build("eventEndTerminate");

    SvgDataUriGlyph END_ERROR_EVENT_GLYPH =
            SvgDataUriGlyph.Builder
                    .create()
                    .setUri(BPMNImageResources.INSTANCE.eventEnd().getSafeUri())
                    .addUri("eventEndError",
                            BPMNImageResources.INSTANCE.eventEndError().getSafeUri())
                    .build("eventEndError");

    SvgDataUriGlyph INTERMEDIATE_NONE_EVENT_GLYPH =
            SvgDataUriGlyph.Builder
                    .create()
                    .setUri(BPMNImageResources.INSTANCE.eventIntermediate().getSafeUri())
                    .build();

    SvgDataUriGlyph INTERMEDIATE_MESSAGE_EVENT_GLYPH =
            SvgDataUriGlyph.Builder
                    .create()
                    .setUri(BPMNImageResources.INSTANCE.eventIntermediate().getSafeUri())
                    .addUri("eventMessage",
                            BPMNImageResources.INSTANCE.eventMessage().getSafeUri())
                    .build("eventMessage");

    SvgDataUriGlyph INTERMEDIATE_SIGNAL_EVENT_GLYPH =
            SvgDataUriGlyph.Builder
                    .create()
                    .setUri(BPMNImageResources.INSTANCE.eventIntermediate().getSafeUri())
                    .addUri("eventSignal",
                            BPMNImageResources.INSTANCE.eventSignal().getSafeUri())
                    .build("eventSignal");

    SvgDataUriGlyph INTERMEDIATE_TIMER_EVENT_GLYPH =
            SvgDataUriGlyph.Builder
                    .create()
                    .setUri(BPMNImageResources.INSTANCE.eventIntermediate().getSafeUri())
                    .addUri("eventTimer",
                            BPMNImageResources.INSTANCE.eventTimer().getSafeUri())
                    .build("eventTimer");

    SvgDataUriGlyph INTERMEDIATE_ERROR_EVENT_GLYPH =
            SvgDataUriGlyph.Builder
                    .create()
                    .setUri(BPMNImageResources.INSTANCE.eventIntermediate().getSafeUri())
                    .addUri("eventIntermediateError",
                            BPMNImageResources.INSTANCE.eventIntermediateError().getSafeUri())
                    .build("eventIntermediateError");

    SvgDataUriGlyph LANE_GLYPH =
            SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.lane().getSafeUri());

    SvgDataUriGlyph REUSABLE_SUBPROCESS_GLYPH =
            SvgDataUriGlyph.Builder
                    .create()
                    .setUri(BPMNImageResources.INSTANCE.subProcess().getSafeUri())
                    .addUri("subProcessReusable",
                            BPMNImageResources.INSTANCE.subProcessReusable().getSafeUri())
                    .build("subProcessReusable");

    SvgDataUriGlyph EMBEDDED_SUBPROCESS_GLYPH =
            SvgDataUriGlyph.Builder
                    .create()
                    .setUri(BPMNImageResources.INSTANCE.subProcess().getSafeUri())
                    .addUri("subProcessEmbedded",
                            BPMNImageResources.INSTANCE.subProcessAdHoc().getSafeUri())
                    .build("subProcessEmbedded");

    SvgDataUriGlyph ADHOC_SUBPROCESS_GLYPH =
            SvgDataUriGlyph.Builder
                    .create()
                    .setUri(BPMNImageResources.INSTANCE.subProcess().getSafeUri())
                    .addUri("subProcessAdHoc",
                            BPMNImageResources.INSTANCE.subProcessAdHoc().getSafeUri())
                    .build("subProcessAdHoc");

    SvgDataUriGlyph EVENT_SUBPROCESS_GLYPH =
            SvgDataUriGlyph.Builder
                    .create()
                    .setUri(BPMNImageResources.INSTANCE.subProcessEvent().getSafeUri())
                    .build("subProcessEvent");

    SvgDataUriGlyph MULTIPLE_INSTANCE_SUBPROCESS_GLYPH =
            SvgDataUriGlyph.Builder
                    .create()
                    .setUri(BPMNImageResources.INSTANCE.subProcess().getSafeUri())
                    .addUri("subProcessMultipleInstance",
                            BPMNImageResources.INSTANCE.subProcessMultipleInstance().getSafeUri())
                    .build("subProcessMultipleInstance");
}
