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

package org.kie.workbench.common.dmn.api.definition.model;

import java.util.Objects;
import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.property.dmn.Name;

/**
 * RuleAnnotationClause extends DMNElement, but it doesn't store "id" and "description" fields.
 * It's only field is "name". DMN Specs 8.3.2 - Table 35
 */
@Portable
public class RuleAnnotationClause extends DMNElement implements HasName {

    private Name name;

    public RuleAnnotationClause() {
        this.name = new Name();
    }

    public RuleAnnotationClause copy() {
        final RuleAnnotationClause clonedRuleAnnotationClause = new RuleAnnotationClause();
        clonedRuleAnnotationClause.name = Optional.ofNullable(name).map(Name::copy).orElse(null);
        return clonedRuleAnnotationClause;
    }

    @Override
    public Name getName() {
        return this.name;
    }

    @Override
    public void setName(final Name name) {
        this.name = name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final RuleAnnotationClause that = (RuleAnnotationClause) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
