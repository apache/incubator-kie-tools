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


package org.kie.workbench.common.stunner.core.util;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClassUtilsTest {

    private ClassUtils classUtils;

    @Before
    public void setUp() throws Exception {
        classUtils = new ClassUtils();
    }

    @Test
    public void isPrimitiveClass() throws Exception {
        assertTrue(classUtils.isPrimitiveClass(Boolean.class));
        assertTrue(classUtils.isPrimitiveClass(Byte.class));
        assertTrue(classUtils.isPrimitiveClass(Character.class));
        assertTrue(classUtils.isPrimitiveClass(Short.class));
        assertTrue(classUtils.isPrimitiveClass(Integer.class));
        assertTrue(classUtils.isPrimitiveClass(Long.class));
        assertTrue(classUtils.isPrimitiveClass(Double.class));
        assertTrue(classUtils.isPrimitiveClass(Float.class));
        assertTrue(classUtils.isPrimitiveClass(Void.class));

        assertFalse(classUtils.isPrimitiveClass(String.class));
        assertFalse(classUtils.isPrimitiveClass(BigInteger.class));
        assertFalse(classUtils.isPrimitiveClass(this.getClass()));
    }

    @Test
    public void TestIsJavaRuntimeClass() {
        assertTrue(ClassUtils.isJavaRuntimeClassname("java.lang.Object"));
        assertTrue(ClassUtils.isJavaRuntimeClassname("java.util.HashMap"));
        assertTrue(ClassUtils.isJavaRuntimeClassname("javax.bind"));
        assertFalse(ClassUtils.isJavaRuntimeClassname("org.kie"));
        assertFalse(ClassUtils.isJavaRuntimeClassname("com.google"));
    }
}