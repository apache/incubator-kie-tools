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

package org.uberfire.ext.wires.client.preferences.central.tree;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.user.client.DOM;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class TreeHierarchyInternalItemView implements IsElement,
                                                      TreeHierarchyInternalItemPresenter.View {

    private TreeHierarchyInternalItemPresenter presenter;

    @Inject
    private TranslationService translationService;

    @DataField("preference-tree-internal-item-label")
    Element label = DOM.createLabel();

    @Inject
    @DataField("preference-tree-internal-item-node")
    Div treeNode;

    @Inject
    @DataField("preference-tree-internal-item-children")
    Div children;

    @DataField("preference-tree-internal-item-expand-icon")
    Element expandIcon = DOM.createElement( "i" );

    @DataField("preference-tree-internal-item-contract-icon")
    Element contractIcon = DOM.createElement( "i" );

    @Override
    public void init( final TreeHierarchyInternalItemPresenter presenter ) {
        this.presenter = presenter;

        final String preferenceLabel = getPreferenceLabel( presenter.getHierarchyElement().getBundleKey() );
        label.setInnerHTML( preferenceLabel );
        children.setHidden( true );

        presenter.getHierarchyItems().forEach( hierarchyItem -> {
            children.appendChild( hierarchyItem.getView().getElement() );
        } );
    }

    @Override
    public void deselect() {
        label.removeClassName( "selected" );
    }

    @EventHandler("preference-tree-internal-item-expand-icon")
    public void expand( final ClickEvent event ) {
        expand();
    }

    @EventHandler("preference-tree-internal-item-contract-icon")
    public void contract( final ClickEvent event ) {
        contract();
    }

    @EventHandler("preference-tree-internal-item-label")
    public void contractExpand( final ClickEvent event ) {
        if ( !label.hasClassName( "selected" ) ) {
            presenter.select();
            label.addClassName( "selected" );
        }
    }

    @EventHandler("preference-tree-internal-item-label")
    public void contractExpand( final DoubleClickEvent event ) {
        if ( !children.getHidden() ) {
            contract();
        } else {
            expand();
        }
    }

    private void expand() {
        expandIcon.addClassName( "hidden" );
        contractIcon.removeClassName( "hidden" );
        children.setHidden( false );
    }

    private void contract() {
        expandIcon.removeClassName( "hidden" );
        contractIcon.addClassName( "hidden" );
        children.setHidden( true );
    }

    private String getPreferenceLabel( String bundleKey ) {
        return translationService.format( bundleKey );
    }
}
