/*
 * Copyright 2014 JBoss Inc
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
package org.kie.uberfire.client.common.popups.footers;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ModalFooter;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Modal Footer that can accept arbitrary buttons
 */
public class GenericModalFooter extends ModalFooter {

    private static ModalFooterGenericButtonsBinder uiBinder = GWT.create( ModalFooterGenericButtonsBinder.class );

    interface ModalFooterGenericButtonsBinder
            extends
            UiBinder<Widget, GenericModalFooter> {

    }

    @UiField
    ModalFooter footer;

    public GenericModalFooter() {
        add( uiBinder.createAndBindUi( this ) );
    }

    public void addButton( final String caption,
                           final Command command,
                           final ButtonType buttonType ) {
        final Button button = new Button( caption,
                                          new ClickHandler() {
                                              @Override
                                              public void onClick( ClickEvent event ) {
                                                  if ( command != null ) {
                                                      command.execute();
                                                  }
                                              }
                                          } );
        button.setType( buttonType );
        footer.add( button );
    }

    public void addButton( final String caption,
                           final Command command,
                           final IconType iconType,
                           final ButtonType buttonType ) {
        final Button button = new Button( caption,
                                          new ClickHandler() {
                                              @Override
                                              public void onClick( ClickEvent event ) {
                                                  if ( command != null ) {
                                                      command.execute();
                                                  }
                                              }
                                          } );
        button.setType( buttonType );
        button.setIcon( iconType );
        footer.add( button );
    }

}
