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

import com.google.gwt.core.ext.typeinfo.JTypeParameter;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.MetaTypeVariable;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class GWTTypeVariable implements MetaTypeVariable {
  private final JTypeParameter typeParameter;
  private final TypeOracle oracle;

  public GWTTypeVariable(final TypeOracle oracle, final JTypeParameter typeParameter) {
    this.typeParameter = typeParameter;
    this.oracle = oracle;
  }

  @Override
  public MetaType[] getBounds() {
    return GWTUtil.fromTypeArray(oracle, typeParameter.getBounds());
  }

  @Override
  public String getName() {
    return typeParameter.getName();
  }

}
