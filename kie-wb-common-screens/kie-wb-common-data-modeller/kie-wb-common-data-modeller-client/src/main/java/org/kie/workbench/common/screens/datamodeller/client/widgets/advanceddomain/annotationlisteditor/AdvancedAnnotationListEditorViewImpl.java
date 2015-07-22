/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationlisteditor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.base.DivWidget;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationwizard.CreateAnnotationWizard;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.util.CommandDrivenAccordionGroup;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSource;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;

public class AdvancedAnnotationListEditorViewImpl
        extends Composite
        implements AdvancedAnnotationListEditorView {

    interface AdvancedAnnotationListEditorViewImplUiBinder
            extends
            UiBinder<Widget, AdvancedAnnotationListEditorViewImpl> {

    }

    private static AdvancedAnnotationListEditorViewImplUiBinder uiBinder = GWT.create( AdvancedAnnotationListEditorViewImplUiBinder.class );

    @UiField
    DivWidget containerPanel;

    @UiField
    Button addAnnotationButton;

    private DivWidget accordionsContainer = new DivWidget( );

    private Presenter presenter;

    private Map<Annotation, CommandDrivenAccordionGroup> annotationAccordion = new HashMap<Annotation, CommandDrivenAccordionGroup>(  );

    private boolean readonly = false;

    @Inject
    private SyncBeanManager iocManager;


    public AdvancedAnnotationListEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
        containerPanel.add( accordionsContainer );
        addAnnotationButton.setType( ButtonType.LINK );
        addAnnotationButton.setIcon( IconType.PLUS_SIGN );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void loadAnnotations( List<Annotation> annotations, Map<String, AnnotationSource> annotationSources ) {
        if ( annotations != null ) {
            for ( Annotation annotation : annotations ) {
                createAnnotationAccordionGroup( annotation, annotationSources != null ?
                        annotationSources.get( annotation.getClassName() ) : null );
            }
        }
    }

    @Override
    public void removeAnnotation( Annotation annotation ) {
        CommandDrivenAccordionGroup accordionGroup = annotationAccordion.get( annotation );
        if ( accordionGroup != null ) {
            accordionsContainer.remove( accordionGroup );
            annotationAccordion.remove( annotation );
        }
    }

    private void createAnnotationAccordionGroup( final Annotation annotation, final AnnotationSource annotationSource ) {

        CommandDrivenAccordionGroup accordionGroup = new CommandDrivenAccordionGroup( "delete", new Command() {
            @Override public void execute() {
                presenter.onDeleteAnnotation( annotation );
            }
        } );
        accordionGroup.setCommandEnabled( !readonly );
        annotationAccordion.put( annotation, accordionGroup );

        accordionGroup.setHeading( accordionHeading( annotation ));
        accordionsContainer.add( accordionGroup );

        if ( annotation.getAnnotationDefinition() != null &&
                annotation.getAnnotationDefinition().getValuePairs() != null ) {
            for ( AnnotationValuePairDefinition valuePairDefinition : annotation.getAnnotationDefinition().getValuePairs() ) {
                accordionGroup.add( createValuePairItem( annotation, valuePairDefinition, annotationSource ) );
            }
        }
    }

    private Widget createValuePairItem( final Annotation annotation,
            final AnnotationValuePairDefinition valuePairDefinition,
            final AnnotationSource annotationSource) {
        FlowPanel valuePairRow = new FlowPanel( );
        valuePairRow.addStyleName( "row-fluid");
        valuePairRow.addStyleName( "control-group" );

        valuePairRow.add( new Label( valuePairDefinition.getName() + ":" ) );

        TextBox content = new TextBox();
        content.addStyleName( "span8" );
        content.addStyleName( "controls" );
        String valuePairString = getValuePairStringValue( annotation, valuePairDefinition, annotationSource );
        content.setText( valuePairString );
        content.setReadOnly( true );
        content.setTitle( valuePairString );
        valuePairRow.add( content );

        Button editButton = new Button( "edit", new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.onEditValuePair( annotation, valuePairDefinition.getName() );
            }
        } );
        editButton.setType( ButtonType.LINK );
        editButton.setEnabled( !readonly );
        valuePairRow.add( editButton );

        Button clearButton = new Button( "clear", new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.onClearValuePair( annotation, valuePairDefinition.getName() );
            }
        } );
        clearButton.setType( ButtonType.LINK );
        clearButton.setEnabled( !readonly );
        valuePairRow.add( clearButton );


        return valuePairRow;
    }

    private String getValuePairStringValue( Annotation annotation,
            AnnotationValuePairDefinition valuePairDefinition,
            AnnotationSource annotationSource ) {

        Object value = annotation.getValue( valuePairDefinition.getName() );
        String strValue;

        if ( value == null ) {
            strValue =  "(value not set)";
        } else {
            strValue = annotationSource != null ? annotationSource.getValuePairSource( valuePairDefinition.getName() ) : null;
            if ( strValue == null ) {
                strValue = "(source code not available)";
            }
        }

        return strValue;
    }

    private String accordionHeading( Annotation annotation ) {
        return "@" + annotation.getClassName();
    }

    @Override
    public void showYesNoDialog( String message, Command yesCommand, Command noCommand, Command cancelCommand ) {

        YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(
                CommonConstants.INSTANCE.Information(), message, yesCommand, noCommand, cancelCommand);

        yesNoCancelPopup.setCloseVisible( false );
        yesNoCancelPopup.show();
    }

    @Override
    public void invokeCreateAnnotationWizard( final Callback<Annotation> callback,
            KieProject kieProject, ElementType elementType ) {
        final CreateAnnotationWizard addAnnotationWizard = iocManager.lookupBean( CreateAnnotationWizard.class ).getInstance();
        //When the wizard is closed destroy it to avoid memory leak
        addAnnotationWizard.onCloseCallback( new Callback<Annotation>() {
            @Override public void callback( Annotation result ) {
                iocManager.destroyBean( addAnnotationWizard );
                callback.callback( result );
            }
        } );
        addAnnotationWizard.init( kieProject, elementType );
        addAnnotationWizard.start();
    }

    @Override
    public void setReadonly( boolean readonly ) {
        this.readonly = readonly;
        addAnnotationButton.setEnabled( !readonly );
    }

    @Override
    public void clear() {
        accordionsContainer.clear();
    }

    @UiHandler( "addAnnotationButton")
    void onAddAnnotation( ClickEvent event ) {
        presenter.onAddAnnotation();
    }
}
