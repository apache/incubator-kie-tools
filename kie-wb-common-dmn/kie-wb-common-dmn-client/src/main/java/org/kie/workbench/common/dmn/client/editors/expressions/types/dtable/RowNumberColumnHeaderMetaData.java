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

import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.definition.v1_1.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.v1_1.HitPolicy;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

class RowNumberColumnHeaderMetaData implements GridColumn.HeaderMetaData {

    private static final String ROW_NUMBER_GROUP = "RowNumberColumn";

    private final Supplier<HitPolicy> hitPolicySupplier;
    private final Supplier<BuiltinAggregator> builtinAggregatorSupplier;

    public RowNumberColumnHeaderMetaData(final Supplier<HitPolicy> hitPolicySupplier,
                                         final Supplier<BuiltinAggregator> builtinAggregatorSupplier) {
        this.hitPolicySupplier = hitPolicySupplier;
        this.builtinAggregatorSupplier = builtinAggregatorSupplier;
    }

    @Override
    public String getColumnGroup() {
        return ROW_NUMBER_GROUP;
    }

    @Override
    public void setColumnGroup(final String columnGroup) {
        throw new UnsupportedOperationException("Group cannot be set.");
    }

    @Override
    public String getTitle() {
        final HitPolicy hitPolicy = hitPolicySupplier.get();
        final BuiltinAggregator builtinAggregator = builtinAggregatorSupplier.get();
        final StringBuffer sb = new StringBuffer(hitPolicy.value().substring(0, 1).toUpperCase());
        if (HitPolicy.COLLECT == hitPolicy) {
            switch (builtinAggregator) {
                case COUNT:
                    sb.append("#");
                    break;
                case MAX:
                    sb.append(">");
                    break;
                case MIN:
                    sb.append("<");
                    break;
                case SUM:
                    sb.append("+");
            }
        }
        return sb.toString();
    }

    @Override
    public void setTitle(final String title) {
        throw new UnsupportedOperationException("Title is derived from the Decision Table Hit Policy and cannot be set on the HeaderMetaData.");
    }
}
