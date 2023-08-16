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


package org.kie.workbench.common.stunner.core.profile;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;

public class BindableDomainProfile {

    public static BindableDomainProfile build(final Class<?>... types) {
        return new BindableDomainProfile(Stream.of(types)
                                                 .map(BindableAdapterUtils::getDefinitionId)
                                                 .collect(Collectors.toSet()));
    }

    private final Set<String> definitionsAllowed;

    BindableDomainProfile(Set<String> definitionsAllowed) {
        this.definitionsAllowed = definitionsAllowed;
    }

    public Predicate<String> definitionAllowedFilter() {
        return definitionsAllowed::contains;
    }
}
