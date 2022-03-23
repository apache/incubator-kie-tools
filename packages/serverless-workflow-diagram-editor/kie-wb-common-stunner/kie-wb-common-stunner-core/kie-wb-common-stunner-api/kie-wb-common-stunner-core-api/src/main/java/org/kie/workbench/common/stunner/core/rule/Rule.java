/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.rule;

/**
 * A Rule defines the concrete conditions that satisfy or violate
 * some semantics or constraints that must be met for a domain.
 * <p>
 * Rules must be visible in both server and client side, portable
 * in the errai bus and do not provide any kind of logic neither
 * enterprise context dependency , they're just beans that act as the holder instances
 * for the inputs at runtime for the evaluation.
 */
public interface Rule {

    /**
     * A name for the rule.
     */
    String getName();
}
