/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.validation;

import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.rule.violations.AbstractRuleViolation;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;

@Portable
public class BPMNViolation extends AbstractRuleViolation implements DomainViolation {

    public BPMNViolation(@MapsTo("message") String message, @MapsTo("type") Type type, @MapsTo("uuid") String uuid) {
        super(type, message);
        setUUID(uuid);
    }

    @Override
    public Optional<Object[]> getArguments() {
        return of(getMessage());
    }
}
