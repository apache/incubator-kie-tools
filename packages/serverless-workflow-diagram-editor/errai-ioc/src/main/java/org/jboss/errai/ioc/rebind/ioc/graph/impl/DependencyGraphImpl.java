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


package org.jboss.errai.ioc.rebind.ioc.graph.impl;

import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Iterators;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraph;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;

/**
 * @see DependencyGraph
 * @author Max Barkley <mbarkley@redhat.com>
 */
class DependencyGraphImpl implements DependencyGraph {

  private final Map<String, Injectable> injectablesByName;

  DependencyGraphImpl(final Map<String, Injectable> injectablesByName) {
    this.injectablesByName = injectablesByName;
  }

  @Override
  public Iterator<Injectable> iterator() {
    return Iterators.unmodifiableIterator(injectablesByName.values().iterator());
  }

  @Override
  public Injectable getConcreteInjectable(final String injectableName) {
    return injectablesByName.get(injectableName);
  }

  @Override
  public int getNumberOfInjectables() {
    return injectablesByName.size();
  }

}