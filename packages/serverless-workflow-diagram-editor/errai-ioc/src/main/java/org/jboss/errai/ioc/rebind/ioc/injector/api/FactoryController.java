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


package org.jboss.errai.ioc.rebind.ioc.injector.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.meta.HasAnnotations;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaClassMember;
import org.jboss.errai.codegen.meta.MetaConstructor;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameter;
import org.jboss.errai.codegen.meta.impl.build.BuildMetaClass;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.ioc.client.api.CodeDecorator;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable.DecorableType;

import static org.jboss.errai.codegen.util.PrivateAccessUtil.getPrivateMethodName;
import static org.jboss.errai.codegen.util.Stmt.castTo;
import static org.jboss.errai.codegen.util.Stmt.invokeStatic;
import static org.jboss.errai.codegen.util.Stmt.loadVariable;
import static org.jboss.errai.ioc.rebind.ioc.bootstrapper.InjectUtil.constructGetReference;
import static org.jboss.errai.ioc.rebind.ioc.bootstrapper.InjectUtil.constructSetReference;

/**
 * The single point of contact for {@link CodeDecorator code decorators} to add
 * generated code to a {@link Factory}.
 *
 * @see CodeDecorator
 * @see Factory
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class FactoryController {

  private final ListMultimap<MetaMethod, Statement> invokeBefore = ArrayListMultimap.create();
  private final ListMultimap<MetaMethod, Statement> invokeAfter = ArrayListMultimap.create();
  private final Map<String, Statement> proxyProperties = new HashMap<String, Statement>();
  private final List<Statement> initializationStatements = new ArrayList<Statement>();
  private final List<Statement> endInitializationStatements = new ArrayList<Statement>();
  private final List<Statement> destructionStatements = new ArrayList<Statement>();
  private final Map<String, Object> attributes = new HashMap<String, Object>();
  private final Set<MetaField> exposedFields = new HashSet<MetaField>();
  private final Set<MetaMethod> exposedMethods = new HashSet<MetaMethod>();
  private final Set<MetaConstructor> exposedConstructors = new HashSet<MetaConstructor>();
  private final List<Statement> factoryInitializationStatements = new ArrayList<Statement>();
  private final MetaClass producedType;
  private final String factoryName;
  private final BuildMetaClass factory;

  public FactoryController(final MetaClass producedType, final String factoryName, final BuildMetaClass factory) {
    this.producedType = producedType;
    this.factoryName = factoryName;
    this.factory = factory;
  }

  /**
   * Add a statement to be run in a proxy before the invocation of a bean's
   * method. Using this method will force the bean to be proxied.
   */
  public void addInvokeBefore(final MetaMethod method, Statement statement) {
    invokeBefore.put(method, statement);
  }

  /**
   * @return All statements added with {@link #addInvokeBefore(MetaMethod, Statement)} for the given method.
   */
  public List<Statement> getInvokeBeforeStatements(final MetaMethod method) {
    return invokeBefore.get(method);
  }

  /**
   * Add a statement to be run in a proxy after the invocation of a bean's
   * method. Using this method will force the bean to be proxied.
   */
  public void addInvokeAfter(final MetaMethod method, Statement statement) {
    invokeAfter.put(method, statement);
  }

  /**
   * @return All statements added with {@link #addInvokeAfter(MetaMethod, Statement)} for the given method.
   */
  public List<Statement> getInvokeAfterStatements(final MetaMethod method) {
    return invokeAfter.get(method);
  }

  /**
   * Add a private field to a bean's proxy. Calling this method forces a bean to
   * be proxied.
   *
   * @param name
   *          The name of the field.
   * @param type
   *          The type of the field.
   * @param statement
   *          The initialization statement. This statement is invoked in a
   *          generated implementation for
   *          {@link Proxy#initProxyProperties(Object)}.
   * @return A statement with the loaded proxy field that was just created. This
   *         statement is only valid when used within the context of the proxy.
   */
  public Statement addProxyProperty(final String name, final Class<?> type, final Statement statement) {
    proxyProperties.put(name, new Statement() {
      @Override
      public MetaClass getType() {
        return MetaClassFactory.get(type);
      }

      @Override
      public String generate(Context context) {
        return statement.generate(context);
      }
    });

    return loadVariable(name);
  }

  /**
   * @return All field names and initialization statements added with {@link #addProxyProperty(String, Class, Statement)}.
   */
  public Collection<Entry<String, Statement>> getProxyProperties() {
    return proxyProperties.entrySet();
  }

  /**
   * Add a list of statements to the implementation of
   * {@link Factory#init(org.jboss.errai.ioc.client.container.Context)}.
   */
  public void addFactoryInitializationStatements(final List<Statement> factoryInitializationStatements) {
    this.factoryInitializationStatements.addAll(factoryInitializationStatements);
  }

  /**
   * @return All statements added with
   *         {@link #addFactoryInitializationStatements(List)}, in the order
   *         they were added.
   */
  public List<Statement> getFactoryInitializaionStatements() {
    return factoryInitializationStatements;
  }

  /**
   * Add a list of statements to the implementation of
   * {@link Factory#createInstance(org.jboss.errai.ioc.client.container.ContextManager)}
   * .
   */
  public void addInitializationStatements(final List<Statement> callbackBodyStatements) {
    initializationStatements.addAll(callbackBodyStatements);
  }

  /**
   * @return All statements added with
   *         {@link #addInitializationStatements(List)} in the order they were
   *         added followed by all statements added with
   *         {@link #addInitializationStatementsToEnd(List)} in the order they
   *         were added.
   */
  public List<Statement> getInitializationStatements() {
    final List<Statement> stmts = new ArrayList<Statement>();
    stmts.addAll(initializationStatements);
    stmts.addAll(endInitializationStatements);

    return stmts;
  }

  /**
   * Add a list of statements to the implementation of
   * {@link Factory#destroyInstance(Object, org.jboss.errai.ioc.client.container.ContextManager)}
   * . The {@code instance} parameter in scope is guaranteed to be unproxied.
   */
  public void addDestructionStatements(final List<Statement> callbackInstanceStatement) {
    destructionStatements.addAll(callbackInstanceStatement);
  }

  /**
   * @return All statements added with {@link #addDestructionStatements(List)}
   *         in the order they were added.
   */
  public List<Statement> getDestructionStatements() {
    return destructionStatements;
  }

  /**
   * @param name The name of an attribute.
   * @return True iff an attribute with the given name has been set with {@link #setAttribute(String, Object)}.
   */
  public boolean hasAttribute(final String name) {
    return attributes.containsKey(name);
  }

  /**
   * Set an attribute that can be looked up with {@link #getAttribute(String)}.
   */
  public void setAttribute(final String name, final Object value) {
    attributes.put(name, value);
  }

  /**
   * Lookup an attribute that was previously added with {@link #setAttribute(String, Object)}.
   *
   * @return The value of the attribute with the given name, or {@code null} if not attribute exists.
   */
  public Object getAttribute(final String name) {
    return attributes.get(name);
  }

  /**
   * @param instanceStmt
   *          The statement for the instance that will be the first parameter to
   *          {@link ContextManager#getInstanceProperty(Object, String, Class)}.
   * @param name
   *          The name of the property.
   * @param refType
   *          The type of the property.
   * @return A statement that looks up a property of an instance from another
   *         factory via
   *         {@link ContextManager#getInstanceProperty(Object, String, Class)}.
   */
  public ContextualStatementBuilder getInstancePropertyStmt(final Statement instanceStmt, final String name, final Class<?> refType) {
    return Stmt.castTo(refType, loadVariable("contextManager").invoke("getInstanceProperty", instanceStmt, name, refType));
  }

  /**
   * @param name
   *          The name of the property.
   * @param refType
   *          The type of the property.
   * @return A statement to call
   *         {@link Factory#getReferenceAs(Object, String, Class)} for an
   *         instance in
   *         {@link Factory#createInstance(org.jboss.errai.ioc.client.container.ContextManager)}
   *         and
   *         {@link Factory#destroyInstance(Object, org.jboss.errai.ioc.client.container.ContextManager)}
   *         methods.
   */
  public ContextualStatementBuilder getReferenceStmt(final String name, final Class<?> refType) {
    return constructGetReference(name, refType);
  }

  /**
   * @param name
   *          The name of the property.
   * @param refType
   *          The type of the property.
   * @return A statement to call
   *         {@link Factory#getReferenceAs(Object, String, Class)} for an
   *         instance in
   *         {@link Factory#createInstance(org.jboss.errai.ioc.client.container.ContextManager)}
   *         and
   *         {@link Factory#destroyInstance(Object, org.jboss.errai.ioc.client.container.ContextManager)}
   *         methods.
   */
  public ContextualStatementBuilder getReferenceStmt(final String name, final MetaClass refType) {
    return constructGetReference(name, refType);
  }

  /**
   * @param name
   *          The name of the property.
   * @param value
   *          A statement for the value to be set.
   * @return A statement to call
   *         {@link Factory#setReference(Object, String, Object)} for an
   *         instance in the
   *         {@link Factory#createInstance(org.jboss.errai.ioc.client.container.ContextManager)}
   *         method.
   */
  public ContextualStatementBuilder setReferenceStmt(final String name, final Statement value) {
    return constructSetReference(name, value);
  }

  /**
   * This should only be called for non-public fields. This method forces a
   * private accessor to be generated for the field.
   *
   * @param field
   *          A field, static or non-static.
   * @return A statement for accessing the value of a field.
   */
  public ContextualStatementBuilder exposedFieldStmt(final MetaField field) {
    if (!field.isPublic()) {
      addExposedField(field);
    }

    return DecorableType.FIELD.getAccessStatement(field, factory);
  }

  /**
   * This should only be called for non-public fields. This method forces a
   * private accessor to be generated for the field.
   *
   * @param field
   *          A non-static field.
   * @return A statement for accessing the value of a field.
   */
  public ContextualStatementBuilder exposedFieldStmt(final Statement instance, final MetaField field) {
    if (!field.isPublic()) {
      addExposedField(field);
    }

    return DecorableType.FIELD.call(instance, field, factory);
  }

  /**
   * For public methods, fields, constructors, and parameters of public methods,
   * this method does nothing. Otherwise this method generates private
   * accessors/mutators. In the case of a parameter this method acts as if
   * called for the declaring method.
   *
   * This method is idempotent.
   *
   * @param annotated
   *          A method, field, or parameter that may or may not be public.
   */
  public void ensureMemberExposed(final HasAnnotations annotated) {
    final MetaClassMember member;
    if (annotated instanceof MetaParameter) {
      member = ((MetaParameter) annotated).getDeclaringMember();
    } else {
      member = (MetaClassMember) annotated;
    }
    if (!member.isPublic()) {
      if (member instanceof MetaField) {
        addExposedField((MetaField) member);
      } else if (member instanceof MetaMethod) {
        addExposedMethod((MetaMethod) member);
      } else if (member instanceof MetaConstructor) {
        addExposedConstructor((MetaConstructor) member);
      }
    }
  }

  /**
   * This method is idempotent.
   *
   * @param field A non-public field for which private accessor and mutator methods should be generated.
   */
  public void addExposedField(final MetaField field) {
    exposedFields.add(field);
  }

  /**
   * This should only be called for non-public methods. This method forces a
   * private accessor to be generated for the method. Dispatches to variable
   * {@code instance}.
   *
   * @param method
   *          A method, static or non-static.
   * @param params
   *          Statements for the values to be passed in as parameters.
   * @return A statement for accessing invoking the given method.
   */
  public ContextualStatementBuilder exposedMethodStmt(final MetaMethod method, final Statement... params) {
    if (!method.isPublic()) {
      addExposedMethod(method);
    }

    return DecorableType.METHOD.getAccessStatement(method, factory, params);
  }

  /**
   * This should only be called for non-public methods. This method forces a
   * private accessor to be generated for the method.
   *
   * @param instance
   *          A statement for the instance on which this method will be called.
   * @param method
   *          A non-static method.
   * @param params
   *          Statements for the values to be passed in as parameters.
   * @return A statement for accessing invoking the given method.
   */
  public ContextualStatementBuilder exposedMethodStmt(final Statement instance, final MetaMethod method, final Statement... params) {
    if (!method.isPublic()) {
      addExposedMethod(method);
    }

    return DecorableType.METHOD.call(instance, method, factory, params);
  }

  /**
   * This method is idempotent.
   *
   * @param method A non-public method for which a private accessor should be generated.
   */
  public void addExposedMethod(final MetaMethod method) {
    exposedMethods.add(method);
  }

  /**
   * This should only be called for non-public constructors. This method forces a
   * private accessor to be generated for the constructor.
   *
   * @param constructor
   *          A non-public constructor.
   * @return A statement for invoking the given constructor.
   */
  public ContextualStatementBuilder exposedConstructorStmt(final MetaConstructor constructor, final Statement... params) {
    addExposedConstructor(constructor);

    return invokeStatic(factory, getPrivateMethodName(constructor), (Object[]) params);
  }

  /**
   * This method is idempotent.
   *
   * @param constructor A non-public constructor for which a private accessor method should be generated.
   */
  public void addExposedConstructor(final MetaConstructor constructor) {
    exposedConstructors.add(constructor);
  }

  /**
   * @return A statement for getting an instance of the type produced by this
   *         factory in
   *         {@link Factory#init(org.jboss.errai.ioc.client.container.Context)}.
   */
  public Statement contextGetInstanceStmt() {
    return castTo(producedType, loadVariable("context").invoke("getInstance", factoryName));
  }

  /**
   * @return An unmodifiable collection of fields added with
   *         {@link #addExposedField(MetaField)} or
   *         {@link #ensureMemberExposed(HasAnnotations)}.
   */
  public Collection<MetaField> getExposedFields() {
    return Collections.unmodifiableCollection(exposedFields);
  }

  /**
   * @return An unmodifiable collection of methods added with
   *         {@link #addExposedMethod(MetaMethod)} or
   *         {@link #ensureMemberExposed(HasAnnotations)}.
   */
  public Collection<MetaMethod> getExposedMethods() {
    return Collections.unmodifiableCollection(exposedMethods);
  }

  /**
   * @return An unmodifiable collection of constructors added with
   *         {@link #addExposedMethod(MetaMethod)} or
   *         {@link #ensureMemberExposed(HasAnnotations)}.
   */
  public Collection<MetaConstructor> getExposedConstructors() {
    return Collections.unmodifiableCollection(exposedConstructors);
  }

  /**
   * Add a list of statements that are to be added to
   * {@link Factory#createInstance(ContextManager)} after all statements added
   * with {@link #addInitializationStatements(List)}.
   *
   * @param statements
   *          A collection of statements to be added to the end of
   *          {@link Factory#createInstance(ContextManager)}.
   */
  public void addInitializationStatementsToEnd(final List<Statement> statements) {
    endInitializationStatements.addAll(statements);
  }

  /**
   * @return True iff any of the following methods have been called:
   *         <ul>
   *            <li>{@link #addInvokeBefore(MetaMethod, Statement)},
   *            <li>{@link #addInvokeAfter(MetaMethod, Statement)},
   *            <li>{@link #addProxyProperty(String, Class, Statement)}.
   *         </ul>
   */
  public boolean requiresProxy() {
    return !(proxyProperties.isEmpty() && invokeAfter.isEmpty() && invokeBefore.isEmpty());
  }

}
