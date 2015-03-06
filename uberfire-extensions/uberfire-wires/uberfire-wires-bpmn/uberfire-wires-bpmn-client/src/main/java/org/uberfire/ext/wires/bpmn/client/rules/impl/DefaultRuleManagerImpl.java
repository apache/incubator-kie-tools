/*
 * Copyright 2015 JBoss Inc
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
package org.uberfire.ext.wires.bpmn.client.rules.impl;

import java.util.HashSet;
import java.util.Set;

import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.bpmn.api.model.rules.Rule;
import org.uberfire.ext.wires.bpmn.client.rules.RuleManager;

/**
 * Default implementation of Rule Manager
 */
public class DefaultRuleManagerImpl implements RuleManager {

    private static final RuleManager INSTANCE = new DefaultRuleManagerImpl();

    private final Set<Rule> rules = new HashSet<Rule>();

    private DefaultRuleManagerImpl() {
        //Singleton
    }

    public static RuleManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void addRule( final Rule rule ) {
        rules.add( PortablePreconditions.checkNotNull( "rule",
                                                       rule ) );
    }

}
