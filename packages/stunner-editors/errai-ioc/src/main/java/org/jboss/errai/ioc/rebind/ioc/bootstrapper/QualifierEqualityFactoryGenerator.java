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

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import org.jboss.errai.codegen.ArithmeticOperator;
import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.AnonymousClassStructureBuilder;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.ConstructorBlockBuilder;
import org.jboss.errai.codegen.builder.MethodBlockBuilder;
import org.jboss.errai.codegen.builder.impl.ClassBuilder;
import org.jboss.errai.codegen.builder.impl.ObjectBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.impl.java.JavaReflectionClass;
import org.jboss.errai.codegen.util.Arith;
import org.jboss.errai.codegen.util.CDIAnnotationUtils;
import org.jboss.errai.codegen.util.If;
import org.jboss.errai.codegen.util.Refs;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.common.metadata.RebindUtils;
import org.jboss.errai.ioc.client.AnnotationComparator;
import org.jboss.errai.ioc.client.QualifierEqualityFactory;
import org.jboss.errai.ioc.client.QualifierUtil;
import org.jboss.errai.ioc.util.TranslatableAnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.errai.codegen.meta.MetaClassFactory.parameterizedAs;
import static org.jboss.errai.codegen.meta.MetaClassFactory.typeParametersOf;
import static org.jboss.errai.codegen.util.Stmt.invokeStatic;

/**
 * @author Mike Brock
 */
public class QualifierEqualityFactoryGenerator extends Generator {

  private static final Logger log = LoggerFactory.getLogger(QualifierEqualityFactoryGenerator.class);

  @Override
  public String generate(final TreeLogger logger, final GeneratorContext context, final String typeName)
      throws UnableToCompleteException {
    try {
      final JClassType classType = context.getTypeOracle().getType(typeName);
      final String packageName = classType.getPackage().getName();
      final String className = classType.getSimpleSourceName() + "Impl";

      // Generate class source code
      generateQualifierEqualityFactory(packageName, className, logger, context);

      // return the fully qualified name of the class generated
      return packageName + "." + className;
    }
    catch (Throwable e) {
      log.error("Error generating QualifierEqualityFactory", e);
      throw new UnableToCompleteException();
    }
  }

  private static final String COMPARATOR_MAP_VAR = "comparatorMap";

  private void generateQualifierEqualityFactory(final String packageName,
                                                final String className,
                                                final TreeLogger logger,
                                                final GeneratorContext generatorContext) {

    final PrintWriter printWriter = generatorContext.tryCreate(logger, packageName, className);
    if (printWriter == null) {
      return;
    }
    final long start = System.currentTimeMillis();
    log.info("Generating QualifierEqualityFactory...");

    final TypeOracle oracle = generatorContext.getTypeOracle();

    final ClassStructureBuilder<? extends ClassStructureBuilder<?>> builder
        = ClassBuilder.define(packageName + "." + className).publicScope()
        .implementsInterface(QualifierEqualityFactory.class)
        .body();

    builder.getClassDefinition().getContext().setPermissiveMode(true);

    final MetaClass mapStringAnnoComp
        = parameterizedAs(HashMap.class, typeParametersOf(String.class, AnnotationComparator.class));

    builder.privateField(COMPARATOR_MAP_VAR, mapStringAnnoComp)
        .initializesWith(Stmt.newObject(mapStringAnnoComp)).finish();

    final ConstructorBlockBuilder<? extends ClassStructureBuilder<?>> constrBuilder = builder.publicConstructor();

    for (final MetaClass MC_annotationClass : TranslatableAnnotationUtils.getTranslatableQualifiers(oracle)) {
      final Collection<MetaMethod> methods = CDIAnnotationUtils.getAnnotationAttributes(MC_annotationClass);

      if (methods.isEmpty()) continue;

      constrBuilder._(Stmt.loadVariable(COMPARATOR_MAP_VAR)
          .invoke("put", MC_annotationClass.getFullyQualifiedName(), generateComparatorFor(MC_annotationClass, methods)));
    }

    // finish constructor
    constrBuilder.finish();

    final MetaClass annotationClazz = JavaReflectionClass.newUncachedInstance(Annotation.class);
    builder.publicMethod(boolean.class, "isEqual",
        Parameter.of(annotationClazz, "a1"), Parameter.of(annotationClazz, "a2"))
        .body()
        ._(If.cond(invokeStatic(QualifierUtil.class, "isSameType", Refs.get("a1"), Refs.get("a2")))
            ._(
                If.cond(Stmt.loadVariable(COMPARATOR_MAP_VAR).invoke("containsKey",
                    Stmt.loadVariable("a1").invoke("annotationType").invoke("getName")))
                    ._(Stmt.castTo(AnnotationComparator.class, Stmt.loadVariable(COMPARATOR_MAP_VAR)
                        .invoke("get", Stmt.loadVariable("a1").invoke("annotationType").invoke("getName"))
                    ).invoke("isEqual", Refs.get("a1"), Refs.get("a2")).returnValue())
                    .finish()
                    .else_()
                    ._(Stmt.load(true).returnValue())
                    .finish()
            )
            .finish()
            .else_()
            ._(Stmt.load(false).returnValue())
            .finish())
        .finish();


    builder.publicMethod(int.class, "hashCodeOf", Parameter.of(Annotation.class, "a1"))
        .body()
        ._(
            If.cond(Stmt.loadVariable(COMPARATOR_MAP_VAR).invoke("containsKey",
                Stmt.loadVariable("a1").invoke("annotationType").invoke("getName")))
                ._(Stmt.castTo(AnnotationComparator.class, Stmt.loadVariable(COMPARATOR_MAP_VAR)
                    .invoke("get", Stmt.loadVariable("a1").invoke("annotationType").invoke("getName"))
                ).invoke("hashCodeOf", Refs.get("a1")).returnValue())
                .finish()
                .else_()
                ._(Stmt.loadVariable("a1").invoke("annotationType").invoke("hashCode").returnValue())
                .finish()).finish();


    final String csq = builder.toJavaString();

    RebindUtils.writeStringToJavaSourceFileInErraiCacheDir(packageName, className, csq);

    printWriter.append(csq);

    log.info("Generated QualifierEqualityFactory in " + (System.currentTimeMillis() - start) + "ms");
    generatorContext.commit(logger, printWriter);
  }

  private Statement generateComparatorFor(final MetaClass MC_annotationClass, final Collection<MetaMethod> methods) {
    final MetaClass MC_annoComparator = parameterizedAs(AnnotationComparator.class, typeParametersOf(MC_annotationClass));

    final AnonymousClassStructureBuilder clsBuilder = ObjectBuilder.newInstanceOf(MC_annoComparator).extend();
    final MethodBlockBuilder<AnonymousClassStructureBuilder> isEqualBuilder = clsBuilder
        .publicMethod(boolean.class, "isEqual",
            Parameter.of(MC_annotationClass, "a1"), Parameter.of(MC_annotationClass, "a2"))
        .annotatedWith(new Override() {
          @Override
          public Class<? extends Annotation> annotationType() {
            return Override.class;
          }
        });

    for (final MetaMethod method : methods) {
      if (method.getReturnType().isPrimitive()) {
        isEqualBuilder._(
            If.notEquals(Stmt.loadVariable("a1").invoke(method), Stmt.loadVariable("a2").invoke(method))
                ._(Stmt.load(false).returnValue())
                .finish()
        );
      }
      else if (method.getReturnType().isArray()) {
        isEqualBuilder._(
            If.not(Stmt.invokeStatic(Arrays.class, "equals",
                Stmt.loadVariable("a1").invoke(method),
                Stmt.loadVariable("a2").invoke(method))
            )
            ._(Stmt.load(false).returnValue())
            .finish()
        );
      }
      else {
        isEqualBuilder._(
            If.not(Stmt.loadVariable("a1").invoke(method).invoke("equals", Stmt.loadVariable("a2").invoke(method)))
                ._(Stmt.load(false).returnValue())
                .finish()
        );
      }
    }

    isEqualBuilder._(Stmt.load(true).returnValue());

    final BlockBuilder<AnonymousClassStructureBuilder> hashCodeOfBuilder
        = clsBuilder.publicOverridesMethod("hashCodeOf", Parameter.of(MC_annotationClass, "a1"));

    hashCodeOfBuilder._(Stmt.declareVariable(int.class).named("hash")
        .initializeWith(Stmt.loadVariable("a1").invoke("annotationType").invoke("hashCode")));

    for (final MetaMethod method : methods) {
      hashCodeOfBuilder._(Stmt.loadVariable("hash")
          .assignValue(hashArith(method)));
    }

    hashCodeOfBuilder._(Stmt.loadVariable("hash").returnValue());

    hashCodeOfBuilder.finish();

    final AnonymousClassStructureBuilder classStructureBuilder = isEqualBuilder.finish();

    return classStructureBuilder.finish();
  }

  private static Statement hashArith(final MetaMethod method) {
    return Arith.expr(
        Arith.expr(31, ArithmeticOperator.Multiplication, Refs.get("hash")),
        ArithmeticOperator.Addition,
        Stmt.invokeStatic(QualifierUtil.class, "hashValueFor", Stmt.loadVariable("a1").invoke(method))
    );
  }
}
