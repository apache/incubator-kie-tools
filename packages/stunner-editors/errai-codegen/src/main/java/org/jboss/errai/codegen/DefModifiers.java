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


package org.jboss.errai.codegen;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.jboss.errai.codegen.builder.Builder;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class DefModifiers implements Builder {
  private final Set<Modifier> modifiers = new TreeSet<Modifier>();

  public DefModifiers() {
  }

  public DefModifiers(final Modifier... modifiers) {
    addModifiers(modifiers);
  }
  
  public static DefModifiers of(final Modifier... modifiers) {
    final DefModifiers m = new DefModifiers();
    m.addModifiers(modifiers);
    return m;
  }

  public static DefModifiers none() {
    return new DefModifiers();
  }
  
  public DefModifiers addModifiers(final Modifier... modifier) {
    modifiers.addAll(Arrays.asList(modifier));
    return this;
  }

  public boolean hasModifier(final Modifier modifier) {
    return modifiers.contains(modifier);
  }

  @Override
  public String toJavaString() {
    final StringBuilder stringBuilder = new StringBuilder(128);
    modifiers.stream().forEach(m -> stringBuilder.append(m.getCanonicalString()).append(" "));
    return stringBuilder.toString().trim();
  }
}
