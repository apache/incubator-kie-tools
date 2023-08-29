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


package org.kie.workbench.common.stunner.svg.gen.codegen.impl;

import java.util.HashMap;
import java.util.Map;

import com.ait.lienzo.client.core.shape.MultiPath;
import org.kie.workbench.common.stunner.svg.gen.codegen.ShapeDefinitionGenerator;
import org.kie.workbench.common.stunner.svg.gen.exception.GeneratorException;
import org.kie.workbench.common.stunner.svg.gen.model.impl.MultiPathDefinition;
import org.uberfire.annotations.processors.GenerationException;

public class MultiPathDefinitionGenerator
        extends AbstractShapeDefinitionGenerator<MultiPathDefinition>
        implements ShapeDefinitionGenerator<MultiPathDefinition> {

    @Override
    public StringBuffer doGenerate(final MultiPathDefinition input) throws GeneratorException {
        final Map<String, Object> root = new HashMap<String, Object>();
        root.put("className",
                 MultiPath.class.getName());
        root.put("pathInstanceId",
                 SVGViewFactoryGenerator.getStaticFieldValidId(input.getId()));

        // Generate the code using the given template.
        try {
            return writeTemplate(root);
        } catch (final GenerationException e) {
            throw new GeneratorException(e);
        }
    }

    @Override
    protected String getTemplatePath() {
        return "MultiPath";
    }

    @Override
    public Class<MultiPathDefinition> getDefinitionType() {
        return MultiPathDefinition.class;
    }
}
