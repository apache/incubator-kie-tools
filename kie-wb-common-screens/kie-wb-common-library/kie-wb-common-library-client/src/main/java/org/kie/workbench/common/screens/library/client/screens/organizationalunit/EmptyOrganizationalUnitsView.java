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
package org.kie.workbench.common.screens.library.client.screens.organizationalunit;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Paragraph;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.TranslationUtils;

@Templated
public class EmptyOrganizationalUnitsView implements EmptyOrganizationalUnitsScreen.View,
                                                     IsElement {

    private EmptyOrganizationalUnitsScreen presenter;

    @Inject
    private TranslationService ts;

    @Inject
    private TranslationUtils translationUtils;

    @Inject
    @DataField("no-organizational-units")
    Paragraph noOrganizationalUnits;

    @Inject
    @DataField("create-organizational-unit")
    Button createOrganizationalUnit;

    @Override
    public void init(final EmptyOrganizationalUnitsScreen presenter) {
        this.presenter = presenter;

        noOrganizationalUnits.setTextContent(ts.format(LibraryConstants.NoOrganizationalUnits,
                                                       translationUtils.getOrganizationalUnitAliasInPlural().toLowerCase(),
                                                       translationUtils.getOrganizationalUnitAliasInSingular().toLowerCase()));
        createOrganizationalUnit.setTextContent(ts.format(LibraryConstants.CreateOrganizationalUnit,
                                                          translationUtils.getOrganizationalUnitAliasInSingular()));

        final boolean userCanCreateOrganizationalUnits = presenter.userCanCreateOrganizationalUnits();
        createOrganizationalUnit.setHidden(!userCanCreateOrganizationalUnits);
    }

    @EventHandler("create-organizational-unit")
    public void createOrganizationalUnit(final ClickEvent event) {
        presenter.createOrganizationalUnit();
    }
}