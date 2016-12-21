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

package org.kie.workbench.common.screens.library.client.widgets;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Node;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class AssetItemWidget implements IsElement {

    @Inject
    @DataField
    Anchor assetName;

    @Inject
    @DataField
    Span assetType;

    @Inject
    @DataField
    Div assetListItem;

    @Inject
    @DataField
    Div assetListItemKebab;

    @Inject
    @DataField
    Div assetListItemText;

    @Inject
    @DataField
    Div assetListItemIcon;

    public void init( String assetName, String assetType, IsWidget assetIcon, Command details, Command select ) {
        if ( assetIcon != null ) {
            HTMLElement assetIconHtml = TemplateUtil.<HTMLElement>nativeCast( assetIcon.asWidget().getElement() );
            final Node clonedAssetIconHtml = assetIconHtml.cloneNode( true );
            this.assetListItemIcon.appendChild( clonedAssetIconHtml );
        }

        this.assetName.setTextContent( assetName );
        this.assetName.setOnclick( e -> {
            e.stopImmediatePropagation();
            select.execute();
        } );

        this.assetType.setTextContent( assetType );

        assetListItem.setOnclick( e -> details.execute() );
    }
}
