/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.handlers;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.uberfire.client.callbacks.Callback;

@Dependent
public class HitPolicySelector
        implements IsWidget {

    private HitPolicySelectorView view;
    private Callback<GuidedDecisionTable52.HitPolicy> valueChangeHandler;

    @Inject
    public HitPolicySelector( final HitPolicySelectorView view ) {
        this.view = view;
        view.init( this );

        for ( final GuidedDecisionTable52.HitPolicy policy : GuidedDecisionTable52.HitPolicy.values() ) {
            view.addHitPolicyOption( policy );
        }

        view.setSelection( GuidedDecisionTable52.HitPolicy.getDefault() );
    }

    public void addValueChangeHandler( final Callback<GuidedDecisionTable52.HitPolicy> valueChangeHandler ) {
        this.valueChangeHandler = valueChangeHandler;
    }

    public void onHitPolicySelected( final GuidedDecisionTable52.HitPolicy hitPolicy ) {
        view.setSelection( hitPolicy );
        valueChangeHandler.callback( hitPolicy );
    }

    @Override
    public Widget asWidget() {
        return ElementWrapperWidget.getWidget( view.getElement() );
    }
}
