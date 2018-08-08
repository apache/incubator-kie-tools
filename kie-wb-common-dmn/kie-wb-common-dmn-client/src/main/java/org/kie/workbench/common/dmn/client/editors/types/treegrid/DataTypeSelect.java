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

package org.kie.workbench.common.dmn.client.editors.types.treegrid;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeUtils;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class DataTypeSelect {

    private final View view;

    private final DataTypeUtils dataTypeUtils;

    private DataType dataType;

    @Inject
    public DataTypeSelect(final View view,
                          final DataTypeUtils dataTypeUtils) {
        this.view = view;
        this.dataTypeUtils = dataTypeUtils;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public void init(final DataType dataType) {
        this.dataType = dataType;
        this.view.setDataType(dataType);
    }

    DataType getDataType() {
        return dataType;
    }

    List<DataType> getDefaultDataTypes() {
        return dataTypeUtils.defaultDataTypes();
    }

    List<DataType> getCustomDataTypes() {
        return dataTypeUtils.customDataTypes();
    }

    public interface View extends UberElemental<DataTypeSelect>,
                                  IsElement {

        void setDataType(DataType type);
    }
}
