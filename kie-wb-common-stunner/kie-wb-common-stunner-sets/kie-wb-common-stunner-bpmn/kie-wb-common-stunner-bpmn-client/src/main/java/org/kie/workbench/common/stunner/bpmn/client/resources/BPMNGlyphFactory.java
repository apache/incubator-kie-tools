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

import org.kie.workbench.common.stunner.core.client.shape.ImageStripGlyph;

public interface BPMNGlyphFactory {

    ImageStripGlyph LANE = ImageStripGlyph.create(BPMNIconsStrip.class, 0);
    ImageStripGlyph SEQUENCE_FLOW = ImageStripGlyph.create(BPMNIconsStrip.class, 1);
    ImageStripGlyph EVENT_END_ERROR = ImageStripGlyph.create(BPMNIconsStrip.class, 2);
    ImageStripGlyph EVENT_END_MESSAGE = ImageStripGlyph.create(BPMNIconsStrip.class, 3);
    ImageStripGlyph EVENT_END_NONE = ImageStripGlyph.create(BPMNIconsStrip.class, 4);
    ImageStripGlyph EVENT_END_SIGNAL = ImageStripGlyph.create(BPMNIconsStrip.class, 5);
    ImageStripGlyph EVENT_END_TERMINATE = ImageStripGlyph.create(BPMNIconsStrip.class, 6);
    ImageStripGlyph EVENT_INTERMEDIATE_ERROR = ImageStripGlyph.create(BPMNIconsStrip.class, 7);
    ImageStripGlyph EVENT_INTERMEDIATE_MESSAGE = ImageStripGlyph.create(BPMNIconsStrip.class, 8);
    ImageStripGlyph EVENT_INTERMEDIATE_THROWING_MESSAGE = ImageStripGlyph.create(BPMNIconsStrip.class, 9);
    ImageStripGlyph EVENT_INTERMEDIATE_SIGNAL = ImageStripGlyph.create(BPMNIconsStrip.class, 10);
    ImageStripGlyph EVENT_INTERMEDIATE_THROWING_SIGNAL = ImageStripGlyph.create(BPMNIconsStrip.class, 11);
    ImageStripGlyph EVENT_INTERMEDIATE_TIMER = ImageStripGlyph.create(BPMNIconsStrip.class, 12);
    ImageStripGlyph EVENT_START_ERROR = ImageStripGlyph.create(BPMNIconsStrip.class, 13);
    ImageStripGlyph EVENT_START_MESSAGE = ImageStripGlyph.create(BPMNIconsStrip.class, 14);
    ImageStripGlyph EVENT_START_NONE = ImageStripGlyph.create(BPMNIconsStrip.class, 15);
    ImageStripGlyph EVENT_START_SIGNAL = ImageStripGlyph.create(BPMNIconsStrip.class, 16);
    ImageStripGlyph EVENT_START_TIMER = ImageStripGlyph.create(BPMNIconsStrip.class, 17);
    ImageStripGlyph GATEWAY_COMPLEX = ImageStripGlyph.create(BPMNIconsStrip.class, 18);
    ImageStripGlyph GATEWAY_EVENT = ImageStripGlyph.create(BPMNIconsStrip.class, 19);
    ImageStripGlyph GATEWAY_EXCLUSIVE = ImageStripGlyph.create(BPMNIconsStrip.class, 20);
    ImageStripGlyph GATEWAY_INCLUSIVE = ImageStripGlyph.create(BPMNIconsStrip.class, 21);
    ImageStripGlyph PARALLEL_EVENT_GATEWAY_GLYPH = ImageStripGlyph.create(BPMNIconsStrip.class, 22);
    ImageStripGlyph GATEWAY_PARALLEL_MULTIPLE = ImageStripGlyph.create(BPMNIconsStrip.class, 23);
    ImageStripGlyph SUBPROCESS_ADHOC = ImageStripGlyph.create(BPMNIconsStrip.class, 24);
    ImageStripGlyph SUBPROCESS_EMBEDDED = ImageStripGlyph.create(BPMNIconsStrip.class, 25);
    ImageStripGlyph SUBPROCESS_EVENT = ImageStripGlyph.create(BPMNIconsStrip.class, 26);
    ImageStripGlyph SUBPROCESS_MULTIPLE_INSTANCE = ImageStripGlyph.create(BPMNIconsStrip.class, 27);
    ImageStripGlyph SUBPROCESS_RESUABLE = ImageStripGlyph.create(BPMNIconsStrip.class, 28);
    ImageStripGlyph TASK = ImageStripGlyph.create(BPMNIconsStrip.class, 29);
    ImageStripGlyph TASK_BUSINESS_RULE = ImageStripGlyph.create(BPMNIconsStrip.class, 30);
    ImageStripGlyph TASK_MANUAL = ImageStripGlyph.create(BPMNIconsStrip.class, 31);
    ImageStripGlyph TASK_SCRIPT = ImageStripGlyph.create(BPMNIconsStrip.class, 32);
    ImageStripGlyph TASK_SERVICE = ImageStripGlyph.create(BPMNIconsStrip.class, 33);
    ImageStripGlyph TASK_USER = ImageStripGlyph.create(BPMNIconsStrip.class, 34);
    ImageStripGlyph EVENT_START_CONDITIONAL = ImageStripGlyph.create(BPMNIconsStrip.class, 35);
    ImageStripGlyph EVENT_INTERMEDIATE_CONDITIONAL = ImageStripGlyph.create(BPMNIconsStrip.class, 36);
    ImageStripGlyph EVENT_START_ESCALATION = ImageStripGlyph.create(BPMNIconsStrip.class, 37);
    ImageStripGlyph EVENT_INTERMEDIATE_ESCALATION = ImageStripGlyph.create(BPMNIconsStrip.class, 38);
    ImageStripGlyph EVENT_INTERMEDIATE_THROWING_ESCALATION = ImageStripGlyph.create(BPMNIconsStrip.class, 39);
    ImageStripGlyph EVENT_END_ESCALATION = ImageStripGlyph.create(BPMNIconsStrip.class, 40);
    ImageStripGlyph EVENT_START_COMPENSATION = ImageStripGlyph.create(BPMNIconsStrip.class, 43);
    ImageStripGlyph EVENT_INTERMEDIATE_COMPENSATION = ImageStripGlyph.create(BPMNIconsStrip.class, 44);
    ImageStripGlyph EVENT_INTERMEDIATE_THROWING_COMPENSATION = ImageStripGlyph.create(BPMNIconsStrip.class, 45);
    ImageStripGlyph EVENT_END_COMPENSATION = ImageStripGlyph.create(BPMNIconsStrip.class, 46);
    ImageStripGlyph ASSOCIATION = ImageStripGlyph.create(BPMNIconsStrip.class, 63);
}
