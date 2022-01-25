/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.api.property.dmn;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.property.DMNProperty;

@Portable
public class DecisionServiceParametersList implements DMNProperty {

    private DecisionService decisionService;

    public DecisionService getDecisionService() {
        return decisionService;
    }

    public void setDecisionService(final DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DecisionServiceParametersList that = (DecisionServiceParametersList) o;
        if (decisionService != null && that.decisionService != null) {
            return Objects.equals(decisionService.getId(), that.decisionService.getId());
        } else {
            return decisionService == null && that.decisionService == null;
        }

    }

    @Override
    public int hashCode() {
        if (decisionService != null) {
            return Objects.hash(decisionService.getId());
        } else {
            return 0;
        }
    }
}
