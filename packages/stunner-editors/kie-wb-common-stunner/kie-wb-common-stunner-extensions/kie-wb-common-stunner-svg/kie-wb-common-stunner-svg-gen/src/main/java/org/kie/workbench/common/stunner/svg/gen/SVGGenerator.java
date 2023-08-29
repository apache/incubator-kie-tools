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


package org.kie.workbench.common.stunner.svg.gen;

import org.kie.workbench.common.stunner.svg.annotation.SVGSource;
import org.kie.workbench.common.stunner.svg.annotation.SVGViewFactory;
import org.kie.workbench.common.stunner.svg.gen.exception.GeneratorException;

/**
 * Main entry point for generating the sources for Stunner's SVG view stuff
 * from a given set of source files.
 * <p/>
 * The generation process relies on the following types and components:
 * <p/>
 * - SVG view model
 * There exist a model for the SVG view entities that have to be generated. For each supported
 * SVG element is should exist a model bean type for it.
 * @See {@link org/kie/workbench/common/stunner/svg/gen/model}
 * <p/>
 * - SVG view translators
 * Once parsed the SVG definition and given an XML DOM model,
 * the translators produce the SVG view model instances by parsing each element's attributes.
 * @See {@link org/kie/workbench/common/stunner/svg/gen/translator}
 * <p/>
 * - SVG view code-generators
 * Once SVG has been parsed, and there exist an SVG view model domain, which contains the translations
 * for the supported SVG elements and attributes, the code generators produce the different pieces of cdoe
 * to finally achieve building an instance of an SVG view factory.
 * @See {@link org/kie/workbench/common/stunner/svg/gen/codegen}
 * <p/>
 * Main use case for generating SVG views at compile time is by using the annotations
 * provided on the SVG client module and let the annotation processors initialize and run the
 * generation process, but it can be used from any other components or integrated
 * into a build environment in a different way rather than using annotations.
 * @See {@link SVGViewFactory}
 * @See {@link SVGSource}
 */
public interface SVGGenerator {

    /**
     * Generates an SVG view factory managed bean source code. The main bean's goal is to produce
     * the different shape views for the SVG translated entities and files.
     */
    StringBuffer generate(final SVGGeneratorRequest request) throws GeneratorException;
}
