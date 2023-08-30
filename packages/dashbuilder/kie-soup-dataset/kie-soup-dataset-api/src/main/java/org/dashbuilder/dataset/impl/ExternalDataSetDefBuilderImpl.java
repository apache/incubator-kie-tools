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

package org.dashbuilder.dataset.impl;

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.ExternalDataSetDef;
import org.dashbuilder.dataset.def.ExternalDataSetDefBuilder;

public class ExternalDataSetDefBuilderImpl extends AbstractDataSetDefBuilder<ExternalDataSetDefBuilderImpl> implements ExternalDataSetDefBuilder<ExternalDataSetDefBuilderImpl> {

    protected DataSetDef createDataSetDef() {
        return new ExternalDataSetDef();
    }

    @Override
    public ExternalDataSetDefBuilderImpl url(String url) {
        ((ExternalDataSetDef) def).setUrl(url);
        return this;
    }

    @Override
    public ExternalDataSetDefBuilderImpl dynamic(boolean dynamic) {
        ((ExternalDataSetDef) def).setDynamic(dynamic);
        return this;
    }
}
