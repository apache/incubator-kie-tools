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


package org.jboss.errai.codegen;

import javax.enterprise.util.TypeLiteral;

import org.jboss.errai.codegen.builder.impl.DeclareAssignmentBuilder;
import org.jboss.errai.codegen.exception.InvalidTypeException;
import org.jboss.errai.codegen.literal.LiteralFactory;
import org.jboss.errai.codegen.literal.LiteralValue;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.util.GenUtil;
import org.jboss.errai.codegen.util.Stmt;

/**
 * This class represents a variable.
 * <p>
 * Note that initialization using {@link LiteralValue}s takes effect immediately,
 * initialization using {@link Statement}s needs to be deferred to generation time.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class Variable extends AbstractStatement {
  private final String name;
  private MetaClass type;
  private Statement value;

  private Object initialization;

  private boolean isFinal;

  private Variable(final String name, final MetaClass type) {
    this.name = name;
    this.type = type;
  }

  private Variable(final String name, final MetaClass type, final Object initialization) {
    this(name, type);

    final LiteralValue<?> val = LiteralFactory.isLiteral(initialization);
    if (val != null) {
      this.type = (type == null) ? val.getType() : type;
      this.value = GenUtil.convert(Context.create(), initialization, this.type);
    }
    else {
      // deferred initialization
      this.initialization = initialization;
    }
  }

  public void initialize(final Object initializationValue) {
    this.initialization = initializationValue;
  }

  private MetaClass inferType(final Context context, final Object initialization) {
    final Statement initStatement = GenUtil.generate(context, initialization);
    final MetaClass inferredType = (initStatement != null) ? initStatement.getType() : null;
    if (inferredType == null) {
      throw new InvalidTypeException("No type specified and no initialization provided to infer the type.");
    }

    return inferredType;
  }

  /**
   * Creates a variable, but does not assign it to a scope. If you are trying to
   * declare a variable, see {@link Stmt#declareFinalVariable(String, Class)}.
   *
   * @param name
   *          The variable name
   * @param type
   *          The variable reference type
   * @return A newly created variable that is not (yet) referencable.
   */
  public static Variable createFinal(final String name, final Class<?> type) {
    return createFinal(name, MetaClassFactory.get(type));
  }

  /**
   * Creates a variable, but does not assign it to a scope. If you are trying to
   * declare a variable, see {@link Stmt#declareFinalVariable(String, TypeLiteral)}.
   *
   * @param name
   *          The variable name
   * @param type
   *          The variable reference type
   * @return A newly created variable that is not (yet) referencable.
   */
  public static Variable createFinal(final String name, final TypeLiteral<?> type) {
    final Variable variable = create(name, type);
    variable.isFinal = true;
    return variable;
  }

  /**
   * Creates a variable, but does not assign it to a scope. If you are trying to
   * declare a variable, see {@link Stmt#declareFinalVariable(String, MetaClass)}.
   *
   * @param name
   *          The variable name
   * @param type
   *          The variable reference type
   * @return A newly created variable that is not (yet) referencable.
   */
  public static Variable createFinal(final String name, final MetaClass type) {
    final Variable variable = create(name, type);
    variable.isFinal = true;
    return variable;
  }

  /**
   * Creates a variable, but does not assign it to a scope. If you are trying to
   * declare a variable, see {@link Stmt#declareFinalVariable(String, Class, Object)}.
   *
   * @param name
   *          The variable name
   * @param type
   *          The variable reference type
   * @return A newly created variable that is not (yet) referencable.
   */
  public static Variable createFinal(final String name, final Class<?> type, final Object initialization) {
    return createFinal(name, MetaClassFactory.get(type), initialization);
  }

  /**
   * Creates a variable, but does not assign it to a scope. If you are trying to
   * declare a variable, see {@link Stmt#declareFinalVariable(String, MetaClass, Object)}.
   *
   * @param name
   *          The variable name
   * @param type
   *          The variable reference type
   * @return A newly created variable that is not (yet) referencable.
   */
  public static Variable createFinal(final String name, final MetaClass type, final Object initialization) {
    final Variable variable = create(name, type, initialization);
    variable.isFinal = true;
    return variable;
  }

  /**
   * Creates a variable, but does not assign it to a scope. If you are trying to
   * declare a variable, see {@link Stmt#declareFinalVariable(String, TypeLiteral, Object)}.
   *
   * @param name
   *          The variable name
   * @param type
   *          The variable reference type
   * @return A newly created variable that is not (yet) referencable.
   */
  public static Variable createFinal(final String name, final TypeLiteral<?> type, final Object initialization) {
    final Variable variable = create(name, type, initialization);
    variable.isFinal = true;
    return variable;
  }

  /**
   * Creates a variable, but does not assign it to a scope. If you are trying to
   * declare a variable, see {@link Stmt#declareVariable(String, Object)}.
   *
   * @param name
   *          The variable name
   * @param type
   *          The variable reference type
   * @return A newly created variable that is not (yet) referencable.
   */
  public static Variable create(final String name, final Object initialization) {
    return new Variable(name, null, initialization);
  }

  public static Variable from(final VariableReference ref) {
    return new Variable(ref.getName(), ref.getType());
  }

  /**
   * Creates a variable, but does not assign it to a scope. If you are trying to
   * declare a variable, see {@link Stmt#declareVariable(String, Class)}.
   *
   * @param name
   *          The variable name
   * @param type
   *          The variable reference type
   * @return A newly created variable that is not (yet) referencable.
   */
  public static Variable create(final String name, final Class<?> type) {
    return new Variable(name, MetaClassFactory.get(type));
  }

  /**
   * Creates a variable, but does not assign it to a scope. If you are trying to
   * declare a variable, see {@link Stmt#declareVariable(String, TypeLiteral)}.
   *
   * @param name
   *          The variable name
   * @param type
   *          The variable reference type
   * @return A newly created variable that is not (yet) referencable.
   */
  public static Variable create(final String name, final TypeLiteral<?> type) {
    return new Variable(name, MetaClassFactory.get(type));
  }

  /**
   * Creates a variable, but does not assign it to a scope. If you are trying to
   * declare a variable, see {@link Stmt#declareVariable(String, MetaClass)}.
   *
   * @param name
   *          The variable name
   * @param type
   *          The variable reference type
   * @return A newly created variable that is not (yet) referencable.
   */
  public static Variable create(final String name, final MetaClass type) {
    return new Variable(name, type);
  }

  /**
   * Creates a variable, but does not assign it to a scope. If you are trying to
   * declare a variable, see {@link Stmt#declareVariable(String, Class, Object)}.
   *
   * @param name
   *          The variable name
   * @param type
   *          The variable reference type
   * @return A newly created variable that is not (yet) referencable.
   */
  public static Variable create(final String name, final Class<?> type, final Object initialization) {
    return new Variable(name, MetaClassFactory.get(type), initialization);
  }

  /**
   * Creates a variable, but does not assign it to a scope. If you are trying to
   * declare a variable, see {@link Stmt#declareVariable(String, TypeLiteral, Object)}.
   *
   * @param name
   *          The variable name
   * @param type
   *          The variable reference type
   * @return A newly created variable that is not (yet) referencable.
   */
  public static Variable create(final String name, final TypeLiteral<?> type, final Object initialization) {
    return new Variable(name, MetaClassFactory.get(type), initialization);
  }

  /**
   * Creates a variable, but does not assign it to a scope. If you are trying to
   * declare a variable, see {@link Stmt#declareVariable(String, MetaClass, Object)}.
   *
   * @param name
   *          The variable name
   * @param type
   *          The variable reference type
   * @return A newly created variable that is not (yet) referencable.
   */
  public static Variable create(final String name, final MetaClass type, final Object initialization) {
    return new Variable(name, type, initialization);
  }

  public static VariableReference get(final String name) {
    return new VariableReference() {
      private MetaClass type;

      @Override
      public String getName() {
        return name;
      }

      @Override
      public Statement getValue() {
        return new Statement() {
          @Override
          public String generate(Context context) {
            return name;
          }

          @Override
          public MetaClass getType() {
            return type;
          }
        };
      }

      @Override
      public String generate(Context context) {
        type = context.getVariable(name).getType();
        return name;
      }

      @Override
      public MetaClass getType() {
        return type;
      }

      @Override
      public String toString() {
        return name;
      }
    };
  }

  public VariableReference getReference() {
    return new VariableReference() {
      @Override
      public String getName() {
        return name;
      }

      @Override
      public MetaClass getType() {
        return type;
      }

      @Override
      public Statement getValue() {
        return value;
      }

      @Override
      public String toString() {
        return name;
      }
    };
  }

  public String getName() {
    return name;
  }

  @Override
  public MetaClass getType() {
    return type;
  }

  public Statement getValue() {
    return value;
  }

  public boolean isFinal() {
    return isFinal;
  }

  private String hashString;

  private String hashString() {
    if (hashString == null) {
      hashString = Variable.class.getName() + ":" + name + ":" + type.getFullyQualifiedName();
    }
    return hashString;
  }

  @Override
  public boolean equals(final Object o) {
    return o instanceof Variable
            && hashString().equals(Variable.class.getName() + ":" + name + ":" + ((Variable) o).type.getFullyQualifiedName());
  }

  @Override
  public int hashCode() {
    return hashString().hashCode();
  }

  @Override
  public String toString() {
    return "Variable [name=" + name + ", type=" + type + "]";
  }

  String generatedCache;

  @Override
  public String generate(final Context context) {
    if (generatedCache != null) return generatedCache;

    if (initialization != null) {
      this.type = (type == null) ? inferType(context, initialization) : type;
      this.value = GenUtil.convert(context, initialization, type);
    }

    return generatedCache = new DeclareAssignmentBuilder(isFinal, getReference(), value).generate(context);
  }
}
