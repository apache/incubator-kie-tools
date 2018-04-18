/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.generalsettings;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.settings.util.KieSelectElement;
import org.kie.workbench.common.screens.projecteditor.model.GitUrl;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants.GitUrlFailedToBeCopiedToClipboard;
import static org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants.GitUrlSuccessfullyCopiedToClipboard;
import static org.kie.workbench.common.screens.library.client.settings.util.KieSelectElement.Option;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.SUCCESS;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.WARNING;

public class GitUrlsPresenter {

    private static final String DEFAULT_SELECTED_PROTOCOL = "ssh";

    private final View view;
    private final Event<NotificationEvent> notificationEventEvent;
    private final TranslationService translationService;
    private final KieSelectElement protocolSelect;

    Map<String, GitUrl> gitUrlsByProtocol;
    String selectedProtocol;

    @Inject
    public GitUrlsPresenter(final View view,
                            final Event<NotificationEvent> notificationEventEvent,
                            final KieSelectElement protocolSelect,
                            final TranslationService translationService) {

        this.view = view;
        this.notificationEventEvent = notificationEventEvent;
        this.protocolSelect = protocolSelect;
        this.translationService = translationService;
    }

    @PostConstruct
    public void init() {
        this.view.init(this);
    }

    public void setup(final List<GitUrl> gitUrls) {

        gitUrlsByProtocol = gitUrls.stream().collect(toMap(GitUrl::getProtocol, identity()));

        selectedProtocol = gitUrlsByProtocol.containsKey(DEFAULT_SELECTED_PROTOCOL)
                ? DEFAULT_SELECTED_PROTOCOL
                : gitUrls.get(0).getProtocol();

        protocolSelect.setup(view.getProtocolSelectContainer(),
                             gitUrls.stream().map(GitUrl::getProtocol).map(p -> new Option(p, p)).collect(toList()),
                             selectedProtocol,
                             this::setSelectedProtocol);

        update();
    }

    public void setSelectedProtocol(final String selectedProtocol) {
        this.selectedProtocol = selectedProtocol;
        update();
    }

    void update() {
        view.setUrl(gitUrlsByProtocol.get(selectedProtocol).getUrl());
    }

    public void copyToClipboard() {
        if (copy()) {
            notificationEventEvent.fire(new NotificationEvent(
                    translationService.format(GitUrlSuccessfullyCopiedToClipboard), SUCCESS));
        } else {
            notificationEventEvent.fire(new NotificationEvent(
                    translationService.format(GitUrlFailedToBeCopiedToClipboard), WARNING));
        }
    }

    boolean copy() {
        return copyNative();
    }

    private native boolean copyNative() /*-{
        return $doc.execCommand("Copy");
    }-*/;

    public View getView() {
        return view;
    }

    public interface View extends UberElemental<GitUrlsPresenter>,
                                  IsElement {

        void setUrl(final String url);

        HTMLElement getProtocolSelectContainer();
    }
}
