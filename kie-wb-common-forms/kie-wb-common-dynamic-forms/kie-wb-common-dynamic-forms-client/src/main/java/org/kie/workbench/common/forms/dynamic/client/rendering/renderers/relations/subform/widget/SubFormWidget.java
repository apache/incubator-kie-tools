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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform.widget;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.kie.workbench.common.forms.processing.engine.handling.IsNestedModel;

@Templated
public class SubFormWidget extends Composite implements TakesValue<Object>, IsNestedModel {

    @Inject
    private DynamicFormRenderer formRenderer;

    @Inject
    @DataField
    private FlowPanel formContent;

    private FormRenderingContext renderingContext;

    @PostConstruct
    protected void init() {
        formContent.add( formRenderer );
    }

    public void render( FormRenderingContext renderingContext ) {
        this.renderingContext = renderingContext;
        formRenderer.render( renderingContext );
    }

    protected void render() {
        formRenderer.render( renderingContext );
    }

    @Override
    public Object getValue() {
        return renderingContext != null ? renderingContext.getModel() : null;
    }

    @Override
    public void setValue( Object value ) {
        formRenderer.bind( value );
    }

    @Override
    public void addFieldChangeHandler( FieldChangeHandler handler ) {
        formRenderer.addFieldChangeHandler( handler );
    }

    public void unBind() {
        formRenderer.unBind();
    }

    public void setReadOnly( boolean readOnly ) {
        formRenderer.switchToMode( readOnly ? RenderMode.READ_ONLY_MODE : RenderMode.EDIT_MODE );
    }
}
