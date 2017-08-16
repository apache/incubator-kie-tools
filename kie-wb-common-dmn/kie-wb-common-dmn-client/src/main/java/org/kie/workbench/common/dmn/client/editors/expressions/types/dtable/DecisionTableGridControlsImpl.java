/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.v1_1.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.v1_1.HitPolicy;
import org.uberfire.client.views.pfly.widgets.Select;
import org.uberfire.mvp.Command;

@Templated
@Dependent
public class DecisionTableGridControlsImpl implements DecisionTableGridControls {

    @DataField("addInputClause")
    private Div addInputClause;

    @DataField("addOutputClause")
    private Div addOutputClause;

    @DataField("addDecisionRule")
    private Div addDecisionRule;

    @DataField("lstHitPolicies")
    private Select lstHitPolicies;

    @DataField("lstBuiltinAggregator")
    private Select lstBuiltinAggregator;

    @DataField("lstDecisionTableOrientation")
    private Select lstDecisionTableOrientation;

    private Document document;

    private Presenter presenter;

    public DecisionTableGridControlsImpl() {
        //CDI proxy
    }

    @Inject
    public DecisionTableGridControlsImpl(final Div addInputClause,
                                         final Div addOutputClause,
                                         final Div addDecisionRule,
                                         final Select lstHitPolicies,
                                         final Select lstBuiltinAggregator,
                                         final Select lstDecisionTableOrientation,
                                         final Document document) {
        this.addInputClause = addInputClause;
        this.addOutputClause = addOutputClause;
        this.addDecisionRule = addDecisionRule;
        this.lstHitPolicies = lstHitPolicies;
        this.lstBuiltinAggregator = lstBuiltinAggregator;
        this.lstDecisionTableOrientation = lstDecisionTableOrientation;
        this.document = document;

        setupHitPolicyEventHandler();
        setupBuiltinAggregatorEventHandler();
        setupDecisionTableOrientationEventHandler();
    }

    private void setupHitPolicyEventHandler() {
        setupChangeEventHandler(lstHitPolicies,
                                () -> {
                                    final HitPolicy hp = HitPolicy.fromValue(lstHitPolicies.getValue());
                                    presenter.setHitPolicy(hp);
                                });
    }

    private void setupBuiltinAggregatorEventHandler() {
        setupChangeEventHandler(lstBuiltinAggregator,
                                () -> {
                                    final BuiltinAggregator aggregator = BuiltinAggregator.fromValue(lstBuiltinAggregator.getValue());
                                    presenter.setBuiltinAggregator(aggregator);
                                });
    }

    private void setupDecisionTableOrientationEventHandler() {
        setupChangeEventHandler(lstDecisionTableOrientation,
                                () -> {
                                    final DecisionTableOrientation orientation = DecisionTableOrientation.fromValue(lstDecisionTableOrientation.getValue());
                                    presenter.setDecisionTableOrientation(orientation);
                                });
    }

    private void setupChangeEventHandler(final Select select,
                                         final Command command) {
        // org.uberfire.client.views.pfly.widgets.Select does not work with @EventHandler
        select.getElement().addEventListener("change",
                                             (event) -> command.execute(),
                                             false);
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initHitPolicies(final List<HitPolicy> hitPolicies) {
        hitPolicies.forEach(hp -> lstHitPolicies.addOption(hp.value()));
    }

    @Override
    public void initBuiltinAggregators(final List<BuiltinAggregator> aggregators) {
        aggregators.forEach(a -> lstBuiltinAggregator.addOption(a.value()));
    }

    @Override
    public void initDecisionTableOrientations(final List<DecisionTableOrientation> orientations) {
        orientations.forEach(o -> lstDecisionTableOrientation.addOption(o.value()));
    }

    @Override
    public void initSelectedHitPolicy(final HitPolicy hitPolicy) {
        initSelect(lstHitPolicies,
                   hitPolicy.value());
    }

    @Override
    public void initSelectedBuiltinAggregator(final BuiltinAggregator aggregator) {
        initSelect(lstBuiltinAggregator,
                   aggregator.value());
    }

    @Override
    public void initSelectedDecisionTableOrientation(final DecisionTableOrientation orientation) {
        initSelect(lstDecisionTableOrientation,
                   orientation.value());
    }

    private void initSelect(final Select select,
                            final String value) {
        // Setting value directly throws a JavaScript error, probably because the Element is
        // not attached to the DOM at this point. Deferring setting the value works around
        select.refresh(s -> s.setValue(value));
    }

    @Override
    public void enableHitPolicies(final boolean enabled) {
        if (enabled) {
            lstHitPolicies.enable();
        } else {
            lstHitPolicies.disable();
        }
    }

    @Override
    public void enableBuiltinAggregators(final boolean enabled) {
        if (enabled) {
            lstBuiltinAggregator.enable();
        } else {
            lstBuiltinAggregator.disable();
        }
    }

    @Override
    public void enableDecisionTableOrientation(final boolean enabled) {
        if (enabled) {
            lstDecisionTableOrientation.enable();
        } else {
            lstDecisionTableOrientation.disable();
        }
    }

    @EventHandler("addInputClause")
    @SinkNative(Event.ONCLICK)
    @SuppressWarnings("unused")
    public void onClickAddInputClause(final Event event) {
        presenter.addInputClause();
    }

    @EventHandler("addOutputClause")
    @SinkNative(Event.ONCLICK)
    @SuppressWarnings("unused")
    public void onClickAddOutputClause(final Event event) {
        presenter.addOutputClause();
    }

    @EventHandler("addDecisionRule")
    @SinkNative(Event.ONCLICK)
    @SuppressWarnings("unused")
    public void onClickAddDecisionRule(final Event event) {
        presenter.addDecisionRule();
    }
}
