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

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;

import javax.enterprise.context.Dependent;

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.meta.HasAnnotations;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassMember;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameter;
import org.jboss.errai.codegen.meta.impl.build.BuildMetaClass;
import org.jboss.errai.codegen.util.CDIAnnotationUtils;
import org.jboss.errai.ioc.client.api.CodeDecorator;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;

import static org.apache.commons.lang3.Validate.notNull;
import static org.jboss.errai.codegen.util.PrivateAccessUtil.getPrivateFieldAccessorName;
import static org.jboss.errai.codegen.util.PrivateAccessUtil.getPrivateMethodName;
import static org.jboss.errai.codegen.util.Stmt.invokeStatic;
import static org.jboss.errai.codegen.util.Stmt.loadStatic;
import static org.jboss.errai.codegen.util.Stmt.loadVariable;
import static org.jboss.errai.codegen.util.Stmt.nestedCall;
import static org.jboss.errai.ioc.rebind.ioc.bootstrapper.FactoryGenerator.getLocalVariableName;

/**
 * Contains metadata for an element of a particular {@link DecorableType}.
 *
 * {@link CodeDecorator Code decorators} can use this instance to inspect
 * annotations and other information regarding a decorable element.
 *
 * This type also has methods for generating code to access the decorable type
 * within the context of certain {@link Factory} methods.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class Decorable {

  /**
   * The kinds of decorable elements.
   *
   * @author Max Barkley <mbarkley@redhat.com>
   */
  public enum DecorableType {
    FIELD  {
      @Override
      public MetaClass getType(final HasAnnotations annotated) {
        return ((MetaField) annotated).getType();
      }

      @Override
      public MetaClass getEnclosingType(final HasAnnotations annotated) {
        return ((MetaField) annotated).getDeclaringClass();
      }

      @Override
      public ContextualStatementBuilder getAccessStatement(final HasAnnotations annotated, final BuildMetaClass factory) {
        return call(loadVariable("instance"), annotated, factory);
      }

      @Override
      public ContextualStatementBuilder call(final Statement instance, final HasAnnotations annotated,
              final BuildMetaClass factory, final Statement... params) {
        final MetaField field = (MetaField) annotated;
        if (field.isPublic()) {
          if (field.isStatic()) {
            return loadStatic(field.getDeclaringClass(), field.getName());
          } else {
            return nestedCall(instance).loadField(field);
          }
        } else {
          final Object[] accessorParams = (field.isStatic() ? new Object[0] : new Object[] { instance });
          return invokeStatic(notNull(factory), getPrivateFieldAccessorName(field), accessorParams);
        }
      }
    },
    METHOD  {
      @Override
      public MetaClass getType(final HasAnnotations annotated) {
        return ((MetaMethod) annotated).getReturnType();
      }

      @Override
      public MetaClass getEnclosingType(final HasAnnotations annotated) {
        return ((MetaMethod) annotated).getDeclaringClass();
      }

      @Override
      public ContextualStatementBuilder getAccessStatement(final HasAnnotations annotated, final BuildMetaClass factory, final Statement[] statement) {
        return call(loadVariable("instance"), annotated, factory, statement);
      }

      @Override
      public ContextualStatementBuilder getAccessStatement(final HasAnnotations annotated, final BuildMetaClass factory) {
        return getAccessStatement(annotated, factory, new Statement[0]);
      }

      @Override
      public ContextualStatementBuilder call(final Statement instance, final HasAnnotations annotated, final BuildMetaClass factory,
              final Statement... statement) {
        final MetaMethod method = (MetaMethod) annotated;
        if (method.isPublic()) {
          if (method.isStatic()) {
            return invokeStatic(method.getDeclaringClass(), method.getName(), (Object[]) statement);
          } else {
            return nestedCall(instance).invoke(method, (Object[]) statement);
          }
        } else {
          final Object[] params = getParams(method.isStatic(), instance, statement);
          return invokeStatic(notNull(factory), getPrivateMethodName(method), params);
        }
      }

      private Object[] getParams(final boolean isStatic, final Statement instance, final Statement... statement) {
        final int offset = (isStatic ? 0 : 1);
        final Object[] params = new Object[statement.length+offset];
        if (!isStatic) {
          params[0] = instance;
        }

        for (int i = 0; i < statement.length; i++) {
          params[i+offset] = statement[i];
        }

        return params;
      }
    },
    PARAM {
      @Override
      public MetaClass getType(final HasAnnotations annotated) {
        return ((MetaParameter) annotated).getType();
      }

      @Override
      public MetaClass getEnclosingType(final HasAnnotations annotated) {
        return ((MetaParameter) annotated).getDeclaringMember().getDeclaringClass();
      }

      @Override
      public ContextualStatementBuilder getAccessStatement(final HasAnnotations annotated, final BuildMetaClass factory) {
        final MetaParameter param = (MetaParameter) annotated;
        return loadVariable(getLocalVariableName(param));
      }

      @Override
      public ContextualStatementBuilder call(final Statement instance, final HasAnnotations annotated,
              final BuildMetaClass factory, final Statement... params) {
        return METHOD.call(instance, ((MetaParameter) annotated).getDeclaringMember(), factory, params);
      }

      @Override
      public ContextualStatementBuilder call(final HasAnnotations annotated, final BuildMetaClass factory, final Statement... params) {
        return call(loadVariable("instance"), annotated, factory, params);
      }

      @Override
      public String getName(final HasAnnotations annotated) {
        return ((MetaParameter) annotated).getName();
      }
    },
    TYPE {
      @Override
      public MetaClass getType(final HasAnnotations annotated) {
        return ((MetaClass) annotated);
      }

      @Override
      public MetaClass getEnclosingType(final HasAnnotations annotated) {
        return ((MetaClass) annotated);
      }

      @Override
      public ContextualStatementBuilder getAccessStatement(final HasAnnotations annotated, final BuildMetaClass factory) {
        return loadVariable("instance");
      }

      @Override
      public String getName(final HasAnnotations annotated) {
        return ((MetaClass) annotated).getName();
      }

      @Override
      public ContextualStatementBuilder call(final Statement instance, final HasAnnotations annotated, final BuildMetaClass factory,
              final Statement... params) {
        return nestedCall(instance);
      }
    };

    public abstract MetaClass getType(HasAnnotations annotated);
    public abstract MetaClass getEnclosingType(HasAnnotations annotated);
    public ContextualStatementBuilder getAccessStatement(HasAnnotations annotated, final BuildMetaClass factory, final Statement[] params) {
      return getAccessStatement(annotated, factory);
    }
    public abstract ContextualStatementBuilder getAccessStatement(final HasAnnotations annotated, final BuildMetaClass factory);
    public abstract ContextualStatementBuilder call(final Statement instance, final HasAnnotations annotated, final BuildMetaClass factory, Statement... params);
    public ContextualStatementBuilder call(final HasAnnotations annotated, final BuildMetaClass factory, Statement... params) {
      return getAccessStatement(annotated, factory, params);
    }
    public String getName(HasAnnotations annotated) {
      return ((MetaClassMember) annotated).getName();
    }

    public static DecorableType fromElementType(ElementType elemType) {
      switch (elemType) {
      case FIELD:
        return DecorableType.FIELD;
      case METHOD:
        return DecorableType.METHOD;
      case PARAMETER:
        return DecorableType.PARAM;
      case TYPE:
        return DecorableType.TYPE;
      default:
        throw new RuntimeException("Unsupported element type " + elemType);
      }
    }
  }

  private final HasAnnotations annotated;
  private final Annotation annotation;
  private final DecorableType decorableType;
  private final InjectionContext injectionContext;
  private final Context context;
  private final BuildMetaClass factory;
  private final Injectable injectable;

  public Decorable(final HasAnnotations annotated, final Annotation annotation, final DecorableType decorableType,
          final InjectionContext injectionContext, final Context context, final BuildMetaClass factory, final Injectable injectable) {
    this.annotated = annotated;
    this.annotation = annotation;
    this.decorableType = decorableType;
    this.injectionContext = injectionContext;
    this.context = context;
    this.factory = factory;
    this.injectable = injectable;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Decorable)) {
      return false;
    }
    final Decorable other = (Decorable) obj;

    return decorableType.equals(other.decorableType) && CDIAnnotationUtils.equals(annotation, other.annotation)
            && annotated.equals(other.annotated) && injectable.equals(other.injectable);
  }

  @Override
  public int hashCode() {
    return decorableType.hashCode() ^ CDIAnnotationUtils.hashCode(annotation) ^ annotated.hashCode() ^ injectable.hashCode();
  }

  /**
   * @return The annotation relevant to the {@link CodeDecorator} for which this {@link Decorable} was created.
   */
  public Annotation getAnnotation() {
    return annotation;
  }

  /**
   * @return The declaring type of the decorable element. If the decorable element is a type, then this returns that type.
   */
  public MetaClass getDecorableDeclaringType() {
    return decorableType().getEnclosingType(annotated);
  }

  /**
   * @return The type of the decorable element. The type of a field or parameter, the return type of a method, the type itself for a type.
   */
  public MetaClass getType() {
    return decorableType().getType(annotated);
  }

  /**
   * For all but parameters, this access statement can be used in generated
   * implementations for
   * {@link Factory#createInstance(org.jboss.errai.ioc.client.container.ContextManager)}
   * and
   * {@link Factory#destroyInstance(Object, org.jboss.errai.ioc.client.container.ContextManager)}
   * . For parameters it is only valid in the former.
   *
   * @param params
   *          If this decorable is a method these parameters are used in the
   *          generated invocation. Otherwise they are ignored.
   * @return A reference to the decorated value. For fields or methods, the
   *         member is loaded/invoked. For types a reference to the constructed
   *         type. For parameters, a reference to a local variable of the value that was passed in to the parameter.
   */
  public Statement getAccessStatement(Statement... params) {
    return decorableType().getAccessStatement(annotated, factory, params);
  }

  /**
   * @return The annotated element of this decorable.
   */
  public HasAnnotations get() {
    return annotated;
  }

  /**
   * @return The context of the {@link ClassStructureBuilder} of the
   *         {@link Factory} being generated.
   */
  public Context getCodegenContext() {
    return context;
  }

  /**
   * @return The value of {@link #get()} cast as a field.
   */
  public MetaMethod getAsMethod() {
    return (MetaMethod) annotated;
  }

  /**
   * @return The kind of this decorable.
   */
  public DecorableType decorableType() {
    return decorableType;
  }

  /**
   * @return The shared injection context used to generate all factories.
   */
  public InjectionContext getInjectionContext() {
    return injectionContext;
  }

  /**
   * @return The value of {@link #get()} cast as a parameter.
   */
  public MetaParameter getAsParameter() {
    return (MetaParameter) annotated;
  }

  /**
   * @return The value of {@link #get()} cast as a field.
   */
  public MetaField getAsField() {
    return (MetaField) annotated;
  }

  /*
   * Behaves differently than getAccessStatement for parameters,
   * where the method is called.
   */
  public Statement call(final Statement... values) {
    return decorableType().call(annotated, factory, values);
  }

  /**
   * @return The name of the annotated element. Calls
   *         {@link MetaClassMember#getName()} for fields and methods. The
   *         source code name for parameter, and the simple class name for
   *         types.
   */
  public String getName() {
    return decorableType().getName(annotated);
  }

  /**
   * @return The injectable for the enclosing type.
   */
  public Injectable getEnclosingInjectable() {
    return injectable;
  }

  /**
   * @return The {@link BuildMetaClass} for the factory being generated.
   */
  public BuildMetaClass getFactoryMetaClass() {
    return factory;
  }

  /**
   * @return True iff the enclosing injectable is {@link Dependent} scoped.
   */
  public boolean isEnclosingTypeDependent() {
    return injectable.getWiringElementTypes().contains(WiringElementType.DependentBean);
  }

}
