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


package org.jboss.errai.reflections.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.reflections.ReflectionUtils;
import org.jboss.errai.reflections.ReflectionsException;

/**
 * a garbage can of convenient methods
 */
public abstract class Utils {

    public static String repeat(String string, int times) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < times; i++) {
            sb.append(string);
        }

        return sb.toString();
    }

    /**
     * isEmpty compatible with Java 5
     */
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static File prepareFile(String filename) {
        File file = new File(filename);
        File parent = file.getAbsoluteFile().getParentFile();
        if (!parent.exists()) {
            //noinspection ResultOfMethodCallIgnored
            parent.mkdirs();
        }
        return file;
    }

    public static Method getMethodFromDescriptor(String descriptor) throws ReflectionsException {
        //todo create method md

        int p0 = descriptor.indexOf('(');
        String methodKey = descriptor.substring(0, p0);
        String methodParameters = descriptor.substring(p0 + 1, descriptor.length() - 1);

        int p1 = methodKey.lastIndexOf('.');
        String className = methodKey.substring(methodKey.lastIndexOf(' ') + 1, p1);
        String methodName = methodKey.substring(p1 + 1);

        Class<?>[] parameterTypes = null;
        if (!isEmpty(methodParameters)) {
            String[] parameterNames = methodParameters.split(", ");
            List<Class<?>> result = new ArrayList<Class<?>>(parameterNames.length);
            for (String className1 : parameterNames) {
                //noinspection unchecked
                result.add(ReflectionUtils.forName(className1));
            }
            parameterTypes = result.toArray(new Class<?>[result.size()]);
        }

        Class<?> aClass = ReflectionUtils.forName(className);
        try {
            if (descriptor.contains("<init>")) {
//                return aClass.getConstructor(parameterTypes);
                return null; //todo add support
            } else {
                return aClass.getDeclaredMethod(methodName, parameterTypes);
            }
        } catch (NoSuchMethodException e) {
            throw new ReflectionsException("Can't resolve method named " + methodName, e);
        }
    }

    public static Field getFieldFromString(String field) {
        //todo create field md
        String className = field.substring(0, field.lastIndexOf('.'));
        String fieldName = field.substring(field.lastIndexOf('.') + 1);

        try {
            return ReflectionUtils.forName(className).getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new ReflectionsException("Can't resolve field named " + fieldName, e);
        }
    }

    public static void close(InputStream inputStream) {
        try { if (inputStream != null) inputStream.close(); }
        catch (IOException e) { e.printStackTrace(); }
    }
}
