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

package org.guvnor.ala.ui.client.wizard;

import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.events.ProviderTypeListRefreshEvent;
import org.guvnor.ala.ui.client.wizard.providertype.EnableProviderTypePagePresenter;
import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.model.ProviderTypeStatus;
import org.guvnor.ala.ui.service.ProviderTypeService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.events.NotificationEvent;

import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.EnableProviderTypeWizard_ProviderTypeEnableErrorMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.EnableProviderTypeWizard_ProviderTypeEnableSuccessMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.EnableProviderTypeWizard_Title;

/**
 * Wizard for enabling the desired provider types on the system. Enabled provider types will be available for defining
 * providers.
 */
@ApplicationScoped
public class EnableProviderTypeWizard
        extends AbstractMultiPageWizard {

    private final EnableProviderTypePagePresenter enableProviderTypePage;
    private final Caller<ProviderTypeService> providerTypeService;
    private final Event<ProviderTypeListRefreshEvent> providerTypeListRefreshEvent;

    @Inject
    public EnableProviderTypeWizard(final EnableProviderTypePagePresenter enableProviderTypePage,
                                    final TranslationService translationService,
                                    final Caller<ProviderTypeService> providerTypeService,
                                    final Event<NotificationEvent> notification,
                                    final Event<ProviderTypeListRefreshEvent> providerTypeListRefreshEvent) {
        super(translationService,
              notification);
        this.enableProviderTypePage = enableProviderTypePage;
        this.providerTypeService = providerTypeService;
        this.notification = notification;
        this.providerTypeListRefreshEvent = providerTypeListRefreshEvent;
    }

    @PostConstruct
    protected void init() {
        pages.add(enableProviderTypePage);
    }

    public void start(final List<Pair<ProviderType, ProviderTypeStatus>> providerTypeStatus) {
        enableProviderTypePage.setup(providerTypeStatus);
        super.start();
    }

    @Override
    public String getTitle() {
        return translationService.getTranslation(EnableProviderTypeWizard_Title);
    }

    @Override
    public int getPreferredHeight() {
        return 550;
    }

    @Override
    public int getPreferredWidth() {
        return 800;
    }

    @Override
    public void complete() {
        final Collection<ProviderType> providerTypes = enableProviderTypePage.getSelectedProviderTypes();

        providerTypeService.call((Void aVoid) -> onEnableTypesSuccess(providerTypes),
                                 (message, throwable) -> onEnableTypesError()).enableProviderTypes(providerTypes);
    }

    private void onEnableTypesSuccess(final Collection<ProviderType> providerTypes) {
        notification.fire(new NotificationEvent(translationService.getTranslation(EnableProviderTypeWizard_ProviderTypeEnableSuccessMessage),
                                                NotificationEvent.NotificationType.SUCCESS));
        EnableProviderTypeWizard.super.complete();
        providerTypeListRefreshEvent.fire(new ProviderTypeListRefreshEvent(providerTypes.iterator().next().getKey()));
    }

    private boolean onEnableTypesError() {
        notification.fire(new NotificationEvent(translationService.getTranslation(EnableProviderTypeWizard_ProviderTypeEnableErrorMessage),
                                                NotificationEvent.NotificationType.ERROR));
        start();
        return false;
    }
}
