/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.widgets.common.client.common.popups.footers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.mvp.Command;

/**
 * A Modal Footer that can accept arbitrary buttons
 */
public class GenericModalFooter extends ModalFooter {

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
        this.add( button );
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
        this.add( button );
    }

}
