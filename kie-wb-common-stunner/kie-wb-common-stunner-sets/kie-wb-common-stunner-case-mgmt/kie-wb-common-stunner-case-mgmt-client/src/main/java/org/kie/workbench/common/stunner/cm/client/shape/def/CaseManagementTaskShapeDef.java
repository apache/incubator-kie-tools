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

import com.google.gwt.safehtml.shared.SafeUri;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.TaskShapeDef;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;

public final class CaseManagementTaskShapeDef
        implements CaseManagementActivityShapeDef<BaseTask> {

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

    @Override
    public SafeUri getIconUri(final Class<? extends BaseTask> task) {
        return TaskShapeDef.ICONS.get(task);
    }

    @Override
    public Class<? extends ShapeDef> getType() {
        return CaseManagementTaskShapeDef.class;
    }
}
