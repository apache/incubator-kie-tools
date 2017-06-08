/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.services.refactoring.model.query;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Convenience class to expose a return type to Errai's marshalling generators
 */
@Portable
public class RefactoringRuleNamePageRow extends RefactoringPageRow<RefactoringRuleNamePageRow.RuleName> {

    @Portable
    public static class RuleName {

        private String simpleRuleName;
        private String packageName;

        public RuleName(final @MapsTo("simpleRuleName") String simpleRuleName,
                        final @MapsTo("packageName") String packageName) {
            this.simpleRuleName = simpleRuleName;
            this.packageName = packageName;
        }

        public String getSimpleRuleName() {
            return simpleRuleName;
        }

        public String getPackageName() {
            return packageName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            RuleName ruleName = (RuleName) o;

            if (!simpleRuleName.equals(ruleName.simpleRuleName)) {
                return false;
            }
            return packageName.equals(ruleName.packageName);
        }

        @Override
        public int hashCode() {
            int result = simpleRuleName.hashCode();
            result = ~~result;
            result = 31 * result + packageName.hashCode();
            result = ~~result;
            return result;
        }
    }
}
