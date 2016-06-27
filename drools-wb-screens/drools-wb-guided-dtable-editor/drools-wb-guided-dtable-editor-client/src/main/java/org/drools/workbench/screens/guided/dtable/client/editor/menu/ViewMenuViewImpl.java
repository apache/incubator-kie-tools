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

package org.drools.workbench.screens.guided.dtable.client.editor.menu;

import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.common.StyleHelper;

@Dependent
@Templated
public class ViewMenuViewImpl extends BaseMenuViewImpl<ViewMenuBuilder> implements ViewMenuView {

    @DataField("viewMenuDropdown")
    ButtonElement viewMenuDropdown = Document.get().createPushButtonElement();

    @SuppressWarnings("unused")
    @DataField("viewMenuZoom125")
    LIElement viewMenuZoom125 = Document.get().createLIElement();

    @DataField("viewMenuZoom125Icon")
    Element viewMenuZoom125Icon = Document.get().createElement( "i" );

    @SuppressWarnings("unused")
    @DataField("viewMenuZoom100")
    LIElement viewMenuZoom100 = Document.get().createLIElement();

    @DataField("viewMenuZoom100Icon")
    Element viewMenuZoom100Icon = Document.get().createElement( "i" );

    @SuppressWarnings("unused")
    @DataField("viewMenuZoom75")
    LIElement viewMenuZoom75 = Document.get().createLIElement();

    @DataField("viewMenuZoom75Icon")
    Element viewMenuZoom75Icon = Document.get().createElement( "i" );

    @SuppressWarnings("unused")
    @DataField("viewMenuZoom50")
    LIElement viewMenuZoom50 = Document.get().createLIElement();

    @DataField("viewMenuZoom50Icon")
    Element viewMenuZoom50Icon = Document.get().createElement( "i" );

    @DataField("viewMenuToggleMergeState")
    LIElement viewMenuToggleMergeState = Document.get().createLIElement();

    @DataField("viewMenuToggleMergeStateIcon")
    Element viewMenuToggleMergeStateIcon = Document.get().createElement( "i" );

    @DataField("viewMenuViewAuditLog")
    LIElement viewMenuViewAuditLog = Document.get().createLIElement();

    @Override
    public boolean isEnabled() {
        return !viewMenuDropdown.isDisabled();
    }

    @Override
    public void setEnabled( final boolean enabled ) {
        viewMenuDropdown.setDisabled( !enabled );
    }

    @Override
    public void setMerged( final boolean merged ) {
        checkElement( viewMenuToggleMergeStateIcon,
                      merged );
    }

    @Override
    public void setZoom125( final boolean checked ) {
        checkElement( viewMenuZoom125Icon,
                      checked );
    }

    @Override
    public void setZoom100( final boolean checked ) {
        checkElement( viewMenuZoom100Icon,
                      checked );
    }

    @Override
    public void setZoom75( final boolean checked ) {
        checkElement( viewMenuZoom75Icon,
                      checked );
    }

    @Override
    public void setZoom50( final boolean checked ) {
        checkElement( viewMenuZoom50Icon,
                      checked );
    }

    private void checkElement( final Element element,
                               final boolean checked ) {
        if ( checked ) {
            StyleHelper.addEnumStyleName( element,
                                          IconType.CHECK );
        } else {
            StyleHelper.removeEnumStyleName( element,
                                             IconType.CHECK );
        }
    }

    @Override
    public void enableToggleMergedStateMenuItem( final boolean enabled ) {
        enableElement( viewMenuToggleMergeState,
                       enabled );
    }

    @Override
    public void enableViewAuditLogMenuItem( final boolean enabled ) {
        enableElement( viewMenuViewAuditLog,
                       enabled );
    }

    @Override
    public void enableZoom( final boolean enabled ) {
        enableElement( viewMenuZoom125,
                       enabled );
        enableElement( viewMenuZoom100,
                       enabled );
        enableElement( viewMenuZoom75,
                       enabled );
        enableElement( viewMenuZoom50,
                       enabled );
    }

    @SuppressWarnings("unused")
    @EventHandler("viewMenuZoom125")
    public void onClickViewMenuZoom125( final ClickEvent e ) {
        presenter.onZoom( 125 );
    }

    @SuppressWarnings("unused")
    @EventHandler("viewMenuZoom100")
    public void onClickViewMenuZoom100( final ClickEvent e ) {
        presenter.onZoom( 100 );
    }

    @SuppressWarnings("unused")
    @EventHandler("viewMenuZoom75")
    public void onClickViewMenuZoom75( final ClickEvent e ) {
        presenter.onZoom( 75 );
    }

    @SuppressWarnings("unused")
    @EventHandler("viewMenuZoom50")
    public void onClickViewMenuZoom50( final ClickEvent e ) {
        presenter.onZoom( 50 );
    }

    @SuppressWarnings("unused")
    @EventHandler("viewMenuToggleMergeState")
    public void onClickViewMenuToggleMergeState( final ClickEvent e ) {
        if ( isDisabled( viewMenuToggleMergeState ) ) {
            return;
        }
        presenter.onToggleMergeState();
    }

    @SuppressWarnings("unused")
    @EventHandler("viewMenuViewAuditLog")
    public void onClickViewMenuViewAuditLog( final ClickEvent e ) {
        if ( isDisabled( viewMenuViewAuditLog ) ) {
            return;
        }
        presenter.onViewAuditLog();
    }

}
