/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.impl;

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.StaticDataSetDef;
import org.dashbuilder.dataset.def.StaticDataSetDefBuilder;

public class StaticDataSetDefBuilderImpl extends AbstractDataSetDefBuilder<StaticDataSetDefBuilderImpl> implements StaticDataSetDefBuilder<StaticDataSetDefBuilderImpl> {

    public StaticDataSetDefBuilderImpl row(Object... values) {
        getDefinition().getDataSet().setValuesAt(getDefinition().getDataSet().getRowCount(), values);
        return this;
    }

    private StaticDataSetDef getDefinition() {
        return (StaticDataSetDef) def;
    }

    protected DataSetDef createDataSetDef() {
        return new StaticDataSetDef();
    }
}
