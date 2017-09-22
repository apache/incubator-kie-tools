/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodeller.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.workbench.common.services.datamodeller.core.Import;
import org.kie.workbench.common.services.datamodeller.core.JavaClass;
import org.kie.workbench.common.services.datamodeller.core.JavaTypeKind;
import org.kie.workbench.common.services.datamodeller.core.Method;
import org.kie.workbench.common.services.datamodeller.core.Visibility;

public class JavaClassImpl extends AbstractJavaType implements JavaClass {

    private String superClassName;

    private List<String> interfaces = new ArrayList<String>();

    private List<JavaClass> nestedClasses = new ArrayList<>();

    private List<Method> methods = new ArrayList<>();

    private List<Import> imports = new ArrayList<>();

    boolean _static = false;

    boolean _final = false;

    boolean _abstract = false;

    public JavaClassImpl() {
        //errai marshalling
    }

    public JavaClassImpl(String packageName,
                         String name) {
        this(packageName,
             name,
             Visibility.PUBLIC);
    }

    public JavaClassImpl(String packageName,
                         String name,
                         Visibility visibility) {
        super(packageName,
              name,
              JavaTypeKind.CLASS,
              visibility);
    }

    public JavaClassImpl(String packageName,
                         String name,
                         Visibility visibility,
                         boolean isAbstract,
                         boolean isFinal) {
        this(packageName,
             name,
             visibility);
        this._abstract = isAbstract;
        this._final = isFinal;
    }

    @Override
    public boolean isAbstract() {
        return _abstract;
    }

    @Override
    public boolean isFinal() {
        return _final;
    }

    @Override
    public boolean isStatic() {
        return _static;
    }

    @Override
    public List<JavaClass> getNestedClasses() {
        return nestedClasses;
    }

    @Override
    public JavaClass addNestedClass(JavaClass javaClass) {
        if (javaClass == null) {
            return null;
        }
        Iterator<JavaClass> iterator = nestedClasses.listIterator();
        while (iterator.hasNext()) {
            JavaClass nestedClass = iterator.next();
            if (nestedClass.getName().equals(javaClass.getName())) {
                iterator.remove();
                break;
            }
        }
        nestedClasses.add(javaClass);
        return javaClass;
    }

    @Override
    public JavaClass removeNestedClass(JavaClass javaClass) {
        boolean removed = nestedClasses.remove(javaClass);
        return removed ? javaClass : null;
    }

    @Override
    public List<Method> getMethods() {
        return methods;
    }

    @Override
    public Method addMethod(Method method) {
        Iterator<Method> iterator = methods.listIterator();
        while (iterator.hasNext()) {
            Method existingMethod = iterator.next();
            if (existingMethod.getName().equals(method.getName()) && existingMethod.getParameters().equals(method.getParameters())) {
                iterator.remove();
                break;
            }
        }
        methods.add(method);

        return method;
    }

    @Override
    public Method getMethod(String name,
                            List<String> parameterTypes) {
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                if (method.getParameters() == null && parameterTypes == null) {
                    return method;
                } else if (method.getParameters().stream().map(p -> p.getType().getName()).collect(Collectors.toList()).equals(parameterTypes)) {
                    return method;
                }
            }
        }
        return null;
    }

    @Override
    public Method removeMethod(Method method) {
        return methods.remove(method) ? method : null;
    }

    @Override
    public String getSuperClassName() {
        return superClassName;
    }

    public void setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
    }

    @Override
    public final boolean isClass() {
        return true;
    }

    @Override
    public List<String> getInterfaces() {
        return interfaces;
    }

    @Override
    public void addInterface(String interfaceDefinition) {
        interfaces.add(interfaceDefinition);
    }

    @Override
    public String removeInterface(String interfaceDefinition) {
        return interfaces.remove(interfaceDefinition) ? interfaceDefinition : null;
    }

    @Override
    public void addImport(Import _import) {
        imports.add(_import);
    }

    @Override
    public List<Import> getImports() {
        return imports;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        JavaClassImpl javaClass = (JavaClassImpl) o;

        if (_static != javaClass._static) {
            return false;
        }
        if (_final != javaClass._final) {
            return false;
        }
        if (_abstract != javaClass._abstract) {
            return false;
        }
        if (superClassName != null ? !superClassName.equals(javaClass.superClassName) : javaClass.superClassName != null) {
            return false;
        }
        if (interfaces != null ? !interfaces.equals(javaClass.interfaces) : javaClass.interfaces != null) {
            return false;
        }
        if (nestedClasses != null ? !nestedClasses.equals(javaClass.nestedClasses) : javaClass.nestedClasses != null) {
            return false;
        }
        return !(methods != null ? !methods.equals(javaClass.methods) : javaClass.methods != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + (superClassName != null ? superClassName.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (interfaces != null ? interfaces.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (nestedClasses != null ? nestedClasses.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (methods != null ? methods.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (_static ? 1 : 0);
        result = ~~result;
        result = 31 * result + (_final ? 1 : 0);
        result = ~~result;
        result = 31 * result + (_abstract ? 1 : 0);
        result = ~~result;
        return result;
    }
}
