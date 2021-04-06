/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassMember;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.util.PrivateAccessUtil;
import org.jboss.errai.marshalling.client.api.Marshaller;
import org.jboss.errai.marshalling.rebind.DefinitionsFactory;
import org.jboss.errai.marshalling.rebind.DefinitionsFactorySingleton;
import org.jboss.errai.marshalling.rebind.MarshallerGeneratorFactory;
import org.jboss.errai.marshalling.server.ServerMappingContext;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class GeneratorMappingContext implements ServerMappingContext {

  private final MarshallerGeneratorFactory marshallerGeneratorFactory;
  private final DefinitionsFactory definitionsFactory = DefinitionsFactorySingleton.get();

  private final Set<String> generatedMarshallers = new HashSet<String>();
  private final List<String> renderedMarshallers = new ArrayList<String>();

  private final ClassStructureBuilder<?> classStructureBuilder;
  private final ArrayMarshallerCallback arrayMarshallerCallback;

  private final Set<String> exposedMembers = new HashSet<String>();

  public GeneratorMappingContext(final MarshallerGeneratorFactory marshallerGeneratorFactory,
      final ClassStructureBuilder<?> classStructureBuilder,

      final ArrayMarshallerCallback callback) {

    this.marshallerGeneratorFactory = marshallerGeneratorFactory;
    this.arrayMarshallerCallback = callback;
    this.classStructureBuilder = classStructureBuilder;
  }

  public MarshallerGeneratorFactory getMarshallerGeneratorFactory() {
    return marshallerGeneratorFactory;
  }

  @Override
  public DefinitionsFactory getDefinitionsFactory() {
    return definitionsFactory;
  }

  public void registerGeneratedMarshaller(final String clazzName) {
    generatedMarshallers.add(clazzName);
  }

  @Override
  public boolean hasMarshaller(final String clazzName) {
    return definitionsFactory.hasDefinition(clazzName);
  }

  @Override
  public Marshaller<Object> getMarshaller(final String clazz) {
    return null;
  }

  private boolean hasGeneratedMarshaller(final String clazzName) {
    return generatedMarshallers.contains(clazzName);
  }

  @Override
  public boolean canMarshal(final String clazz) {
    return hasMarshaller(clazz) || hasGeneratedMarshaller(clazz);
  }

  public void markRendered(final MetaClass metaClass) {
    renderedMarshallers.add(metaClass.asBoxed().getFullyQualifiedName());
  }

  public boolean isRendered(final MetaClass metaClass) {
    return renderedMarshallers.contains(metaClass.asBoxed().getFullyQualifiedName());
  }

  public ArrayMarshallerCallback getArrayMarshallerCallback() {
    return arrayMarshallerCallback;
  }
  
  private static String getPrivateMemberName(final MetaClassMember member) {
    if (member instanceof MetaField) {
      return PrivateAccessUtil.getPrivateFieldAccessorName((MetaField) member);
    }
    else {
      return PrivateAccessUtil.getPrivateMethodName((MetaMethod) member);
    }
  }

  public void markExposed(final MetaClassMember member, final String marshallerClass) {
    exposedMembers.add(marshallerClass  + "." + getPrivateMemberName(member));
  }

  public boolean isExposed(final MetaClassMember member, final String marshallerClass) {
    return exposedMembers.contains(marshallerClass  + "." + getPrivateMemberName(member));
  }

  public ClassStructureBuilder<?> getClassStructureBuilder() {
    return classStructureBuilder;
  }
}
