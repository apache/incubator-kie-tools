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


package org.jboss.errai.codegen.builder.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.util.TypeLiteral;

import org.jboss.errai.codegen.BooleanExpression;
import org.jboss.errai.codegen.Cast;
import org.jboss.errai.codegen.Comment;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.Variable;
import org.jboss.errai.codegen.builder.ArrayInitializationBuilder;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.CaseBlockBuilder;
import org.jboss.errai.codegen.builder.CatchBlockBuilder;
import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.builder.ElseBlockBuilder;
import org.jboss.errai.codegen.builder.StatementBegin;
import org.jboss.errai.codegen.builder.StatementEnd;
import org.jboss.errai.codegen.builder.VariableDeclarationInitializer;
import org.jboss.errai.codegen.builder.VariableDeclarationNamed;
import org.jboss.errai.codegen.builder.VariableDeclarationStart;
import org.jboss.errai.codegen.builder.VariableReferenceContextualStatementBuilder;
import org.jboss.errai.codegen.builder.WhileBuilder;
import org.jboss.errai.codegen.builder.callstack.BranchCallElement;
import org.jboss.errai.codegen.builder.callstack.DeclareVariable;
import org.jboss.errai.codegen.builder.callstack.DefineLabel;
import org.jboss.errai.codegen.builder.callstack.DynamicLoad;
import org.jboss.errai.codegen.builder.callstack.LoadClassReference;
import org.jboss.errai.codegen.builder.callstack.LoadField;
import org.jboss.errai.codegen.builder.callstack.LoadLiteral;
import org.jboss.errai.codegen.builder.callstack.LoadNested;
import org.jboss.errai.codegen.builder.callstack.LoadVariable;
import org.jboss.errai.codegen.builder.callstack.MethodCall;
import org.jboss.errai.codegen.builder.callstack.ResetCallElement;
import org.jboss.errai.codegen.builder.callstack.ThrowException;
import org.jboss.errai.codegen.control.branch.BreakStatement;
import org.jboss.errai.codegen.control.branch.ContinueStatement;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;

/**
 * The root of our fluent StatementBuilder API.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Mike Brock <cbrock@redhat.com>
 */
public class StatementBuilder extends AbstractStatementBuilder implements StatementBegin {

  private static final Pattern THIS_OR_SUPPER_PATTERN = Pattern.compile("(this|super)");
  private static final Pattern THIS_PATTERN = Pattern.compile("(this\\.)(.)*");

  public StatementBuilder(final Context context) {
    super(context);

    if (context != null) {
      context.getDeclaredVariables().stream()
        .filter(v -> !THIS_OR_SUPPER_PATTERN.matcher(v.getName()).matches())
        .forEach(v -> appendCallElement(new DeclareVariable(v)));
    }
    appendCallElement(new ResetCallElement());
  }

  public static StatementBegin create() {
    return new StatementBuilder(null);
  }

  public static StatementBegin create(final Context context) {
    return new StatementBuilder(context);
  }

  @Override
  public VariableDeclarationStart declareVariable(final Class<?> type) {
    return declareVariable(MetaClassFactory.get(type));
  }

  @Override
  public VariableDeclarationStart declareVariable(final MetaClass type) {
    return new VariableDeclarationStart<StatementBuilder>() {
      boolean isFinal;
      String name;
      Object initialization;

      @Override
      public VariableDeclarationNamed<StatementBuilder> asFinal() {
        isFinal = true;
        return this;
      }

      @Override
      public VariableDeclarationInitializer<StatementBuilder> named(final String name) {
        this.name = name;
        return this;
      }

      @Override
      public StatementBuilder initializeWith(final Object initialization) {
        this.initialization = initialization;
        return finish();
      }

      @Override
      public StatementBuilder initializeWith(final Statement initialization) {
        this.initialization = initialization;
        return finish();
      }

      @Override
      public StatementBuilder finish() {
        if (initialization == null) {
          declareVariable(isFinal ? Variable.createFinal(name, type) : Variable.create(name, type));
        }
        else {
          declareVariable(isFinal ? Variable.createFinal(name, type, initialization) : Variable.create(name, type,
              initialization));
        }
        return StatementBuilder.this;
      }
    };
  }

  @Override
  public StatementBuilder declareVariable(final String name, final Class<?> type) {
    return declareVariable(Variable.create(name, type));
  }

  @Override
  public StatementBuilder declareVariable(final String name, final TypeLiteral<?> type) {
    return declareVariable(Variable.create(name, type));
  }

  @Override
  public StatementBuilder declareVariable(final String name, final Object initialization) {
    return declareVariable(Variable.create(name, initialization));
  }

  @Override
  public StatementBuilder declareVariable(final String name, final MetaClass type, final Object initialization) {
    return declareVariable(Variable.create(name, type, initialization));
  }

  @Override
  public StatementBuilder declareVariable(final String name, final Class<?> type, final Object initialization) {
    return declareVariable(Variable.create(name, type, initialization));
  }

  @Override
  public StatementBuilder declareVariable(final String name, final TypeLiteral<?> type, final Object initialization) {
    return declareVariable(Variable.create(name, type, initialization));
  }

  @Override
  public StatementBuilder declareFinalVariable(final String name, final Class<?> type) {
    return declareVariable(Variable.createFinal(name, type));
  }

  @Override
  public StatementBuilder declareFinalVariable(final String name, final TypeLiteral<?> type) {
    return declareVariable(Variable.createFinal(name, type));
  }

  @Override
  public StatementBuilder declareFinalVariable(final String name, final MetaClass type, final Object initialization) {
    return declareVariable(Variable.createFinal(name, type, initialization));
  }

  @Override
  public StatementBuilder declareFinalVariable(final String name, final Class<?> type, final Object initialization) {
    return declareVariable(Variable.createFinal(name, type, initialization));
  }

  @Override
  public StatementBuilder declareFinalVariable(final String name, final TypeLiteral<?> type, final Object initialization) {
    return declareVariable(Variable.createFinal(name, type, initialization));
  }

  private StatementBuilder declareVariable(final Variable v) {
    appendCallElement(new DeclareVariable(v));
    return this;
  }

  @Override
  public VariableReferenceContextualStatementBuilder loadVariable(final String name, final Object... indexes) {
    final Matcher m = THIS_PATTERN.matcher(name);
    if (m.matches()) {
      return loadClassMember(name.replaceFirst("(this\\.)", ""), indexes);
    }
    appendCallElement(new LoadVariable(name, indexes));
    return new ContextualStatementBuilderImpl(context, callElementBuilder);
  }

  @Override
  public VariableReferenceContextualStatementBuilder loadClassMember(final String name, final Object... indexes) {
    appendCallElement(new LoadVariable(name, true, indexes));
    return new ContextualStatementBuilderImpl(context, callElementBuilder);
  }

  @Override
  public ContextualStatementBuilder loadLiteral(final Object o) {
    appendCallElement(new LoadLiteral(o));
    return new ContextualStatementBuilderImpl(context, callElementBuilder);
  }

  @Override
  public ContextualStatementBuilder load(final Object o) {
    appendCallElement(new DynamicLoad(o));
    return new ContextualStatementBuilderImpl(context, callElementBuilder);
  }

  @Override
  public ContextualStatementBuilder loadClassReference(final Object o) {
    final MetaClass c;
    if (o instanceof MetaClass) {
      c = (MetaClass) o;
    }
    else if (o instanceof Class) {
      c = MetaClassFactory.get((Class) o);
    }
    else {
      throw new RuntimeException("unknown class reference type: " + (o == null ? "null" : o.getClass().getName()));
    }
    appendCallElement(new LoadClassReference(c));
    return new ContextualStatementBuilderImpl(context, callElementBuilder);
  }

  @Override
  public ContextualStatementBuilder invokeStatic(final MetaClass clazz, final String methodName, final Object... parameters) {
    appendCallElement(new LoadClassReference(clazz));
    appendCallElement(new MethodCall(methodName, parameters, true));
    return new ContextualStatementBuilderImpl(context, callElementBuilder);
  }

  @Override
  public ContextualStatementBuilder invokeStatic(final Class<?> clazz, final String methodName, final Object... parameters) {
    return invokeStatic(MetaClassFactory.get(clazz), methodName, parameters);
  }

  @Override
  public ContextualStatementBuilder loadStatic(final Class<?> clazz, final String fieldName) {
    return loadStatic(MetaClassFactory.get(clazz), fieldName);
  }

  @Override
  public ContextualStatementBuilder loadStatic(final MetaClass clazz, final String fieldName) {
    appendCallElement(new LoadClassReference(clazz));
    appendCallElement(new LoadField(fieldName));
    return new ContextualStatementBuilderImpl(context, callElementBuilder);
  }

  @Override
  public ContextualStatementBuilder nestedCall(final Statement statement) {
    appendCallElement(new LoadNested(statement));
    return new ContextualStatementBuilderImpl(context, callElementBuilder);
  }

  @Override
  public ObjectBuilder newObject(final Class<?> type) {
    return ObjectBuilder.newInstanceOf(type, context, callElementBuilder);
  }

  @Override
  public ObjectBuilder newObject(final MetaClass type) {
    return ObjectBuilder.newInstanceOf(type, context, callElementBuilder);
  }

  @Override
  public ObjectBuilder newObject(final TypeLiteral<?> type) {
    return ObjectBuilder.newInstanceOf(type, context, callElementBuilder);
  }

  @Override
  public Statement newObject(final Class<?> type, final Object... parameters) {
    return ObjectBuilder.newInstanceOf(type, context, callElementBuilder)
        .withParameters(parameters);
  }

  @Override
  public Statement newObject(final MetaClass type, final Object... parameters) {
    return ObjectBuilder.newInstanceOf(type, context, callElementBuilder)
        .withParameters(parameters);
  }

  @Override
  public Statement newObject(final TypeLiteral<?> type, final Object... parameters) {
    return ObjectBuilder.newInstanceOf(type, context, callElementBuilder)
        .withParameters(parameters);
  }

  @Override
  public ArrayInitializationBuilder newArray(final MetaClass componentType, final Object... dimensions) {
    return new ArrayBuilderImpl(context, callElementBuilder).newArray(componentType, dimensions);
  }

  @Override
  public ArrayInitializationBuilder newArray(final Class<?> componentType, final Object... dimensions) {
    return new ArrayBuilderImpl(context, callElementBuilder).newArray(componentType, dimensions);
  }

  @Override
  public BlockBuilder<WhileBuilder> do_() {
    return new LoopBuilderImpl(context, callElementBuilder).do_();
  }

  @Override
  public BlockBuilder<ElseBlockBuilder> if_(final BooleanExpression stmt) {
    return new IfBlockBuilderImpl(context, callElementBuilder).if_(stmt);
  }

  @Override
  public BlockBuilder<StatementEnd> while_(final BooleanExpression stmt) {
    return new LoopBuilderImpl(context, callElementBuilder).while_(stmt);
  }

  @Override
  public BlockBuilder<StatementEnd> for_(final BooleanExpression condition) {
    return new LoopBuilderImpl(context, callElementBuilder).for_(condition);
  }

  @Override
  public BlockBuilder<StatementEnd> for_(final Statement initializer, final BooleanExpression condition) {
    return new LoopBuilderImpl(context, callElementBuilder).for_(initializer, condition);
  }

  @Override
  public BlockBuilder<StatementEnd> for_(final Statement initializer, final BooleanExpression condition,
                                         final Statement countingExpression) {
    return new LoopBuilderImpl(context, callElementBuilder).for_(initializer, condition, countingExpression);
  }

  @Override
  public CaseBlockBuilder switch_(final Statement statement) {
    return new SwitchBlockBuilderImpl(context, callElementBuilder).switch_(statement);
  }

  @Override
  public BlockBuilder<CatchBlockBuilder> try_() {
    return new TryBlockBuilderImpl(context, callElementBuilder).try_();
  }

  @Override
  public StatementEnd throw_(final Class<? extends Throwable> throwableType, final Object... parameters) {
    appendCallElement(new ThrowException(throwableType, parameters));
    return this;
  }

  @Override
  public StatementEnd throw_(final String exceptionVarName) {
    appendCallElement(new ThrowException(exceptionVarName));
    return this;
  }

  @Override
  public StatementEnd label(final String label) {
    appendCallElement(new DefineLabel(label));
    return this;
  }

  @Override
  public StatementEnd break_() {
    appendCallElement(new BranchCallElement(new BreakStatement()));
    return this;
  }

  @Override
  public StatementEnd break_(final String label) {
    appendCallElement(new BranchCallElement(new BreakStatement(label)));
    return this;
  }

  @Override
  public StatementEnd continue_() {
    appendCallElement(new BranchCallElement(new ContinueStatement()));
    return this;
  }

  @Override
  public StatementEnd continue_(final String label) {
    appendCallElement(new BranchCallElement(new ContinueStatement(label)));
    return this;
  }

  @Override
  public StatementEnd returnVoid() {
    return new StatementEnd() {
      @Override
      public String toJavaString() {
        return "return";
      }

      @Override
      public String generate(final Context context) {
        return toJavaString();
      }

      @Override
      public MetaClass getType() {
        return MetaClassFactory.get(void.class);
      }
    };
  }

  @Override
  public ContextualStatementBuilder castTo(final Class<?> type, final Statement statement) {
    return nestedCall(Cast.to(type, statement));
  }

  @Override
  public ContextualStatementBuilder castTo(final MetaClass type, final Statement statement) {
    return nestedCall(Cast.to(type, statement));
  }

  @Override
  public Statement codeComment(final String comment) {
    return new Comment(comment);
  }
}
