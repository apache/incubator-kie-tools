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


package org.kie.workbench.common.forms.dynamic.client.init;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Assert;
import org.kie.workbench.common.forms.dynamic.service.shared.DynamicContext;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.StaticContext;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.processing.engine.handling.FormHandler;

@Dependent
public class FormHandlerGeneratorManager {

    protected FormHandlerGenerator staticGenerator;

    protected FormHandlerGenerator dynamicGenerator;

    @Inject
    public FormHandlerGeneratorManager(@StaticContext FormHandlerGenerator staticGenerator,
                                       @DynamicContext FormHandlerGenerator dynamicGenerator) {
        this.staticGenerator = staticGenerator;
        this.dynamicGenerator = dynamicGenerator;
    }

    public FormHandler getFormHandler(FormRenderingContext context) {
        Assert.notNull("Context cannot be null",
                       context);
        if (context instanceof MapModelRenderingContext) {
            return dynamicGenerator.generateFormHandler(context);
        }
        return staticGenerator.generateFormHandler(context);
    }
}
