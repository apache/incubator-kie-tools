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


package org.kie.workbench.common.stunner.core.rule;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import jsinterop.annotations.JsType;

@JsType
public class RuleSetImpl implements RuleSet {

    private final String name;
    private final Collection<Rule> rules;

    public static RuleSetImpl build(String name,
                                    Rule[] rules) {
        return new RuleSetImpl(name, Arrays.stream(rules).collect(Collectors.toList()));
    }

    public RuleSetImpl(String name,
                       Collection<Rule> rules) {
        this.name = name;
        this.rules = rules;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<Rule> getRules() {
        return rules;
    }
}
