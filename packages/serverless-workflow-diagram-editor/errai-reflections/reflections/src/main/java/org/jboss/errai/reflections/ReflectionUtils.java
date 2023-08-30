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


package org.jboss.errai.reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.jboss.errai.reflections.util.ClasspathHelper;

//todo add some ReflectionUtils stuff here
/** convenient reflection methods */
public abstract class ReflectionUtils {

    //primitive parallel arrays
    public final static List<String> primitiveNames = Lists.newArrayList("boolean", "char", "byte", "short", "int", "long", "float", "double", "void");
    @SuppressWarnings({"unchecked"}) public final static List<Class> primitiveTypes = Lists.<Class>newArrayList(boolean.class, char.class, byte.class, short.class, int.class, long.class, float.class, double.class, void.class);
    public final static List<String> primitiveDescriptors = Lists.newArrayList("Z", "C", "B", "S", "I", "J", "F", "D", "V");

    public static <T> Collection<? extends Class<?>> getAllSuperTypes(final Class<T> type) {
        Collection<Class<?>> result = Lists.newArrayList();

        Class<? super T> superclass = type.getSuperclass();
        Class<?>[] interfaces = type.getInterfaces();

        Collections.addAll(result, interfaces);
        result.add(superclass);

        result = Collections2.filter(result, Predicates.notNull());

        Collection<Class<?>> subResult = Lists.newArrayList();
        for (Class<?> aClass1 : result) {
            Collection<? extends Class<?>> classes = getAllSuperTypes(aClass1);
            subResult.addAll(classes);
        }

        result.addAll(subResult);
        return result;
    }

    /** return all super types of a given annotated element annotated with a given annotation up in hierarchy, including the given type */
    public static List<AnnotatedElement> getAllSuperTypesAnnotatedWith(final AnnotatedElement annotatedElement, final Annotation annotation) {
        final List<AnnotatedElement> annotated = Lists.newArrayList();

        if (annotatedElement != null) {
            if (annotatedElement.isAnnotationPresent(annotation.annotationType())) {
                annotated.add(annotatedElement);
            }

            if (annotatedElement instanceof Class<?>) {
                List<AnnotatedElement> subResult = Lists.newArrayList();
                Class<?> aClass = (Class<?>) annotatedElement;
                subResult.addAll(getAllSuperTypesAnnotatedWith(aClass.getSuperclass(), annotation));
                for (AnnotatedElement anInterface : aClass.getInterfaces()) {
                    subResult.addAll(getAllSuperTypesAnnotatedWith(anInterface, annotation));
                }
                annotated.addAll(subResult);
            }
        }

        return annotated;
    }

    /**
     * checks for annotation member values matching, based on equality of members
     */
    public static boolean areAnnotationMembersMatching(Annotation annotation1, Annotation annotation2) {
        if (annotation2 != null && annotation1.annotationType() == annotation2.annotationType()) {
            for (Method method : annotation1.annotationType().getDeclaredMethods()) {
                try {
                    if (!method.invoke(annotation1).equals(method.invoke(annotation2))) {
                        return false;
                    }
                } catch (Exception e) {
                    throw new ReflectionsException(String.format("could not invoke method %s on annotation %s", method.getName(), annotation1.annotationType()), e);
                }
            }
            return true;
        }

        return false;
    }

    /**
     * checks for annotation member values matching on an annotated element or it's first annotated super type, based on equality of members
     */
    protected static boolean areAnnotationMembersMatching(final Annotation annotation1, final AnnotatedElement annotatedElement) {
        List<AnnotatedElement> elementList = ReflectionUtils.getAllSuperTypesAnnotatedWith(annotatedElement, annotation1);

        if (!elementList.isEmpty()) {
            AnnotatedElement element = elementList.get(0);
            Annotation annotation2 = element.getAnnotation(annotation1.annotationType());

            return areAnnotationMembersMatching(annotation1, annotation2);
        }

        return false;
    }

    /**
     * returns a subset of given annotatedWith, where annotation member values matches the given annotation
     */
    protected static <T extends AnnotatedElement> Set<T> getMatchingAnnotations(final Set<T> annotatedElements, final Annotation annotation) {
        Set<T> result = Sets.newHashSet();

        for (T annotatedElement : annotatedElements) {
            if (areAnnotationMembersMatching(annotation, annotatedElement)) {
                result.add(annotatedElement);
            }
        }

        return result;
    }

    /** tries to resolve a java type name to a Class
     * <p>if optional {@link ClassLoader}s are not specified, then both {@link org.jboss.errai.reflections.util.ClasspathHelper#getContextClassLoader()} and {@link org.jboss.errai.reflections.util.ClasspathHelper#getStaticClassLoader()} are used
     * */
    public static Class<?> forName(String typeName, ClassLoader... classLoaders) {
        if (primitiveNames.contains(typeName)) {
            return primitiveTypes.get(primitiveNames.indexOf(typeName));
        } else {
            String type;
            if (typeName.contains("[")) {
                int i = typeName.indexOf("[");
                type = typeName.substring(0, i);
                String array = typeName.substring(i).replace("]", "");

                if (primitiveNames.contains(type)) {
                    type = primitiveDescriptors.get(primitiveNames.indexOf(type));
                } else {
                    type = "L" + type + ";";
                }

                type = array + type;
            } else {
                type = typeName;
            }

            for (ClassLoader classLoader : ClasspathHelper.classLoaders(classLoaders)) {
                try { return Class.forName(type, false, classLoader); }
                catch (ClassNotFoundException e) { /*continue*/ }
            }

            throw new IllegalArgumentException("Unable to load class \"" + type + "\"");
        }
    }

    /** try to resolve all given string representation of types to a list of java types */
    public static <T> Class<? extends T>[] forNames(final Iterable<String> classes, ClassLoader... classLoaders) {
        List<Class<? extends T>> result = new ArrayList<Class<? extends T>>();
        for (String className : classes) {
            //noinspection unchecked
            result.add((Class<? extends T>) forName(className, classLoaders));
        }
        return result.toArray(new Class[result.size()]);
    }
}
