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

import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.Dependency;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;

final class AsyncValidator implements Validator {
  @Override
  public boolean canValidate(final Injectable injectable) {
    return !injectable.loadAsync();
  }

  @Override
  public void validate(final Injectable injectable, final Collection<String> problems) {
    for (final Dependency dep : injectable.getDependencies()) {
      if (dep.getInjectable().loadAsync()) {
        problems.add("The bean " + injectable + " is not @LoadAsync but depends on the @LoadAsync bean " + dep.getInjectable());
      }
    }
  }
}