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

package org.uberfire.client.editors.gadget;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.backend.server.GadgetService;
import org.uberfire.shared.gadget.WidgetModel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;

public class GadgetView extends Composite
    implements
    RequiresResize,
    GadgetPresenter.View {
	
    @Inject
    private Caller<GadgetService> gadgetService;

	PortalLayout portalLayout = new PortalLayout("1", 1);

    @PostConstruct
    public void init() {        
    	gadgetService.call( new RemoteCallback<WidgetModel>() {
            @Override
            public void callback(WidgetModel response) {
          	    portalLayout.addPortlet(0, new Portlet(response, portalLayout.getPortletWidth(), portalLayout.getPortalId()));

                //initWidget( portalLayout);
               
            }
        } ).getWidgetModel("http://www.gstatic.com/ig/modules/datetime_v3/datetime_v3.xml");
        
    	gadgetService.call( new RemoteCallback<WidgetModel>() {
            @Override
            public void callback(WidgetModel response) {
          	    portalLayout.addPortlet(0, new Portlet(response, portalLayout.getPortletWidth(), portalLayout.getPortalId()));

                //initWidget( portalLayout);
               
            }
        } ).getWidgetModel("http://research.stlouisfed.org/gadgets/code/alfredgraph.xml");
    	
    	initWidget( portalLayout);
    }

    @Override
    public void onResize() {
    }
 }