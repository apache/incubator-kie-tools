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


package org.jboss.errai.reflections.adapters;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;

import static javassist.bytecode.AccessFlag.isPrivate;
import static javassist.bytecode.AccessFlag.isProtected;
import static javassist.bytecode.AccessFlag.isPublic;

/**
 *
 */
public class JavassistAdapter implements MetadataAdapter<ClassFile, FieldInfo, MethodInfo> {
    public List<FieldInfo> getFields(final ClassFile cls) {
        //noinspection unchecked
        return cls.getFields();
    }

    public List<MethodInfo> getMethods(final ClassFile cls) {
        //noinspection unchecked
        return cls.getMethods();
    }

    public String getMethodName(final MethodInfo method) {
        return method.getName();
    }

    public List<String> getParameterNames(final MethodInfo method) {
        String descriptor = method.getDescriptor();
        descriptor = descriptor.substring(descriptor.indexOf("(") + 1, descriptor.lastIndexOf(")"));
        return splitDescriptorToTypeNames(descriptor);
    }

    public List<String> getClassAnnotationNames(final ClassFile aClass) {
        AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) aClass.getAttribute(AnnotationsAttribute.visibleTag);
        return getAnnotationNames(annotationsAttribute);
    }

    public List<String> getFieldAnnotationNames(final FieldInfo field) {
        AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) field.getAttribute(AnnotationsAttribute.visibleTag);

        return getAnnotationNames(annotationsAttribute);
    }

    public List<String> getMethodAnnotationNames(final MethodInfo method) {
        AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) method.getAttribute(AnnotationsAttribute.visibleTag);

        return getAnnotationNames(annotationsAttribute);
    }

    public List<String> getParameterAnnotationNames(final MethodInfo method, final int parameterIndex) {
        ParameterAnnotationsAttribute parameterAnnotationsAttribute = (ParameterAnnotationsAttribute) method.getAttribute(ParameterAnnotationsAttribute.visibleTag);

        if (parameterAnnotationsAttribute != null) {
            Annotation[][] annotations = parameterAnnotationsAttribute.getAnnotations();
            if (parameterIndex < annotations.length) {
                Annotation[] annotation = annotations[parameterIndex];
                return getAnnotationNames(annotation);
            }
        }

        return new ArrayList<String>();
    }

    public String getReturnTypeName(final MethodInfo method) {
        String descriptor = method.getDescriptor();
        descriptor = descriptor.substring(descriptor.lastIndexOf(")") + 1);
        return splitDescriptorToTypeNames(descriptor).get(0);
    }

    public String getFieldName(final FieldInfo field) {
        return field.getName();
    }

    public ClassFile createClassObject(InputStream inputStream) throws IOException {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(inputStream))) {
            return new ClassFile(dis);
        }
    }

    public String getMethodModifier(MethodInfo method) {
        int accessFlags = method.getAccessFlags();
        return isPrivate(accessFlags) ? "private" :
               isProtected(accessFlags) ? "protected" :
               isPublic(accessFlags) ? "public" : "";
    }

    public String getMethodKey(ClassFile cls, MethodInfo method) {
        return getMethodName(method) + "(" + Joiner.on(", ").join(getParameterNames(method)) + ")";
    }

    public String getMethodFullKey(ClassFile cls, MethodInfo method) {
        return getClassName(cls) + "." + getMethodKey(cls, method);
    }

    //
    public String getClassName(final ClassFile cls) {
        return cls.getName();
    }

    public String getSuperclassName(final ClassFile cls) {
        return cls.getSuperclass();
    }

    public List<String> getInterfacesNames(final ClassFile cls) {
        return Arrays.asList(cls.getInterfaces());
    }

    //
    private List<String> getAnnotationNames(final AnnotationsAttribute annotationsAttribute) {
        if (annotationsAttribute == null) {return new ArrayList<String>(0);}

        final Annotation[] annotations = annotationsAttribute.getAnnotations();
        return getAnnotationNames(annotations);
    }

    private List<String> getAnnotationNames(final Annotation[] annotations) {
        List<String> result = Lists.newArrayList();

        for (Annotation annotation : annotations) {
            result.add(annotation.getTypeName());
        }

        return result;
    }

    private List<String> splitDescriptorToTypeNames(final String descriptors) {
        List<String> result = Lists.newArrayList();

        if (descriptors != null && descriptors.length() != 0) {

            List<Integer> indices = Lists.newArrayList();
            Descriptor.Iterator iterator = new Descriptor.Iterator(descriptors);
            while (iterator.hasNext()) {
                indices.add(iterator.next());
            }
            indices.add(descriptors.length());

            for (int i = 0; i < indices.size() - 1; i++) {
                String s1 = Descriptor.toString(descriptors.substring(indices.get(i), indices.get(i + 1)));
                result.add(s1);
            }

        }

        return result;
    }
}
