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

package org.kie.workbench.common.stunner.svg.gen.apt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.svg.annotation.SVGViewFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SVGShapeProcessorTest {

    @Mock
    private ProcessingEnvironment processingEnvironment;

    @Mock
    private Messager messager;

    @Mock
    private Filer filer;

    @Mock
    private RoundEnvironment roundEnvironment;

    @Mock
    private Elements elements;

    @Mock
    private TypeElement SVGViewFactoryAnnotation;

    @Mock
    private SVGViewFactory svgViewFactory;

    @Mock
    private DeclaredType svgViewFactoryBuilder;

    @Captor
    private ArgumentCaptor<String> generatedSourceFileNames;

    private Set<? extends TypeElement> elementTypes = new HashSet<>();
    private Set types = new HashSet<>();

    private SVGShapeProcessor processor;

    @Before
    public void setup() throws IOException {
        this.processor = new SVGShapeProcessor();
        this.processor.init(processingEnvironment);

        when(processingEnvironment.getElementUtils()).thenReturn(elements);
        when(processingEnvironment.getMessager()).thenReturn(messager);
        when(processingEnvironment.getFiler()).thenReturn(filer);

        final JavaFileObject jfo = mock(JavaFileObject.class);
        when(filer.createSourceFile(any(CharSequence.class))).thenReturn(jfo);
        when(jfo.openWriter()).thenReturn(mock(Writer.class));

        doThrow(new MirroredTypeException(svgViewFactoryBuilder)).when(svgViewFactory).builder();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultipleSVGViewFactories() throws Exception {
        types.add(makeTypeElement("Factory1"));
        types.add(makeTypeElement("Factory2"));

        when(roundEnvironment.errorRaised()).thenReturn(false);
        when(roundEnvironment.getElementsAnnotatedWith(SVGViewFactoryAnnotation)).thenReturn(types);
        when(elements.getTypeElement(SVGShapeProcessor.ANNOTATION_SVGSHAPE_VIEW_FACTORY)).thenReturn(SVGViewFactoryAnnotation);

        //First round processing gathering requirements
        when(roundEnvironment.processingOver()).thenReturn(false);
        processor.processWithExceptions(elementTypes, roundEnvironment);

        //Last round processing writing generated files
        when(roundEnvironment.processingOver()).thenReturn(true);
        processor.processWithExceptions(elementTypes, roundEnvironment);

        verify(filer, times(2)).createSourceFile(generatedSourceFileNames.capture());

        assertContains(this.getClass().getPackage().getName() + ".Factory1Impl");
        assertContains(this.getClass().getPackage().getName() + ".Factory2Impl");
    }

    private void assertContains(final String fileName) throws FileNotFoundException {
        generatedSourceFileNames.getAllValues().stream()
                .filter(fn -> Objects.equals(fn, fileName))
                .findFirst()
                .orElseThrow(() -> new FileNotFoundException("Expected generated file '" + fileName + "' not found."));
    }

    private TypeElement makeTypeElement(final String name) {
        final Name typeElementName = mock(Name.class);
        final Name packageElementName = mock(Name.class);
        final TypeElement typeElement = mock(TypeElement.class);
        final PackageElement packageElement = mock(PackageElement.class);
        when(typeElementName.toString()).thenReturn(name);
        when(packageElementName.toString()).thenReturn(this.getClass().getPackage().getName());
        when(typeElement.getKind()).thenReturn(ElementKind.INTERFACE);
        when(typeElement.getEnclosingElement()).thenReturn(packageElement);
        when(typeElement.getSimpleName()).thenReturn(typeElementName);
        when(typeElement.getAnnotation(SVGViewFactory.class)).thenReturn(svgViewFactory);
        when(packageElement.getQualifiedName()).thenReturn(packageElementName);

        return typeElement;
    }
}
