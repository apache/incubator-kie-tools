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

package org.jboss.errai.codegen.meta.impl.build;

import org.jboss.errai.codegen.BlockStatement;
import org.jboss.errai.codegen.DefModifiers;
import org.jboss.errai.codegen.DefParameters;
import org.jboss.errai.codegen.ThrowsDeclaration;
import org.jboss.errai.codegen.builder.impl.Scope;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaMethod;

import java.lang.reflect.Method;

/**
 * @author Mike Brock
 */
public class ShadowBuildMetaMethod extends BuildMetaMethod {
  private MetaMethod shadow;

  public ShadowBuildMetaMethod(BuildMetaClass declaringClass, BlockStatement body, Scope scope, DefModifiers modifiers, String name,
                               MetaClass returnType, DefParameters defParameters,
                               ThrowsDeclaration throwsDeclaration, MetaMethod shadow) {
    super(declaringClass, body, scope, modifiers, name, returnType, defParameters, throwsDeclaration);
    this.shadow = shadow;
  }

  @Override
  public Method asMethod() {
    return shadow.asMethod();
  }
}
