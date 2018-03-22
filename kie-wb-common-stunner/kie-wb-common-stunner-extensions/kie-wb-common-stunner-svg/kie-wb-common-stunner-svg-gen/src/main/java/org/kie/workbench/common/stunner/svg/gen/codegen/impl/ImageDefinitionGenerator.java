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

package org.kie.workbench.common.stunner.svg.gen.codegen.impl;

import java.util.HashMap;
import java.util.Map;

import com.ait.lienzo.client.core.shape.Picture;
import org.kie.workbench.common.stunner.svg.gen.codegen.ShapeDefinitionGenerator;
import org.kie.workbench.common.stunner.svg.gen.exception.GeneratorException;
import org.kie.workbench.common.stunner.svg.gen.model.impl.ImageDefinition;
import org.uberfire.annotations.processors.exceptions.GenerationException;

public class ImageDefinitionGenerator
        extends AbstractShapeDefinitionGenerator<ImageDefinition>
        implements ShapeDefinitionGenerator<ImageDefinition> {

    @Override
    public StringBuffer doGenerate(final ImageDefinition input) throws GeneratorException {
        final String href = input.getHref();
        final Map<String, Object> root = new HashMap<>();
        root.put("className",
                 Picture.class.getName());
        root.put("href",
                 href);
        // Generate the code using the given template.
        try {
            return writeTemplate(root);
        } catch (final GenerationException e) {
            throw new GeneratorException(e);
        }
    }

    @Override
    public Class<ImageDefinition> getDefinitionType() {
        return ImageDefinition.class;
    }

    @Override
    protected String getTemplatePath() {
        return "Image";
    }
}
