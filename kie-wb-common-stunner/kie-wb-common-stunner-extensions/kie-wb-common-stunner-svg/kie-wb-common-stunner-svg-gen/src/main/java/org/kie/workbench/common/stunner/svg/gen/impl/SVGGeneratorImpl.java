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

package org.kie.workbench.common.stunner.svg.gen.impl;

import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.gen.SVGGenerator;
import org.kie.workbench.common.stunner.svg.gen.SVGGeneratorRequest;
import org.kie.workbench.common.stunner.svg.gen.codegen.impl.SVGViewFactoryGenerator;
import org.kie.workbench.common.stunner.svg.gen.exception.GeneratorException;
import org.kie.workbench.common.stunner.svg.gen.model.ViewDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewFactory;
import org.kie.workbench.common.stunner.svg.gen.model.impl.ViewDefinitionImpl;
import org.kie.workbench.common.stunner.svg.gen.model.impl.ViewFactoryImpl;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGDocumentTranslator;
import org.w3c.dom.Document;

public class SVGGeneratorImpl implements SVGGenerator {

    private final SVGDocumentTranslator translator;
    private final SVGViewFactoryGenerator viewFactoryGenerator;
    private final DocumentBuilder documentBuilder;

    public SVGGeneratorImpl(final SVGDocumentTranslator translator,
                            final SVGViewFactoryGenerator viewFactoryGenerator) throws ParserConfigurationException {
        this.translator = translator;
        this.viewFactoryGenerator = viewFactoryGenerator;
        this.documentBuilder = newBuilder();
    }

    @Override
    public StringBuffer generate(final SVGGeneratorRequest request) throws GeneratorException {
        final String name = request.getName();
        final String pkg = request.getPkg();
        final String typeOf = request.getImplementedType();
        final Map<String, String> viewSources = request.getViewSources();
        final ViewFactory viewFactory = new ViewFactoryImpl(name,
                                                            pkg,
                                                            typeOf);
        viewSources.entrySet().forEach(svgEntry -> {
            final String fMethodName = svgEntry.getKey();
            final String svgPath = svgEntry.getValue();
            final InputStream svgStream = getClass().getClassLoader().getResourceAsStream(svgPath);
            if (null != svgStream) {
                try {
                    final ViewDefinition<SVGShapeView> viewDefinition = parseSVGView(fMethodName,
                                                                                     svgPath,
                                                                                     svgStream);
                    viewFactory.getViewDefinitions().add(viewDefinition);
                } catch (Exception e) {
                    throw new RuntimeException("Error while processing the SVG [" + svgPath + "]",
                                               e);
                }
            } else {
                throw new RuntimeException("No SVG file found at [" + svgPath + "]");
            }
        });
        return viewFactoryGenerator.generate(viewFactory);
    }

    private ViewDefinition<SVGShapeView> parseSVGView(final String fMethodName,
                                                      final String svgPath,
                                                      final InputStream svgStream) throws Exception {
        ViewDefinition<SVGShapeView> svgShapeViewSource = null;
        try {
            Document root = parse(svgStream);
            svgShapeViewSource = translate(fMethodName,
                                           svgPath,
                                           root);
        } catch (final Exception e) {
            throw new GeneratorException(e);
        }
        return svgShapeViewSource;
    }

    private Document parse(final InputStream inputStream) throws Exception {
        Document root = documentBuilder.parse(inputStream);
        root.getDocumentElement().normalize();
        return root;
    }

    private ViewDefinition<SVGShapeView> translate(final String fMethodName,
                                                   final String svgPath,
                                                   final Document document) throws Exception {
        final ViewDefinitionImpl viewDefinition = (ViewDefinitionImpl) translator.translate(document);
        viewDefinition.setFactoryMethodName(fMethodName);
        viewDefinition.setPath(svgPath);
        return viewDefinition;
    }

    private DocumentBuilder newBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory
                .newInstance();
        documentFactory.setNamespaceAware(true);
        return documentFactory.newDocumentBuilder();
    }
}
