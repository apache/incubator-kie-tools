/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.forms.common.rendering.client.widgets.slider.Slider;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.model.impl.basic.slider.SliderBase;

@Dependent
public class SliderFieldRenderer extends FieldRenderer<SliderBase> {

    private Slider slider;

    @Override
    public String getName() {
        return SliderBase.CODE;
    }

    @Override
    public void initInputWidget() {
        slider = new Slider( field.getMin().doubleValue(),
                             field.getMax().doubleValue(),
                             field.getPrecision().doubleValue(),
                             field.getStep().doubleValue() );
        slider.setEnabled( !field.getReadonly() && renderingContext.getRenderMode().equals( RenderMode.EDIT_MODE ) );
    }

    @Override
    public IsWidget getInputWidget() {
        return slider;
    }

    @Override
    public IsWidget getPrettyViewWidget() {
        initInputWidget();
        return getInputWidget();
    }

    @Override
    public String getSupportedCode() {
        return SliderBase.CODE;
    }

    @Override
    protected void setReadOnly( boolean readOnly ) {
        slider.setEnabled( !readOnly );
    }
}
