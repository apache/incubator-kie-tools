/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client.infra;

import org.gwtbootstrap3.client.ui.constants.ColumnSize;

public class ColumnSizeBuilder {

    public static String buildColumnSize(final int value) {
        switch (value) {
            case 1:
                return buildSize(ColumnSize.MD_1);
            case 2:
                return buildSize(ColumnSize.MD_2);
            case 3:
                return buildSize(ColumnSize.MD_3);
            case 4:
                return buildSize(ColumnSize.MD_4);
            case 5:
                return buildSize(ColumnSize.MD_5);
            case 6:
                return buildSize(ColumnSize.MD_6);
            case 7:
                return buildSize(ColumnSize.MD_7);
            case 8:
                return buildSize(ColumnSize.MD_8);
            case 9:
                return buildSize(ColumnSize.MD_9);
            case 10:
                return buildSize(ColumnSize.MD_10);
            case 11:
                return buildSize(ColumnSize.MD_11);
            case 12:
                return buildSize(ColumnSize.MD_12);
            default:
                return buildSize(ColumnSize.MD_12);
        }
    }

    private static String buildSize(ColumnSize mdSize) {
        String size = mdSize.getCssName() + " " + ColumnSize.XS_12.getCssName();
        if (mdSize == ColumnSize.MD_4) {
            size += " " + ColumnSize.SM_6.getCssName();
        } else if (mdSize == ColumnSize.MD_5) {
            size += " " + ColumnSize.SM_6.getCssName();
        } else if (mdSize == ColumnSize.MD_6) {
            size += " " + ColumnSize.SM_6.getCssName();
        } else {
            size += " " + ColumnSize.SM_12.getCssName();
        }
        return size;
    }
}
