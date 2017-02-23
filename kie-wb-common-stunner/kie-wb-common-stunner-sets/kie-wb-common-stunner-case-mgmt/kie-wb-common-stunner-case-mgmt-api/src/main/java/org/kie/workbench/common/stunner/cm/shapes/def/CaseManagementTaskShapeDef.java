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

package org.kie.workbench.common.stunner.cm.shapes.def;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskType;
import org.kie.workbench.common.stunner.bpmn.shape.def.BPMNPictures;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementBaseTask;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementBusinessRuleTask;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementNoneTask;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementScriptTask;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementUserTask;
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
        extends AbstractShapeDef<CaseManagementBaseTask>
        implements RectangleShapeDef<CaseManagementBaseTask>,
                   HasChildShapeDefs<CaseManagementBaseTask> {

    @Override
    public double getAlpha(final CaseManagementBaseTask element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final CaseManagementBaseTask element) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha(final CaseManagementBaseTask element) {
        return 1;
    }

    @Override
    public String getBorderColor(final CaseManagementBaseTask element) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize(final CaseManagementBaseTask element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final CaseManagementBaseTask element) {
        return 1;
    }

    @Override
    public String getFontFamily(final CaseManagementBaseTask element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final CaseManagementBaseTask element) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public double getFontSize(final CaseManagementBaseTask element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize(final CaseManagementBaseTask element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition(final CaseManagementBaseTask element) {
        return HasTitle.Position.CENTER;
    }

    @Override
    public double getFontRotation(final CaseManagementBaseTask element) {
        return 0;
    }

    private static final PictureGlyphDef<CaseManagementBaseTask, BPMNPictures> TASK_GLYPH_DEF = new PictureGlyphDef<CaseManagementBaseTask, BPMNPictures>() {

        private final Map<Class<?>, BPMNPictures> PICTURES = new HashMap<Class<?>, BPMNPictures>(3) {{
            // TODO: Change NoneTask image!
            put(CaseManagementNoneTask.class,
                BPMNPictures.TASK_USER);
            put(CaseManagementUserTask.class,
                BPMNPictures.TASK_USER);
            put(CaseManagementScriptTask.class,
                BPMNPictures.TASK_SCRIPT);
            put(CaseManagementBusinessRuleTask.class,
                BPMNPictures.TASK_BUSINESS_RULE);
        }};

        @Override
        public String getGlyphDescription(final CaseManagementBaseTask element) {
            return element.getDescription();
        }

        @Override
        public BPMNPictures getSource(final Class<?> type) {
            return PICTURES.get(type);
        }
    };

    @Override
    public GlyphDef<CaseManagementBaseTask> getGlyphDef() {
        return TASK_GLYPH_DEF;
    }

    @Override
    public Map<ShapeDef<CaseManagementBaseTask>, HasChildren.Layout> getChildShapeDefs() {
        return new HashMap<ShapeDef<CaseManagementBaseTask>, HasChildren.Layout>() {{
            put(new TaskTypeProxy(),
                HasChildren.Layout.TOP);
        }};
    }

    @Override
    public double getWidth(final CaseManagementBaseTask element) {
        return element.getDimensionsSet().getWidth().getValue();
    }

    @Override
    public double getHeight(final CaseManagementBaseTask element) {
        return element.getDimensionsSet().getHeight().getValue();
    }

    @Override
    public double getCornerRadius(final CaseManagementBaseTask element) {
        return 5;
    }

    public final class TaskTypeProxy extends AbstractShapeDef<CaseManagementBaseTask> implements PictureShapeDef<CaseManagementBaseTask, BPMNPictures> {

        @Override
        public BPMNPictures getPictureSource(final CaseManagementBaseTask element) {
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
        public double getWidth(final CaseManagementBaseTask element) {
            return 15d;
        }

        @Override
        public double getHeight(final CaseManagementBaseTask element) {
            return 15d;
        }
    }
}
