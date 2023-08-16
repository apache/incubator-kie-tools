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

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public abstract class MetaParameter extends AbstractHasAnnotations {
  public abstract String getName();

  public abstract MetaClass getType();

  public abstract MetaClassMember getDeclaringMember();

  private String _hashString;
  private Integer index = null;

  public String hashString() {
    if (_hashString != null) return _hashString;
    return _hashString = MetaParameter.class.getName() + ":" + getName() + ":"
            + getType().getFullyQualifiedName();
  }

  @Override
  public int hashCode() {
    return hashString().hashCode() * 31;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof  MetaParameter && ((MetaParameter) o).hashString().equals(hashString());
  }

  public Integer getIndex() {
    if (index == null) {
      final MetaClassMember member = getDeclaringMember();
      final MetaParameter[] params;
      if (member instanceof MetaMethod) {
        params = ((MetaMethod) member).getParameters();
      } else if (member instanceof MetaConstructor) {
        params = ((MetaConstructor) member).getParameters();
      } else {
        throw new RuntimeException("Not yet implemented!");
      }

      for (int i = 0; i < params.length; i++) {
        if (params[i] == this || params[i].getName().equals(getName())) {
          index = i;

          return index;
        }
      }

      throw new RuntimeException("Could not find index of parameter " + getName() + " in "
              + getDeclaringMember().getName() + " in " + getDeclaringMember().getDeclaringClassName());
    }

    return index;
  }
}
