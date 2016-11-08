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

package org.kie.workbench.common.forms.dynamic.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.IsFormView;
import org.kie.workbench.common.forms.dynamic.client.helper.MapModelBindingHelper;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldLayoutComponent;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContextGeneratorService;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.impl.relations.SubFormFieldDefinition;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.kie.workbench.common.forms.processing.engine.handling.FormHandler;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform.widget.SubFormWidget;
import org.uberfire.mvp.Command;

@Dependent
public class DynamicFormRenderer implements IsWidget, IsFormView {

    public interface DynamicFormRendererView extends IsWidget {
        void setPresenter( DynamicFormRenderer presenter );

        void render( FormRenderingContext context );
        void bind();

        FieldLayoutComponent getFieldLayoutComponentForField( FieldDefinition field );
    }

    private DynamicFormRendererView view;

    private Caller<FormRenderingContextGeneratorService> transformerService;

    private FormHandler formHandler;

    private FormRenderingContext context;

    private MapModelBindingHelper helper;

    @Inject
    public DynamicFormRenderer( DynamicFormRendererView view,
                                Caller<FormRenderingContextGeneratorService> transformerService,
                                FormHandler formHandler,
                                MapModelBindingHelper helper ) {
        this.view = view;
        this.transformerService = transformerService;
        this.formHandler = formHandler;
        this.helper = helper;
    }

    @PostConstruct
    protected void init() {
        view.setPresenter( this );
    }

    public void renderDefaultForm( final Object model ) {
        renderDefaultForm( model, null );
    }

    public void renderDefaultForm( final Object model, final Command callback ) {
        transformerService.call( new RemoteCallback<FormRenderingContext>() {
            @Override
            public void callback( FormRenderingContext context ) {
                context.setModel( model );
                render( context );
                if ( callback != null ) {
                    callback.execute();
                }
            }
        } ).createContext( model );
    }

    public void render ( FormRenderingContext context ) {
        Assert.notNull( "FormRenderingContext must not be null", context);

        this.context = context;

        if ( context instanceof MapModelRenderingContext && context.getParentContext() == null ) {
            MapModelRenderingContext mapContext = (MapModelRenderingContext) context;
            helper.initContext( mapContext );
            ( (MapModelRenderingContext) context ).setModel(  mapContext.getModel() );
        }

        view.render( context );
        if ( context.getModel() != null ) {
            bind( context.getModel() );
        }
    }

    public void bind( Object model ) {
        if ( context != null && model != null ) {
            context.setModel( model );
            formHandler.setUp( model );
            view.bind();
        }
    }

    protected void bind( FieldRenderer renderer ) {
        doBind( renderer );
    }

    protected void doBind( FieldRenderer renderer ) {
        if ( isInitialized() &&  renderer.getFormField() != null) {
            formHandler.registerInput( renderer.getFormField() );
        }
    }

    public void addFieldChangeHandler( FieldChangeHandler handler ) {
        addFieldChangeHandler( null, handler );
    }

    public void addFieldChangeHandler( String fieldName, FieldChangeHandler handler ) {
        if ( context != null && isInitialized() ) {
            if ( fieldName != null ) {
                FieldDefinition field = context.getRootForm().getFieldByName( fieldName );
                if ( field == null ) {
                    throw new IllegalArgumentException( "Form doesn't contain any field identified by: '" + fieldName + "'" );
                } else {
                    formHandler.addFieldChangeHandler( fieldName, handler );
                }
            } else {
                formHandler.addFieldChangeHandler( handler );
            }
        }
    }

    public void unBind() {
        if ( isInitialized() ) {
            for ( FieldDefinition field : context.getRootForm().getFields() ) {
                if ( field instanceof SubFormFieldDefinition ) {
                    FieldLayoutComponent component = view.getFieldLayoutComponentForField( field );
                    SubFormWidget subFormWidget = (SubFormWidget) component.getFieldRenderer().getInputWidget();
                    subFormWidget.unBind();
                }
            }
            formHandler.clear();
        }
    }

    public void setModel( Object model ) {
        bind( model );
    }

    public Object getModel() {
        if ( formHandler != null ) {
            return formHandler.getModel();
        }
        return null;
    }

    public void switchToMode( RenderMode renderMode ) {
        Assert.notNull( "RenderMode cannot be null", renderMode );
        RenderMode currentMode = context.getRenderMode();
        if ( context != null && isInitialized() && !currentMode.equals( renderMode ) ) {

            context.setRenderMode( renderMode );

            if ( currentMode.equals( RenderMode.PRETTY_MODE )) {
                render( context );
            } else if ( renderMode.equals( RenderMode.PRETTY_MODE ) ) {
                render( context );
            } else {
                formHandler.setReadOnly( RenderMode.READ_ONLY_MODE.equals( renderMode ) );
            }
        }
    }

    public boolean isValid() {
        return formHandler.validate();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    protected boolean isInitialized() {
        return formHandler != null;
    }
}
