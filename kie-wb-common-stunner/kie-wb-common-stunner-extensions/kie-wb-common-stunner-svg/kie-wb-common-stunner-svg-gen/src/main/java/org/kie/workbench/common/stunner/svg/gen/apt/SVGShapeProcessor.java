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

package org.kie.workbench.common.stunner.svg.gen.apt;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import org.kie.workbench.common.stunner.svg.annotation.SVGSource;
import org.kie.workbench.common.stunner.svg.annotation.SVGViewFactory;
import org.kie.workbench.common.stunner.svg.gen.SVGGenerator;
import org.kie.workbench.common.stunner.svg.gen.SVGGeneratorRequest;
import org.kie.workbench.common.stunner.svg.gen.impl.SVGGeneratorFactory;
import org.uberfire.annotations.processors.AbstractErrorAbsorbingProcessor;

/**
 * Note that current implementation only considers a single SVGViewFactory for each module.
 */
@SupportedAnnotationTypes({SVGShapeProcessor.ANNOTATION_SVGSHAPE_VIEW_FACTORY})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SVGShapeProcessor extends AbstractErrorAbsorbingProcessor {

    public final static String ANNOTATION_SVGSHAPE_VIEW_FACTORY = "org.kie.workbench.common.stunner.svg.annotation.SVGViewFactory";
    private final static String GENERATED_TYPE_SUFFIX = "Impl";

    private final SVGShapeProcessorContext context = new SVGShapeProcessorContext();
    private SVGGenerator generator;

    @Override
    protected boolean processWithExceptions(Set<? extends TypeElement> set,
                                            RoundEnvironment roundEnv) throws Exception {
        if (roundEnv.processingOver()) {
            return processLastRound(set,
                                    roundEnv);
        }
        //If prior processing threw an error exit
        if (roundEnv.errorRaised()) {
            return false;
        }
        //Initialize the generator
        generator = SVGGeneratorFactory.newGenerator();
        // Process SVG Shape View Factories for the annotated types.
        final Elements elementUtils = processingEnv.getElementUtils();
        for (Element e : roundEnv.getElementsAnnotatedWith(elementUtils.getTypeElement(ANNOTATION_SVGSHAPE_VIEW_FACTORY))) {
            processSvgShapeViewFactory(set,
                                       e,
                                       roundEnv);
        }
        return true;
    }

    private boolean processSvgShapeViewFactory(final Set<? extends TypeElement> set,
                                               final Element e,
                                               final RoundEnvironment roundEnv) throws Exception {
        final boolean isIface = e.getKind() == ElementKind.INTERFACE;
        if (isIface) {
            final TypeElement classElement = (TypeElement) e;
            final PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
            final String name = classElement.getSimpleName().toString();
            // Obtain type element information and create a new generation request into the processor's context.
            String packageName = packageElement.getQualifiedName().toString();
            String fqcn = packageName + "." + name;
            String absPkgPath = packageName.replaceAll("\\.",
                                                       "/");
            note("Discovered @SVGViewFactory for type [" + fqcn + "]");
            final SVGViewFactory svgViewFactoryAnn = classElement.getAnnotation(SVGViewFactory.class);
            final String viewBuilderTypeName = parseAnnotationFieldTypeName(svgViewFactoryAnn::builder,
                                                                            "No builder class specified for the @SVGViewFactory.");
            final SVGGeneratorRequest request = new SVGGeneratorRequest(name + GENERATED_TYPE_SUFFIX,
                                                                        packageName,
                                                                        fqcn,
                                                                        absPkgPath + "/" + svgViewFactoryAnn.cssPath(),
                                                                        viewBuilderTypeName,
                                                                        processingEnv.getMessager());
            context.setGeneratorRequest(request);
            // Find and process method annotation as @SVGSource.
            List<ExecutableElement> methodElements = ElementFilter.methodsIn(classElement.getEnclosedElements());
            methodElements.forEach(methodElement -> {
                SVGSource svgSourceAnnotation = methodElement.getAnnotation(SVGSource.class);
                if (null != svgSourceAnnotation) {
                    String fileName = svgSourceAnnotation.value();
                    String absPath = absPkgPath + "/" + fileName;
                    final String fieldName = methodElement.getSimpleName().toString();
                    note("Discovered @SVGSource to be processed at path [" + absPath + "]");
                    context.getGeneratorRequest().getViewSources().put(fieldName,
                                                                       absPath);
                }
            });
        }
        return true;
    }

    private boolean processLastRound(final Set<? extends TypeElement> set,
                                     final RoundEnvironment roundEnv) throws Exception {
        final Messager messager = processingEnv.getMessager();
        try {
            final SVGGeneratorRequest request = context.getGeneratorRequest();
            final String classFQName = request.getPkg() + "." + request.getName();
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Starting generation for SVGShapeViewFactory named [" + classFQName + "]");
            final StringBuffer result = generator.generate(request);
            writeCode(request.getPkg(),
                      request.getName(),
                      result);
        } catch (org.kie.workbench.common.stunner.svg.gen.exception.GeneratorException ge) {
            final String msg = ge.getMessage();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                                     msg);
        }
        return true;
    }

    private void note(String message) {
        log(Diagnostic.Kind.NOTE,
            message);
    }

    private void warn(String message) {
        log(Diagnostic.Kind.WARNING,
            message);
    }

    private void error(String message) {
        log(Diagnostic.Kind.ERROR,
            message);
    }

    private void log(Diagnostic.Kind kind,
                     String message) {
        final Messager messager = processingEnv.getMessager();
        messager.printMessage(kind,
                              message);
    }

    private static String parseAnnotationFieldTypeName(final Supplier<Class<?>> theTypeSupplier,
                                                       final String errorMessage) {
        TypeMirror mirror = null;
        try {
            Class<?> theType = theTypeSupplier.get();
        } catch (MirroredTypeException mte) {
            mirror = mte.getTypeMirror();
        }

        if (null == mirror) {
            throw new RuntimeException(errorMessage);
        } else {
            return mirror.toString();
        }
    }
}
