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

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.server.api.model.Message;
import org.kie.workbench.common.screens.server.management.client.util.IOCUtil;
import org.kie.workbench.common.screens.server.management.client.widget.card.body.notification.NotificationPresenter;
import org.uberfire.client.mvp.UberView;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class BodyPresenter {

    public interface View extends UberView<BodyPresenter> {

        void addNotification( IsWidget view );

        void clear();
    }

    private final View view;
    private final IOCUtil iocUtil;

    @Inject
    public BodyPresenter( final View view,
                          final IOCUtil iocUtil ) {
        this.view = view;
        this.iocUtil = iocUtil;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public View getView() {
        return view;
    }

    public void setup( final Collection<Message> messages ) {
        checkNotNull( "messages", messages );

        if ( messages.isEmpty() ) {
            view.addNotification( setupNotification( null ).getView() );
        } else {
            for ( final Message message : messages ) {
                view.addNotification( setupNotification( message ).getView() );
            }
        }
    }

    private NotificationPresenter setupNotification( final Message message ) {
        final NotificationPresenter presenter = iocUtil.newInstance( this, NotificationPresenter.class );
        if ( message == null ) {
            presenter.setupOk();
        } else {
            presenter.setup( message );
        }
        return presenter;
    }

    @PreDestroy
    public void destroy() {
        iocUtil.cleanup( this );
    }

}
