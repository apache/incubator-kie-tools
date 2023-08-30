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

import com.ait.lienzo.client.core.shape.Group;
import org.kie.workbench.common.stunner.svg.gen.codegen.PrimitiveDefinitionGenerator;
import org.kie.workbench.common.stunner.svg.gen.exception.GeneratorException;
import org.kie.workbench.common.stunner.svg.gen.model.impl.GroupDefinition;
import org.uberfire.annotations.processors.GenerationException;

public class GroupDefinitionGenerator
        extends AbstractPrimitiveDefinitionGenerator<GroupDefinition>
        implements PrimitiveDefinitionGenerator<GroupDefinition> {

    private final static String ADD_WRAPPED = ".add( %1s )";

    @Override
    protected StringBuffer doGenerate(final GroupDefinition input) throws GeneratorException {
        // Generate the code using the given template.
        try {
            return writeTemplate(new HashMap<>());
        } catch (final GenerationException e) {
            throw new GeneratorException(e);
        }
    }

    @Override
    public StringBuffer generate(final GroupDefinition input) throws GeneratorException {
        final StringBuffer wrapped = super.generate(input);
        final StringBuffer wrapper;
        try {
            wrapper = writeTemplate(new HashMap<>());
            super.appendDraggable(wrapper,
                                  input);
            super.appendListening(wrapped,
                                  input);
            wrapper.append(formatString(ADD_WRAPPED,
                                        wrapped.toString()));
        } catch (final GenerationException e) {
            throw new GeneratorException(e);
        }
        return wrapper;
    }

    @Override
    protected StringBuffer writeTemplate(final Map<String, Object> ctxt) throws GenerationException {
        ctxt.put("className",
                 Group.class.getName());
        return super.writeTemplate(ctxt);
    }

    @Override
    public Class<GroupDefinition> getDefinitionType() {
        return GroupDefinition.class;
    }

    @Override
    protected String getTemplatePath() {
        return "Group";
    }
}
