/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import org.kie.workbench.common.dmn.api.definition.v1_1.Relation;

public class ContextUIModelMapperHelper {

    public static final int ROW_INDEX_COLUMN_COUNT = 1;
    public static final int NAME_COLUMN_COUNT = 1;
    public static final int EXPRESSION_COLUMN_COUNT = 1;

    public enum ContextSection {
        NONE,
        ROW_INDEX,
        NAME,
        EXPRESSION
    }

    public static ContextSection getSection(final int columnIndex) {
        int _columnIndex = columnIndex;
        if ((_columnIndex = _columnIndex - ROW_INDEX_COLUMN_COUNT) < 0) {
            return ContextSection.ROW_INDEX;
        }
        if ((_columnIndex = _columnIndex - NAME_COLUMN_COUNT) < 0) {
            return ContextSection.NAME;
        }
        if (_columnIndex - EXPRESSION_COLUMN_COUNT < 0) {
            return ContextSection.EXPRESSION;
        }
        return ContextSection.NONE;
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
