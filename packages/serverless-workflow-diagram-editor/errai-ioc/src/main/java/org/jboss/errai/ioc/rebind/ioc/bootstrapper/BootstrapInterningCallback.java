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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;

import org.jboss.errai.codegen.AnnotationEncoder;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.InterningCallback;
import org.jboss.errai.codegen.Modifier;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.literal.ArrayLiteral;
import org.jboss.errai.codegen.literal.LiteralFactory;
import org.jboss.errai.codegen.literal.LiteralValue;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.util.PrivateAccessUtil;
import org.jboss.errai.codegen.util.Refs;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.ioc.client.QualifierUtil;

/**
 * The interning callback for handling the interning of qualifier annotations and arrays of qualifier annotations.
 *
 * @author Mike Brock
 */
public class BootstrapInterningCallback implements InterningCallback {
  private final Map<Set<Annotation>, String> cachedArrays = new HashMap<Set<Annotation>, String>();
  private final MetaClass Annotation_MC = MetaClassFactory.get(Annotation.class);
  private final ClassStructureBuilder<?> classStructureBuilder;
  private final Context buildContext;

  public BootstrapInterningCallback(final ClassStructureBuilder<?> classStructureBuilder,
                                    final Context buildContext) {
    this.classStructureBuilder = classStructureBuilder;
    this.buildContext = buildContext;
  }

  @Override
  public Statement intern(final LiteralValue<?> literalValue) {
    if (literalValue.getValue() == null) {
      return null;
    }

    if (literalValue.getValue() instanceof Annotation) {
      final Annotation annotation = (Annotation) literalValue.getValue();

      if (annotation.annotationType().equals(Default.class)) {
        return Stmt.loadStatic(QualifierUtil.class, "DEFAULT_ANNOTATION");
      }
      else if (annotation.annotationType().equals(Any.class)) {
        return Stmt.loadStatic(QualifierUtil.class, "ANY_ANNOTATION");
      }


      final Class<? extends Annotation> aClass = annotation.annotationType();
      final String fieldName = PrivateAccessUtil.condensify(aClass.getPackage().getName()) + "_" +
          aClass.getSimpleName() + "_" + String.valueOf(literalValue.getValue().hashCode()).replaceFirst("\\-", "_");

      classStructureBuilder.privateField(fieldName, annotation.annotationType())
          .modifiers(Modifier.Final).initializesWith(AnnotationEncoder.encode(annotation))
          .finish();

      return Refs.get(fieldName);
    }
    else if (literalValue.getType().isArray()
        && Annotation_MC.isAssignableFrom(literalValue.getType().getOuterComponentType())) {

      final Set<Annotation> annotationSet
          = new HashSet<Annotation>(Arrays.asList((Annotation[]) literalValue.getValue()));

      if (QualifierUtil.isDefaultAnnotations(annotationSet)) {
        return Stmt.loadStatic(QualifierUtil.class, "DEFAULT_QUALIFIERS");
      }

      if (cachedArrays.containsKey(annotationSet)) {
        return Refs.get(cachedArrays.get(annotationSet));
      }

      final MetaClass type = literalValue.getType().getOuterComponentType();
      final String fieldName = "arrayOf" + PrivateAccessUtil.condensify(type.getPackageName()) +
          type.getName().replaceAll("\\.", "_") + "_"
          + String.valueOf(literalValue.getValue().hashCode()).replaceAll("\\-", "_");

      // force rendering of literals in this array first.
      for (final Annotation a : annotationSet) {
        LiteralFactory.getLiteral(a).generate(buildContext);
      }

      classStructureBuilder.privateField(fieldName, literalValue.getType())
          .modifiers(Modifier.Final).initializesWith(new Statement() {
        @Override
        public String generate(final Context context) {
          return new ArrayLiteral(literalValue.getValue()).getCanonicalString(context);
        }

        @Override
        public MetaClass getType() {
          return literalValue.getType();
        }
      }).finish();

      cachedArrays.put(annotationSet, fieldName);

      return Refs.get(fieldName);
    }

    return null;
  }
}
