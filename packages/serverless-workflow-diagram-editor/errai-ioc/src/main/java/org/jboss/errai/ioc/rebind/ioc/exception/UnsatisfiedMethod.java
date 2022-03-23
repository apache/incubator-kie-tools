/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ioc.rebind.ioc.exception;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaMethod;

/**
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class UnsatisfiedMethod extends UnsatisfiedDependency {

  private MetaMethod method;


  public UnsatisfiedMethod(MetaMethod method, MetaClass enclosingType, MetaClass injectedType, String message) {
    super(enclosingType, injectedType, message);
    this.method = method;
  }

  @Override
  public String toString() {
    StringBuilder sbuf = new StringBuilder();
    sbuf.append(super.toString());
    sbuf.append(" - setter ").append(method)
      .append(" could not be satisified for type: ").append(injectedType.getFullyQualifiedName()).append("\n");

    if (message.length() > 0) {
      sbuf.append("  Message: ").append(message).append("\n");
    }

    return sbuf.toString();
  }

  public MetaMethod getMethod() {
    return method;
  }
}
