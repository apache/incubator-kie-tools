/*
 * Copyright 2011 JBoss Inc
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
package org.drools.workbench.screens.guided.dtable.client.widget.table;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.auditlog.AuditLog;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.uberfire.security.Identity;

/**
 * Simple container for controls to manipulate a Decision Table
 */
public class DecisionTableControlsWidget extends Composite {

    private Button addRowButton;
    private Button otherwiseButton;
    private Button analyzeButton;
    private Button auditLogButton;

    public DecisionTableControlsWidget( final AbstractDecisionTableWidget dtable,
                                        final GuidedDecisionTable52 model,
                                        final Identity identity,
                                        final boolean isReadOnly ) {

        Panel panel = new HorizontalPanel();

        // Add row button
        addRowButton = new Button( GuidedDecisionTableConstants.INSTANCE.AddRow(),
                                   new ClickHandler() {
                                       public void onClick( ClickEvent event ) {
                                           if ( dtable != null ) {
                                               dtable.appendRow();
                                           }
                                       }
                                   } );
        addRowButton.setEnabled( !isReadOnly );
        panel.add( addRowButton );

        //Otherwise button
        otherwiseButton = new Button( GuidedDecisionTableConstants.INSTANCE.Otherwise(),
                                      new ClickHandler() {
                                          public void onClick( ClickEvent event ) {
                                              if ( dtable != null ) {
                                                  dtable.makeOtherwiseCell();
                                              }
                                          }
                                      } );
        otherwiseButton.setEnabled( false );
        panel.add( otherwiseButton );

        // Analyse button
        analyzeButton = new Button( GuidedDecisionTableConstants.INSTANCE.Analyze(),
                                    new ClickHandler() {
                                        public void onClick( ClickEvent event ) {
                                            if ( dtable != null ) {
                                                dtable.analyze();
                                            }
                                        }
                                    } );
        analyzeButton.setEnabled( !isReadOnly );
        panel.add( analyzeButton );

        // Audit Log button
        auditLogButton = new Button( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLog(),
                                     new ClickHandler() {
                                         public void onClick( ClickEvent event ) {
                                             if ( dtable != null ) {
                                                 AuditLog log = new AuditLog( dtable.model,
                                                                              identity );
                                                 log.show();
                                             }
                                         }
                                     } );
        auditLogButton.setEnabled( !isReadOnly );
        panel.add( auditLogButton );

        initWidget( panel );
    }

    /**
     * Enable the "Otherwise" button
     */
    void setEnableOtherwiseButton( boolean isEnabled ) {
        otherwiseButton.setEnabled( isEnabled );
    }

}
