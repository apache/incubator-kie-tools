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

import java.util.Collection;
import java.util.HashSet;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.ioc.rebind.ioc.graph.api.HasInjectableHandle;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Qualifier;

/**
 * This is used in the {@link DependencyGraphBuilderImpl} for representing
 * injectables in dependencies that have not yet been resolved.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
class InjectableReference extends InjectableBase implements HasInjectableHandle {
  // TODO needs to be renamed and not be an Injectable

  final Collection<InjectableBase> linked = new HashSet<>();
  Injectable resolution;

  InjectableReference(final MetaClass type, final Qualifier qualifier) {
    super(type, qualifier);
  }

  @Override
  public String toString() {
    return String.format("%s %s", getQualifier(), getInjectedType().getFullyQualifiedNameWithTypeParms());
  }

}
