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


package org.jboss.errai.codegen.meta.impl.gwt;

import java.lang.annotation.Annotation;

import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaType;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class GWTField extends MetaField {
  private final JField field;
  private final Annotation[] annotations;
  private final TypeOracle oracle;

  GWTField(final TypeOracle oracle, final JField field) {
    this.oracle = oracle;
    this.field = field;
    this.annotations = field.getAnnotations();
  }

  @Override
  public MetaClass getType() {
    return GWTUtil.eraseOrReturn(oracle, field.getType());
  }

  @Override
  public String getName() {
    return field.getName();
  }

  @Override
  public Annotation[] getAnnotations() {
    return annotations == null ? new Annotation[0] : annotations;
  }

  @Override
  public MetaType getGenericType() {
    return GWTUtil.fromType(oracle, field.getType());
  }

  @Override
  public MetaClass getDeclaringClass() {
    return GWTClass.newInstance(oracle, field.getEnclosingType());
  }
  
  @Override
  public String getDeclaringClassName() {
    return field.getEnclosingType().getName();
  }

  @Override
  public boolean isAbstract() {
    return false;
  }

  @Override
  public boolean isPublic() {
    return field.isPublic();
  }

  @Override
  public boolean isPrivate() {
    return field.isPrivate();
  }

  @Override
  public boolean isProtected() {
    return field.isProtected();
  }

  @Override
  public boolean isFinal() {
    return field.isFinal();
  }

  @Override
  public boolean isStatic() {
    return field.isStatic();
  }

  @Override
  public boolean isTransient() {
    return field.isTransient();
  }

  @Override
  public boolean isVolatile() {
    return field.isVolatile();
  }

  @Override
  public boolean isSynthetic() {
    return false;
  }

  @Override
  public boolean isSynchronized() {
    return false;
  }

}
