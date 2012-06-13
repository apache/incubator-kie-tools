/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.client.editors.monitoring;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import org.drools.guvnor.client.mvp.PlaceManager;
import org.drools.guvnor.client.mvp.PlaceRequest;
import org.drools.guvnor.client.perspective.workspace.DefaultPlace;
import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.framework.RequestDispatcher;

@Dependent
public class MonitoringPerspectiveViewImpl extends Composite implements MonitoringPerspectivePresenter.MyView {

    @Inject private UiBinder<Widget, MonitoringPerspectiveViewImpl> uiBinder;
    
    @Inject
    private PlaceManager placeManager;

    @UiField public SpanElement userName;

    @UiField public HTMLPanel titlePanel;

    @UiField public HTMLPanel footerPanel;

    @UiField public Button backWorkspace;

    @UiField public Button callService;

    @PostConstruct
    public void init() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setUserName(String userName) {
        this.userName.setInnerText(userName);
    }

    @UiHandler("backWorkspace")
    public void backToWorkspace(ClickEvent e) {
        PlaceRequest placeRequest = new PlaceRequest("AdminArea");
        placeManager.goTo(placeRequest);
    }

    @UiHandler("callService")
    public void callService(ClickEvent e) {
        final RequestDispatcher dispatcher = ErraiBus.getDispatcher();

        MessageBuilder.createMessage()
                .toSubject("IsDirtyService")
                .signalling()
                .noErrorHandling()
                .sendNowWith(dispatcher);
    }

}
