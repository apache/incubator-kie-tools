/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons;

import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;

public interface ActionWrapper {

    ActionWrapper EMPTY_COLUMN = new ActionWrapper() {

        @Override
        public boolean isInsertLogical() {
            return false;
        }

        @Override
        public void setInsertLogical(final boolean insertLogical) {
        }

        @Override
        public boolean isUpdateEngine() {
            return false;
        }

        @Override
        public void setUpdate(final boolean update) {
        }

        @Override
        public DTCellValue52 getDefaultValue() {
            return null;
        }

        @Override
        public void setDefaultValue(final DTCellValue52 defaultValue) {
        }

        @Override
        public String getBoundName() {
            return "";
        }

        @Override
        public void setBoundName(final String boundName) {
        }

        @Override
        public String getFactField() {
            return "";
        }

        @Override
        public void setFactField(final String factField) {
        }

        @Override
        public String getFactType() {
            return "";
        }

        @Override
        public void setFactType(final String factType) {
        }

        @Override
        public String getHeader() {
            return "";
        }

        @Override
        public void setHeader(final String header) {
        }

        @Override
        public String getType() {
            return "";
        }

        @Override
        public void setType(final String type) {
        }

        @Override
        public String getValueList() {
            return "";
        }

        @Override
        public void setValueList(final String valueList) {
        }

        @Override
        public ActionCol52 getActionCol52() {
            return new ActionCol52();
        }
    };

    boolean isInsertLogical();

    void setInsertLogical(final boolean insertLogical);

    boolean isUpdateEngine();

    void setUpdate(final boolean update);

    DTCellValue52 getDefaultValue();

    void setDefaultValue(final DTCellValue52 defaultValue);

    String getBoundName();

    void setBoundName(final String boundName);

    String getFactField();

    void setFactField(final String factField);

    String getFactType();

    void setFactType(final String factType);

    String getHeader();

    void setHeader(final String header);

    String getType();

    void setType(final String type);

    String getValueList();

    void setValueList(final String valueList);

    ActionCol52 getActionCol52();
}
