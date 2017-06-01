/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.cm.client.shape.def;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.stunner.bpmn.client.shape.BPMNPictures;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskType;
import org.kie.workbench.common.stunner.core.client.shape.HasChildren;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.kie.workbench.common.stunner.shapes.def.HasChildShapeDefs;
import org.kie.workbench.common.stunner.shapes.def.RectangleShapeDef;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureGlyphDef;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureShapeDef;

public final class CaseManagementTaskShapeDef
        extends AbstractShapeDef<BaseTask>
        implements RectangleShapeDef<BaseTask>,
                   HasChildShapeDefs<BaseTask> {

    private static final PictureGlyphDef<BaseTask, BPMNPictures> TASK_GLYPH_DEF = new PictureGlyphDef<BaseTask, BPMNPictures>() {

        private final Map<Class<?>, BPMNPictures> PICTURES = new HashMap<Class<?>, BPMNPictures>(3) {{
            // TODO: Change NoneTask image!
            put(NoneTask.class,
                BPMNPictures.GLYPH_OOME_HACK);
            put(UserTask.class,
                BPMNPictures.GLYPH_OOME_HACK);
            put(ScriptTask.class,
                BPMNPictures.GLYPH_OOME_HACK);
            put(BusinessRuleTask.class,
                BPMNPictures.GLYPH_OOME_HACK);
        }};

        @Override
        public String getGlyphDescription(final BaseTask element) {
            return element.getDescription();
        }

        @Override
        public BPMNPictures getSource(final Class<?> type) {
            return PICTURES.get(type);
        }
    };

    @Override
    public double getAlpha(final BaseTask element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final BaseTask element) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha(final BaseTask element) {
        return 1;
    }

    @Override
    public String getBorderColor(final BaseTask element) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize(final BaseTask element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final BaseTask element) {
        return 1;
    }

    @Override
    public String getFontFamily(final BaseTask element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final BaseTask element) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public String getFontBorderColor(final BaseTask element) {
        return element.getFontSet().getFontBorderColor().getValue();
    }

    @Override
    public double getFontSize(final BaseTask element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize(final BaseTask element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition(final BaseTask element) {
        return HasTitle.Position.CENTER;
    }

    @Override
    public double getFontRotation(final BaseTask element) {
        return 0;
    }

    @Override
    public GlyphDef<BaseTask> getGlyphDef() {
        return TASK_GLYPH_DEF;
    }

    @Override
    public Map<ShapeDef<BaseTask>, HasChildren.Layout> getChildShapeDefs() {
        return new HashMap<ShapeDef<BaseTask>, HasChildren.Layout>() {{
            put(new TaskTypeProxy(),
                HasChildren.Layout.TOP);
        }};
    }

    @Override
    public double getWidth(final BaseTask element) {
        return element.getDimensionsSet().getWidth().getValue();
    }

    @Override
    public double getHeight(final BaseTask element) {
        return element.getDimensionsSet().getHeight().getValue();
    }

    @Override
    public double getCornerRadius(final BaseTask element) {
        return 5;
    }

    public final class TaskTypeProxy extends AbstractShapeDef<BaseTask> implements PictureShapeDef<BaseTask, BPMNPictures> {

        @Override
        public BPMNPictures getPictureSource(final BaseTask element) {
            final TaskType taskType = element.getTaskType();
            switch (taskType.getValue()) {
                case USER:
                    return BPMNPictures.TASK_USER;
                case SCRIPT:
                    return BPMNPictures.TASK_SCRIPT;
                case BUSINESS_RULE:
                    return BPMNPictures.TASK_BUSINESS_RULE;
            }
            return null;
        }

        @Override
        public double getWidth(final BaseTask element) {
            return 15d;
        }

        @Override
        public double getHeight(final BaseTask element) {
            return 15d;
        }
    }
}
