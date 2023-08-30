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


package org.jboss.errai.enterprise.rebind;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.ConstructorBlockBuilder;
import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.builder.impl.ClassBuilder;
import org.jboss.errai.codegen.builder.impl.ObjectBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.util.CDIAnnotationUtils;
import org.jboss.errai.codegen.util.ClassChangeUtil;
import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.common.client.util.AnnotationPropertyAccessorBuilder;
import org.jboss.errai.common.client.util.SharedAnnotationSerializer;
import org.jboss.errai.enterprise.client.cdi.EventQualifierSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.errai.codegen.builder.impl.ObjectBuilder.newInstanceOf;
import static org.jboss.errai.codegen.util.Stmt.castTo;
import static org.jboss.errai.codegen.util.Stmt.invokeStatic;
import static org.jboss.errai.codegen.util.Stmt.loadVariable;
import static org.jboss.errai.enterprise.client.cdi.EventQualifierSerializer.SERIALIZER_CLASS_NAME;
import static org.jboss.errai.enterprise.client.cdi.EventQualifierSerializer.SERIALIZER_PACKAGE_NAME;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class NonGwtEventQualifierSerializerGenerator {

  private static final Logger logger = LoggerFactory.getLogger(NonGwtEventQualifierSerializerGenerator.class);

  private NonGwtEventQualifierSerializerGenerator() {}

  @SuppressWarnings("unchecked")
  public static Class<? extends EventQualifierSerializer> generateAndLoad() {
    logger.info("Generating source for {}.{}...", SERIALIZER_PACKAGE_NAME, SERIALIZER_CLASS_NAME);
    final String source = generateSource(CDIAnnotationUtils.getQualifiers());
    logger.info("Successfully generated source for {}.{}", SERIALIZER_PACKAGE_NAME, SERIALIZER_CLASS_NAME);

    logger.info("Attempting to compile and load {}.{}", SERIALIZER_PACKAGE_NAME, SERIALIZER_CLASS_NAME);
    return (Class<? extends EventQualifierSerializer>) ClassChangeUtil
            .compileAndLoadFromSource(SERIALIZER_PACKAGE_NAME, SERIALIZER_CLASS_NAME, source);
  }

  static String generateSource(final Iterable<MetaClass> qualifiers) {
    final ClassStructureBuilder<?> body = ClassBuilder
            .define(SERIALIZER_PACKAGE_NAME + "." + SERIALIZER_CLASS_NAME, EventQualifierSerializer.class)
            .publicScope().body();
    final ConstructorBlockBuilder<?> ctor = body.publicConstructor();

    for (final MetaClass qual : qualifiers) {
      final Collection<MetaMethod> bindingAttributes = CDIAnnotationUtils.getAnnotationAttributes(qual);
      if (!bindingAttributes.isEmpty()) {
        ctor.append(loadVariable("serializers").invoke("put", qual.getFullyQualifiedName(), generateEntryStatement(qual, bindingAttributes)));
      }
    }
    ctor.finish();

    return body.toJavaString();
  }

  private static ContextualStatementBuilder generateEntryStatement(final MetaClass qual,
          final Collection<MetaMethod> bindingAttributes) {
    ContextualStatementBuilder entryStmt = invokeStatic(AnnotationPropertyAccessorBuilder.class, "create");

    for (final MetaMethod attr : bindingAttributes) {
      entryStmt = entryStmt.invoke("with", attr.getName(), anonymousAttributeAccessorFor(attr));
    }

    entryStmt = entryStmt.invoke("build");
    return entryStmt;
  }

  private static ObjectBuilder anonymousAttributeAccessorFor(final MetaMethod attr) {
    return newInstanceOf(Function.class).extend()
            .publicOverridesMethod("apply", Parameter.finalOf(Object.class, "anno"))
            .append(invokeStatic(SharedAnnotationSerializer.class, "stringify",
                            castTo(attr.getDeclaringClass(), loadVariable("anno")).invoke(attr))
                    .returnValue())
            .finish().finish();
  }

  public static void loadAndSetEventQualifierSerializer() {
    logger.info("Attempting to load {}.{}", SERIALIZER_PACKAGE_NAME, SERIALIZER_CLASS_NAME);
    final Optional<Class<?>> loadedImpl = ClassChangeUtil.loadClassIfPresent(
            SERIALIZER_PACKAGE_NAME,
            SERIALIZER_CLASS_NAME);

    if (loadedImpl.isPresent()) {
      logger.info("Successfully loaded {}.{}", SERIALIZER_PACKAGE_NAME, SERIALIZER_CLASS_NAME);
      final Class<?> clazz = loadedImpl.get();
      instantiateAndSetEventQualifierSerializer(clazz);
    }
    else {
      logger.warn("No {}.{} found on the classpath. Attempting to generate and load.",
              SERIALIZER_PACKAGE_NAME, SERIALIZER_CLASS_NAME);
      final Class<? extends EventQualifierSerializer> clazz;
      try {
        clazz = Assert.notNull(generateAndLoad());
      } catch (final Throwable t) {
        throw new RuntimeException("Could not generate " + EventQualifierSerializer.SERIALIZER_CLASS_NAME, t);
      }

      logger.info("Successfully generated and loaded {}.{}", SERIALIZER_PACKAGE_NAME, SERIALIZER_CLASS_NAME);
      instantiateAndSetEventQualifierSerializer(clazz);
    }
  }

  public static void instantiateAndSetEventQualifierSerializer(final Class<?> clazz) {
    try {
      EventQualifierSerializer.set(clazz.asSubclass(EventQualifierSerializer.class).newInstance());
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException("Could not instantiate " + SERIALIZER_PACKAGE_NAME + "."
              + SERIALIZER_CLASS_NAME + " with default constructor.", e);
    } catch (final ClassCastException e) {
      throw new RuntimeException(SERIALIZER_PACKAGE_NAME + "." + SERIALIZER_CLASS_NAME
              + " must be a subclass of " + EventQualifierSerializer.class.getName(), e);
    }
  }
}
