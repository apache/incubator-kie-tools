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

package org.kie.workbench.common.dmn.client.widgets.grid.model;

import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

public abstract class BaseUIModelMapper<E extends Expression> implements UIModelMapper {

    protected Supplier<GridData> uiModel;
    protected Supplier<Optional<E>> dmnModel;

    public BaseUIModelMapper(final Supplier<GridData> uiModel,
                             final Supplier<Optional<E>> dmnModel) {
        this.uiModel = uiModel;
        this.dmnModel = dmnModel;
    }

    public Supplier<GridData> getUiModel() {
        return uiModel;
    }

    public Supplier<Optional<E>> getDmnModel() {
        return dmnModel;
    }
}
