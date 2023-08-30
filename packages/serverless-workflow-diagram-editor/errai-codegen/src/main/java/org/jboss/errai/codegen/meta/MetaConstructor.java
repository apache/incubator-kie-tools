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


package org.jboss.errai.codegen.meta;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public abstract class MetaConstructor extends MetaMethod implements MetaClassMember, MetaGenericDeclaration {
  public abstract MetaParameter[] getParameters();

  public abstract MetaType[] getGenericParameterTypes();

  public abstract boolean isVarArgs();

  public Constructor asConstructor() {
    try {
      final Class cls = Class.forName(getDeclaringClass().getFullyQualifiedName());
      final Class[] parms = MetaClassFactory.asClassArray(getParameters());

      for (Constructor constructor : cls.getDeclaredConstructors()) {
        if (Arrays.equals(parms, constructor.getParameterTypes())) {
          return constructor;
        }
      }
      return null;
    }
    catch (Throwable t) {
      return null;
    }
  }
}
