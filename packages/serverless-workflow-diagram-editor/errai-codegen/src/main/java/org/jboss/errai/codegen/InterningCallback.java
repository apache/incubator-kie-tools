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

import org.jboss.errai.codegen.literal.LiteralValue;

/**
 * An <tt>InternCallback</tt> can be registered with {@link Context#addInterningCallback(InterningCallback)}.
 * <p>
 * Some care should be taken in implementing a callback considering the recursive nature of code generation
 * within the framework. For instance, the following code will produce undesirable results:
 * {code}
 * new InterningCallback() {
 *   public Statement intern(LiteralValue&lt;?&gt; literalValue) {
 *     if (literalValue instanceof StringLiteral) {
 *        final String varName = "stringLiteral_" + literalValue.getValue().hashCode();
 *        getClassBuilder().publicField(varName, String.class)
 *           .initializesWith(literalValue.getValue());
 *
 *       return Refs.get(varName);
 *     }
 *     return null;
 *   }
 * }
 * {code}
 * On the surface, the above seems like a reasonable enough implementation. But there is a recursion problem
 * hidden in it. Because we initialize the field with the string value in the default manner, the value will
 * be obtained from the {@link org.jboss.errai.codegen.literal.LiteralFactory} in the regular manner, and the
 * interned value will reference itself!
 * <p>
 * You'll end up with code which looks like this:
 * {code}
 *     stringLiteral_382389210 = stringLiteral_382389210;
 * {code}
 * ... And it's fair to say the compiler will not like this.
 * <p>
 * Instead, you should create non-recursive constructs. We can fix the above code like so:
 * {code}
 * new InterningCallback() {
 *   public Statement intern(final LiteralValue&lt;?&gt; literalValue) {
 *     if (literalValue instanceof StringLiteral) {
 *        final String varName = "stringLiteral_" + literalValue.getValue().hashCode();
 *        getClassBuilder().publicField(varName, String.class)
 *           .initializesWith(
 *              new Statement() {
 *                 public String generate(Context context) {
 *                   return new StringLiteral(literalValue.getValue()).getCanonicalString(context);
 *                 }
 *
 *                 public MetaClass getType() {
 *                   return literalValue.getType();
 *                 }
 *              }
 *           );
 *
 *       return Refs.get(varName);
 *     }
 *     return null;
 *   }
 * }
 * {code}
 *
 * @author Mike Brock
 */
public interface InterningCallback {
  /**
   * Intern the supplied {@link LiteralValue}. This interface allows you to implement an interning strategy for
   * literal values within the code generator framework. For instance, having literalized annotations render
   * to a final field within a generated class with all subsequent references to matching annotations reference
   * that field.
   *
   * @param literalValue the literal value to intern.
   *
   * @return If this method returns a non-null reference,the generator will assume this value is interned and will
   *         use the returned {@link Statement} for this literal in all future code generation.
   */
  public Statement intern(LiteralValue<?> literalValue);
}
