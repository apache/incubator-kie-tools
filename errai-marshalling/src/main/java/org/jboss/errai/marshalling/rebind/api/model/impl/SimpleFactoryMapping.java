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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.impl.java.JavaReflectionClass;
import org.jboss.errai.marshalling.rebind.api.model.FactoryMapping;
import org.jboss.errai.marshalling.rebind.api.model.Mapping;

/**
 * @author Mike Brock
 */
public class SimpleFactoryMapping implements FactoryMapping {
  private MetaClass toMap;
  
  private Map<Integer, MetaClass> indexToType = new TreeMap<Integer, MetaClass>();
  private Map<Integer, String> indexToName = new TreeMap<Integer, String>();
  private Map<String, Integer> nameToIndex = new HashMap<String, Integer>();

  private MetaMethod method;

  public void mapParmToIndex(String parm, int index, Class<?> type) {
    mapParmToIndex(parm, index, JavaReflectionClass.newUncachedInstance(type));
  }

  public void mapParmToIndex(String parm, int index, MetaClass type) {
    indexToType.put(index, type);
    indexToName.put(index, parm);
    nameToIndex.put(parm, index);
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

  public void setMethod(MetaMethod method) {
    this.method = method;
  }

  @Override
  public MetaMethod getMember() {
    return method;
  }

  public void setMappingClass(MetaClass toMap) {
    this.toMap = toMap;
  }

  @Override
  public MetaClass getMappingClass() {
    return toMap;
  }
  
  @Override
  public int getIndex(String key) {
    return nameToIndex.get(key);
  }


}
