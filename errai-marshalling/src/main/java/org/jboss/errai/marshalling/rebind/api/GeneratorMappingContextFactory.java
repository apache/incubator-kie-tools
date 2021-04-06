/*
 * Copyright (C) 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.marshalling.rebind.api;

import java.util.Map;
import java.util.WeakHashMap;

import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.exception.GenerationException;
import org.jboss.errai.marshalling.rebind.MarshallerGeneratorFactory;
import org.jboss.errai.marshalling.rebind.MarshallerOutputTarget;

import com.google.gwt.core.ext.GeneratorContext;

/**
 * Creates and holds references to {@link GeneratorMappingContext}s for different
 * {@link MarshallerOutputTarget}s.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class GeneratorMappingContextFactory {
  private static final Map<GeneratorContext, GeneratorMappingContext> gwtContexts =
      new WeakHashMap<GeneratorContext, GeneratorMappingContext>();

  private static GeneratorMappingContext javaContext = null;

  public static GeneratorMappingContext getFor(GeneratorContext context, MarshallerOutputTarget target) {

    GeneratorMappingContext mappingContext = null;
    if (target == MarshallerOutputTarget.GWT) {
      mappingContext = gwtContexts.get(context);
    }
    else if (target == MarshallerOutputTarget.Java) {
      mappingContext = javaContext;
    }

    if (mappingContext == null) {
      throw new GenerationException("Generation context for output target " + target + " was not created!");
    }

    return mappingContext;
  }

  public static GeneratorMappingContext create(
      final GeneratorContext context,
      final MarshallerOutputTarget target,
      final MarshallerGeneratorFactory marshallerGeneratorFactory,
      final ClassStructureBuilder<?> classStructureBuilder,
      final ArrayMarshallerCallback arrayMarshallerCallback) {

    GeneratorMappingContext mappingContext =
        new GeneratorMappingContext(marshallerGeneratorFactory, classStructureBuilder, arrayMarshallerCallback);

    if (target == MarshallerOutputTarget.GWT) {
      gwtContexts.put(context, mappingContext);
    }
    else if (target == MarshallerOutputTarget.Java) {
      javaContext = mappingContext;
    }

    return mappingContext;
  }
}
