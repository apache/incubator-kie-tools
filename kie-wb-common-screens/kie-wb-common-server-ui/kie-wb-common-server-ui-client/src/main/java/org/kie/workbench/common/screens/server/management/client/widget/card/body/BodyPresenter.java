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

package org.kie.workbench.common.screens.server.management.client.widget.card.body;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.server.api.model.Message;
import org.kie.workbench.common.screens.server.management.client.widget.card.body.notification.NotificationPresenter;
import org.uberfire.client.mvp.UberView;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
public class BodyPresenter {

    public interface View extends UberView<BodyPresenter> {

        void addNotification(IsWidget view);

        void clear();
    }

    private final View view;
    private final ManagedInstance<NotificationPresenter> presenterProvider;

    @Inject
    public BodyPresenter(final View view,
                         final ManagedInstance<NotificationPresenter> presenterProvider) {
        this.view = view;
        this.presenterProvider = presenterProvider;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public View getView() {
        return view;
    }

    public void setup(final Collection<Message> messages) {
        checkNotNull("messages",
                     messages);
        view.clear();
        if (messages.isEmpty()) {
            view.addNotification(setupNotification(null).getView());
        } else {
            for (final Message message : messages) {
                view.addNotification(setupNotification(message).getView());
            }
        }
    }

    private NotificationPresenter setupNotification(final Message message) {
        final NotificationPresenter presenter = presenterProvider.get();
        if (message == null) {
            presenter.setupOk();
        } else {
            presenter.setup(message);
        }
        return presenter;
    }
}
