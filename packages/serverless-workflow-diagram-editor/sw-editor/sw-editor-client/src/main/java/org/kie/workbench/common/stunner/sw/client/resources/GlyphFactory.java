/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.client.resources;

import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;

public interface GlyphFactory {

    ImageDataUriGlyph START = ImageDataUriGlyph.create(ImageResources.INSTANCE.start().getSafeUri());

    ImageDataUriGlyph END = ImageDataUriGlyph.create(ImageResources.INSTANCE.end().getSafeUri());

    ImageDataUriGlyph STATE_INJECT = ImageDataUriGlyph.create(ImageResources.INSTANCE.stateInject().getSafeUri());

    ImageDataUriGlyph STATE_SWITCH = ImageDataUriGlyph.create(ImageResources.INSTANCE.stateSwitch().getSafeUri());

    ImageDataUriGlyph STATE_OPERATION = ImageDataUriGlyph.create(ImageResources.INSTANCE.stateOperation().getSafeUri());

    ImageDataUriGlyph STATE_EVENT = ImageDataUriGlyph.create(ImageResources.INSTANCE.stateEvent().getSafeUri());

    ImageDataUriGlyph EVENTS = ImageDataUriGlyph.create(ImageResources.INSTANCE.events().getSafeUri());

    ImageDataUriGlyph EVENT = ImageDataUriGlyph.create(ImageResources.INSTANCE.event().getSafeUri());

    ImageDataUriGlyph CALL_FUNCTION = ImageDataUriGlyph.create(ImageResources.INSTANCE.callFunction().getSafeUri());

    ImageDataUriGlyph CALL_SUBFLOW = ImageDataUriGlyph.create(ImageResources.INSTANCE.callSubflow().getSafeUri());

    ImageDataUriGlyph EVENT_TIMEOUT = ImageDataUriGlyph.create(ImageResources.INSTANCE.eventTimeout().getSafeUri());

    ImageDataUriGlyph TRANSITION = ImageDataUriGlyph.create(ImageResources.INSTANCE.transition().getSafeUri());

    ImageDataUriGlyph TRANSITION_START = ImageDataUriGlyph.create(ImageResources.INSTANCE.transitionStart().getSafeUri());

    ImageDataUriGlyph TRANSITION_ERROR = ImageDataUriGlyph.create(ImageResources.INSTANCE.transitionError().getSafeUri());

    ImageDataUriGlyph TRANSITION_CONDITION = ImageDataUriGlyph.create(ImageResources.INSTANCE.transitionCondition().getSafeUri());

    ImageDataUriGlyph TRANSITION_ACTION = ImageDataUriGlyph.create(ImageResources.INSTANCE.transitionAction().getSafeUri());

    ImageDataUriGlyph TRANSITION_COMPENSATION = ImageDataUriGlyph.create(ImageResources.INSTANCE.transitionCompensation().getSafeUri());
}
