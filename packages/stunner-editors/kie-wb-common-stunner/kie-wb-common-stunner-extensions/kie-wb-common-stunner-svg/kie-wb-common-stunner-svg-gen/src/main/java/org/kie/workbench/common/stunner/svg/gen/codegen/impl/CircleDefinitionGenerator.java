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

import com.ait.lienzo.client.core.shape.Circle;
import org.kie.workbench.common.stunner.svg.gen.codegen.ShapeDefinitionGenerator;
import org.kie.workbench.common.stunner.svg.gen.exception.GeneratorException;
import org.kie.workbench.common.stunner.svg.gen.model.impl.CircleDefinition;
import org.uberfire.annotations.processors.GenerationException;

public class CircleDefinitionGenerator
        extends AbstractShapeDefinitionGenerator<CircleDefinition>
        implements ShapeDefinitionGenerator<CircleDefinition> {

    @Override
    public StringBuffer doGenerate(final CircleDefinition input) throws GeneratorException {
        final double radius = input.getRadius();
        final Map<String, Object> root = new HashMap<String, Object>();
        root.put("className",
                 Circle.class.getName());
        root.put("radius",
                 formatDouble(radius));
        // Generate the code using the given template.
        try {
            return writeTemplate(root);
        } catch (final GenerationException e) {
            throw new GeneratorException(e);
        }
    }

    @Override
    public Class<CircleDefinition> getDefinitionType() {
        return CircleDefinition.class;
    }

    @Override
    protected String getTemplatePath() {
        return "Circle";
    }
}
