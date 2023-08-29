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


package org.kie.workbench.common.stunner.client.yaml.processor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.auto.service.AutoService;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YAMLMapper;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTypeDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTypeSerializer;
import org.kie.workbench.common.stunner.client.yaml.processor.context.GenerationContext;
import org.kie.workbench.common.stunner.client.yaml.processor.logger.PrintWriterTreeLogger;
import org.kie.workbench.common.stunner.client.yaml.processor.logger.TreeLogger;
import org.kie.workbench.common.stunner.client.yaml.processor.processor.BeanProcessor;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ApplicationProcessor extends AbstractProcessor {

  private final PrintWriterTreeLogger logger = new PrintWriterTreeLogger();
  private final Set<TypeElement> beans = new HashSet<>();

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return supportedAnnotations().stream().map(Class::getCanonicalName).collect(Collectors.toSet());
  }

  @Override
  public boolean process(
          Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
    if (!annotations.isEmpty()) {
      GenerationContext context = new GenerationContext(roundEnvironment, processingEnv);
      roundEnvironment.getElementsAnnotatedWith(YAMLMapper.class).stream()
              .map(MoreElements::asType)
              .forEach(beans::add);

      logger.setMaxDetail(TreeLogger.Type.INFO);
      long started = System.currentTimeMillis();

      addCustomDeserializers(
              roundEnvironment.getElementsAnnotatedWith(YamlTypeSerializer.class).stream()
                      .filter(e -> e instanceof TypeElement)
                      .map(MoreElements::asType),
              context);

      addCustomSerializers(
              roundEnvironment.getElementsAnnotatedWith(YamlTypeDeserializer.class).stream()
                      .filter(e -> e instanceof TypeElement)
                      .map(MoreElements::asType),
              context);

      new BeanProcessor(context, logger, beans).process();
      logger.log(
              TreeLogger.Type.INFO,
              "YAML ser/deser generated in " + (System.currentTimeMillis() - started) + " ms");
    }
    return false;
  }

  private void addCustomDeserializers(Stream<TypeElement> elements, GenerationContext context) {
    elements.forEach(
            type ->
                    context
                            .getTypeUtils()
                            .getClassValueFromAnnotation(type, YamlTypeDeserializer.class, "value")
                            .ifPresent(
                                    serializer ->
                                            context
                                                    .getTypeRegistry()
                                                    .registerDeserializer(
                                                            type.getQualifiedName().toString(),
                                                            MoreTypes.asTypeElement(serializer))));
  }

  private void addCustomSerializers(Stream<TypeElement> elements, GenerationContext context) {
    elements.forEach(
            type ->
                    context
                            .getTypeUtils()
                            .getClassValueFromAnnotation(type, YamlTypeSerializer.class, "value")
                            .ifPresent(
                                    serializer ->
                                            context
                                                    .getTypeRegistry()
                                                    .registerSerializer(
                                                            type.getQualifiedName().toString(),
                                                            MoreTypes.asTypeElement(serializer))));
  }

  private List<Class<?>> supportedAnnotations() {
    return Arrays.asList(YAMLMapper.class);
  }
}
