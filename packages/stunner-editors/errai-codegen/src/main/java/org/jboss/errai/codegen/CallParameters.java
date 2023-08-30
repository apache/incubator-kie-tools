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
import java.util.Collections;
import java.util.List;

import org.jboss.errai.codegen.literal.NullLiteral;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.mvel2.util.NullType;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class CallParameters extends AbstractStatement {
  private final List<Statement> parameters;

  public CallParameters(final List<Statement> parameters) {
    this.parameters = parameters;
  }

  public static CallParameters fromStatements(final Statement... statements) {
    return new CallParameters(Arrays.asList(statements));
  }


  public static CallParameters none() {
    return new CallParameters(Collections.<Statement>emptyList());
  }

  public MetaClass[] getParameterTypes() {
    final MetaClass[] parameterTypes = new MetaClass[parameters.size()];
    for (int i = 0; i < parameters.size(); i++) {
      if (parameters.get(i) instanceof NullLiteral) {
        parameterTypes[i] = MetaClassFactory.get(NullType.class);
      }
      else if ((parameterTypes[i] = parameters.get(i).getType()) == null) {
        parameterTypes[i] = MetaClassFactory.get(Object.class);
      }

    }
    return parameterTypes;
  }

  public List<Statement> getParameters() {
    return parameters;
  }

  @Override
  public String generate(final Context context) {
    final StringBuilder buf = new StringBuilder("(");
    for (int i = 0; i < parameters.size(); i++) {
      String parm = parameters.get(i).generate(context).trim();

      if (parm.endsWith(";")) {
        parm = parm.substring(0, parm.length() - 1);
      }

      buf.append(parm);

      if (i + 1 < parameters.size()) {
        buf.append(", ");
      }
    }
    return buf.append(")").toString();
  }
}
