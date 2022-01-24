/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.enterprise.client.cdi;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

/**
 * @author Mike Brock
 */
public final class CDIEventTypeLookup {
  private Map<String, Collection<String>> typeLookup = new HashMap<String, Collection<String>>();

  private CDIEventTypeLookup() {
  }

  private final static CDIEventTypeLookup typeLookupSingleton = new CDIEventTypeLookup();

  public static CDIEventTypeLookup get() {
    return typeLookupSingleton;
  }

  /**
   * Returns a defensively copied, unmodifiable Map of all sub-types to their supertypes.
   *
   * @return a defensively copied, unmodifiable Map of all sub-types to their supertypes.
   */
  public Map<String, Collection<String>> getTypeLookupMap() {
    // build a defensive copy of the type lookup map.

    Map<String, Collection<String>> newLookupMap = new HashMap<String, Collection<String>>();
    for (Map.Entry<String, Collection<String>> entry : typeLookup.entrySet()) {
      newLookupMap.put(entry.getKey(), Collections.unmodifiableSet(new LinkedHashSet<String>(entry.getValue())));
    }

    return unmodifiableMap(newLookupMap);
  }

  public void addLookup(final String subtype, final String superType) {
    Collection<String> superTypes = typeLookup.get(subtype);
    if (superTypes == null) {
      typeLookup.put(subtype, superTypes = new LinkedHashSet<String>());
    }
    superTypes.add(superType);
  }
}
