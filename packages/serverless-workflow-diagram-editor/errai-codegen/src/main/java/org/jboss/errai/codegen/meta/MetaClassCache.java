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


package org.jboss.errai.codegen.meta;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.jboss.errai.common.rebind.CacheStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mike Brock
 */
public class MetaClassCache implements CacheStore {
  private static final Logger logger = LoggerFactory.getLogger(MetaClassCache.class);

  private final Map<String, CacheEntry> PRIMARY_CLASS_CACHE
      = new ConcurrentHashMap<String, CacheEntry>(2000);

  private final Map<String, CacheEntry> PERMANENT_CLASS_CACHE
      = new ConcurrentHashMap<String, CacheEntry>(2000);

  private final Map<String, MetaClass> ERASED_CLASS_CACHE
      = new ConcurrentHashMap<String, MetaClass>(2000);

  private final Set<String> invalidated =  Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
  private final Set<MetaClass> added = Collections.newSetFromMap(new ConcurrentHashMap<MetaClass, Boolean>());
  private final Set<String> removed =  Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
  private final Map<String, CacheEntry> backupClassCache = new ConcurrentHashMap<String, MetaClassCache.CacheEntry>();

  @Override
  public void clear() {
    backupClassCache.clear();
    backupClassCache.putAll(PRIMARY_CLASS_CACHE);

    invalidated.clear();
    removed.clear();
    added.clear();

    PRIMARY_CLASS_CACHE.clear();
    ERASED_CLASS_CACHE.clear();

    PRIMARY_CLASS_CACHE.putAll(PERMANENT_CLASS_CACHE);
  }

  public void updateCache(Map<String, MetaClass> mapToPush) {
    logger.debug("updateCache called for " + mapToPush.size() + " MetaClasses.");
    addNewOrUpdatedToInvalidated(mapToPush);
    addRemoved();
  }

  private void addRemoved() {
    final Set<String> oldKeys = new HashSet<String>(backupClassCache.keySet());
    oldKeys.removeAll(PRIMARY_CLASS_CACHE.keySet());

    for (final String clazzName : oldKeys) {
      if (backupClassCache.get(clazzName).hashCode != CacheEntry.PLACE_HOLDER)
        removed.add(clazzName);
    }
  }

  private void addNewOrUpdatedToInvalidated(Map<String, MetaClass> mapToPush) {
    for (final Entry<String, MetaClass> entry : mapToPush.entrySet()) {
      logger.trace("Creating new " + entry.getValue().getClass().getSimpleName() + " cache entry for " + entry.getKey());
      final CacheEntry newCacheEntry = createCacheEntry(entry.getValue());
      PRIMARY_CLASS_CACHE.put(entry.getKey(), newCacheEntry);
      final CacheEntry previousCacheEntry = backupClassCache.get(entry.getKey());
      if (previousCacheEntry == null || previousCacheEntry.hashCode != newCacheEntry.hashCode) {
        logger.trace("Old cache entry replaced for " + entry.getKey());
        invalidated.add(entry.getKey());

        if (previousCacheEntry == null) {
          added.add(entry.getValue());
        }
      }
    }
  }

  public void pushCache(final MetaClass clazz) {
    pushCache(clazz.getFullyQualifiedName(), clazz);
  }

  public void pushCache(final String fqcn, final MetaClass clazz) {
    logger.trace("Creating new " + clazz.getClass().getSimpleName() + " cache entry for " + fqcn);
    if (!PRIMARY_CLASS_CACHE.containsKey(fqcn)) {
      PRIMARY_CLASS_CACHE.put(fqcn, new CacheEntry(clazz, CacheEntry.PLACE_HOLDER));
      if (!backupClassCache.containsKey(clazz.getFullyQualifiedName())) {
        invalidated.add(fqcn);
      }
    }
  }

  public void pushToPermanentCache(final MetaClass clazz) {
    pushToPermanentCache(clazz.getFullyQualifiedName(), clazz);
  }

  public void pushToPermanentCache(final String fullyQualifiedName, final MetaClass clazz) {
    logger.trace("Creating new permanent " + clazz.getClass().getSimpleName() + " cache entry for " + fullyQualifiedName);
    pushCache(fullyQualifiedName, clazz);
    PERMANENT_CLASS_CACHE.put(fullyQualifiedName, PRIMARY_CLASS_CACHE.get(fullyQualifiedName));
  }

  public MetaClass get(String fqcn) {
    final CacheEntry entry = PRIMARY_CLASS_CACHE.get(fqcn);
    return (entry != null) ? entry.cachedClass : null;
  }

  public Collection<MetaClass> getAllCached() {
    return PRIMARY_CLASS_CACHE.values().stream().filter(c -> c != null).map(c -> c.cachedClass).collect(Collectors.toList());
  }

  public Collection<MetaClass> getAllNewOrUpdated() {
    return invalidated.stream().map(fcqn -> PRIMARY_CLASS_CACHE.get(fcqn).cachedClass).collect(Collectors.toList());
  }

  public Set<String> getAllDeletedClasses() {
    return Collections.unmodifiableSet(removed);
  }

  public Set<MetaClass> getAllNewClasses() {
    return Collections.unmodifiableSet(added);
  }

  public boolean isKnownErasedType(final String fqcn) {
    return ERASED_CLASS_CACHE.containsKey(fqcn);
  }

  public MetaClass getErased(final String fqcn) {
    return ERASED_CLASS_CACHE.get(fqcn);
  }

  public void pushErasedCache(final String fqcn, final MetaClass clazz) {
    logger.trace("Creating new " + clazz.getClass().getSimpleName() + " cache entry for " + fqcn);
    ERASED_CLASS_CACHE.put(fqcn, clazz);
  }

  public int size() {
    return PRIMARY_CLASS_CACHE.size();
  }

  public boolean isKnownType(String fqcn) {
    return PRIMARY_CLASS_CACHE.containsKey(fqcn);
  }

  public boolean isNewOrUpdated(String fqcn) {
    return invalidated.contains(fqcn);
  }

  private class CacheEntry {
    final MetaClass cachedClass;
    final int hashCode;

    static final int PLACE_HOLDER = 0;

    CacheEntry(final MetaClass cachedClass, final int hashCode) {
      this.cachedClass = cachedClass;
      this.hashCode = hashCode;
    }
  }

  private CacheEntry createCacheEntry(final MetaClass cachedClass) {
    return new CacheEntry(cachedClass, cachedClass.hashContent());
  }

}
