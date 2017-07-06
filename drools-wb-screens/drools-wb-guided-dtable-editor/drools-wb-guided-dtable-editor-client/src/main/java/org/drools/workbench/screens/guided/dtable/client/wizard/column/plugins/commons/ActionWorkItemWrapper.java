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

import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;

public interface ActionWorkItemWrapper extends ActionWrapper {

    ActionWorkItemWrapper EMPTY_COLUMN = new ActionWorkItemWrapper() {

        @Override
        public boolean isInsertLogical() {
            return false;
        }

        @Override
        public void setInsertLogical(boolean insertLogical) {

        }

        @Override
        public boolean isUpdateEngine() {
            return false;
        }

        @Override
        public void setUpdate(boolean update) {

        }

        @Override
        public DTCellValue52 getDefaultValue() {
            return null;
        }

        @Override
        public void setDefaultValue(DTCellValue52 defaultValue) {

        }

        @Override
        public String getBoundName() {
            return "";
        }

        @Override
        public void setBoundName(String boundName) {

        }

        @Override
        public String getFactField() {
            return "";
        }

        @Override
        public void setFactField(String factField) {

        }

        @Override
        public String getFactType() {
            return "";
        }

        @Override
        public void setFactType(String factType) {

        }

        @Override
        public String getHeader() {
            return "";
        }

        @Override
        public void setHeader(String header) {

        }

        @Override
        public String getType() {
            return "";
        }

        @Override
        public void setType(String type) {

        }

        @Override
        public String getValueList() {
            return "";
        }

        @Override
        public void setValueList(String valueList) {

        }

        @Override
        public ActionCol52 getActionCol52() {
            return new ActionCol52();
        }

        @Override
        public List<BaseColumnFieldDiff> diff(BaseColumn otherColumn) {
            return null;
        }

        @Override
        public String getWorkItemName() {
            return "";
        }

        @Override
        public void setWorkItemName(String workItemName) {

        }

        @Override
        public String getWorkItemResultParameterName() {
            return "";
        }

        @Override
        public void setWorkItemResultParameterName(String workItemResultParameterName) {

        }

        @Override
        public String getParameterClassName() {
            return "";
        }

        @Override
        public void setParameterClassName(String parameterClassName) {

        }
    };

    List<BaseColumnFieldDiff> diff(BaseColumn otherColumn);

    String getWorkItemName();

    void setWorkItemName(String workItemName);

    String getWorkItemResultParameterName();

    void setWorkItemResultParameterName(String workItemResultParameterName);

    String getParameterClassName();

    void setParameterClassName(String parameterClassName);
}
