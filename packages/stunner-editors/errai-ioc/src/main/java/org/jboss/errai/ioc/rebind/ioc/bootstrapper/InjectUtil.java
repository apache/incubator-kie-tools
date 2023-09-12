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


package org.jboss.errai.ioc.rebind.ioc.bootstrapper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Qualifier;

import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.meta.HasAnnotations;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.rebind.ioc.injector.api.FactoryController;

import static org.jboss.errai.codegen.util.Stmt.castTo;
import static org.jboss.errai.codegen.util.Stmt.loadLiteral;
import static org.jboss.errai.codegen.util.Stmt.loadVariable;

/**
 * Some useful methods for generating code to inject dependencies in {@link Factory factories}.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class InjectUtil {

  public static Statement invokePublicOrPrivateMethod(final FactoryController controller, final MetaMethod method, final Statement... params) {
    if (method.isPublic()) {
      return loadVariable("instance").invoke(method, (Object[]) params);
    } else {
      return controller.exposedMethodStmt(method, params);
    }
  }

  public static List<Annotation> extractQualifiers(final HasAnnotations annotated) {
    final List<Annotation> qualifiers = new ArrayList<Annotation>();
    for (final Annotation anno : annotated.getAnnotations()) {
      if (anno.annotationType().isAnnotationPresent(Qualifier.class)) {
        qualifiers.add(anno);
      }
    }

    return qualifiers;
  }

  public static ContextualStatementBuilder getPublicOrPrivateFieldValue(final FactoryController controller, final MetaField field) {
    if (field.isPublic()) {
      return loadVariable("instance").loadField(field);
    } else {
      return controller.exposedFieldStmt(field);
    }
  }

  /**
   * Generates code to call
   * {@link Factory#getReferenceAs(Object, String, Class)} for an instance in
   * {@link Factory#createInstance(org.jboss.errai.ioc.client.container.ContextManager)}
   * and
   * {@link Factory#destroyInstance(Object, org.jboss.errai.ioc.client.container.ContextManager)}
   * methods.
   */
  public static ContextualStatementBuilder constructGetReference(final String name, final Class<?> refType) {
    // This cast is for the benefit of codegen, which is sometimes unable to
    // identify the value for the type parameter of Factory.getReferenceAs.
    return castTo(refType, loadVariable("thisInstance").invoke("getReferenceAs", loadVariable("instance"), name, refType));
  }

  /**
   * Generates code to call
   * {@link Factory#getReferenceAs(Object, String, Class)} for an instance in
   * {@link Factory#createInstance(org.jboss.errai.ioc.client.container.ContextManager)}
   * and
   * {@link Factory#destroyInstance(Object, org.jboss.errai.ioc.client.container.ContextManager)}
   * methods.
   */
  public static ContextualStatementBuilder constructGetReference(final String name, final MetaClass refType) {
    // This cast is for the benefit of codegen, which is sometimes unable to
    // identify the value for the type parameter of Factory.getReferenceAs.
    return castTo(refType, loadVariable("thisInstance").invoke("getReferenceAs", loadVariable("instance"), name, loadLiteral(refType)));
  }

  /**
   * Generates code to call {@link Factory#setReference(Object, String, Object)}
   * for an instance in the
   * {@link Factory#createInstance(org.jboss.errai.ioc.client.container.ContextManager)}
   * method.
   */
  public static ContextualStatementBuilder constructSetReference(final String name, final Statement value) {
    return loadVariable("thisInstance").invoke("setReference", Stmt.loadVariable("instance"), name, value);
  }

}
