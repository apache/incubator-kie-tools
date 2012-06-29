/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client.editors.factmodel;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.annotations.SupportedFormat;
import org.drools.guvnor.client.mvp.AbstractEditorScreenActivity;
import org.drools.guvnor.client.mvp.EditorScreenService;
import org.drools.guvnor.client.mvp.IPlaceRequest;
import org.drools.guvnor.client.mvp.NameToken;
import org.drools.guvnor.client.mvp.PlaceManager;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * 
 */
@Dependent
@NameToken("FactModelEditor")
@SupportedFormat("model.drl")
public class FactModelEditorActivity extends AbstractEditorScreenActivity {

    @Inject
    private IOCBeanManager           iocManager;

    @Inject
    private PlaceManager             placeManager;

    private FactModelEditorPresenter presenter;

    public FactModelEditorActivity() {
    }

    @Override
    public EditorScreenService getPresenter() {
        this.presenter = iocManager.lookupBean( FactModelEditorPresenter.class ).getInstance();
        return this.presenter;
    }

    @Override
    public String getTitle() {
        IPlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
        final String uriPath = placeRequest.getParameter( "path",
                                                          null );
        return "FactModel Editor [" + uriPath + "]";
    }

    @Override
    public IsWidget getWidget() {
        return presenter.view;
    }

}
