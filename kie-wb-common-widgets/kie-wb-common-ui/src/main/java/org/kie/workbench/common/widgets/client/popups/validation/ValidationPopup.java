/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.widgets.client.popups.validation;

import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.messageconsole.client.console.widget.MessageTableWidget;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKButton;

/**
 * A popup that lists BuildMessages
 */
public class ValidationPopup extends BaseModal {

    private static ValidationPopup instance = new ValidationPopup();

    protected final MessageTableWidget<ValidationMessage> dataGrid = new MessageTableWidget<ValidationMessage>( MessageTableWidget.Mode.PAGED ) {{
        setDataProvider( new ListDataProvider<ValidationMessage>() );
    }};

    private ValidationPopup() {
        setTitle( CommonConstants.INSTANCE.ValidationErrors() );
        setHideOtherModals( false );

        setBody( dataGrid );

        add( new ModalFooterOKButton( new Command() {
            @Override
            public void execute() {
                hide();
            }
        } ) );

        dataGrid.setToolBarVisible( false );

        dataGrid.addLevelColumn( 10, new MessageTableWidget.ColumnExtractor<Level>() {
            @Override
            public Level getValue( final Object row ) {
                final Level level = ( (ValidationMessage) row ).getLevel();
                return level != null ? level : Level.ERROR;
            }
        } );

        dataGrid.addTextColumn( 90, new MessageTableWidget.ColumnExtractor<String>() {
            @Override
            public String getValue( final Object row ) {
                return ( (ValidationMessage) row ).getText();
            }
        } );
    }

    private void setMessages( final List<ValidationMessage> messages ) {
        final ListDataProvider<ValidationMessage> listDataProvider = (ListDataProvider<ValidationMessage>) this.dataGrid.getDataProvider();
        listDataProvider.getList().clear();
        listDataProvider.getList().addAll( messages );
        this.dataGrid.setVisibleRangeAndClearData( new Range( 0, 5 ), true );
    }

    public static void showMessages( final List<ValidationMessage> messages ) {
        instance.setMessages( messages );
        instance.show();
    }

}
