/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy;

import java.util.Arrays;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.v1_1.HitPolicy;

@ApplicationScoped
public class HitPolicyPopoverImpl implements HitPolicyPopoverView.Presenter {

    private HitPolicyPopoverView view;
    private Optional<HasHitPolicyControl> binding = Optional.empty();

    public HitPolicyPopoverImpl() {
        //CDI proxy
    }

    @Inject
    public HitPolicyPopoverImpl(final HitPolicyPopoverView view,
                                final BuiltinAggregatorUtils builtinAggregatorUtils) {
        this.view = view;

        view.init(this);
        view.initHitPolicies(Arrays.asList(HitPolicy.values()));
        view.initBuiltinAggregators(builtinAggregatorUtils.getAllValues());
        view.initDecisionTableOrientations(Arrays.asList(DecisionTableOrientation.values()));
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public void bind(final HasHitPolicyControl bound,
                     final int uiRowIndex,
                     final int uiColumnIndex) {
        binding = Optional.ofNullable(bound);
        refresh();
    }

    private void refresh() {
        binding.ifPresent(b -> {
            if (b.getHitPolicy() == null) {
                view.enableHitPolicies(false);
                view.enableBuiltinAggregators(false);
            } else {
                view.enableHitPolicies(true);
                view.initSelectedHitPolicy(b.getHitPolicy());
                view.enableBuiltinAggregators(HitPolicy.COLLECT.equals(b.getHitPolicy()));
                view.initSelectedBuiltinAggregator(b.getBuiltinAggregator());
            }

            if (b.getDecisionTableOrientation() == null) {
                view.enableDecisionTableOrientation(false);
            } else {
                view.enableDecisionTableOrientation(true);
                view.initSelectedDecisionTableOrientation(b.getDecisionTableOrientation());
            }
        });
    }

    @Override
    public void setHitPolicy(final HitPolicy hitPolicy) {
        binding.ifPresent(b -> b.setHitPolicy(hitPolicy,
                                              this::refresh));
    }

    @Override
    public void setBuiltinAggregator(final BuiltinAggregator aggregator) {
        binding.ifPresent(b -> b.setBuiltinAggregator(aggregator));
    }

    @Override
    public void setDecisionTableOrientation(final DecisionTableOrientation orientation) {
        binding.ifPresent(b -> b.setDecisionTableOrientation(orientation));
    }

    @Override
    public void show(final Optional<String> editorTitle) {
        binding.ifPresent(b -> view.show(editorTitle));
    }

    @Override
    public void hide() {
        binding.ifPresent(b -> view.hide());
    }
}
