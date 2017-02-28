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

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.user.client.Event;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.HitPolicyInternationalizer;
import org.jboss.errai.common.client.dom.Node;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class HitPolicySelectorViewImpl
        implements HitPolicySelectorView,
                   IsElement {

    @Inject
    @DataField("selectedValue")
    Span selectedValue;

    @Inject
    @DataField("listOfAvailableHitModes")
    UnorderedList listOfAvailableHitModes;

    private HitPolicySelector presenter;

    @Override
    public void init( final HitPolicySelector presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setSelection( final GuidedDecisionTable52.HitPolicy hitPolicy ) {
        selectedValue.setTextContent( HitPolicyInternationalizer.internationalize( hitPolicy ) );
    }

    @Override
    public void addHitPolicyOption( final GuidedDecisionTable52.HitPolicy hitPolicy ) {
        final AnchorElement anchor = Document.get()
                .createAnchorElement();
        anchor.setInnerText( HitPolicyInternationalizer.internationalize( hitPolicy ) );

        Event.sinkEvents( anchor,
                          Event.ONCLICK );
        Event.setEventListener( anchor,
                                event -> {
                                    if ( Event.ONCLICK == event.getTypeInt() ) {
                                        presenter.onHitPolicySelected( hitPolicy );
                                    }
                                } );

        final LIElement li = Document.get()
                .createLIElement();
        li.appendChild( anchor );
        listOfAvailableHitModes.appendChild( (Node) li );
    }

}
