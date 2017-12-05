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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridRenderer;

public class FunctionGridRenderer extends BaseExpressionGridRenderer {

    public FunctionGridRenderer(final boolean hideHeader) {
        //TODO {manstis} We only want to hide the top of the header when nested
        // i.e. we still need to show the expression language and formal parameters
        // Perhaps using a fixed row instead of two header rows is better!?!
        super(false);
    }
}