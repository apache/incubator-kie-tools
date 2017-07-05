/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.shape.def;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.safehtml.shared.SafeUri;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNImageResources;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskTypes;
import org.kie.workbench.common.stunner.core.client.shape.SvgDataUriGlyph;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class TaskShapeDef
        implements BPMNSvgShapeDef<BaseTask> {

    private static final String SVG_TASK_BR = "taskBusinessRule";
    private static final String SVG_TASK_SCRIPT = "taskScript";
    private static final String SVG_TASK_USER = "taskUser";

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

    public final static Map<Class<? extends BaseTask>, SafeUri> ICONS = new HashMap<Class<? extends BaseTask>, SafeUri>(2) {{
        put(NoneTask.class,
            BPMNImageResources.INSTANCE.taskUser().getSafeUri());
        put(UserTask.class,
            BPMNImageResources.INSTANCE.taskUser().getSafeUri());
        put(ScriptTask.class,
            BPMNImageResources.INSTANCE.taskScript().getSafeUri());
        put(BusinessRuleTask.class,
            BPMNImageResources.INSTANCE.taskBusinessRule().getSafeUri());
    }};

    @Override
    public Glyph getGlyph(final Class<? extends BaseTask> type) {
        return SvgDataUriGlyph.create(ICONS.get(type));
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
    public boolean isSVGViewVisible(final String viewName,
                                    final BaseTask element) {
        switch (viewName) {
            case SVG_TASK_USER:
                return isTaskType(element,
                                  TaskTypes.USER);
            case SVG_TASK_SCRIPT:
                return isTaskType(element,
                                  TaskTypes.SCRIPT);
            case SVG_TASK_BR:
                return isTaskType(element,
                                  TaskTypes.BUSINESS_RULE);
        }
        return false;
    }

    private boolean isTaskType(final BaseTask element,
                               final TaskTypes types) {
        return element.getTaskType().getValue().equals(types);
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final BaseTask task) {
        return factory.task(getWidth(task),
                            getHeight(task),
                            true);
    }

    @Override
    public Class<BPMNSVGViewFactory> getViewFactoryType() {
        return BPMNSVGViewFactory.class;
    }
}