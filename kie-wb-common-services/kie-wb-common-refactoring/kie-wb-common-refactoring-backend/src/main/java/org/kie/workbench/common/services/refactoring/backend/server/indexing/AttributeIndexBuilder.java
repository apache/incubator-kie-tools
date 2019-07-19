/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.refactoring.backend.server.indexing;

import org.drools.compiler.lang.descr.AttributeDescr;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.refactoring.SharedPart;
import org.kie.workbench.common.services.refactoring.service.PartType;

public class AttributeIndexBuilder {

    private final DefaultIndexBuilder builder;

    public AttributeIndexBuilder(final DefaultIndexBuilder builder) {
        this.builder = PortablePreconditions.checkNotNull("builder",
                                                          builder);
    }

    public void visit(final AttributeDescr descr) {
        visit(descr.getName(), descr.getValue());
    }

    public void visit(final String name,
                      final String value) {

        if (isIgnored(name)) {
            return;
        }

        final PartType type = PartType.getPartTypeFromAttribueDescrName(name);
        switch (type) {
            case AGENDA_GROUP:
            case ACTIVATION_GROUP:
            case RULEFLOW_GROUP:
                SharedPart sharedRef = new SharedPart(value,
                                                      type);
                builder.addGenerator(sharedRef);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported attribute encountered: " + name);
        }
    }

    public boolean isIgnored(final String attr) {
        switch (attr) {
            case "no-loop":
            case "lock-on-active":
            case "salience":
            case "auto-focus":
            case "dialect":
            case "date-effective":
            case "date-expires":
            case "enabled":
            case "duration":
            case "timer":
            case "calendars":
                return true;
        }
        return false;
    }
}
