/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtree.client.widget.popups;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.drools.workbench.models.guided.dtree.shared.model.parser.GuidedDecisionTreeParserError;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.AmbiguousRootParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.BindingNotFoundParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.DataTypeConversionErrorParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.DataTypeNotFoundParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.DefaultParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.InvalidRootParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.ParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.UnsupportedFieldConstraintParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.UnsupportedFieldConstraintTypeParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.UnsupportedFieldNatureTypeParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.UnsupportedIActionParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.UnsupportedIPatternParserMessage;
import org.drools.workbench.screens.guided.dtree.client.resources.i18n.GuidedDecisionTreeConstants;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.messageconsole.client.console.widget.MessageTableWidget;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.widgets.client.source.ViewDRLSourceWidget;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class ParserMessagesPopup extends BaseModal {

    interface ParserMessagesBinder
            extends
            UiBinder<Widget, ParserMessagesPopup> {

    }

    private static ParserMessagesBinder uiBinder = GWT.create( ParserMessagesBinder.class );

    private final GenericModalFooter footer = new GenericModalFooter();

    @UiField(provided = true)
    MessageTableWidget<Pair<String, ParserMessage>> messages;

    @UiField
    ViewDRLSourceWidget drlPreview;

    private final GuidedDecisionTree model;

    private final List<Pair<String, ParserMessage>> errors = new ArrayList<Pair<String, ParserMessage>>();

    public ParserMessagesPopup( final GuidedDecisionTree model ) {
        this.model = checkNotNull( "model", model );

        for ( GuidedDecisionTreeParserError error : model.getParserErrors() ) {
            for ( ParserMessage msg : error.getMessages() ) {
                errors.add( Pair.newPair( error.getOriginalDrl(), msg ) );
            }
        }

        setTitle( GuidedDecisionTreeConstants.INSTANCE.popupTitleParserMessages() );
        messages = new MessageTableWidget<Pair<String, ParserMessage>>();
        messages.setToolBarVisible( false );
        messages.setHeight( "150px" );

        footer.addButton( GuidedDecisionTreeConstants.INSTANCE.remove(),
                          new Command() {
                              @Override
                              public void execute() {
                                  model.getParserErrors().clear();
                                  hide();
                              }
                          },
                          IconType.WARNING,
                          ButtonType.DANGER );
        footer.addButton( GuidedDecisionTreeConstants.INSTANCE.ignore(),
                          new Command() {
                              @Override
                              public void execute() {
                                  hide();
                              }
                          },
                          ButtonType.PRIMARY );

        setBody( uiBinder.createAndBindUi( this ) );
        add( footer );

        messages.addCellPreviewHandler( new CellPreviewEvent.Handler<Pair<String, ParserMessage>>() {
            @Override
            public void onCellPreview( final CellPreviewEvent<Pair<String, ParserMessage>> event ) {
                if ( Event.getTypeInt( event.getNativeEvent().getType() ) == Event.ONCLICK ) {
                    drlPreview.setContent( event.getValue().getK1() );
                }
            }
        } );

        messages.setRowData( errors );

        messages.addLevelColumn( 10, new MessageTableWidget.ColumnExtractor<Level>() {
            @Override
            public Level getValue( final Object row ) {
                return Level.ERROR;
            }
        } );

        messages.addTextColumn( 90, new MessageTableWidget.ColumnExtractor<String>() {
            @Override
            public String getValue( final Object row ) {
                return getMessage( ( (Pair<String, ParserMessage>) row ).getK2() );
            }
        } );
    }

    private String getMessage( final ParserMessage msg ) {
        if ( msg instanceof AmbiguousRootParserMessage ) {
            final TypeNode tn = model.getRoot();
            final AmbiguousRootParserMessage m = (AmbiguousRootParserMessage) msg;
            return GuidedDecisionTreeConstants.INSTANCE.parserMessageAmbiguousRootParserMessage( tn.getClassName(),
                                                                                                 m.getClassName() );

        } else if ( msg instanceof BindingNotFoundParserMessage ) {
            final BindingNotFoundParserMessage m = (BindingNotFoundParserMessage) msg;
            return GuidedDecisionTreeConstants.INSTANCE.parserMessageBindingNotFoundParserMessage( m.getBinding() );

        } else if ( msg instanceof DataTypeConversionErrorParserMessage ) {
            final DataTypeConversionErrorParserMessage m = (DataTypeConversionErrorParserMessage) msg;
            return GuidedDecisionTreeConstants.INSTANCE.parserMessageDataTypeConversionErrorParserMessage( m.getValue(),
                                                                                                           m.getDataTypeClassName() );

        } else if ( msg instanceof DataTypeNotFoundParserMessage ) {
            final DataTypeNotFoundParserMessage m = (DataTypeNotFoundParserMessage) msg;
            return GuidedDecisionTreeConstants.INSTANCE.parserMessageDataTypeNotFoundParserMessage( m.getClassName(),
                                                                                                    m.getFieldName() );

        } else if ( msg instanceof DefaultParserMessage ) {
            final DefaultParserMessage m = (DefaultParserMessage) msg;
            return GuidedDecisionTreeConstants.INSTANCE.parserMessageDefaultParserMessage( m.getMessage() );

        } else if ( msg instanceof InvalidRootParserMessage ) {
            return GuidedDecisionTreeConstants.INSTANCE.parserMessageInvalidRootParserMessage();

        } else if ( msg instanceof UnsupportedFieldConstraintParserMessage ) {
            return GuidedDecisionTreeConstants.INSTANCE.parserMessageUnsupportedFieldConstraintParserMessage();

        } else if ( msg instanceof UnsupportedFieldConstraintTypeParserMessage ) {
            return GuidedDecisionTreeConstants.INSTANCE.parserMessageUnsupportedFieldConstraintTypeParserMessage();

        } else if ( msg instanceof UnsupportedFieldNatureTypeParserMessage ) {
            return GuidedDecisionTreeConstants.INSTANCE.parserMessageUnsupportedFieldNatureTypeParserMessage();

        } else if ( msg instanceof UnsupportedIActionParserMessage ) {
            return GuidedDecisionTreeConstants.INSTANCE.parserMessageUnsupportedIActionParserMessage();

        } else if ( msg instanceof UnsupportedIPatternParserMessage ) {
            return GuidedDecisionTreeConstants.INSTANCE.parserMessageUnsupportedIPatternParserMessage();

        } else {
            return GuidedDecisionTreeConstants.INSTANCE.parserMessageUnknownMessage();
        }
    }

}
