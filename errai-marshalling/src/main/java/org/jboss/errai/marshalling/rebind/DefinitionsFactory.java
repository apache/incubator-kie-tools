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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.marshalling.rebind.api.model.MappingDefinition;

/**
 * The definitions factory is responsible for loading / registering mapping definitions for entities that will
 * be marshalled.
 *
 * @author Mike Brock
 */
public interface DefinitionsFactory {
  /**
   * Returns true if a definition is registered matching fully-qualified class name provided.
   * If this DefinitionsFactory has a definition for {@param clazz}, that means it can marshall and demarshall
   * instances of clazz.
   * @param clazz fully qualified class name.
   * @return boolean true if defined.
   */
  boolean hasDefinition(String clazz);

  /**
   * Return true if a definition is registered for the specified {@link MetaClass}
   * @param clazz class reference
   * @return boolean true if defined.
   */
  boolean hasDefinition(MetaClass clazz);

  /**
   * Return true if a definition is registered for the specified Class reference.
   * @param clazz class reference.
   * @return boolean true if defined.
   */
  boolean hasDefinition(Class<?> clazz);

  /**
   * Registers a mapping definition with the factory
   * @param definition a mapping definition
   */
  void addDefinition(MappingDefinition definition);

  /**
   * Returns a definition for the fully-qualified class name provided.
   * @param clazz fully qualified class name.
   * @return an instance of the mapping definition, if defined. returns null if not defined.
   */
  MappingDefinition getDefinition(String clazz);


  /**
   * Returns a definition for the specified {@link MetaClass} reference.
   * @param clazz class reference
   * @return an instance of the mapping definition, if defined. returns null if not defined.
   */
  MappingDefinition getDefinition(MetaClass clazz);

  /**
   * Returns a definition for the sepcified Class reference.
   * @param clazz class reference
   * @return an instance of the mapping definition, if defined. returns null if not defined.
   */
  MappingDefinition getDefinition(Class<?> clazz);

  /**
   * Merge the specified definition with any existing definitions in the specified definitions class hierarchy.
   * In general, if a  mapping exists for the parent class of the specified definition, then its mappings
   * will be merged in -- if they're not covered by the specified mapping. Overloaded constructor mappings will
   * be detected, if the specified mapping does not define a constructor mapping.
   * @param def a mapping definition to merge.
   */
  void mergeDefinition(MappingDefinition def);

  /**
   * Returns true if the class is exposed for marshalling. This is not the same as whether or not it has a defined
   * definition.Just that the class is expected to be marshallable.
   * @param clazz fully qualified class name.
   * @return boolean true if exposed.
   */
  boolean isExposedClass(MetaClass clazz);

  /**
   * Returns a set of all exposed classes.
   * @return a set of exposed classes.
   */
  Set<MetaClass> getExposedClasses();

  /**
   * Returns a map of aliases mappers. The keys represent the aliases and values represent the concrete mapping
   * classes to use as a basis.
   * @return a map of aliases.
   */
  Map<String, String> getMappingAliases();
  
  /**
   * Returns true if the marshalling system should treat the given type as polymorphic.
   * 
   * @param type
   *          The type to test for the existence of portable implementation/subtypes.
   */
  boolean shouldUseObjectMarshaller(MetaClass type);

  /**
   * Returns a collection of all registered mapping definitions.
   *
   * @retur a collection of mapping definitions.
   */
  Collection<MappingDefinition> getMappingDefinitions();

  void resetDefinitionsAndReload();

  Set<MetaClass> getArraySignatures();
  
  boolean hasBuiltInDefinition(MetaClass type);
}
