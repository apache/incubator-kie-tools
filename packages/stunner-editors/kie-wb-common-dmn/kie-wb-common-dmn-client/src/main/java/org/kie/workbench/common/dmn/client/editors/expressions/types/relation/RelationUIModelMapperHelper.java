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

package org.kie.workbench.common.dmn.client.editors.expressions.types.relation;

import org.kie.workbench.common.dmn.api.definition.model.Relation;

public class RelationUIModelMapperHelper {

    public static final int ROW_INDEX_COLUMN_COUNT = 1;

    public enum RelationSection {
        NONE,
        ROW_INDEX,
        INFORMATION_ITEM
    }

    public static RelationSection getSection(final Relation relation,
                                             final int columnIndex) {
        if (columnIndex < 0) {
            return RelationSection.NONE;
        }

        final int iiColumnCount = relation.getColumn().size();

        int _columnIndex = columnIndex;
        if ((_columnIndex = _columnIndex - ROW_INDEX_COLUMN_COUNT) < 0) {
            return RelationSection.ROW_INDEX;
        }
        if ((_columnIndex - iiColumnCount) < 0) {
            return RelationSection.INFORMATION_ITEM;
        }
        return RelationSection.NONE;
    }

    public static int getInformationItemIndex(final Relation relation,
                                              final int columnIndex) {
        final int iiColumnCount = relation.getColumn().size();

        int _columnIndex = columnIndex;
        if ((_columnIndex = _columnIndex - ROW_INDEX_COLUMN_COUNT) < 0) {
            throw new IllegalArgumentException("columnIndex referenced 'Row index' column. Should be a valid InformationItem column.");
        }
        if (_columnIndex > iiColumnCount - 1) {
            throw new IllegalArgumentException("columnIndex did not reference a valid InformationItem column.");
        }
        return _columnIndex;
    }
}
