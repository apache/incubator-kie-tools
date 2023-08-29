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


package org.jboss.errai.codegen.builder.callstack;

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.meta.MetaClass;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class LoadNested extends AbstractCallElement {
  private Statement statement;

  public LoadNested(final Statement statement) {
    this.statement = new Statement() {
      MetaClass type;

      String generatedCache;
      
      @Override
      public String generate(final Context context) {
        if (generatedCache != null) return generatedCache;

        final String res = statement.generate(context).trim();

        type = statement.getType();

        if (getNext() == null || (getNext() instanceof ReturnValue)) {
          return generatedCache = res;
        }
        else if (isIdentifierOnly(res)) {
          return generatedCache =res;
        }
        else {
          return generatedCache = "(" + res + ")";
        }
      }

      @Override
      public MetaClass getType() {
        return type;
      }

      @Override
      public String toString() {
        return statement.toString();
      }
    };
  }

  private static boolean isIdentifierOnly(final String s) {
    for (int i = 0; i < s.length(); i++) {
      if (!Character.isJavaIdentifierPart(s.charAt(0))) return false;
    }
    
    return true;
  }
  
  @Override
  public void handleCall(final CallWriter writer, final Context context, final Statement statement) {
    nextOrReturn(writer, context, this.statement);
  }

  @Override
  public String toString() {
    return "[[Nest<" + statement + ">]" + next + "]";
  }
}
