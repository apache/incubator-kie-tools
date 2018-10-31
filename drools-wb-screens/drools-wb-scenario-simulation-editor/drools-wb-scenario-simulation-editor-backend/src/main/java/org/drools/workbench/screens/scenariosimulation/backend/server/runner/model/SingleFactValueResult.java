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

package org.drools.workbench.screens.scenariosimulation.backend.server.runner.model;

public class SingleFactValueResult {

    private final boolean satisfied;

    private final Object result;

    public SingleFactValueResult(Object result, boolean satisfied) {
        this.satisfied = satisfied;
        this.result = result;
    }

    public static SingleFactValueResult createResult(Object result) {
        return new SingleFactValueResult(result, true);
    }

    public static SingleFactValueResult createErrorResult() {
        return new SingleFactValueResult(null, false);
    }

    public boolean isSatisfied() {
        return satisfied;
    }

    public Object getResult() {
        return result;
    }
}
