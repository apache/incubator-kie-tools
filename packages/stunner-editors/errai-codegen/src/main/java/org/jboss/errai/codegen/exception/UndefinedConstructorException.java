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


package org.jboss.errai.codegen.exception;

import java.util.Arrays;

import org.jboss.errai.codegen.meta.MetaClass;

/**
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class UndefinedConstructorException extends GenerationException {
  private static final long serialVersionUID = 1L;

  private MetaClass type;
  private MetaClass[] parameterTypes;

  public UndefinedConstructorException() {
    super();
  }

  public UndefinedConstructorException(String msg) {
    super(msg);
  }

  public UndefinedConstructorException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public UndefinedConstructorException(MetaClass type, MetaClass... parameterTypes) {
    this.type = type;
    this.parameterTypes = parameterTypes;
  }

  public UndefinedConstructorException(MetaClass type, Throwable cause, MetaClass... parameterTypes) {
    super(cause);
    this.type = type;
    this.parameterTypes = parameterTypes;
  }

  @Override
  public String toString() {
    final StringBuilder buf = new StringBuilder(128);

    buf.append(super.toString()).append(": class:").append(type.getFullyQualifiedName()).append(" parameterTypes:");
    Arrays.stream(parameterTypes).forEach(type -> buf.append(type.getFullyQualifiedName()).append(" "));
    return buf.toString();
  }
}
