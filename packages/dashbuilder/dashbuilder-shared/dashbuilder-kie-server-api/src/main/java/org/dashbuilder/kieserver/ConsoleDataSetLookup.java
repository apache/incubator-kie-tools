/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.kieserver;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetOp;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ConsoleDataSetLookup extends DataSetLookup {

    private String serverTemplateId;

    public static DataSetLookup fromInstance(DataSetLookup orig,
                                             String serverTemplateId) {
        ConsoleDataSetLookup clone = new ConsoleDataSetLookup();
        clone.setDataSetUUID(orig.getDataSetUUID());
        clone.setRowOffset(orig.getRowOffset());
        clone.setNumberOfRows(orig.getNumberOfRows());
        for (DataSetOp dataSetOp : orig.getOperationList()) {
            clone.getOperationList().add(dataSetOp.cloneInstance());
        }
        clone.setServerTemplateId(serverTemplateId);
        return clone;
    }

    public String getServerTemplateId() {
        return serverTemplateId;
    }

    public void setServerTemplateId(String serverTemplateId) {
        this.serverTemplateId = serverTemplateId;
    }

    @Override
    public DataSetLookup cloneInstance() {
        return fromInstance(super.cloneInstance(),
                            getServerTemplateId());
    }
}