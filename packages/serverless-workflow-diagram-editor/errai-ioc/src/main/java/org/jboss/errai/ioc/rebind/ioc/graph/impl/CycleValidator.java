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
import java.util.LinkedHashSet;
import java.util.Set;

import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.Dependency;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.DependencyType;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.InjectableType;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType;

final class CycleValidator implements Validator {
  private final Set<Injectable> visited = new HashSet<Injectable>();
  private final Set<Injectable> visiting = new LinkedHashSet<Injectable>();

  @Override
  public boolean canValidate(final Injectable injectable) {
    return injectable.getWiringElementTypes().contains(WiringElementType.PseudoScopedBean) && !visited.contains(injectable);
  }

  @Override
  public void validate(final Injectable injectable, final Collection<String> problems) {
    validateDependentScopedInjectable(injectable, visiting, visited, problems, false);
  }

  private static void validateDependentScopedInjectable(final Injectable injectable, final Set<Injectable> visiting,
          final Set<Injectable> visited, final Collection<String> problems, final boolean onlyConstuctorDeps) {
    if (InjectableType.Disabled.equals(injectable.getInjectableType())) {
      visited.add(injectable);
      return;
    }
    if (visiting.contains(injectable)) {
      problems.add(createCycleMessage(visiting, injectable));
      return;
    }

    visiting.add(injectable);
    for (final Dependency dep : injectable.getDependencies()) {
      if (onlyConstuctorDeps && !dep.getDependencyType().equals(DependencyType.Constructor)) {
        continue;
      }

      final Injectable resolved = GraphUtil.getResolvedDependency(dep, injectable);
      if (!visited.contains(resolved)) {
        if (dep.getDependencyType().equals(DependencyType.ProducerMember)) {
          validateDependentScopedInjectable(resolved, visiting, visited, problems, true);
        } else if (resolved.getWiringElementTypes().contains(WiringElementType.PseudoScopedBean)) {
          validateDependentScopedInjectable(resolved, visiting, visited, problems, false);
        }
      }
    }
    visiting.remove(injectable);
    visited.add(injectable);
  }

  private static String createCycleMessage(final Set<Injectable> visiting, final Injectable injectable) {
    final StringBuilder builder = new StringBuilder();
    boolean cycleStarted = false;
    boolean hasProducer = false;

    for (final Injectable visitingInjectable : visiting) {
      if (visitingInjectable.equals(injectable)) {
        cycleStarted = true;
      }
      if (cycleStarted) {
        builder.append("\t");
        visitingInjectable.getQualifier().stream()
          .forEach(anno -> builder.append(anno.toString()).append(' '));
        builder.append(visitingInjectable.getInjectedType().getFullyQualifiedName())
               .append("\n");
        if (visitingInjectable.getInjectableType().equals(InjectableType.Producer)) {
          hasProducer = true;
        }
      }
    }

    if (hasProducer) {
      builder.insert(0, "A cycle was found containing a producer and no other normal scoped types:\n");
    } else {
      builder.insert(0, "A cycle of only pseudo-scoped beans was found:\n");
    }

    return builder.toString();
  }
}