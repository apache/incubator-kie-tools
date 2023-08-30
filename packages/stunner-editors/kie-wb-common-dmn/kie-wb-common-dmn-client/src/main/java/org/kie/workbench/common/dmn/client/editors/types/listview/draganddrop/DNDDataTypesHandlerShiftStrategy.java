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

package org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop;

import java.util.function.BiConsumer;

import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;

enum DNDDataTypesHandlerShiftStrategy {

    INSERT_TOP_LEVEL_DATA_TYPE_AT_THE_TOP(DataTypeListItem::insertFieldAbove),
    INSERT_INTO_HOVERED_DATA_TYPE(DataTypeListItem::insertNestedField),
    INSERT_TOP_LEVEL_DATA_TYPE(DataTypeListItem::insertFieldBelow),
    INSERT_SIBLING_DATA_TYPE(DataTypeListItem::insertFieldBelow),
    INSERT_NESTED_DATA_TYPE(DataTypeListItem::insertNestedField);

    private final BiConsumer<DataTypeListItem, DataType> consumer;

    DNDDataTypesHandlerShiftStrategy(final BiConsumer<DataTypeListItem, DataType> consumer) {
        this.consumer = consumer;
    }

    BiConsumer<DataTypeListItem, DataType> getConsumer() {
        return consumer;
    }
}
