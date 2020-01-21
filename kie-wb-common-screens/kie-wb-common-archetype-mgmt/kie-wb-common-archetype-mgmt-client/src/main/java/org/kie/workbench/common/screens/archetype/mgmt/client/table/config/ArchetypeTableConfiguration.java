/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.archetype.mgmt.client.table.config;

import java.util.Objects;

public class ArchetypeTableConfiguration {

    private boolean showIncludeColumn;
    private boolean showStatusColumn;

    private boolean showAddAction;
    private boolean showDeleteAction;
    private boolean showValidateAction;

    private ArchetypeTableConfiguration(final Builder builder) {
        showIncludeColumn = builder.showIncludeColumn;
        showStatusColumn = builder.showStatusColumn;
        showAddAction = builder.showAddAction;
        showDeleteAction = builder.showDeleteAction;
        showValidateAction = builder.showValidateAction;
    }

    public boolean isShowIncludeColumn() {
        return showIncludeColumn;
    }

    public boolean isShowStatusColumn() {
        return showStatusColumn;
    }

    public boolean isShowAddAction() {
        return showAddAction;
    }

    public boolean isShowDeleteAction() {
        return showDeleteAction;
    }

    public boolean isShowValidateAction() {
        return showValidateAction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArchetypeTableConfiguration that = (ArchetypeTableConfiguration) o;
        return showIncludeColumn == that.showIncludeColumn &&
                showStatusColumn == that.showStatusColumn &&
                showAddAction == that.showAddAction &&
                showDeleteAction == that.showDeleteAction &&
                showValidateAction == that.showValidateAction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(showIncludeColumn,
                            showStatusColumn,
                            showAddAction,
                            showDeleteAction,
                            showValidateAction);
    }

    public static class Builder {

        private boolean showIncludeColumn;
        private boolean showStatusColumn;
        private boolean showAddAction;
        private boolean showDeleteAction;
        private boolean showValidateAction;

        public Builder withIncludeColumn() {
            showIncludeColumn = true;
            return this;
        }

        public Builder withStatusColumn() {
            showStatusColumn = true;
            return this;
        }

        public Builder withAddAction() {
            showAddAction = true;
            return this;
        }

        public Builder withDeleteAction() {
            showDeleteAction = true;
            return this;
        }

        public Builder withValidateAction() {
            showValidateAction = true;
            return this;
        }

        public ArchetypeTableConfiguration build() {
            return new ArchetypeTableConfiguration(this);
        }
    }
}
