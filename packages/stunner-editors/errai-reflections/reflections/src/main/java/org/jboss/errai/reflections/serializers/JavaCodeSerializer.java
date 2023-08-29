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


package org.jboss.errai.reflections.serializers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import org.jboss.errai.reflections.ReflectionUtils;
import org.jboss.errai.reflections.Reflections;
import org.jboss.errai.reflections.ReflectionsException;
import org.jboss.errai.reflections.scanners.TypeElementsScanner;
import org.jboss.errai.reflections.scanners.TypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.errai.reflections.util.Utils.prepareFile;
import static org.jboss.errai.reflections.util.Utils.repeat;

/** serialization of Reflections to java code
 * <p> serializes types and types elements into interfaces respectively to fully qualified name,
 * <p> for example:
 * <pre>
 * public interface MyTestModelStore {
 *	public interface <b>org</b> extends IPackage {
 *	    public interface <b>reflections</b> extends IPackage {
 *			public interface <b>TestModel$AC1</b> extends IClass {}
 *			public interface <b>TestModel$C4</b> extends IClass {
 *				public interface <b>f1</b> extends IField {}
 *				public interface <b>m1</b> extends IMethod {}
 *				public interface <b>m1_int_java$lang$String$$$$</b> extends IMethod {}
 *	...
 * }
 * </pre>
 * <p> use the different resolve methods to resolve the serialized element into Class, Field or Method. for example:
 * <pre>
 *  Class&#60? extends IMethod> imethod = MyTestModelStore.org.reflections.TestModel$C4.m1.class;
 *  Method method = JavaCodeSerializer.resolve(imethod);
 * </pre>
 * <p>depends on Reflections configured with {@link org.jboss.errai.reflections.scanners.TypesScanner} and {@link org.jboss.errai.reflections.scanners.TypeElementsScanner}
 * <p><p>the {@link #save(org.jboss.errai.reflections.Reflections, String)} method filename should be in the pattern: path/path/path/package.package.classname
 * */
public class JavaCodeSerializer implements Serializer {
    private static final Logger log = LoggerFactory.getLogger(JavaCodeSerializer.class);

    private static final char pathSeparator = '$';
    private static final String arrayDescriptor = "$$";
    private static final String tokenSeparator = "_"; 

    public static interface IElement {}
    public static interface IPackage extends IElement {}
    public static interface IClass extends IElement {}
    public static interface IField extends IElement {}
    public static interface IMethod extends IElement {}

    public Reflections read(InputStream inputStream) {
        throw new UnsupportedOperationException("read is not implemented on JavaCodeSerializer");
    }

    /**
     * name should be in the pattern: path/path/path/package.package.classname,
     * for example <pre>/data/projects/my/src/main/java/org.my.project.MyStore</pre>
     * would create class MyStore in package org.my.project in the path /data/projects/my/src/main/java
     */
    public File save(Reflections reflections, String name) {
        if (name.endsWith("/")) {
            name = name.substring(0, name.length() - 1); //trim / at the end
        }

        //prepare file
        String filename = name.replace('.', '/').concat(".java");
        File file = prepareFile(filename);

        //get package and class names
        String packageName;
        String className;
        int lastDot = name.lastIndexOf('.');
        if (lastDot == -1) {
            packageName = "";
            className = name.substring(name.lastIndexOf('/') + 1);
        } else {
            packageName = name.substring(name.lastIndexOf('/') + 1, lastDot);
            className = name.substring(lastDot + 1);
        }

        //generate
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("//generated using Reflections JavaCodeSerializer")
                    .append(" [").append(new Date()).append("]")
                    .append("\n");
            if (packageName.length() != 0) {
                sb.append("package ").append(packageName).append(";\n");
                sb.append("\n");
            }
            sb.append("import static org.jboss.errai.reflections.serializers.JavaCodeSerializer.*;\n");
            sb.append("\n");
            sb.append("public interface ").append(className).append(" extends IElement").append(" {\n\n");
            sb.append(toString(reflections));
            sb.append("}\n");

            FileWriter writer = new FileWriter(filename);
            writer.write(sb.toString());
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException();
        }

        return file;
    }

    public String toString(Reflections reflections) {
        if (reflections.getStore().get(TypesScanner.class).isEmpty() ||
                reflections.getStore().get(TypeElementsScanner.class).isEmpty()) {
            log.warn("JavaCodeSerializer needs TypeScanner and TypeElemenetsScanner configured");
        }

        StringBuilder sb = new StringBuilder();

        List<String> prevPaths = Lists.newArrayList();
        int indent = 1;

        List<String> keys = Lists.newArrayList(reflections.getStore().get(TypesScanner.class).keySet());
        Collections.sort(keys);
        for (String fqn : keys) {
            List<String> typePaths = Lists.newArrayList(fqn.split("\\."));

            //skip indention
            int i = 0;
            while (i < Math.min(typePaths.size(), prevPaths.size()) && typePaths.get(i).equals(prevPaths.get(i))) {
                i++;
            }

            //indent left
            for (int j = prevPaths.size(); j > i; j--) {
                sb.append(repeat("\t", --indent)).append("}\n");
            }

            //indent right - add packages
            for (int j = i; j < typePaths.size() - 1; j++) {
                sb.append(repeat("\t", indent++)).append("public interface ").append(getNonDuplicateName(typePaths.get(j), typePaths, j)).append(" extends IPackage").append(" {\n");
            }

            //indent right - add class
            String className = typePaths.get(typePaths.size() - 1);

            //get fields and methods
            List<String> fields = Lists.newArrayList();
            final Multimap<String,String> methods = Multimaps.newSetMultimap(new HashMap<String, Collection<String>>(), new Supplier<Set<String>>() {
                public Set<String> get() {
                    return Sets.newHashSet();
                }
            });

            for (String element : reflections.getStore().get(TypeElementsScanner.class, fqn)) {
                if (element.contains("(")) {
                    //method
                    if (!element.startsWith("<")) {
                        int i1 = element.indexOf('(');
                        String name = element.substring(0, i1);
                        String params = element.substring(i1 + 1, element.indexOf(")"));

                        String paramsDescriptor = "";
                        if (params.length() != 0) {
                            paramsDescriptor = tokenSeparator + params.replace('.', pathSeparator).replace(", ", tokenSeparator).replace("[]", arrayDescriptor);
                        }
                        String normalized = name + paramsDescriptor;
                        methods.put(name, normalized);
                    }
                } else {
                    //field
                    fields.add(element);
                }
            }

            //add class and it's fields and methods
            sb.append(repeat("\t", indent++)).append("public interface ").append(getNonDuplicateName(className, typePaths, typePaths.size() - 1)).append(" extends IClass").append(" {\n");

            //add fields
            if (!fields.isEmpty()) {
                for (String field : fields) {
                    sb.append(repeat("\t", indent)).append("public interface ").append(getNonDuplicateName(field, typePaths)).append(" extends IField").append(" {}\n");
                }
            }

            //add methods
            if (!methods.isEmpty()) {
                for (Map.Entry<String, String> entry : methods.entries()) {
                    String simpleName = entry.getKey();
                    String normalized = entry.getValue();

                    String methodName = methods.get(simpleName).size() == 1 ? simpleName : normalized;

                    methodName = getNonDuplicateName(methodName, fields); //because fields and methods are both inners of the type, they can't duplicate

                    sb.append(repeat("\t", indent)).append("public interface ").append(getNonDuplicateName(methodName, typePaths)).append(" extends IMethod").append(" {}\n");
                }
            }

            prevPaths = typePaths;
        }

        //close indention
        for (int j = prevPaths.size(); j >= 1; j--) {
            sb.append(repeat("\t", j)).append("}\n");
        }

        return sb.toString();
    }

    //inner interface with name equals to one of it's enclosing types would lead to duplicate class
    private String getNonDuplicateName(String candidate, List<String> prev, int offset) {
        for (int i = 0; i < offset; i++) {
            if (candidate.equals(prev.get(i))) {
                return getNonDuplicateName(candidate + tokenSeparator, prev, offset);
            }
        }

        return candidate;
    }

    private String getNonDuplicateName(String candidate, List<String> prev) {
        return getNonDuplicateName(candidate, prev, prev.size());
    }

    //
    public static Class<?> resolveClassOf(final Class<? extends IElement> element) throws ClassNotFoundException {
        Class<?> cursor = element;
        List<Class<? extends IElement>> path = Lists.newArrayList();

        while (cursor != null && IElement.class.isAssignableFrom(cursor)) {
            //noinspection unchecked
            path.add((Class<? extends IElement>) cursor);
            cursor = cursor.getDeclaringClass();
        }

        Collections.reverse(path);

        int i = 1; //first one is the store type

        List<String> ognl = Lists.newArrayList();
        while (i < path.size() &&
                (IPackage.class.isAssignableFrom(path.get(i)) || IClass.class.isAssignableFrom(path.get(i)))) {
            ognl.add(path.get(i).getSimpleName());
            i++;
        }

        String classOgnl = Joiner.on(".").join(ognl).replace(".$", "$");
        return Class.forName(classOgnl);
    }

    public static Class<?> resolveClass(final Class<? extends IClass> aClass) {
        try {
            return resolveClassOf(aClass);
        } catch (Exception e) {
            throw new ReflectionsException("could not resolve to class " + aClass.getName(), e);
        }
    }

    public static Field resolveField(final Class<? extends IField> aField) {
        try {
            String name = aField.getSimpleName();
            return resolveClassOf(aField).getDeclaredField(name);
        } catch (Exception e) {
            throw new ReflectionsException("could not resolve to field " + aField.getName(), e);
        }
    }

    public static Method resolveMethod(final Class<? extends IMethod> aMethod) {
        String methodOgnl = aMethod.getSimpleName();

        try {
            String methodName;
            Class<?>[] paramTypes;
            if (methodOgnl.contains(tokenSeparator)) {
                methodName = methodOgnl.substring(0, methodOgnl.indexOf(tokenSeparator));
                String[] params = methodOgnl.substring(methodOgnl.indexOf(tokenSeparator) + 1).split(tokenSeparator);
                paramTypes = new Class<?>[params.length];
                for (int i = 0; i < params.length; i++) {
                    String typeName = params[i].replace(arrayDescriptor, "[]").replace(pathSeparator, '.');
                    paramTypes[i] = ReflectionUtils.forName(typeName);
                }
            } else {
                methodName = methodOgnl;
                paramTypes = null;
            }

            return resolveClassOf(aMethod).getDeclaredMethod(methodName, paramTypes);
        } catch (Exception e) {
            throw new ReflectionsException("could not resolve to method " + aMethod.getName(), e);
        }
    }
}
