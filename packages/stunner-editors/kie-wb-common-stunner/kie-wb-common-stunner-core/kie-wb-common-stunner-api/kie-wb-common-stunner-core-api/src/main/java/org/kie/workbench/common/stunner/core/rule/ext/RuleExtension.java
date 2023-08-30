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


package org.kie.workbench.common.stunner.core.rule.ext;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.rule.Rule;

/**
 * A rule extension is an Stunner's built-in mechanism
 * that allows to re-use or provide custom rule evaluation handlers
 * for complex scenarios, in which default built-in rule or contexts
 * do not satisfy the domain semantic restrictions.
 * <p>
 * A rule extension:
 * - Is given for a certain Definition
 * - Provides the type of the rule evaluation handler
 * to be used on the evaluation's operation.
 * - Optionally provide an array of Strings or an array of types
 * that can be further used by the concrete handler type
 * for the runtime evaluation.
 * @See {@link RuleExtensionHandler}
 * @See {@link org.kie.workbench.common.stunner.core.rule.annotation.RuleExtension}
 */
@Portable
public class RuleExtension implements Rule {

    private final String name;
    private final String id;
    private transient Class<? extends RuleExtensionHandler> handlerType;
    private transient String[] arguments;
    private transient Class<?>[] typeArguments;

    public RuleExtension(
            final @MapsTo("name") String name,
            final @MapsTo("id") String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public RuleExtension setHandlerType(final Class<? extends RuleExtensionHandler> handlerType) {
        this.handlerType = handlerType;
        return this;
    }

    public Class<? extends RuleExtensionHandler> getHandlerType() {
        return handlerType;
    }

    public RuleExtension setArguments(final String[] arguments) {
        this.arguments = arguments;
        return this;
    }

    public String[] getArguments() {
        return arguments;
    }

    public Class<?>[] getTypeArguments() {
        return typeArguments;
    }

    public RuleExtension setTypeArguments(final Class<?>[] typeArguments) {
        this.typeArguments = typeArguments;
        return this;
    }
}
