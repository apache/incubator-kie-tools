/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.ui.client.navigation;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.widget.CustomGroupItem;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Window;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ProviderTypeNavigationView_TitleText;
import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;

@Dependent
@Templated
public class ProviderTypeNavigationView
        implements IsElement,
                   ProviderTypeNavigationPresenter.View {

    @DataField
    private HTMLElement title = Window.getDocument().createElement("strong");

    @Inject
    @DataField("provider-type-list-group")
    private Div providerTypeItems;

    private ProviderTypeNavigationPresenter presenter;

    @Inject
    private TranslationService translationService;

    private final Map<ProviderTypeKey, CustomGroupItem> itemsMap = new HashMap<>();

    private CustomGroupItem selected = null;

    @Override
    public void init(final ProviderTypeNavigationPresenter presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void init() {
        title.setTextContent(getTitleText());
    }

    @Override
    public void addProviderType(final ProviderTypeKey providerTypeKey,
                                final String name,
                                final Command select) {
        final CustomGroupItem providerTypeItem = CustomGroupItem.createAnchor(name,
                                                                              IconType.FOLDER_O,
                                                                              select);
        itemsMap.put(providerTypeKey,
                     providerTypeItem);

        providerTypeItems.appendChild(providerTypeItem);
    }

    @Override
    public void select(final ProviderTypeKey providerTypeKey) {
        if (selected != null) {
            selected.setActive(false);
        }
        selected = itemsMap.get(providerTypeKey);
        if (selected != null) {
            selected.setActive(true);
        }
    }

    @Override
    public void clear() {
        removeAllChildren(providerTypeItems);
        selected = null;
        itemsMap.clear();
    }

    @EventHandler("enable-provider-type-button")
    private void onAddProviderType(@ForEvent("click") final Event event) {
        presenter.onAddProviderType();
    }

    @EventHandler("refresh-provider-type-list-icon")
    private void onRefresh(@ForEvent("click") final Event event) {
        presenter.onRefresh();
    }

    private String getTitleText() {
        return translationService.format(ProviderTypeNavigationView_TitleText);
    }
}
