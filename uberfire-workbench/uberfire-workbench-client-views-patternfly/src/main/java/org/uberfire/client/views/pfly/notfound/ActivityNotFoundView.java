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
package org.uberfire.client.views.pfly.notfound;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.workbench.widgets.notfound.ActivityNotFoundPresenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

/**
 * Gets shown inside a popup activity when the PlaceManager can't find a particular place.
 */
@Dependent
@Templated
public class ActivityNotFoundView extends Composite implements ActivityNotFoundPresenter.View {

    private ActivityNotFoundPresenter presenter;

    @Inject @DataField
    private Label requestedPlaceIdentifier;

    @Inject @DataField
    private Button okButton;

    @Override
    public void init( final ActivityNotFoundPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setRequestedPlaceIdentifier( String identifier ) {
        requestedPlaceIdentifier.setText( identifier );
    }

    @EventHandler("okButton")
    public void onClickOkButton( final ClickEvent event ) {
        presenter.close();
    }

}
