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

package org.kie.workbench.common.stunner.svg.gen.codegen.impl;

import org.kie.workbench.common.stunner.svg.gen.codegen.PrimitiveDefinitionGenerator;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGDocumentTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGElementTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.impl.SVGCircleTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.impl.SVGDocumentTranslatorImpl;
import org.kie.workbench.common.stunner.svg.gen.translator.impl.SVGGroupTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.impl.SVGImageTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.impl.SVGMultiPathTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.impl.SVGRectTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.impl.SVGUseTranslator;
import org.w3c.dom.Element;

public class ViewGenerators {

    public static SVGDocumentTranslator newTranslator() {
        return new SVGDocumentTranslatorImpl(newDefaultTranslators());
    }

    public static SVGViewFactoryGenerator newViewFactoryGenerator() {
        return new SVGViewFactoryGenerator();
    }

    public static SVGViewDefinitionGenerator newShapeViewGenerator() {
        return new SVGViewDefinitionGenerator();
    }

    @SuppressWarnings("unchecked")
    public static SVGElementTranslator<Element, Object>[] newDefaultTranslators() {
        return new SVGElementTranslator[]{
                new SVGMultiPathTranslator(),
                new SVGRectTranslator(),
                new SVGCircleTranslator(),
                new SVGImageTranslator(),
                new SVGGroupTranslator(),
                new SVGUseTranslator()
        };
    }

    @SuppressWarnings("unchecked")
    public static PrimitiveDefinitionGenerator<?>[] newPrimitiveDefinitionGenerators() {
        return new PrimitiveDefinitionGenerator<?>[]{
                new MultiPathDefinitionGenerator(),
                new RectDefinitionGenerator(),
                new CircleDefinitionGenerator(),
                new ImageDefinitionGenerator(),
                new GroupDefinitionGenerator()
        };
    }
}
