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

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.InjectableType;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Qualifier;

import static org.jboss.errai.ioc.util.GeneratedNamesUtil.qualifiedClassNameToIdentifier;
import static org.jboss.errai.ioc.util.GeneratedNamesUtil.shortenGeneratedIdentifier;

public class FactoryNameGenerator {

  private final Multiset<String> allFactoryNames = HashMultiset.create();
  /**
   * If this property is set to true, the factory names of injectables will be shortened versions, instead of containing fully qualified type names.
   */
  public static final String SHORT_NAMES_PROP = "errai.graph_builder.short_factory_names";
  public static final boolean SHORT_NAMES = Boolean.parseBoolean(System.getProperty(SHORT_NAMES_PROP, "true"));

  public String generateFor(final MetaClass type, final Qualifier qualifier, final InjectableType injectableType) {
    final String typeName = qualifiedClassNameToIdentifier(type);
    final String qualNames = qualifier.getIdentifierSafeString();
    String factoryName;
    if (SHORT_NAMES) {
      factoryName = injectableType + "_factory__" + shorten(typeName) + "__quals__" + shorten(qualNames);

    } else {
      factoryName = injectableType + "_factory_for__" + typeName + "__with_qualifiers__" + qualNames;
    }
    final int collisions = allFactoryNames.count(factoryName);
    allFactoryNames.add(factoryName);
    if (collisions > 0) {
      factoryName = factoryName + "_" + String.valueOf(collisions);
    }

    return factoryName;
  }

  public String generateFor(final InjectableHandle handle, final InjectableType injectableType) {
    return generateFor(handle.getType(), handle.getQualifier(), injectableType);
  }

  private String shorten(final String compoundName) {
    final String[] names = compoundName.split("__");
    final StringBuilder builder = new StringBuilder();
    for (final String name : names) {
      builder.append(shortenGeneratedIdentifier(name)).append('_');
    }
    builder.delete(builder.length() - 1, builder.length());

    return builder.toString();
  }

}
