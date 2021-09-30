/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataprovider.sql.model;

import org.dashbuilder.dataset.sort.SortOrder;

public class SortColumn extends Column {

    protected Column source = null;
    protected SortOrder order = SortOrder.ASCENDING;

    public SortColumn(Column source, SortOrder order) {
        super(source.getName());
        this.source = source;
        this.order = order;
    }

    public Column getSource() {
        return source;
    }

    public SortOrder getOrder() {
        return order;
    }
}
