/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.common.metadata;

import javassist.bytecode.ClassFile;

/**
* @author Mike Brock
*/
class SortableClassFileWrapper implements Comparable<SortableClassFileWrapper> {
  private String name;
  private ClassFile classFile;

  SortableClassFileWrapper(final String name, final ClassFile classFile) {
    this.name = name;
    this.classFile = classFile;
  }

  public ClassFile getClassFile() {
    return classFile;
  }

  @Override
  public int compareTo(final SortableClassFileWrapper o) {
    return name.compareTo(o.name);
  }
}
