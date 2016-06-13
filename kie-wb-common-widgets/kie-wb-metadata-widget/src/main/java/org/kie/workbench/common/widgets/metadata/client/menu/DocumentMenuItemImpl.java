/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.metadata.client.menu;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.mvp.Command;

@Templated
public class DocumentMenuItemImpl implements RegisteredDocumentsMenuView.DocumentMenuItem {

    private static final String ACTIVE_CSS_CLASS = "kie-document-active";
    private static final String INACTIVE_CSS_CLASS = "kie-document-inactive";

    private String name;
    private Command activateDocumentCommand;
    private Command removeDocumentCommand;

    @Inject
    @DataField("kie-document-name")
    Div kieDocumentName;

    @Inject
    @DataField("kie-document-registration")
    Div kieDocumentRegistration;

    @Inject
    @DataField("kie-document-close")
    Div kieDocumentClose;

    @Override
    public HTMLElement getElement() {
        return kieDocumentRegistration;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName( final String name ) {
        this.name = name;
        this.kieDocumentName.setInnerHTML( getSafeHtml( name ).asString());
    }

    private SafeHtml getSafeHtml( final String message ) {
        final SafeHtmlBuilder shb = new SafeHtmlBuilder();
        shb.appendEscaped( message );
        return shb.toSafeHtml();
    }

    @Override
    public void setActivateDocumentCommand( final Command activateDocumentCommand ) {
        this.activateDocumentCommand = PortablePreconditions.checkNotNull( "activateDocumentCommand",
                                                                           activateDocumentCommand );
    }

    @Override
    public void setRemoveDocumentCommand( final Command removeDocumentCommand ) {
        this.removeDocumentCommand = PortablePreconditions.checkNotNull( "removeDocumentCommand",
                                                                         removeDocumentCommand );
    }

    @Override
    public void setActive( final boolean isActive ) {
        if ( isActive ) {
            kieDocumentRegistration.setClassName( ACTIVE_CSS_CLASS );
        } else {
            kieDocumentRegistration.setClassName( INACTIVE_CSS_CLASS );
        }
    }

    @SuppressWarnings("unused")
    @EventHandler("kie-document-name")
    public void onClickFileName( final ClickEvent e ) {
        if ( this.activateDocumentCommand != null ) {
            activateDocumentCommand.execute();
        }
    }

    @SuppressWarnings("unused")
    @EventHandler("kie-document-close")
    public void onClickClose( final ClickEvent e ) {
        if ( this.removeDocumentCommand != null ) {
            removeDocumentCommand.execute();
        }
    }

}
