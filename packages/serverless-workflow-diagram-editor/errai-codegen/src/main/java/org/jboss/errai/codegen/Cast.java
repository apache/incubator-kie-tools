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

import org.jboss.errai.codegen.builder.callstack.LoadClassReference;
import org.jboss.errai.codegen.exception.InvalidTypeException;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.common.client.api.Assert;
import org.mvel2.util.NullType;

/**
 * Implements code generation of type casting statements.
 * 
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
public class Cast implements Statement {
  private final MetaClass toType;
  private final Statement statement;

  private Cast(final MetaClass toType,
               final Statement statement) {

    this.toType = Assert.notNull(toType);
    this.statement = statement;
  }

  public static Statement to(final Class<?> cls,
                             final Statement stmt) {

    return to(MetaClassFactory.get(cls), stmt);
  }

  public static Cast to(final MetaClass cls,
                        final Statement stmt) {

    return new Cast(cls, stmt);
  }

  @Override
  public String generate(final Context context) {
    final String stmt = statement.generate(context);

    if (!toType.isPrimitive() && !toType.isAssignableFrom(statement.getType())
            && !toType.isAssignableTo(statement.getType()) && !toType.isInterface()
            && !statement.getType().asBoxed().equals(toType)) {

      if (context.isPermissiveMode()) {
        return "(" + LoadClassReference.getClassReference(toType, context) + ") " + stmt;
      }
      else {
        throw new InvalidTypeException(statement.getType() + " cannot be cast to " + toType);
      }
    }
    else if (toType.isAssignableFrom(statement.getType()) && !statement.getType().equals(MetaClassFactory.get(NullType.class))) {
      return stmt;
    }
    else {
      return "(" + LoadClassReference.getClassReference(toType, context) + ") " + stmt;
    }
  }

  @Override
  public MetaClass getType() {
    return toType;
  }

  @Override
  public String toString() {
    return "((" + toType.getFullyQualifiedName() + ")" + statement.toString() + ")";
  }
}
