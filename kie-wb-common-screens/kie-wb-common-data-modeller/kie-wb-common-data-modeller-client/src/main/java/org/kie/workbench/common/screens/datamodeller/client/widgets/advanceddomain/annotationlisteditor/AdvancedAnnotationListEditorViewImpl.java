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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.ColumnOffset;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationwizard.CreateAnnotationWizard;
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
    Button addAnnotationButton;

    @UiField
    PanelGroup accordionsContainer;

    private Presenter presenter;

    private Map<Annotation, Panel> annotationAccordion = new HashMap<Annotation, Panel>();

    private boolean readonly = false;

    @Inject
    private SyncBeanManager iocManager;

    public AdvancedAnnotationListEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
        accordionsContainer.setId( DOM.createUniqueId() );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void loadAnnotations( List<Annotation> annotations,
                                 Map<String, AnnotationSource> annotationSources ) {
        if ( annotations != null ) {
            for ( Annotation annotation : annotations ) {
                createAnnotationAccordionGroup( annotation, annotationSources != null ?
                        annotationSources.get( annotation.getClassName() ) : null );
            }
        }
    }

    @Override
    public void removeAnnotation( Annotation annotation ) {
        final Panel accordionGroup = annotationAccordion.get( annotation );
        if ( accordionGroup != null ) {
            accordionsContainer.remove( accordionGroup );
            annotationAccordion.remove( annotation );
        }
    }

    private void createAnnotationAccordionGroup( final Annotation annotation,
                                                 final AnnotationSource annotationSource ) {

        final Panel container = new Panel();
        final PanelHeader header = new PanelHeader();
        final PanelCollapse collapse = new PanelCollapse();
        final PanelBody body = new PanelBody();

        container.add( header );
        collapse.add( body );
        container.add( collapse );

        final Button remove = new Button();
        remove.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent clickEvent ) {
                presenter.onDeleteAnnotation( annotation );
            }
        } );
        remove.setPull( Pull.RIGHT );
        remove.setIcon( IconType.TRASH );
        remove.setType( ButtonType.DANGER );
        remove.setSize( ButtonSize.SMALL );
        remove.getElement().getStyle().setMarginTop( -4, Style.Unit.PX );
        header.add( remove );

        final Heading heading = new Heading( HeadingSize.H4 );
        final Anchor anchor = new Anchor();
        anchor.setText( accordionHeading( annotation ) );
        anchor.setDataToggle( Toggle.COLLAPSE );
        anchor.setDataParent( accordionsContainer.getId() );
        anchor.setDataTargetWidget( collapse );
        anchor.addStyleName( "collapsed" );
        heading.add( anchor );
        header.add( heading );

        accordionsContainer.add( container );

        if ( annotation.getAnnotationDefinition() != null &&
                annotation.getAnnotationDefinition().getValuePairs() != null ) {
            for ( AnnotationValuePairDefinition valuePairDefinition : annotation.getAnnotationDefinition().getValuePairs() ) {
                body.add( createValuePairItem( annotation, valuePairDefinition, annotationSource ) );
            }
        }
    }

    private Widget createValuePairItem( final Annotation annotation,
                                        final AnnotationValuePairDefinition valuePairDefinition,
                                        final AnnotationSource annotationSource ) {
        final Row valuePairRow = new Row();
        final FormGroup formGroup = new FormGroup();
        valuePairRow.add( formGroup );

        final FormLabel formLabel = new FormLabel();
        formLabel.addStyleName( ColumnSize.MD_3.getCssName() );
        formLabel.setText( valuePairDefinition.getName() );
        formGroup.add( formLabel );

        final Column column = new Column( ColumnSize.MD_9 );
        formGroup.add( column );
        final TextBox content = new TextBox();
        column.add( content );
        final String valuePairString = getValuePairStringValue( annotation, valuePairDefinition, annotationSource );
        content.setText( valuePairString );
        content.setReadOnly( true );
        content.setTitle( valuePairString );

        final Button editButton = new Button(
                Constants.INSTANCE.advanced_domain_annotation_list_editor_action_edit(),
                new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.onEditValuePair( annotation, valuePairDefinition.getName() );
                    }
                } );
        editButton.setEnabled( !readonly );
        editButton.setIcon( IconType.EDIT );

        final Button clearButton = new Button(
                Constants.INSTANCE.advanced_domain_annotation_list_editor_action_clear(),
                new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.onClearValuePair( annotation, valuePairDefinition.getName() );
                    }
                } );
        clearButton.setEnabled( !readonly );
        clearButton.setIcon( IconType.ERASER );

        final FormGroup formGroupButton = new FormGroup();
        formGroupButton.add( editButton );
        formGroupButton.add( clearButton );
        final Column columnButton = new Column( ColumnSize.MD_9 );
        columnButton.setOffset( ColumnOffset.MD_3 );
        columnButton.add( formGroupButton );
        valuePairRow.add( columnButton );

        return valuePairRow;
    }

    private String getValuePairStringValue( Annotation annotation,
                                            AnnotationValuePairDefinition valuePairDefinition,
                                            AnnotationSource annotationSource ) {

        Object value = annotation.getValue( valuePairDefinition.getName() );
        String strValue;

        if ( value == null ) {
            strValue = Constants.INSTANCE.advanced_domain_annotation_list_editor_message_value_not_set();
        } else {
            strValue = annotationSource != null ? annotationSource.getValuePairSource( valuePairDefinition.getName() ) : null;
            if ( strValue == null ) {
                strValue = Constants.INSTANCE.advanced_domain_annotation_list_editor_message_source_code_not_available();
            }
        }

        return strValue;
    }

    private String accordionHeading( final Annotation annotation ) {
        return "@" + annotation.getClassName();
    }

    @Override
    public void showYesNoDialog( String message,
                                 Command yesCommand,
                                 Command noCommand,
                                 Command cancelCommand ) {

        YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(
                CommonConstants.INSTANCE.Information(), message, yesCommand, noCommand, cancelCommand );

        yesNoCancelPopup.setClosable( false );
        yesNoCancelPopup.show();
    }

    @Override
    public void invokeCreateAnnotationWizard( final Callback<Annotation> callback,
                                              final KieProject kieProject,
                                              final ElementType elementType ) {
        final CreateAnnotationWizard addAnnotationWizard = iocManager.lookupBean( CreateAnnotationWizard.class ).getInstance();
        //When the wizard is closed destroy it to avoid memory leak
        addAnnotationWizard.onCloseCallback( new Callback<Annotation>() {
            @Override
            public void callback( Annotation result ) {
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

    @UiHandler( "addAnnotationButton" )
    void onAddAnnotation( ClickEvent event ) {
        presenter.onAddAnnotation();
    }
}
