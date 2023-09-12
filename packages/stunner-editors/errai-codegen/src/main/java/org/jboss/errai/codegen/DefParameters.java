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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameter;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class DefParameters extends AbstractStatement {
  private final List<Parameter> parameters;

  private DefParameters(final List<Parameter> parameters) {
    this.parameters = parameters;
  }

  public static DefParameters from(final MetaMethod method) {
    final List<Parameter> parameters = new ArrayList<Parameter>();

    int i = 0;
    for (final MetaParameter parm : method.getParameters()) {
      parameters.add(Parameter.of(parm.getType(), "a" + i++));
    }
    return new DefParameters(parameters);
  }
  
  public static DefParameters from(final MetaMethod method, final Parameter... parms) {
    final List<Parameter> parameters = new ArrayList<Parameter>();

    int i = 0;
    for (final MetaParameter parm : method.getParameters()) {
      parameters.add(Parameter.of(parm.getType(), parms[i].getName(), parms[i++].isFinal()));
    }
    return new DefParameters(parameters);
  }

  public static DefParameters fromParameters(final List<Parameter> statements) {
    return new DefParameters(statements);
  }

  public static DefParameters fromParameters(final Parameter... statements) {
    return new DefParameters(Arrays.asList(statements));
  }

  public static DefParameters fromTypeArray(final MetaClass... types) {
    return fromTypeArray("a", types);
  }

  public static DefParameters fromTypeArray(final String prefix, final MetaClass... types) {
    final List<Parameter> parms = new ArrayList<Parameter>();
    int idx = 0;

    for (final MetaClass metaClass : types) {
      parms.add(Parameter.of(metaClass, prefix + idx++));
    }

    return new DefParameters(parms);
  }
  
  public static DefParameters of(final Parameter... parms) {
    return new DefParameters(Arrays.asList(parms));
    
  }

  public static DefParameters none() {
    return new DefParameters(Collections.<Parameter>emptyList());
  }

  public List<Parameter> getParameters() {
    return parameters;
  }

  @Override
  public String generate(final Context context) {
    final StringBuilder buf = new StringBuilder("(");
    for (int i = 0; i < parameters.size(); i++) {
      buf.append(parameters.get(i).generate(context));

      if (i + 1 < parameters.size()) {
        buf.append(", ");
      }
    }
    return buf.append(")").toString();
  }

  @Override
  public String toString() {
    final StringBuilder buf = new StringBuilder("(");
    for (int i = 0; i < parameters.size(); i++) {
      buf.append(parameters.get(i).getType().getFullyQualifiedName())
              .append(' ').append(parameters.get(i).getName());

      if (i + 1 < parameters.size()) {
        buf.append(", ");
      }
    }
    return buf.append(")").toString();
  }

}
