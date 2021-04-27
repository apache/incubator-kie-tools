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

package org.jboss.errai.marshalling.rebind;

import static org.jboss.errai.config.rebind.EnvUtil.getEnvironmentConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.common.metadata.MetaDataScanner;
import org.jboss.errai.common.metadata.ScannerSingleton;
import org.jboss.errai.config.rebind.EnvUtil;
import org.jboss.errai.config.rebind.EnvironmentConfig;
import org.jboss.errai.config.util.ClassScanner;
import org.jboss.errai.marshalling.client.api.Marshaller;
import org.jboss.errai.marshalling.client.api.annotations.ClientMarshaller;
import org.jboss.errai.marshalling.client.api.annotations.ImplementationAliases;
import org.jboss.errai.marshalling.client.api.annotations.ServerMarshaller;
import org.jboss.errai.marshalling.client.api.exceptions.InvalidMappingException;
import org.jboss.errai.marshalling.client.marshallers.BigDecimalMarshaller;
import org.jboss.errai.marshalling.client.marshallers.BigIntegerMarshaller;
import org.jboss.errai.marshalling.client.marshallers.BooleanMarshaller;
import org.jboss.errai.marshalling.client.marshallers.ByteMarshaller;
import org.jboss.errai.marshalling.client.marshallers.CharacterMarshaller;
import org.jboss.errai.marshalling.client.marshallers.DateMarshaller;
import org.jboss.errai.marshalling.client.marshallers.DoubleMarshaller;
import org.jboss.errai.marshalling.client.marshallers.FloatMarshaller;
import org.jboss.errai.marshalling.client.marshallers.IntegerMarshaller;
import org.jboss.errai.marshalling.client.marshallers.LinkedHashSetMarshaller;
import org.jboss.errai.marshalling.client.marshallers.LinkedListMarshaller;
import org.jboss.errai.marshalling.client.marshallers.LinkedMapMarshaller;
import org.jboss.errai.marshalling.client.marshallers.ListMarshaller;
import org.jboss.errai.marshalling.client.marshallers.LongMarshaller;
import org.jboss.errai.marshalling.client.marshallers.MapMarshaller;
import org.jboss.errai.marshalling.client.marshallers.ObjectMarshaller;
import org.jboss.errai.marshalling.client.marshallers.OptionalMarshaller;
import org.jboss.errai.marshalling.client.marshallers.PriorityQueueMarshaller;
import org.jboss.errai.marshalling.client.marshallers.QueueMarshaller;
import org.jboss.errai.marshalling.client.marshallers.SQLDateMarshaller;
import org.jboss.errai.marshalling.client.marshallers.SetMarshaller;
import org.jboss.errai.marshalling.client.marshallers.ShortMarshaller;
import org.jboss.errai.marshalling.client.marshallers.SortedMapMarshaller;
import org.jboss.errai.marshalling.client.marshallers.SortedSetMarshaller;
import org.jboss.errai.marshalling.client.marshallers.StringBufferMarshaller;
import org.jboss.errai.marshalling.client.marshallers.StringBuilderMarshaller;
import org.jboss.errai.marshalling.client.marshallers.StringMarshaller;
import org.jboss.errai.marshalling.client.marshallers.TimeMarshaller;
import org.jboss.errai.marshalling.client.marshallers.TimestampMarshaller;
import org.jboss.errai.marshalling.rebind.api.CustomMapping;
import org.jboss.errai.marshalling.rebind.api.InheritedMappings;
import org.jboss.errai.marshalling.rebind.api.impl.defaultjava.DefaultJavaDefinitionMapper;
import org.jboss.errai.marshalling.rebind.api.model.ConstructorMapping;
import org.jboss.errai.marshalling.rebind.api.model.InstantiationMapping;
import org.jboss.errai.marshalling.rebind.api.model.Mapping;
import org.jboss.errai.marshalling.rebind.api.model.MappingDefinition;
import org.jboss.errai.marshalling.rebind.api.model.MemberMapping;
import org.jboss.errai.marshalling.rebind.api.model.impl.SimpleConstructorMapping;
import org.jboss.errai.marshalling.rebind.mappings.builtin.StackTraceElementDefinition;
import org.jboss.errai.marshalling.rebind.mappings.builtin.ThrowableDefinition;
import org.jboss.errai.marshalling.server.marshallers.DefaultDefinitionMarshaller;
import org.jboss.errai.marshalling.server.marshallers.ServerClassMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * The default implementation of {@link DefinitionsFactory}. This implementation covers the detection and mapping of
 * classes annotated with the {@link Portable} annotation, and custom mappings annotated with {@link CustomMapping}.
 *
 * @author Mike Brock
 */
public class DefinitionsFactoryImpl implements DefinitionsFactory {
  private final Set<MetaClass> exposedClasses = Collections.newSetFromMap(new LinkedHashMap<MetaClass, Boolean>());
  private final Set<MetaClass> typesWithBuiltInMarshallers = new HashSet<>();

  /**
   * Map of aliases to the mapped marshalling type.
   */
  private final Map<String, String> mappingAliases
      = new LinkedHashMap<>();

  private final Set<MetaClass> arraySignatures
      = new LinkedHashSet<>();

  private final Map<String, MappingDefinition> mappingDefinitions
      = new LinkedHashMap<>();

  private final Logger log = LoggerFactory.getLogger(MarshallerGeneratorFactory.class);

  // key = all types, value = list of all types which inherit from.
  private final Multimap<String, String> inheritanceMap
      = HashMultimap.create();

  DefinitionsFactoryImpl() {
    loadCustomMappings();
  }

  @Override
  public boolean hasDefinition(final String clazz) {
    return mappingDefinitions.containsKey(clazz);
  }

  @Override
  public MappingDefinition getDefinition(final String clazz) {
    return mappingDefinitions.get(clazz);
  }

  @Override
  public boolean hasDefinition(final MetaClass clazz) {
    return hasDefinition(clazz.getFullyQualifiedName());
  }

  @Override
  public boolean hasDefinition(final Class<?> clazz) {
    return hasDefinition(clazz.getName());
  }

  @Override
  public void addDefinition(final MappingDefinition definition) {
    final String fqcn = definition.getMappingClass().getFullyQualifiedName();
    final String internalName = definition.getMappingClass().getInternalName();

    putDefinitionIfAbsent(fqcn, definition);

    if (definition.getMappingClass().isPrimitiveWrapper()) {
      putDefinitionIfAbsent(definition.getMappingClass().asUnboxed().getInternalName(), definition);
      putDefinitionIfAbsent(definition.getMappingClass().asUnboxed().getFullyQualifiedName(), definition);
    }

    if (!fqcn.equals(internalName) && definition.getMappingClass().isArray()
        && definition.getMappingClass().getOuterComponentType().isPrimitive()) {
      putDefinitionIfAbsent(internalName, definition);
    }

    if (log.isDebugEnabled())
      log.debug("loaded definition: " + fqcn);
  }

  private void putDefinitionIfAbsent(final String key, final MappingDefinition value) {
    if (mappingDefinitions.containsKey(key)) {
      throw new IllegalStateException(
          "Mapping definition collision for " + key +
              "\nAlready have: " + mappingDefinitions.get(key) +
              "\nAttempted to add: " + value);
    }
    mappingDefinitions.put(key, value);
  }

  @Override
  public MappingDefinition getDefinition(final MetaClass clazz) {
    MappingDefinition def = getDefinition(clazz.getFullyQualifiedName());
    if (def == null) {
      def = getDefinition(clazz.getInternalName());
    }
    return def;
  }

  @Override
  public MappingDefinition getDefinition(final Class<?> clazz) {
    return getDefinition(clazz.getName());
  }

  private void loadCustomMappings() {
    exposedClasses.add(MetaClassFactory.get(Object.class));

    final MetaDataScanner scanner = ScannerSingleton.getOrCreateInstance();

    EnvUtil.clearCache();
    final EnvironmentConfig environmentConfig = getEnvironmentConfig();
    final Set<MetaClass> envExposedClasses = environmentConfig.getExposedClasses();

    for (final Class<?> cls : findCustomMappings(scanner)) {
      if (!MappingDefinition.class.isAssignableFrom(cls)) {
        throw new RuntimeException("@CustomMapping class: " + cls.getName() + " does not inherit "
            + MappingDefinition.class.getName());
      }

      try {
        final MappingDefinition definition = (MappingDefinition) cls.newInstance();
        definition.setMarshallerInstance(new DefaultDefinitionMarshaller(definition));
        addDefinition(definition);

        if (!envExposedClasses.contains(definition.getMappingClass())) {
          definition.setLazy(true);
        }
        exposedClasses.add(definition.getMappingClass());

        if (log.isDebugEnabled())
          log.debug("loaded custom mapping class: " + cls.getName() + " (for mapping: "
              + definition.getMappingClass().getFullyQualifiedName() + ")");

        if (cls.isAnnotationPresent(InheritedMappings.class)) {
          final InheritedMappings inheritedMappings = cls.getAnnotation(InheritedMappings.class);

          for (final Class<?> c : inheritedMappings.value()) {
            final MetaClass metaClass = MetaClassFactory.get(c);
            final MappingDefinition aliasMappingDef = new MappingDefinition(metaClass, definition.alreadyGenerated());
            aliasMappingDef.setMarshallerInstance(new DefaultDefinitionMarshaller(aliasMappingDef));
            addDefinition(aliasMappingDef);

            if (!envExposedClasses.contains(metaClass)) {
              aliasMappingDef.setLazy(true);
            }

            exposedClasses.add(metaClass);

            if (log.isDebugEnabled())
              log.debug("mapping inherited mapping " + c.getName() + " -> " + cls.getName());
          }
        }
      }
      catch (final Throwable t) {
        throw new RuntimeException("Failed to load definition", t);
      }
    }

    for (final MappingDefinition def : mappingDefinitions.values()) {
      mergeDefinition(def);
    }

    final Collection<MetaClass> cliMarshallers = ClassScanner.getTypesAnnotatedWith(ClientMarshaller.class, true);
    final MetaClass Marshaller_MC = MetaClassFactory.get(Marshaller.class);

    for (final MetaClass marshallerMetaClass : cliMarshallers) {
      if (Marshaller_MC.isAssignableFrom(marshallerMetaClass)) {
        final Class<? extends Marshaller> marshallerCls = marshallerMetaClass.asClass().asSubclass(Marshaller.class);
        try {
          final Class<?> type = marshallerMetaClass.getAnnotation(ClientMarshaller.class).value();

          final MappingDefinition marshallMappingDef = new MappingDefinition(type, true);
          marshallMappingDef.setClientMarshallerClass(marshallerCls);
          addDefinition(marshallMappingDef);

          exposedClasses.add(MetaClassFactory.get(type).asBoxed());
          typesWithBuiltInMarshallers.add(MetaClassFactory.get(type).asBoxed());

          if (marshallerCls.isAnnotationPresent(ImplementationAliases.class)) {
            for (final Class<?> aliasCls : marshallerCls.getAnnotation(ImplementationAliases.class).value()) {
              final MappingDefinition aliasMappingDef = new MappingDefinition(aliasCls, true);
              aliasMappingDef.setClientMarshallerClass(marshallerCls.asSubclass(Marshaller.class));
              addDefinition(aliasMappingDef);

              exposedClasses.add(MetaClassFactory.get(aliasCls).asBoxed());
              typesWithBuiltInMarshallers.add(MetaClassFactory.get(type).asBoxed());
              mappingAliases.put(aliasCls.getName(), type.getName());
            }
          }
        }
        catch (final Throwable t) {
          throw new RuntimeException("could not instantiate marshaller class: " + marshallerCls.getName(), t);
        }
      }
      else {
        throw new RuntimeException("class annotated with " + ClientMarshaller.class.getCanonicalName()
            + " does not implement " + Marshaller.class.getName());
      }
    }

    final Set<Class<?>> serverMarshallers = findServerMarshallers(scanner);

    for (final Class<?> marshallerCls : serverMarshallers) {
      if (Marshaller.class.isAssignableFrom(marshallerCls)) {
        try {
          final Class<?> type = marshallerCls.getAnnotation(ServerMarshaller.class).value();
          final MappingDefinition definition;

          if (hasDefinition(type)) {
            definition = getDefinition(type);
            definition.setServerMarshallerClass(marshallerCls.asSubclass(Marshaller.class));
          }
          else {
            definition = new MappingDefinition(type, true);
            definition.setServerMarshallerClass(marshallerCls.asSubclass(Marshaller.class));
            addDefinition(definition);

            exposedClasses.add(MetaClassFactory.get(type).asBoxed());
            typesWithBuiltInMarshallers.add(MetaClassFactory.get(type).asBoxed());
          }

          if (marshallerCls.isAnnotationPresent(ImplementationAliases.class)) {
            for (final Class<?> aliasCls : marshallerCls.getAnnotation(ImplementationAliases.class).value()) {
              if (hasDefinition(aliasCls)) {
                getDefinition(aliasCls).setServerMarshallerClass(marshallerCls.asSubclass(Marshaller.class));
              }
              else {
                final MappingDefinition aliasMappingDef = new MappingDefinition(aliasCls, true);
                aliasMappingDef.setServerMarshallerClass(marshallerCls.asSubclass(Marshaller.class));
                addDefinition(aliasMappingDef);

                exposedClasses.add(MetaClassFactory.get(aliasCls));
                typesWithBuiltInMarshallers.add(MetaClassFactory.get(type).asBoxed());
                mappingAliases.put(aliasCls.getName(), type.getName());
              }
            }
          }
        }
        catch (final Throwable t) {
          throw new RuntimeException("could not instantiate marshaller class: " + marshallerCls.getName(), t);
        }
      }
      else {
        throw new RuntimeException("class annotated with " + ServerMarshaller.class.getCanonicalName()
            + " does not implement " + Marshaller.class.getName());
      }
    }


    exposedClasses.addAll(envExposedClasses);

    final List<MetaClass> exposedSuperTypes = exposedClasses
      .stream()
      .filter(mc -> mc.isAnnotationPresent(Portable.class) && mc.getAnnotation(Portable.class).mapSuperTypes())
      .flatMap(mc -> {
        final Builder<MetaClass> builder = Stream.builder();
        MetaClass cur = mc;
        while (cur.getSuperClass() != null && !cur.getSuperClass().getFullyQualifiedName().equals(Object.class.getName())) {
          builder.accept((cur = cur.getSuperClass()));
        }
        return builder.build().filter(superType -> superType.isConcrete());
      })
      .collect(Collectors.toList());
    exposedClasses.addAll(exposedSuperTypes);

    final Map<String, String> configuredMappingAliases = new HashMap<>();
    configuredMappingAliases.putAll(environmentConfig.getMappingAliases());
    configuredMappingAliases.putAll(defaultMappingAliases());

    mappingAliases.putAll(configuredMappingAliases);

    final Map<MetaClass, MetaClass> aliasToMarshaller = new HashMap<>();

    final List<MetaClass> enums = new ArrayList<>();

    for (final MetaClass cls : exposedClasses) {
      MetaClass mappedClass;
      if (cls.isArray()) {
        arraySignatures.add(cls);
        mappedClass = cls.getOuterComponentType();
      }
      else {
        mappedClass = cls;
      }

      if (mappedClass.isSynthetic())
        continue;

      final Portable portable = mappedClass.getAnnotation(Portable.class);
      if (portable != null && !portable.aliasOf().equals(Object.class)) {
        aliasToMarshaller.put(mappedClass, MetaClassFactory.get(portable.aliasOf()));
      }
      else if (!hasDefinition(mappedClass)) {
        final MappingDefinition def = DefaultJavaDefinitionMapper.map(mappedClass, this);
        def.setMarshallerInstance(new DefaultDefinitionMarshaller(def));
        addDefinition(def);

        for (final Mapping mapping : def.getAllMappings()) {
          if (mapping.getType().isEnum()) {
            enums.add(mapping.getType());
          }
        }
      }
    }

    for (final MetaClass enumType : enums) {
      if (!hasDefinition(enumType)) {
        final MappingDefinition enumDef = DefaultJavaDefinitionMapper
            .map(MetaClassFactory.get(enumType.asClass()), this);
        enumDef.setMarshallerInstance(new DefaultDefinitionMarshaller(enumDef));
        addDefinition(enumDef);
        exposedClasses.add(MetaClassFactory.get(enumType.asClass()));
      }
    }

    for (final Map.Entry<String, String> entry : configuredMappingAliases.entrySet()) {
      try {
        aliasToMarshaller.put(MetaClassFactory.get(entry.getKey()), MetaClassFactory.get(entry.getValue()));
      }
      catch (final Throwable t) {
        throw new RuntimeException("error loading mapping alias", t);
      }
    }


    for (final Map.Entry<MetaClass, MetaClass> entry : aliasToMarshaller.entrySet()) {
      final MappingDefinition def = getDefinition(entry.getValue());
      if (def == null) {
        throw new InvalidMappingException("cannot alias type " + entry.getKey().getName()
            + " to " + entry.getValue().getName() + ": the specified alias type does not exist ");
      }

      final MappingDefinition aliasDef = new MappingDefinition(
          def.getMarshallerInstance(), entry.getKey(), false
      );
      if (def.getMarshallerInstance() instanceof DefaultDefinitionMarshaller) {
        aliasDef.setMarshallerInstance(new DefaultDefinitionMarshaller(aliasDef));
      }
      else {
        aliasDef.setClientMarshallerClass(def.getClientMarshallerClass());
        aliasDef.setServerMarshallerClass(def.getServerMarshallerClass());
      }
      mergeDefinition(aliasDef);
      addDefinition(aliasDef);
    }

    for (final Map.Entry<String, MappingDefinition> entry : mappingDefinitions.entrySet()) {
      fillInheritanceMap(entry.getValue().getMappingClass());
    }

    final MetaClass javaLangObjectRef = MetaClassFactory.get(Object.class);

    for (final Map.Entry<String, MappingDefinition> entry : mappingDefinitions.entrySet()) {
      final MappingDefinition def = entry.getValue();

      final InstantiationMapping instantiationMapping = def.getInstantiationMapping();
      for (final Mapping mapping : instantiationMapping.getMappings()) {
        if (shouldUseObjectMarshaller(mapping.getType().getErased())) {
          mapping.setType(javaLangObjectRef);
        }
      }

      for (final Mapping mapping : entry.getValue().getMemberMappings()) {
        if (shouldUseObjectMarshaller(mapping.getType().getErased())) {
          mapping.setType(javaLangObjectRef);
        }
      }
    }

    assert getDefinition("java.util.Arrays$ArrayList") != null;

    log.debug("comprehended " + exposedClasses.size() + " classes");
  }

  private Set<Class<?>> findCustomMappings(final MetaDataScanner scanner) {
    Set<Class<?>> scannedMappings = scanner.getTypesAnnotatedWith(CustomMapping.class, true);
    if (scannedMappings.isEmpty()) {
      // This should only happen in OSGI environments where we can't get classpath URLs
      log.warn("Unable to scan classpath for CustomMappings. Falling back to default.");
      scannedMappings = new HashSet<>();
      scannedMappings.add(ThrowableDefinition.class);
      scannedMappings.add(StackTraceElementDefinition.class);
    }

    return scannedMappings;
  }

  private Set<Class<?>> findServerMarshallers(final MetaDataScanner scanner) {
    Set<Class<?>> serverMarshallers = scanner.getTypesAnnotatedWith(ServerMarshaller.class, true);

    if (serverMarshallers.isEmpty()) {
      // This should only happen in OSGI environments where we can't get classpath URLs
      log.warn("Unable to scan classpath for ServerMarshallers. Falling back to default.");
      serverMarshallers = new HashSet<>();
      serverMarshallers.add(BigDecimalMarshaller.class);
      serverMarshallers.add(BigIntegerMarshaller.class);
      serverMarshallers.add(BooleanMarshaller.class);
      serverMarshallers.add(ByteMarshaller.class);
      serverMarshallers.add(CharacterMarshaller.class);
      serverMarshallers.add(DateMarshaller.class);
      serverMarshallers.add(DoubleMarshaller.class);
      serverMarshallers.add(FloatMarshaller.class);
      serverMarshallers.add(IntegerMarshaller.class);
      serverMarshallers.add(LinkedHashSetMarshaller.class);
      serverMarshallers.add(LinkedListMarshaller.class);
      serverMarshallers.add(LinkedMapMarshaller.class);
      serverMarshallers.add(ListMarshaller.class);
      serverMarshallers.add(LongMarshaller.class);
      serverMarshallers.add(MapMarshaller.class);
      serverMarshallers.add(ObjectMarshaller.class);
      serverMarshallers.add(PriorityQueueMarshaller.class);
      serverMarshallers.add(QueueMarshaller.class);
      serverMarshallers.add(SetMarshaller.class);
      serverMarshallers.add(ShortMarshaller.class);
      serverMarshallers.add(SortedMapMarshaller.class);
      serverMarshallers.add(SortedSetMarshaller.class);
      serverMarshallers.add(SQLDateMarshaller.class);
      serverMarshallers.add(StringBufferMarshaller.class);
      serverMarshallers.add(StringBuilderMarshaller.class);
      serverMarshallers.add(StringMarshaller.class);
      serverMarshallers.add(TimeMarshaller.class);
      serverMarshallers.add(TimestampMarshaller.class);
      serverMarshallers.add(ServerClassMarshaller.class);
      serverMarshallers.add(OptionalMarshaller.class);
    }
    return serverMarshallers;
  }

  @Override
  public boolean shouldUseObjectMarshaller(final MetaClass type) {
    final boolean hasPortableSubtypes = inheritanceMap.containsKey(type.getFullyQualifiedName());
    final MappingDefinition definition = getDefinition(type);
    final boolean hasMarshaller = definition != null;

    if (hasMarshaller) {
      if (definition.getClass().isAnnotationPresent(CustomMapping.class)
          || definition.getClientMarshallerClass() != null) {
        return false;
      }
    }

    final boolean isConcrete = !(type.isAbstract() || type.isInterface());
    if (!type.isArray() && !type.isEnum() && !isConcrete && !hasPortableSubtypes) {
      throw new IllegalStateException("A field of type " + type
          + " appears in a portable class, but " + type + " has no portable implementations.");
    }
    return (hasPortableSubtypes && !hasMarshaller) || (hasPortableSubtypes && hasMarshaller && isConcrete);
  }

  /**
   * Populates the inheritance map with all supertypes (except java.lang.Object) and all directly- and
   * indirectly-implemented interfaces of the given class.
   *
   * @param mappingClass
   */
  private void fillInheritanceMap(final MetaClass mappingClass) {
    fillInheritanceMap(inheritanceMap, mappingClass, mappingClass);
  }

  /**
   * Recursive subroutine of {@link #fillInheritanceMap(org.jboss.errai.codegen.meta.MetaClass)}.
   */
  private static void fillInheritanceMap(final Multimap<String, String> inheritanceMap,
                                         final MetaClass visiting,
                                         final MetaClass mappingClass) {
    if (visiting == null || visiting.equals(MetaClassFactory.get(Object.class)))
      return;

    if (!visiting.equals(mappingClass)) {
      inheritanceMap.put(visiting.getFullyQualifiedName(), mappingClass.getFullyQualifiedName());
    }

    fillInheritanceMap(inheritanceMap, visiting.getSuperClass(), mappingClass);

    for (final MetaClass iface : visiting.getInterfaces()) {
      fillInheritanceMap(inheritanceMap, iface, mappingClass);
    }
  }

  @Override
  public void mergeDefinition(final MappingDefinition def) {
    MetaClass cls = def.getMappingClass();

    while ((cls = cls.getSuperClass()) != null) {
      if (hasDefinition(cls) && cls.getParameterizedType() == null) {
        final MappingDefinition toMerge = getDefinition(cls);
        final Set<String> parentKeys = new HashSet<>();

        for (final Mapping m : toMerge.getInstantiationMapping().getMappings())
          parentKeys.add(m.getKey());

        for (final MemberMapping m : toMerge.getMemberMappings())
          parentKeys.add(m.getKey());

        final Iterator<MemberMapping> defMappings = def.getMemberMappings().iterator();
        while (defMappings.hasNext()) {
          if (parentKeys.contains(defMappings.next().getKey()))
            defMappings.remove();
        }

        for (final MemberMapping memberMapping : toMerge.getMemberMappings()) {
          def.addInheritedMapping(memberMapping);
        }

        final InstantiationMapping instantiationMapping = def.getInstantiationMapping();

        if (instantiationMapping instanceof ConstructorMapping &&
            def.getInstantiationMapping().getMappings().length == 0 &&
            def.getMappingClass().getDeclaredConstructor(toMerge.getInstantiationMapping().getSignature()) != null) {

          final ConstructorMapping parentConstructorMapping = (ConstructorMapping) toMerge.getInstantiationMapping();
          final MetaClass mergingClass = def.getMappingClass();

          if (parentConstructorMapping instanceof SimpleConstructorMapping) {
            final ConstructorMapping newMapping = ((SimpleConstructorMapping) parentConstructorMapping)
                .getCopyForInheritance();
            newMapping.setMappingClass(mergingClass);
            def.setInheritedInstantiationMapping(newMapping);
          }
        }

        if (log.isDebugEnabled())
          log.debug("merged definition " + def.getMappingClass() + " with " + cls.getFullyQualifiedName());
      }
    }
  }

  @Override
  public boolean isExposedClass(final MetaClass clazz) {
    return exposedClasses.contains(clazz);
  }

  @Override
  public Set<MetaClass> getExposedClasses() {
    return Collections.unmodifiableSet(exposedClasses);
  }

  @Override
  public Set<MetaClass> getArraySignatures() {
    return arraySignatures;
  }

  @Override
  public Map<String, String> getMappingAliases() {
    return mappingAliases;
  }

  @Override
  public Collection<MappingDefinition> getMappingDefinitions() {
    return Collections.unmodifiableCollection(new ArrayList<>(mappingDefinitions.values()));
  }

  private static Map<String, String> defaultMappingAliases() {
    final Map<String, String> mappingAliases = new HashMap<>();
    mappingAliases.put("java.util.Arrays$ArrayList", "java.util.List");
    mappingAliases.put("java.util.Collections$UnmodifiableList", "java.util.List");
    mappingAliases.put("java.util.Collections$UnmodifiableSet", "java.util.Set");
    mappingAliases.put("java.util.Collections$UnmodifiableMap", "java.util.Map");
    mappingAliases.put("java.util.Collections$UnmodifiableRandomAccessList", "java.util.List");
    mappingAliases.put("java.util.Collections$SynchronizedList", "java.util.List");
    mappingAliases.put("java.util.Collections$SynchronizedSet", "java.util.Set");
    mappingAliases.put("java.util.Collections$SynchronizedMap", "java.util.Map");
    mappingAliases.put("java.util.Collections$SynchronizedRandomAccessList", "java.util.List");
    mappingAliases.put("java.util.Collections$UnmodifiableSortedMap", "java.util.SortedMap");
    mappingAliases.put("java.util.Collections$SynchronizedSortedMap", "java.util.SortedMap");
    mappingAliases.put("java.util.Collections$UnmodifiableSortedSet", "java.util.SortedSet");
    mappingAliases.put("java.util.Collections$SynchronizedSortedSet", "java.util.SortedSet");
    mappingAliases.put("java.util.Collections$EmptySet", "java.util.Set");
    mappingAliases.put("java.util.Collections$EmptyList", "java.util.List");
    mappingAliases.put("java.util.Collections$EmptyMap", "java.util.Map");
    mappingAliases.put("java.util.Collections$SingletonSet", "java.util.Set");
    mappingAliases.put("java.util.Collections$SingletonList", "java.util.List");
    mappingAliases.put("java.util.Collections$SingletonMap", "java.util.Map");
    return mappingAliases;
  }

  @Override
  public void resetDefinitionsAndReload() {
    this.exposedClasses.clear();
    this.mappingAliases.clear();
    this.mappingDefinitions.clear();
    this.typesWithBuiltInMarshallers.clear();
    loadCustomMappings();
  }

  @Override
  public boolean hasBuiltInDefinition(final MetaClass type) {
    return typesWithBuiltInMarshallers.contains(type.asBoxed());
  }
}
