/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.components.toolbox;

import org.kie.workbench.common.stunner.client.lienzo.components.toolbox.builder.LienzoToolboxBuilderImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.toolbox.builder.LienzoToolboxButtonBuilder;
import org.kie.workbench.common.stunner.client.lienzo.components.toolbox.builder.LienzoToolboxButtonGridBuilder;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.builder.ToolboxBuilder;
import org.kie.workbench.common.stunner.core.client.components.toolbox.builder.ToolboxButtonBuilder;
import org.kie.workbench.common.stunner.core.client.components.toolbox.builder.ToolboxButtonGridBuilder;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LienzoToolboxFactory implements ToolboxFactory {

    @Override
    public ToolboxBuilder<?, ?, ?> toolboxBuilder() {
        return new LienzoToolboxBuilderImpl();
    }

    @Override
    public ToolboxButtonGridBuilder toolboxGridBuilder() {
        return new LienzoToolboxButtonGridBuilder();
    }

    @Override
    public ToolboxButtonBuilder<?> toolboxButtonBuilder() {
        return new LienzoToolboxButtonBuilder();
    }

}
