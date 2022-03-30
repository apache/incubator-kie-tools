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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.kie.workbench.common.stunner.svg.gen.SVGGenerator;
import org.kie.workbench.common.stunner.svg.gen.SVGGeneratorRequest;
import org.kie.workbench.common.stunner.svg.gen.codegen.impl.SVGGeneratorFormatUtils;
import org.kie.workbench.common.stunner.svg.gen.codegen.impl.SVGViewFactoryGenerator;
import org.kie.workbench.common.stunner.svg.gen.exception.GeneratorException;
import org.kie.workbench.common.stunner.svg.gen.model.StyleSheetDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.ViewDefinitionImpl;
import org.kie.workbench.common.stunner.svg.gen.model.impl.ViewFactoryImpl;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGDocumentTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGTranslatorContext;
import org.kie.workbench.common.stunner.svg.gen.translator.css.SVGStyleTranslator;
import org.w3c.dom.Document;

public class SVGGeneratorImpl implements SVGGenerator {

    private final SVGDocumentTranslator translator;
    private final SVGViewFactoryGenerator viewFactoryGenerator;
    private final Filer filer;
    private final DocumentBuilder documentBuilder;

    public SVGGeneratorImpl(final SVGDocumentTranslator translator,
                            final SVGViewFactoryGenerator viewFactoryGenerator, Filer filer) throws ParserConfigurationException {
        this.translator = translator;
        this.viewFactoryGenerator = viewFactoryGenerator;
        this.filer = filer;
        this.documentBuilder = newBuilder();
    }

    @Override
    public StringBuffer generate(final SVGGeneratorRequest request) throws GeneratorException {

        final String name = request.getName();
        final String pkg = request.getPkg();
        final String typeOf = request.getImplementedType();
        final String cssPath = request.getCssPath();
        final String viewBuilderType = request.getViewBuilderType();
        final Map<String, String> viewSources = request.getViewSources();
        final ViewFactoryImpl viewFactory = new ViewFactoryImpl(name,
                pkg,
                typeOf,
                viewBuilderType);

        // Process the global CSS declaration specified in the factory, if any.
        final StyleSheetDefinition[] styleSheetDefinition = new StyleSheetDefinition[1];
        if (null != cssPath && cssPath.trim().length() > 0) {
            try (InputStream cssStream = loadResource(cssPath)) {
                try {
                    styleSheetDefinition[0] = SVGStyleTranslator.parseStyleSheetDefinition(cssPath, cssStream);
                    viewFactory.setStyleSheetDefinition(styleSheetDefinition[0]);
                } catch (Exception e) {
                    throw new RuntimeException("Error while processing the glocal CSS file [" + cssPath + "] ",
                            e);
                }
            } catch (IOException ioException) {
                throw new RuntimeException("Failed to read css", ioException);
            }
        }

        // Process all SVG files specified in the factory.
        final Set<String> processedSvgIds = new LinkedHashSet<>(); // TODO: Hmmm
        viewSources.forEach((fMethodName, svgPath) -> {
            parseSVGViewSource(fMethodName,
                    svgPath,
                    styleSheetDefinition[0],
                    result -> {
                        result.setId(fMethodName);
                        result.setFactoryMethodName(fMethodName);
                        viewFactory.getViewDefinitions().add(result);
                    });
            processedSvgIds.add(fMethodName);
        });

        // Parse referenced svg files as well, if any.
        final List<ViewDefinition<?>> referencedViewDefinitions = new LinkedList<>();
        viewFactory.getViewDefinitions().stream()
                .flatMap(v -> v.getSVGViewRefs().stream())
                .filter(vd -> !processedSvgIds.contains(vd.getViewRefId()))
                .forEach(vd -> parseSVGViewSource(vd.getViewRefId(),
                        vd.getFilePath(),
                        styleSheetDefinition[0],
                        result -> {
                            final String id = SVGGeneratorFormatUtils.getValidInstanceId(result);
                            result.setFactoryMethodName(id);
                            referencedViewDefinitions.add(result);
                            processedSvgIds.add(id);
                        }));
        viewFactory.getViewDefinitions().addAll(referencedViewDefinitions);

        return viewFactoryGenerator.generate(viewFactory);
    }

    private void parseSVGViewSource(final String viewId,
                                    final String svgPath,
                                    final StyleSheetDefinition styleSheetDefinition,
                                    final Consumer<ViewDefinitionImpl> viewDefinitionConsumer) {
        try (InputStream svgStream = loadResource(svgPath)) {
            try {
                final ViewDefinitionImpl viewDefinition = parseSVGView(viewId,
                        svgPath,
                        svgStream,
                        styleSheetDefinition);
                viewDefinitionConsumer.accept(viewDefinition);
            } catch (Exception e) {
                throw new RuntimeException("Error while processing the SVG file [" + svgPath + "]",
                        e);
            }
        } catch (IOException ioException) {
            throw new RuntimeException("No SVG file found at [" + svgPath + "]");
        }
    }

    private InputStream loadResource(final String path) throws IOException {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(path);
        if (resourceAsStream != null) {
            return resourceAsStream;
        }
        for (JavaFileManager.Location o : Arrays.asList(
                StandardLocation.SOURCE_PATH,
                StandardLocation.SOURCE_OUTPUT,
                StandardLocation.CLASS_PATH,
                StandardLocation.CLASS_OUTPUT,
                StandardLocation.ANNOTATION_PROCESSOR_PATH
        )) {
            String pkg = path.substring(0, path.lastIndexOf("/") + 1);
            String filename = path.substring(path.lastIndexOf("/") + 1);
            FileObject resource = filer.getResource(o, "", pkg + filename);
            if (new File(resource.getName()).exists()) {
                return new FileInputStream(resource.getName());
            }
        }
        throw new FileNotFoundException(path);
    }

    private ViewDefinitionImpl parseSVGView(final String viewId,
                                            final String svgPath,
                                            final InputStream svgStream,
                                            final StyleSheetDefinition styleSheetDefinition) throws Exception {
        ViewDefinitionImpl svgShapeViewSource = null;
        try {
            Document root = parse(svgStream);
            svgShapeViewSource = translate(viewId,
                    svgPath,
                    root,
                    styleSheetDefinition);
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

    private ViewDefinitionImpl translate(final String viewId,
                                         final String svgPath,
                                         final Document document,
                                         final StyleSheetDefinition styleSheetDefinition) throws Exception {
        final Path path = Paths.get(svgPath);
        final String relativePath = path.getNameCount() > 1 ?
                path.subpath(0, path.getNameCount() - 1).toString() :
                "";
        final SVGTranslatorContext context = new SVGTranslatorContext(document,
                relativePath,
                styleSheetDefinition);
        if (null != viewId) {
            context.setViewId(viewId);
        }
        final ViewDefinitionImpl viewDefinition = (ViewDefinitionImpl) translator.translate(context);
        viewDefinition.setPath(svgPath);
        viewDefinition.getStaticFields().putAll(context.getStaticStringMembers());
        return viewDefinition;
    }

    private DocumentBuilder newBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory
                .newInstance();
        documentFactory.setNamespaceAware(true);
        return documentFactory.newDocumentBuilder();
    }
}
