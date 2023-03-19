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

import org.dashbuilder.dataset.def.BeanDataSetDef;
import org.dashbuilder.dataset.def.BeanDataSetDefBuilder;
import org.dashbuilder.dataset.def.DataSetDef;

public class BeanDataSetDefBuilderImpl extends AbstractDataSetDefBuilder<BeanDataSetDefBuilderImpl> implements BeanDataSetDefBuilder<BeanDataSetDefBuilderImpl> {

    protected DataSetDef createDataSetDef() {
        return new BeanDataSetDef();
    }

    public BeanDataSetDefBuilderImpl generatorClass(String className) {
        ((BeanDataSetDef) def).setGeneratorClass(className);
        return this;
    }

    public BeanDataSetDefBuilderImpl generatorParam(String paramName, String paramValue) {
        ((BeanDataSetDef) def).getParamaterMap().put(paramName, paramValue);
        return this;
    }
}
