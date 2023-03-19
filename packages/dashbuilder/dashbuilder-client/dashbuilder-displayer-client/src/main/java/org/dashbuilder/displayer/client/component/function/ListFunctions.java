/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client.component.function;

import java.util.Map;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.displayer.external.ExternalComponentFunction;

/**
 * A meta function to list available functions.
 *
 */
@Dependent
public class ListFunctions implements ExternalComponentFunction {

    @Inject
    ComponentFunctionLocator locator;

    @Override
    public void exec(Map<String, Object> params, Consumer<Object> onFinish, Consumer<String> onError) {
        onFinish.accept(locator.listFunctions());
    }

}
