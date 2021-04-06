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

package org.jboss.errai.marshalling.rebind.api.model.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaConstructor;
import org.jboss.errai.codegen.meta.impl.java.JavaReflectionClass;
import org.jboss.errai.marshalling.rebind.api.model.ConstructorMapping;
import org.jboss.errai.marshalling.rebind.api.model.Mapping;

/**
 * @author Mike Brock
 */
public class SimpleConstructorMapping implements ConstructorMapping {
  private MetaClass toMap;

  private Map<Integer, MetaClass> indexToType = new TreeMap<Integer, MetaClass>();
  private Map<Integer, String> indexToName = new TreeMap<Integer, String>();
  private Map<String, Integer> nameToIndex = new HashMap<String, Integer>();

  protected MetaConstructor constructor;

  public void mapParmToIndex(String parm, int index, Class<?> type) {
    mapParmToIndex(parm, index, toMap = JavaReflectionClass.newUncachedInstance(type));
  }


  public void mapParmToIndex(String parm, int index, MetaClass type) {
    indexToType.put(index, type);
    indexToName.put(index, parm);
    nameToIndex.put(parm, index);
  }

  public SimpleConstructorMapping getCopyForInheritance() {
    SimpleConstructorMapping mapping = new SimpleConstructorMapping();
    mapping.toMap = toMap;
    mapping.indexToType = Collections.unmodifiableMap(indexToType);
    mapping.indexToName = Collections.unmodifiableMap(indexToName);
    mapping.nameToIndex = Collections.unmodifiableMap(nameToIndex);

    return mapping;
  }


  public MetaClass[] getConstructorParmTypes() {
    return indexToType.values().toArray(new MetaClass[indexToType.size()]);
  }

  public String[] getKeyNames() {
    return indexToName.values().toArray(new String[indexToName.size()]);
  }

  private Mapping[] _mappingsCache;

  @Override
  public Mapping[] getMappings() {
    if (_mappingsCache != null) {
      return _mappingsCache;
    }
    Mapping[] mappings = new Mapping[indexToType.size()];
    final MetaClass[] types = getConstructorParmTypes();
    final String[] keys = getKeyNames();

    for (int i = 0; i < mappings.length; i++) {
      mappings[i] = new SimpleMapping(keys[i], types[i]);
    }

    return _mappingsCache = mappings;
  }

  private Mapping[] _mappingsCacheInMemberMappingOrder;
  
  @Override
  public Mapping[] getMappingsInKeyOrder(List<String> keys) {
    if (_mappingsCacheInMemberMappingOrder != null) {
      return _mappingsCacheInMemberMappingOrder;
    }

    final Mapping[] mappings = getMappings();
    Mapping[] sortedMappings = new Mapping[mappings.length];
    
    int i = 0;
    for (String key : keys) {
      Integer index = nameToIndex.get(key);
      if (index != null) {
        sortedMappings[i++] = mappings[index];
      }
      else {
        sortedMappings = mappings;
        break;
      }
    }
    
    return _mappingsCacheInMemberMappingOrder = sortedMappings;
  }
  
  private Class<?>[] _constructorSignature;

  @Override
  public Class<?>[] getSignature() {
    if (_constructorSignature != null) {
      return _constructorSignature;
    }

    Mapping[] ms = getMappings();
    Class<?>[] sig = new Class<?>[ms.length];
    int i = 0;
    for (Mapping m : ms) {
      sig[i++] = m.getTargetType().asClass();
    }
    return _constructorSignature = sig;
  }

  public void setConstructor(MetaConstructor constructor) {
    this.constructor = constructor;
  }

  @Override
  public MetaConstructor getMember() {
    return constructor;
  }

  public void setMappingClass(MetaClass toMap) {
    this.toMap = toMap;

    /**
     * Initialize the default no-arg constructor if it exists.
     */
    if (constructor == null) {
      constructor = toMap.getBestMatchingConstructor(getSignature());
    }
  }

  @Override
  public MetaClass getMappingClass() {
    return toMap;
  }

  private static class SimpleMapping extends org.jboss.errai.marshalling.rebind.api.model.impl.SimpleMapping {
    private SimpleMapping(String key, MetaClass type) {
      super(key, type);
    }
  }

  @Override
  public boolean isNoConstruct() {
    return false;
  }


  @Override
  public int getIndex(String key) {
    return nameToIndex.get(key);
  }

}
