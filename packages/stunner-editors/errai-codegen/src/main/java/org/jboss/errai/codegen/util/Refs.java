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


package org.jboss.errai.codegen.util;

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.VariableReference;
import org.jboss.errai.codegen.exception.OutOfScopeException;
import org.jboss.errai.codegen.meta.MetaClass;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public abstract class Refs {
  public static VariableReference get(final String name, final MetaClass type) {
    return new VariableReference() {
      @Override
      public String getName() {
        return name;
      }

      @Override
      public Statement getValue() {
        return new Statement() {
          @Override
          public String generate(Context context) {
            return name;
          }

          @Override
          public MetaClass getType() {
            return type;
          }
        };
      }
    };
  }

  public static VariableReference get(final String name) {
    return new VariableReference() {
      private MetaClass type;

      @Override
      public String getName() {
        return name;
      }

      @Override
      public Statement getValue() {
        return new Statement() {

          String generatedCache;

          @Override
          public String generate(final Context context) {
            if (generatedCache != null) return generatedCache;

            final VariableReference var = context.getVariable(name);

            if (var == null) {
              throw new OutOfScopeException("could not access variable: " + name);
            }

            type = var.getType();

            return generatedCache = name;
          }

          @Override
          public MetaClass getType() {
            return type;
          }
        };
      }

      @Override
      public String generate(final Context context) {
        return getValue().generate(context);
      }

      @Override
      public MetaClass getType() {
        return type;
      }

      @Override
      public String toString() {
        return "[var-ref:" + name + "::" + type + "]";
      }
    };
  }
}
