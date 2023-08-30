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


package org.jboss.errai.codegen.test;

import org.jboss.errai.codegen.ArithmeticExpression;
import org.jboss.errai.codegen.ArithmeticOperator;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.util.Arith;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArithmeticTest {

  @Test
  public void testWideningByteChar() throws Exception {
    ArithmeticExpression expr = Arith.expr((byte) 1, ArithmeticOperator.Addition, (char) 3);
    assertEquals(MetaClassFactory.get(int.class), expr.getType());
  }

  @Test
  public void testWideningCharByte() throws Exception {
    ArithmeticExpression expr = Arith.expr((char) 1, ArithmeticOperator.Addition, (byte) 3);
    assertEquals(MetaClassFactory.get(int.class), expr.getType());
  }

  @Test
  public void testWideningIntLong() throws Exception {
    ArithmeticExpression expr = Arith.expr(1, ArithmeticOperator.Addition, (long) 3);
    assertEquals(MetaClassFactory.get(long.class), expr.getType());
  }

  @Test
  public void testWideningLongInt() throws Exception {
    ArithmeticExpression expr = Arith.expr((long) 1, ArithmeticOperator.Addition, 3);
    assertEquals(MetaClassFactory.get(long.class), expr.getType());
  }

  @Test
  public void testDoubleDoubleMakesDouble() throws Exception {
    ArithmeticExpression expr = Arith.expr(1.0, ArithmeticOperator.Addition, 3.0);
    assertEquals(MetaClassFactory.get(double.class), expr.getType());
  }

  @Test
  public void testCharCharMakesInt() throws Exception {
    ArithmeticExpression expr = Arith.expr((char) 1, ArithmeticOperator.Addition, (char) 3);
    assertEquals(MetaClassFactory.get(int.class), expr.getType());
  }

}
