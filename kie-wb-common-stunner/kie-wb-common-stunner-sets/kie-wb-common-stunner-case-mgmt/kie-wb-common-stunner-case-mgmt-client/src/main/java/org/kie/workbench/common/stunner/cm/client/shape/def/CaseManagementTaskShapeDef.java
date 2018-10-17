/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.client.shape.def;

import java.util.Map;

import com.google.gwt.safehtml.shared.SafeUri;
import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNImageResources;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.cm.client.shape.view.ActivityView;
import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;

public final class CaseManagementTaskShapeDef
        implements CaseManagementActivityShapeDef<BaseTask, ActivityView> {

    public static final Map<Class<? extends BaseTask>, ImageDataUriGlyph> GLYPHS =
            new Maps.Builder<Class<? extends BaseTask>, ImageDataUriGlyph>()
                    .put(NoneTask.class, ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.task().getSafeUri()))
                    .put(UserTask.class, ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.taskUser().getSafeUri()))
                    .put(ScriptTask.class, ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.taskScript().getSafeUri()))
                    .put(BusinessRuleTask.class, ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.taskBusinessRule().getSafeUri()))
                    .build();

    @Override
    public SafeUri getIconUri(final Class<? extends BaseTask> task) {
        return GLYPHS.get(task).getUri();
    }

    @Override
    public Class<? extends ShapeDef> getType() {
        return CaseManagementTaskShapeDef.class;
    }
}
