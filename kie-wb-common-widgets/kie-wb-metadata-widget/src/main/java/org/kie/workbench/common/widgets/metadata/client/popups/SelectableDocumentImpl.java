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

package org.kie.workbench.common.widgets.metadata.client.popups;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class SelectableDocumentImpl implements SelectDocumentPopupView.SelectableDocumentView {

    private static final String ACTIVE_CSS_CLASS = "kie-selectable-document-active";
    private static final String INACTIVE_CSS_CLASS = "kie-selectable-document-inactive";

    private Path path;
    private Command activateDocumentCommand;

    @Inject
    @DataField("kie-selectable-document")
    Div kieSelectableDocument;

    @Inject
    @DataField("kie-selectable-document-name")
    Div kieSelectableDocumentName;

    @Override
    public HTMLElement getElement() {
        return kieSelectableDocument;
    }

    @Override
    public Path getPath() {
        return this.path;
    }

    @Override
    public void setPath( final Path path ) {
        this.path = PortablePreconditions.checkNotNull( "path",
                                                        path );
        this.kieSelectableDocumentName.setInnerHTML( getSafeHtml( path.toURI() ).asString() );
    }

    private SafeHtml getSafeHtml( final String message ) {
        final SafeHtmlBuilder shb = new SafeHtmlBuilder();
        shb.appendEscaped( message );
        return shb.toSafeHtml();
    }

    @Override
    public void setSelectDocumentCommand( final Command activateDocumentCommand ) {
        this.activateDocumentCommand = PortablePreconditions.checkNotNull( "activateDocumentCommand",
                                                                           activateDocumentCommand );
    }

    @Override
    public void setSelected( final boolean isSelected ) {
        if ( isSelected ) {
            kieSelectableDocument.setClassName( ACTIVE_CSS_CLASS );
        } else {
            kieSelectableDocument.setClassName( INACTIVE_CSS_CLASS );
        }
    }

    @SuppressWarnings("unused")
    @EventHandler("kie-selectable-document-name")
    public void onClickFileName( final ClickEvent e ) {
        if ( this.activateDocumentCommand != null ) {
            activateDocumentCommand.execute();
        }
    }

}
