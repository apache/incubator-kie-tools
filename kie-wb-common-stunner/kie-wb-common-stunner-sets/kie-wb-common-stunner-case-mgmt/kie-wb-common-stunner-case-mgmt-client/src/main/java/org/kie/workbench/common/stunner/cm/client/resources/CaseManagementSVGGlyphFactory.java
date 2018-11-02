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

package org.kie.workbench.common.stunner.cm.client.resources;

import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;

public interface CaseManagementSVGGlyphFactory {

    ImageDataUriGlyph STAGE_GLYPH = ImageDataUriGlyph.create(CaseManagementImageResources.INSTANCE.stage().getSafeUri());

    ImageDataUriGlyph TASK_GLYPH = ImageDataUriGlyph.create(CaseManagementImageResources.INSTANCE.task().getSafeUri());

    ImageDataUriGlyph SUBPROCESS_GLYPH = ImageDataUriGlyph.create(CaseManagementImageResources.INSTANCE.subprocess().getSafeUri());

    ImageDataUriGlyph SUBCASE_GLYPH = ImageDataUriGlyph.create(CaseManagementImageResources.INSTANCE.subcase().getSafeUri());
}
