/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.datamodel.backend.server.builder.projects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.MethodInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class ClassMethodInspectorTest {

    @Test
    public void testSimpleMethods() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector(SimpleMethods.class,
                                                                  new JavaTypeSystemTranslator());

        for (String s : ext.getMethodNames()) {
            assertFalse("Method " + s + " is not allowed.",
                        checkBlackList(s));
        }
        assertEquals(3,
                     ext.getMethodInfos().size());
        assertNotNull(getMethodInfo("addOrSimilar",
                                    ext.getMethodInfos()));
        assertNotNull(getMethodInfo("cleanOrSimilar",
                                    ext.getMethodInfos()));
        assertNotNull(getMethodInfo("methodThatReturnsIfItWasSuccesful",
                                    ext.getMethodInfos()));

        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     getMethodInfo("addOrSimilar",
                                   ext.getMethodInfos()).getParams().get(0));
    }

    @Test
    public void testInheritedPublicMethods() {
        final ClassMethodInspector ext = new ClassMethodInspector(ChildClass.class,
                                                                  new JavaTypeSystemTranslator());

        for (String s : ext.getMethodNames()) {
            assertFalse("Method " + s + " is not allowed.",
                        checkBlackList(s));
        }
        assertEquals(2,
                     ext.getMethodInfos().size());
        assertNotNull(getMethodInfo("aParentMethod",
                                    ext.getMethodInfos()));
        assertNotNull(getMethodInfo("aChildMethod",
                                    ext.getMethodInfos()));
    }

    private MethodInfo getMethodInfo(final String methodName,
                                     final List<MethodInfo> methodInfos) {
        for (MethodInfo methodInfo : methodInfos) {
            if (methodInfo.getName().equals(methodName)) {
                return methodInfo;
            }
        }
        return null;
    }

    @Test
    public void testMoreThanOneMethodWithTheSameName() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector(MoreThanOneMethodWithTheSameName.class,
                                                                  new JavaTypeSystemTranslator());

        for (String s : ext.getMethodNames()) {
            assertFalse("Method " + s + " is not allowed.",
                        checkBlackList(s));
        }
    }

    @Test
    public void testCollection() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector(Collection.class,
                                                                  new JavaTypeSystemTranslator());

        for (String s : ext.getMethodNames()) {
            assertFalse("Method " + s + " is not allowed.",
                        checkBlackList(s));
        }
    }

    @Test
    public void testArrayList() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector(ArrayList.class,
                                                                  new JavaTypeSystemTranslator());

        for (String s : ext.getMethodNames()) {
            assertFalse("Method " + s + " is not allowed.",
                        checkBlackList(s));
        }
    }

    @Test
    public void testList() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector(List.class,
                                                                  new JavaTypeSystemTranslator());

        for (String s : ext.getMethodNames()) {
            assertFalse("Method " + s + " is not allowed.",
                        checkBlackList(s));
        }
    }

    @Test
    public void testSet() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector(Set.class,
                                                                  new JavaTypeSystemTranslator());

        for (String s : ext.getMethodNames()) {
            assertFalse("Method " + s + " is not allowed.",
                        checkBlackList(s));
        }
    }

    @Test
    public void testMap() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector(Map.class,
                                                                  new JavaTypeSystemTranslator());

        for (String s : ext.getMethodNames()) {
            assertFalse("Method " + s + " is not allowed.",
                        checkBlackList(s));
        }
    }

    @Test
    public void testMyMap() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector(MyMap.class,
                                                                  new JavaTypeSystemTranslator());

        for (String s : ext.getMethodNames()) {
            assertFalse("Method " + s + " is not allowed.",
                        checkBlackList(s));
        }
    }

    @Test
    public void testMultiParameterSetter() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector(MultiParameterSetter.class,
                                                                  new JavaTypeSystemTranslator());

        for (String s : ext.getMethodNames()) {
            assertFalse("Method " + s + " is not allowed.",
                        checkBlackList(s));
        }
        assertEquals(1,
                     ext.getMethodNames().size());
    }

    @Test
    public void testGetterWithParameters() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector(MyMap.class,
                                                                  new JavaTypeSystemTranslator());

        for (String s : ext.getMethodNames()) {
            assertFalse("Method " + s + " is not allowed.",
                        checkBlackList(s));
        }
    }

    @Test
    public void testBooleanGetterWithParameters() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector(MyMap.class,
                                                                  new JavaTypeSystemTranslator());

        for (String s : ext.getMethodNames()) {
            assertFalse("Method " + s + " is not allowed.",
                        checkBlackList(s));
        }
    }

    private boolean checkBlackList(String methodName) {
        return ("hashCode".equals(methodName)
                || "equals".equals(methodName)
                || "addAll".equals(methodName)
                || "containsAll".equals(methodName)
                || "iterator".equals(methodName)
                || "removeAll".equals(methodName)
                || "retainAll".equals(methodName)
                || "toArray".equals(methodName)
                || "listIterator".equals(methodName)
                || "subList".equals(methodName)
                || "entrySet".equals(methodName)
                || "keySet".equals(methodName)
                || "putAll".equals(methodName));
    }

    public static class SimpleMethods {

        public void cleanOrSimilar() {

        }

        public void addOrSimilar(int i) {

        }

        public boolean methodThatReturnsIfItWasSuccesful() {
            return true;
        }
    }

    public static class MoreThanOneMethodWithTheSameName {

        public void justAMethod() {

        }

        public void justAMethod(int x) {

        }

        public void justAMethod(Object x) {

        }

        public void justAMethod(int x,
                                Object y) {

        }

        public Object justAMethod(int x,
                                  int y) {
            return null;
        }
    }

    public static class MyMap
            implements
            Map {

        public void magicMethod() {

        }

        public void clear() {
        }

        public boolean containsKey(Object arg0) {
            return false;
        }

        public boolean containsValue(Object arg0) {
            return false;
        }

        public Set entrySet() {
            return null;
        }

        public Object get(Object arg0) {
            return null;
        }

        public boolean isEmpty() {
            return false;
        }

        public Set keySet() {
            return null;
        }

        public Object put(Object arg0,
                          Object arg1) {
            return null;
        }

        public void putAll(Map arg0) {
        }

        public Object remove(Object arg0) {
            return null;
        }

        public int size() {
            return 0;
        }

        public Collection values() {
            return null;
        }
    }

    public static class MultiParameterSetter {

        public void setSomething(String a,
                                 String b) {

        }
    }

    public static class GetterWithParameters {

        public void getSomething(String a) {

        }
    }

    public static class BooleanGetterWithParameters {

        public void isSomething(String a) {

        }
    }

    public static class ParentClass {

        public void aParentMethod() {

        }
    }

    public static class ChildClass extends ParentClass {

        public void aChildMethod() {
        }
    }
}
