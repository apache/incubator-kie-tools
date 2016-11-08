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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.picture;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.forms.common.rendering.client.widgets.picture.PictureInput;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.model.impl.basic.image.PictureFieldDefinition;

@Dependent
public class PictureFieldRenderer extends FieldRenderer<PictureFieldDefinition> {

    private PictureInput pictureInput;

    @Inject
    public PictureFieldRenderer( PictureInput pictureInput ) {
        this.pictureInput = pictureInput;
    }

    @Override
    public String getName() {
        return PictureFieldDefinition.CODE;
    }

    @Override
    public void initInputWidget() {
        pictureInput.init( field.getSize().getWidth(), field.getSize().getHeight() );
        pictureInput.setReadOnly( field.getReadonly() || renderingContext.getRenderMode().equals( RenderMode.PRETTY_MODE ) );
    }

    @Override
    public IsWidget getInputWidget() {
        return pictureInput;
    }

    @Override
    public IsWidget getPrettyViewWidget() {
        initInputWidget();
        return getInputWidget();
    }

    @Override
    public String getSupportedCode() {
        return PictureFieldDefinition.CODE;
    }

    @Override
    protected void setReadOnly( boolean readOnly ) {
        pictureInput.setReadOnly( readOnly );
    }
}
