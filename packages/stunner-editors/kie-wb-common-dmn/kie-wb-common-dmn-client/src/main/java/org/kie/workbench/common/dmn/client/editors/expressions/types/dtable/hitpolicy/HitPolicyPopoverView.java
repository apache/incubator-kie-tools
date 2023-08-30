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

import java.util.List;

import org.kie.workbench.common.dmn.api.definition.model.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.model.HitPolicy;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.PopoverView;
import org.uberfire.client.mvp.UberElement;

public interface HitPolicyPopoverView extends PopoverView,
                                              UberElement<HitPolicyPopoverView.Presenter> {

    interface Presenter extends HasCellEditorControls.Editor<HasHitPolicyControl> {

        void setHitPolicy(final HitPolicy hitPolicy);

        void setBuiltinAggregator(final BuiltinAggregator aggregator);
    }

    void initHitPolicies(final List<HitPolicy> hitPolicies);

    void initBuiltinAggregators(final List<BuiltinAggregator> aggregators);

    void initSelectedHitPolicy(final HitPolicy hitPolicy);

    void initSelectedBuiltinAggregator(final BuiltinAggregator aggregator);

    void enableHitPolicies(final boolean enabled);

    void enableBuiltinAggregators(final boolean enabled);
}
