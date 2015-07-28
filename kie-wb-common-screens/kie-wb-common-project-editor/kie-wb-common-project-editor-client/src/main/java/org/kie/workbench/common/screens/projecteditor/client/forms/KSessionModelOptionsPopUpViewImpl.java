/*
 * Copyright 2013 JBoss Inc
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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import java.util.List;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.WorkItemHandlerModel;
import org.kie.workbench.common.services.shared.kmodule.ListenerModel;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKButton;

public class KSessionModelOptionsPopUpViewImpl
        extends BaseModal
        implements KSessionModelOptionsPopUpView {

    interface Binder
            extends
            UiBinder<Widget, KSessionModelOptionsPopUpViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField(provided = true)
    ListenersPanel listenersPanel;

    @UiField(provided = true)
    WorkItemHandlersPanel workItemHandlersPanel;

    @Inject
    private javax.enterprise.event.Event<LockRequiredEvent> lockRequired;

    @Inject
    public KSessionModelOptionsPopUpViewImpl( ListenersPanel listenersPanel,
                                              WorkItemHandlersPanel workItemHandlersPanel ) {
        this.listenersPanel = listenersPanel;
        this.workItemHandlersPanel = workItemHandlersPanel;

        setTitle( CommonConstants.INSTANCE.Edit() );
        setBody( uiBinder.createAndBindUi( KSessionModelOptionsPopUpViewImpl.this ) );

        add( new ModalFooterOKButton( new Command() {
            @Override
            public void execute() {
                hide();
                lockRequired.fire( new LockRequiredEvent() );
            }
        } ) );

    }

    @Override
    public void setListeners( List<ListenerModel> listeners ) {
        listenersPanel.setListeners( listeners );

        Scheduler.get().scheduleFixedDelay( new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                listenersPanel.redraw();
                return false;
            }
        }, 1000 );
    }

    @Override
    public void setWorkItemHandlers( List<WorkItemHandlerModel> workItemHandlerModels ) {
        workItemHandlersPanel.setHandlerModels( workItemHandlerModels );

        Scheduler.get().scheduleFixedDelay( new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                workItemHandlersPanel.redraw();
                return false;
            }
        }, 1000 );
    }

}
