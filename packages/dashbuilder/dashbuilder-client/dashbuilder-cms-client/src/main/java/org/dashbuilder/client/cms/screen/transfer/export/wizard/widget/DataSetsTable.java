/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.cms.screen.transfer.export.wizard.widget;

import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.dataset.def.DataSetDef;

@ApplicationScoped
public class DataSetsTable extends AssetsTableAbstractPresenter<DataSetDef> {

    private static final String[] HEADERS = {"UUID", "Name", "Type"};

    @Override
    public String[] getHeaders() {
        return HEADERS;
    }

    @Override
    public String[] toRow(DataSetDef dataSetDef) {
        return new String[]{
                            dataSetDef.getUUID(),
                            dataSetDef.getName(),
                            dataSetDef.getProvider().getName()
        };
    }

}