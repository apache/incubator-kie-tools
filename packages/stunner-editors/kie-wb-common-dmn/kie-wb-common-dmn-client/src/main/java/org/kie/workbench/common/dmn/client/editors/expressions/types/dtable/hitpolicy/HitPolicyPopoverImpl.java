/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy;

import java.util.Arrays;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.model.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.model.HitPolicy;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverImpl;

@ApplicationScoped
public class HitPolicyPopoverImpl extends AbstractPopoverImpl<HitPolicyPopoverView, HasHitPolicyControl> implements HitPolicyPopoverView.Presenter {

    private TranslationService translationService;

    public HitPolicyPopoverImpl() {
        //CDI proxy
    }

    @Inject
    public HitPolicyPopoverImpl(final HitPolicyPopoverView view,
                                final TranslationService translationService,
                                final BuiltinAggregatorUtils builtinAggregatorUtils) {
        super(view);
        this.translationService = translationService;

        view.init(this);
        view.initHitPolicies(Arrays.asList(HitPolicy.values()));
        view.initBuiltinAggregators(builtinAggregatorUtils.getAllValues());
    }

    @Override
    public String getPopoverTitle() {
        return translationService.getTranslation(DMNEditorConstants.DecisionTableEditor_EditHitPolicy);
    }

    @Override
    public void bind(final HasHitPolicyControl bound,
                     final int uiRowIndex,
                     final int uiColumnIndex) {
        super.bind(bound, uiRowIndex, uiColumnIndex);
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
}
