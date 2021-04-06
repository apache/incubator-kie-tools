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

package org.jboss.errai.marshalling.rebind.api.impl.defaultjava;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassMember;
import org.jboss.errai.codegen.meta.MetaConstructor;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.marshalling.client.api.annotations.Key;
import org.jboss.errai.marshalling.client.api.exceptions.InvalidMappingException;
import org.jboss.errai.marshalling.rebind.DefinitionsFactory;
import org.jboss.errai.marshalling.rebind.api.model.ConstructorMapping;
import org.jboss.errai.marshalling.rebind.api.model.Mapping;
import org.jboss.errai.marshalling.rebind.api.model.MappingDefinition;
import org.jboss.errai.marshalling.rebind.api.model.MemberMapping;
import org.jboss.errai.marshalling.rebind.api.model.impl.ReadMapping;
import org.jboss.errai.marshalling.rebind.api.model.impl.SimpleConstructorMapping;
import org.jboss.errai.marshalling.rebind.api.model.impl.SimpleFactoryMapping;
import org.jboss.errai.marshalling.rebind.api.model.impl.WriteMapping;
import org.jboss.errai.marshalling.rebind.util.MarshallingGenUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Mike Brock
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class DefaultJavaDefinitionMapper {
  public static MappingDefinition map(final MetaClass toMap, final DefinitionsFactory definitionsFactory) {
    if ((toMap.isAbstract() && !toMap.isEnum()) || toMap.isInterface()) {
      throw new RuntimeException("cannot marshal an abstract class or interface: " + toMap.getFullyQualifiedName());
    }

    if (definitionsFactory.hasDefinition(toMap.asBoxed())) {
      return definitionsFactory.getDefinition(toMap.asBoxed());
    }

    final Set<MetaConstructor> constructors = new HashSet<>();
    final SimpleConstructorMapping simpleConstructorMapping = new SimpleConstructorMapping();
    final MappingDefinition definition = new MappingDefinition(toMap, false);

    for (final MetaConstructor c : toMap.getDeclaredConstructors()) {
      final List<Boolean> hasMapsTos = new ArrayList<>();
      if (c.getParameters().length != 0) {
        for (int i = 0; i < c.getParameters().length; i++) {
          final Annotation[] annotations = c.getParameters()[i].getAnnotations();
          if (annotations.length == 0) {
            hasMapsTos.add(false);
          }
          else {
            boolean hasMapsTo = false;
            for (final Annotation a : annotations) {
              if (MapsTo.class.isAssignableFrom(a.annotationType())) {
                hasMapsTo = true;
                final MapsTo mapsTo = (MapsTo) a;
                final String key = mapsTo.value();
                simpleConstructorMapping.mapParmToIndex(key, i, c.getParameters()[i].getType());
              }
            }
            hasMapsTos.add(hasMapsTo);
          }
        }
        if (hasMapsTos.contains(true) && hasMapsTos.contains(false)) {
          throw new InvalidMappingException("Not all parameters of constructor " + c.asConstructor()
              + " have a @" + MapsTo.class.getSimpleName() + " annotation");
        }

        if (hasMapsTos.contains(true)) {
          constructors.add(c);
        }
      }
    }

    final MetaConstructor constructor;
    if (constructors.isEmpty()) {
      constructor = toMap.getConstructor(new MetaClass[0]);
    }
    else if (constructors.size() > 1) {
      throw new InvalidMappingException("found more than one matching constructor for mapping: "
              + toMap.getFullyQualifiedName());
    }
    else {
      constructor = constructors.iterator().next();
    }

    simpleConstructorMapping.setConstructor(constructor);
    definition.setInstantiationMapping(simpleConstructorMapping);

    if (toMap.isEnum()) {
      return definition;
    }

    if (simpleConstructorMapping.getMappings().length == 0) {
      final Set<MetaMethod> factoryMethods = new HashSet<>();
      final SimpleFactoryMapping simpleFactoryMapping = new SimpleFactoryMapping();

      for (final MetaMethod method : toMap.getDeclaredMethods()) {
        if (method.isStatic()) {
          final List<Boolean> hasMapsTos = new ArrayList<>();
          for (int i = 0; i < method.getParameters().length; i++) {
            final Annotation[] annotations = method.getParameters()[i].getAnnotations();
            if (annotations.length == 0) {
              hasMapsTos.add(false);
            }
            else {
              boolean hasMapsTo = false;
              for (final Annotation a : annotations) {
                if (MapsTo.class.isAssignableFrom(a.annotationType())) {
                  hasMapsTo = true;
                  final MapsTo mapsTo = (MapsTo) a;
                  final String key = mapsTo.value();
                  simpleFactoryMapping.mapParmToIndex(key, i, method.getParameters()[i].getType());
                }
              }
              hasMapsTos.add(hasMapsTo);
            }
          }
          if (hasMapsTos.contains(true) && hasMapsTos.contains(false)) {
            throw new InvalidMappingException("Not all parameters of method " + method.asMethod()
                + " have a @" + MapsTo.class.getSimpleName() + " annotation");
          }

          if (hasMapsTos.contains(true)) {
            factoryMethods.add(method);
          }
        }
      }

      if (factoryMethods.size() > 1) {
        throw new InvalidMappingException("found more than one matching factory method for mapping: "
                + toMap.getFullyQualifiedName());
      }
      else if (factoryMethods.size() == 1) {
        final MetaMethod method = factoryMethods.iterator().next();
        simpleFactoryMapping.setMethod(method);
        definition.setInheritedInstantiationMapping(simpleFactoryMapping);
      }
    }

    if (definition.getInstantiationMapping() instanceof ConstructorMapping
            && definition.getInstantiationMapping().getMappings().length == 0) {

      final MetaConstructor defaultConstructor = toMap.getDeclaredConstructor(new Class[0]);
      if (defaultConstructor == null || !defaultConstructor.isPublic()) {
        throw new InvalidMappingException("there is no custom mapping or default no-arg constructor to map: "
                + toMap.getFullyQualifiedName());
      }
    }

    final Set<String> writeKeys = new HashSet<>();
    final Set<String> readKeys = new HashSet<>();

    for (final Mapping m : simpleConstructorMapping.getMappings()) {
      writeKeys.add(m.getKey());
    }

    for (final MetaMethod method : toMap.getDeclaredMethods()) {
      if (method.isAnnotationPresent(Key.class)) {
        final String key = method.getAnnotation(Key.class).value();

        if (method.getParameters().length == 0) {
          // assume this is a getter

          definition.addMemberMapping(new ReadMapping(key, method.getReturnType(), method.getName()));
          readKeys.add(key);
        }
        else if (method.getParameters().length == 1) {
          // assume this is a setter

          definition.addMemberMapping(new WriteMapping(key, method.getParameters()[0].getType(), method.getName()));
          writeKeys.add(key);
        }
        else {
          throw new InvalidMappingException("annotated @Key method is unrecognizable as a setter or getter: "
                  + toMap.getFullyQualifiedName() + "#" + method.getName());
        }
      }
    }

    MetaClass c = toMap;

    do {
      for (final MetaField field : c.getDeclaredFields()) {
        if (field.isTransient() || field.isStatic()) {
          continue;
        }

        try {
          final Field fld = field.asField();
          fld.setAccessible(true);
        }
        catch (final IllegalStateException e) {
          // field is not known to the current classloader. continue anyway.
        }

        if (writeKeys.contains(field.getName()) && readKeys.contains(field.getName())) {
          continue;
        }

        final MetaClass type = field.getType().getErased();
        final MetaClass compType = type.isArray() ? type.getOuterComponentType().asBoxed() : type.asBoxed();

        if (!(compType.isAbstract() || compType.isInterface() || compType.isEnum()) && !definitionsFactory.isExposedClass(compType)) {
          throw new InvalidMappingException("portable entity " + toMap.getFullyQualifiedName()
                  + " contains a field (" + field.getName() + ") that is not known to the marshaller framework: "
                  + compType.getFullyQualifiedName());
        }

        /**
         * This case handles the case where a constructor mapping has mapped the value, and there is no manually mapped
         * reader on the key.
         */
        if (writeKeys.contains(field.getName()) && !readKeys.contains(field.getName())) {
          final MetaMethod getterMethod = MarshallingGenUtil.findGetterMethod(toMap, field.getName());

          if (getterMethod != null) {
            definition.addMemberMapping(new ReadMapping(field.getName(), field.getType(), getterMethod.getName()));
            continue;
          }
        }

        definition.addMemberMapping(new MemberMapping() {
          private MetaClass type = (field.getType().isArray() ? field.getType() : field.getType());
          private final MetaClass targetType = type.getErased().asBoxed();

          @Override
          public MetaClassMember getBindingMember() {
            return field;
          }

          @Override
          public MetaClassMember getReadingMember() {
            return field;
          }

          @Override
          public String getKey() {
            return field.getName();
          }

          @Override
          public MetaClass getType() {
            return type.asBoxed();
          }

          @Override
          public void setType(final MetaClass type) {
            this.type = type;
          }

          @Override
          public MetaClass getTargetType() {
            return targetType.asBoxed();
          }

          @Override
          public boolean canRead() {
            return true;
          }

          @Override
          public boolean canWrite() {
            return true;
          }

          @Override
          public void setMappingClass(final MetaClass clazz) {
          }

          @Override
          public String toString() {
            return toMap.getFullyQualifiedName() + "#" + field.getName();
          }
        });
      }
    }
    while ((c = c.getSuperClass()) != null);

    definitionsFactory.mergeDefinition(definition);
    return definition;
  }
}
