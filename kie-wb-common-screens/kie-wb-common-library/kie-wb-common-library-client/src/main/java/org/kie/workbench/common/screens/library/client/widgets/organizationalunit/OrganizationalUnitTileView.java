/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.widgets.organizationalunit;

import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Heading;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.TranslationUtils;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.mvp.Command;

@Templated
public class OrganizationalUnitTileView implements OrganizationalUnitTileWidget.View,
                                                   IsElement {

    private OrganizationalUnitTileWidget presenter;

    @Inject
    private TranslationService ts;

    @Inject
    private TranslationUtils translationUtils;

    @Inject
    @DataField("card")
    private Div card;

    @Inject
    @DataField("edit")
    private Span edit;

    @Inject
    @DataField("remove")
    private Span remove;

    @Inject
    @DataField("icon")
    private Span icon;

    @Inject
    @DataField("iconOnHover")
    private Span iconOnHover;

    @Inject
    @Named("h2")
    @DataField("label")
    private Heading label;

    @Inject
    @DataField("count")
    private Span count;

    @Inject
    @DataField("owner")
    private Span owner;

    @Override
    public void init(final OrganizationalUnitTileWidget presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setup(final String iconClass,
                      final String iconOnHoverClass,
                      final OrganizationalUnit organizationalUnit,
                      final Command selectCommand,
                      final Command editCommand,
                      final Command removeCommand) {
        this.icon.getClassList().add(iconClass);
        this.iconOnHover.getClassList().add(iconOnHoverClass);
        this.label.setTextContent(organizationalUnit.getName());
        this.count.setTextContent(String.valueOf(organizationalUnit.getRepositories().size()));
        this.count.setTitle(organizationalUnit.getRepositories().size() + " " + ts.format(LibraryConstants.Repositories));
        this.owner.setTextContent(organizationalUnit.getOwner());

        this.card.setOnclick(event -> selectCommand.execute());
        this.edit.setOnclick(event -> editCommand.execute());
        this.remove.setOnclick(event -> removeCommand.execute());
    }

    @Override
    public String getRemovingBusyIndicatorMessage() {
        return ts.format(LibraryConstants.Removing);
    }

    @Override
    public String getRemoveWarningMessage(final String ouName) {
        return ts.format(LibraryConstants.RemoveOrganizationalUnitWarningMessage,
                         ouName,
                         translationUtils.getOrganizationalUnitAliasInSingular().toLowerCase());
    }

    @Override
    public String getRemoveSuccessMessage() {
        return ts.format(LibraryConstants.RemoveOrganizationalUnitSuccess,
                         translationUtils.getOrganizationalUnitAliasInSingular());
    }

    @Override
    public void hideEditAction() {
        this.edit.setHidden(true);
    }

    @Override
    public void hideRemoveAction() {
        this.remove.setHidden(true);
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }
}
