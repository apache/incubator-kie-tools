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

import java.util.Arrays;

import javax.enterprise.util.TypeLiteral;

import org.jboss.errai.codegen.CallParameters;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.Variable;
import org.jboss.errai.codegen.builder.AnonymousClassStructureBuilder;
import org.jboss.errai.codegen.builder.BuildCallback;
import org.jboss.errai.codegen.builder.StatementEnd;
import org.jboss.errai.codegen.builder.callstack.DeferredCallElement;
import org.jboss.errai.codegen.builder.callstack.LoadClassReference;
import org.jboss.errai.codegen.exception.InvalidTypeException;
import org.jboss.errai.codegen.exception.UndefinedConstructorException;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.util.GenUtil;

import static org.jboss.errai.codegen.CallParameters.fromStatements;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class ObjectBuilder extends AbstractStatementBuilder {

  private final MetaClass type;
  private Object[] parameters;

  private Statement extendsBlock;

  private final RuntimeException blame = new RuntimeException("Problem was caused by this call");

  ObjectBuilder(final MetaClass type, final Context context, final CallElementBuilder callElementBuilder) {
    super(context, callElementBuilder);

    if (context != null) {
      context.attachClass(type);

      Arrays.stream(type.getDeclaredFields()).        
        forEach(f -> context.addVariable(Variable.create(f.getName(), f.getType()))); 
    }

    this.type = type;
  }

  ObjectBuilder(final MetaClass type, final Context context) {
    this(type, context, new CallElementBuilder());
  }

  ObjectBuilder(final MetaClass type) {
    this(type, Context.create(), new CallElementBuilder());
  }

  public static ObjectBuilder newInstanceOf(final MetaClass type) {
    return new ObjectBuilder(type);
  }

  public static ObjectBuilder newInstanceOf(final Class<?> type) {
    return newInstanceOf(MetaClassFactory.get(type));
  }

  public static ObjectBuilder newInstanceOf(final TypeLiteral<?> type) {
    return newInstanceOf(MetaClassFactory.get(type));
  }

  public static ObjectBuilder newInstanceOf(final MetaClass type, final Context context) {
    return new ObjectBuilder(type, context);
  }

  public static ObjectBuilder newInstanceOf(final Class<?> type, final Context context) {
    return newInstanceOf(MetaClassFactory.get(type), context);
  }

  public static ObjectBuilder newInstanceOf(final TypeLiteral<?> type, final Context context) {
    return newInstanceOf(MetaClassFactory.get(type), context);
  }

  public static ObjectBuilder newInstanceOf(final MetaClass type, final Context context, final CallElementBuilder callElementBuilder) {
    return new ObjectBuilder(type, context, callElementBuilder);
  }

  public static ObjectBuilder newInstanceOf(final Class<?> type, final Context context, final CallElementBuilder callElementBuilder) {
    return newInstanceOf(MetaClassFactory.get(type), context, callElementBuilder);
  }

  public static ObjectBuilder newInstanceOf(final TypeLiteral<?> type, final Context context, final CallElementBuilder callElementBuilder) {
    return newInstanceOf(MetaClassFactory.get(type), context, callElementBuilder);
  }

  public StatementEnd withParameters(final Object... parameters) {
    this.parameters = parameters;
    return this;
  }

  public AnonymousClassStructureBuilder extend() {
    return new AnonymousClassStructureBuilderImpl(type, new BuildCallback<ObjectBuilder>() {
      @Override
      public ObjectBuilder callback(final Statement statement) {
        extendsBlock = statement;
        return ObjectBuilder.this;
      }

      @Override
      public Context getParentContext() {
        return context;
      }
    });
  }

  public AnonymousClassStructureBuilder extend(final Object... parameters) {
    this.parameters = parameters;
    return extend();
  }

  @Override
  public MetaClass getType() {
    return type;
  }

  @Override
  public String generate(final Context context) {

    if (!generated) {
      appendCallElement(new DeferredCallElement((writer, context1, statement) -> {
        if (extendsBlock == null && (type.isAbstract() || type.isInterface() || type.isPrimitive()))
          throw new InvalidTypeException("Cannot instantiate type:" + type, blame);

        writer.reset();

        final CallParameters callParameters = (parameters != null) ?
            fromStatements(GenUtil.generateCallParameters(context1, parameters)) : CallParameters.none();

        if (!type.isInterface() && type.getBestMatchingConstructor(callParameters.getParameterTypes()) == null) {
          if (context1.isPermissiveMode()) {
            // fall-through
          }
          else {
            throw new UndefinedConstructorException(type, blame, callParameters.getParameterTypes());
          }
        }

        final StringBuilder buf = new StringBuilder();
        buf.append("new ").append(LoadClassReference.getClassReference(type, context1, true));
        if (callParameters != null) {
          buf.append(callParameters.generate(Context.create(context1)));
        }
        if (extendsBlock != null) {
          Arrays.stream(type.getDeclaredFields())
                  .forEach(field -> context1.addVariable(Variable.create(field.getName(), field.getType())));
          buf.append(" {\n").append(extendsBlock.generate(context1)).append("\n}\n");
        }
        writer.append(buf.toString());
      }));
    }

    try {
      return super.generate(context);
    }
    catch (final Throwable t) {
      GenUtil.throwIfUnhandled("while instantiating class: " + type.getFullyQualifiedName(), t);
      return null;
    }
  }
}
