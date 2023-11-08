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


package org.kie.workbench.common.stunner.sw.client.resources;

import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;

public interface GlyphFactory {

    ImageDataUriGlyph START = ImageDataUriGlyph.create(ImageResources.INSTANCE.start().getSrc());

    ImageDataUriGlyph END = ImageDataUriGlyph.create(ImageResources.INSTANCE.end().getSrc());

    ImageDataUriGlyph STATE_INJECT = ImageDataUriGlyph.create(ImageResources.INSTANCE.stateInject().getSrc());

    ImageDataUriGlyph STATE_SLEEP = ImageDataUriGlyph.create(ImageResources.INSTANCE.stateSleep().getSrc());

    ImageDataUriGlyph STATE_PARALLEL = ImageDataUriGlyph.create(ImageResources.INSTANCE.stateParallel().getSrc());

    ImageDataUriGlyph STATE_FOREACH = ImageDataUriGlyph.create(ImageResources.INSTANCE.stateForeach().getSrc());

    ImageDataUriGlyph STATE_CALLBACK = ImageDataUriGlyph.create(ImageResources.INSTANCE.stateCallback().getSrc());

    ImageDataUriGlyph STATE_SWITCH = ImageDataUriGlyph.create(ImageResources.INSTANCE.stateSwitch().getSrc());

    ImageDataUriGlyph STATE_OPERATION = ImageDataUriGlyph.create(ImageResources.INSTANCE.stateOperation().getSrc());

    ImageDataUriGlyph STATE_EVENT = ImageDataUriGlyph.create(ImageResources.INSTANCE.stateEvent().getSrc());

    ImageDataUriGlyph EVENTS = ImageDataUriGlyph.create(ImageResources.INSTANCE.events().getSrc());

    ImageDataUriGlyph EVENT = ImageDataUriGlyph.create(ImageResources.INSTANCE.event().getSrc());

    ImageDataUriGlyph CALL_FUNCTION = ImageDataUriGlyph.create(ImageResources.INSTANCE.callFunction().getSrc());

    ImageDataUriGlyph CALL_SUBFLOW = ImageDataUriGlyph.create(ImageResources.INSTANCE.callSubflow().getSrc());

    ImageDataUriGlyph EVENT_TIMEOUT = ImageDataUriGlyph.create(ImageResources.INSTANCE.eventTimeout().getSrc());

    ImageDataUriGlyph TRANSITION = ImageDataUriGlyph.create(ImageResources.INSTANCE.transition().getSrc());

    ImageDataUriGlyph TRANSITION_START = ImageDataUriGlyph.create(ImageResources.INSTANCE.transitionStart().getSrc());

    ImageDataUriGlyph TRANSITION_ERROR = ImageDataUriGlyph.create(ImageResources.INSTANCE.transitionError().getSrc());

    ImageDataUriGlyph TRANSITION_ACTION = ImageDataUriGlyph.create(ImageResources.INSTANCE.transitionAction().getSrc());

    ImageDataUriGlyph TRANSITION_COMPENSATION = ImageDataUriGlyph.create(ImageResources.INSTANCE.transitionCompensation().getSrc());

    ImageDataUriGlyph TRANSITION_DEFAULT_CONDITION = ImageDataUriGlyph.create(ImageResources.INSTANCE.transitionDefaultCondition().getSrc());

    ImageDataUriGlyph TRANSITION_DATA_CONDITION = ImageDataUriGlyph.create(ImageResources.INSTANCE.transitionDataCondition().getSrc());

    ImageDataUriGlyph TRANSITION_EVENT_CONDITION = ImageDataUriGlyph.create(ImageResources.INSTANCE.transitionEventCondition().getSrc());
}
