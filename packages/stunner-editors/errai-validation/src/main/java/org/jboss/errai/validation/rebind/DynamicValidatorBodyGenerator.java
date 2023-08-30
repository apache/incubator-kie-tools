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


package org.jboss.errai.validation.rebind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.validation.client.ProviderValidationMessageResolver;
import com.google.gwt.validation.client.ValidationMessageResolver;
import com.google.gwt.validation.client.impl.ConstraintViolationImpl;
import org.jboss.errai.codegen.InnerClass;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.TernaryStatement;
import org.jboss.errai.codegen.builder.AnonymousClassStructureBuilder;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.builder.impl.ClassBuilder;
import org.jboss.errai.codegen.builder.impl.ObjectBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.util.Bool;
import org.jboss.errai.codegen.util.Refs;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.AbstractBodyGenerator;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraph;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;
import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.jboss.errai.validation.client.dynamic.DynamicValidatorUtil;
import org.jboss.errai.validation.client.dynamic.GeneratedDynamicValidator;

import static org.jboss.errai.codegen.Parameter.finalOf;
import static org.jboss.errai.codegen.meta.MetaClassFactory.parameterizedAs;
import static org.jboss.errai.codegen.meta.MetaClassFactory.typeParametersOf;
import static org.jboss.errai.codegen.util.Stmt.castTo;
import static org.jboss.errai.codegen.util.Stmt.if_;
import static org.jboss.errai.codegen.util.Stmt.invokeStatic;
import static org.jboss.errai.codegen.util.Stmt.loadLiteral;
import static org.jboss.errai.codegen.util.Stmt.loadVariable;
import static org.jboss.errai.codegen.util.Stmt.newObject;

/**
 * Geneate the {@link Factory} body for the factory producing the {@link DynamicValidator}. This generates a
 * {@link GeneratedDynamicValidator} for any GWT-translatable {@link ConstraintValidator} found at compile-time.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class DynamicValidatorBodyGenerator extends AbstractBodyGenerator {

  private final List<MetaClass> validators;

  public DynamicValidatorBodyGenerator(final List<MetaClass> validators) {
    this.validators = validators;
  }

  @Override
  protected List<Statement> generateCreateInstanceStatements(final ClassStructureBuilder<?> bodyBlockBuilder,
          final Injectable injectable, final DependencyGraph graph, final InjectionContext injectionContext) {
    final List<Statement> statements = new ArrayList<>(validators.size()+2);
    statements.add(Stmt.declareFinalVariable("dynamicValidator", DynamicValidator.class,
            ObjectBuilder.newInstanceOf(DynamicValidator.class)));

    bodyBlockBuilder.privateField("messageResolver", ValidationMessageResolver.class)
      .initializesWith(Stmt.invokeStatic(GWT.class, "create", Stmt.loadLiteral(ProviderValidationMessageResolver.class))).finish();
    
    validators
      .stream()
      .map(validator -> addDynamicValidator(bodyBlockBuilder, validator))
      .collect(Collectors.toCollection(() -> statements));

    statements.add(loadVariable("dynamicValidator").returnValue());

    return statements;
  }

  private Statement addDynamicValidator(final ClassStructureBuilder<?> bodyBlockBuilder, final MetaClass validator) {
    final MetaClass constraintValidatorIface = getConstraintValidatorIface(validator);
    final MetaClass valueType = getValueType(validator, constraintValidatorIface);
    final MetaClass annoType = getConstraintType(validator, constraintValidatorIface);
    final ObjectBuilder annoImpl = createAnnoImpl(annoType);

    final ClassStructureBuilder<?> generatedValidatorBuilder = ClassBuilder
      .define("Dynamic" + fullyQualifiedClassNameToCamelCase(validator.getFullyQualifiedName()), validator)
      .publicScope()
      .implementsInterface(parameterizedAs(GeneratedDynamicValidator.class, typeParametersOf(valueType)))
      .body();

    final BlockBuilder<?> validateMethod = generatedValidatorBuilder
      .publicMethod(parameterizedAs(Set.class, typeParametersOf(ConstraintViolation.class)), "validate",
                    finalOf(parameterizedAs(Map.class, typeParametersOf(String.class, Object.class)), "parameters"),
                    finalOf(valueType, "value"))
      .body();


    bodyBlockBuilder.declaresInnerClass(new InnerClass(generatedValidatorBuilder.getClassDefinition()));


    validateMethod
      .append(loadVariable("this").invoke("initialize", annoImpl))
      .append(if_(Bool.expr(loadVariable("this").invoke("isValid", loadVariable("value"), castTo(ConstraintValidatorContext.class, loadLiteral(null)))))
                .append(invokeStatic(Collections.class, "emptySet").returnValue())
              .finish().else_()
                .append(Stmt.declareVariable("paramMessage", String.class, castTo(String.class, 
                        loadVariable("parameters").invoke("get", Stmt.loadLiteral("message")))))
                .append(Stmt.loadVariable("paramMessage").assignValue(
                        new TernaryStatement(Bool.isNotNull(Stmt.loadVariable("paramMessage")),
                                Stmt.loadVariable("paramMessage")
                                .invoke("replaceAll", Stmt.loadLiteral("{"), Stmt.loadLiteral(""))
                                .invoke("replaceAll", Stmt.loadLiteral("}"), Stmt.loadLiteral(""))
                                ,Stmt.loadLiteral(""))))
                .append(Stmt.declareFinalVariable("message", String.class, 
                        Stmt.loadVariable("messageResolver").invoke("get", Refs.get("paramMessage"))))
                .append(invokeStatic(Collections.class, "singleton", createConstraintViolation()).returnValue())
              .finish())
      .finish();

    return loadVariable("dynamicValidator").invoke("addValidator", loadLiteral(annoType.getFullyQualifiedName()),
            loadLiteral(valueType.getFullyQualifiedName()),
            newObject(generatedValidatorBuilder.getClassDefinition()));
  }

  private String fullyQualifiedClassNameToCamelCase(final String fqcn) {
    final StringBuilder builder = new StringBuilder();
    boolean capitalize = true;
    for (int i = 0; i < fqcn.length(); i++) {
      final char charAt = fqcn.charAt(i);
      if (capitalize) {
        builder.append(Character.toUpperCase(charAt));
        capitalize = false;
      }
      else if (charAt == '.') {
        capitalize = true;
      }
      else {
        builder.append(charAt);
      }
    }

    return builder.toString();
  }

  private ContextualStatementBuilder createConstraintViolation() {
    return invokeStatic(ConstraintViolationImpl.class, "builder").invoke("setInvalidValue", loadVariable("value"))
            .invoke("setMessage", Stmt.invokeStatic(DynamicValidatorUtil.class, "interpolateMessage", Refs.get("parameters"), Refs.get("message")))
            .invoke("build");
  }

  private ObjectBuilder createAnnoImpl(final MetaClass annoType) {
    final AnonymousClassStructureBuilder builder = ObjectBuilder.newInstanceOf(annoType).extend();
    Arrays.stream(annoType.getDeclaredMethods())
          .forEach(m -> builder.publicOverridesMethod(m.getName())
                  .append(castTo(m.getReturnType(), loadVariable("parameters").invoke("get", m.getName())).returnValue()).finish());

    builder.publicOverridesMethod("annotationType").append(loadLiteral(annoType).returnValue()).finish();

    return builder.finish();
  }

  private MetaClass getConstraintType(final MetaClass validator, final MetaClass constraintValidatorIface) {
    final MetaType annoTypeVariable = constraintValidatorIface.getParameterizedType().getTypeParameters()[0];
    if (!(annoTypeVariable instanceof MetaClass)) {
      throw new RuntimeException("Cannot determine constraint type for " + validator.getFullyQualifiedName());
    }
    final MetaClass annoType = (MetaClass) annoTypeVariable;
    return annoType;
  }

  private MetaClass getValueType(final MetaClass validator, final MetaClass constraintValidatorIface) {
    final MetaType valueTypeVariable = constraintValidatorIface.getParameterizedType().getTypeParameters()[1];
    if (!(valueTypeVariable instanceof MetaClass)) {
      throw new RuntimeException("Cannot determine validated type of " + validator.getFullyQualifiedName());
    }
    final MetaClass valueType = (MetaClass) valueTypeVariable;
    return valueType;
  }

  private MetaClass getConstraintValidatorIface(final MetaClass validator) {
    final Optional<MetaClass> ifaceOptional = validator.getAllSuperTypesAndInterfaces().stream()
            .filter(iface -> iface.getFullyQualifiedName().equals(ConstraintValidator.class.getName())).findAny();
    if (!ifaceOptional.isPresent()) {
      throw new RuntimeException("Tried to generate dynamic validator for type that isn't a ConstraintValidator: "
              + validator.getFullyQualifiedName());
    }
    return ifaceOptional.get();
  }
}