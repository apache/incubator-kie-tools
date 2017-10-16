/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.datamodeller.parser.util;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.soup.project.datamodel.commons.types.ClassTypeResolver;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverException;
import org.kie.workbench.common.services.datamodeller.parser.descr.ClassOrInterfaceTypeDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.FileDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.IdentifierWithTypeArgumentsDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ImportDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ModifierDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.TypeArgumentDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.TypeArgumentListDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.TypeDescr;

public class JavaDriverUtils {

    public static ClassTypeResolver createClassTypeResolver(FileDescr fileDescr, ClassLoader classLoader) {

        String packageName;
        Set<String> classImports = new HashSet<String>();

        List<ImportDescr> fileImports = fileDescr.getImports();
        if (fileImports != null) {
            for (ImportDescr importDescr : fileDescr.getImports()) {
                classImports.add(importDescr.getName(true));
            }
        }

        if (fileDescr.getPackageDescr() != null) {
            packageName = fileDescr.getPackageDescr().getPackageName();
        } else {
            packageName = null;
        }
        //add current package too, if not added, the class type resolver don't resolve current package classes.
        if (packageName != null && !"".equals(packageName)) {
            classImports.add(packageName + ".*");
        }

        return new ClassTypeResolver(classImports, classLoader);
    }

    public static boolean isPrimitiveType(TypeDescr typeDescr) {
        return typeDescr.isPrimitiveType();
    }

    public static boolean isSimpleClass(TypeDescr typeDescr) {
        if (!typeDescr.isClassOrInterfaceType()) {
            return false;
        }

        ClassOrInterfaceTypeDescr classOrInterfaceTypeDescr = typeDescr.getClassOrInterfaceType();
        List<IdentifierWithTypeArgumentsDescr> identifierWithTypeArgumentsList = classOrInterfaceTypeDescr.getIdentifierWithTypeArguments();

        if (identifierWithTypeArgumentsList == null || identifierWithTypeArgumentsList.size() == 0) {
            return false;
        }

        for (IdentifierWithTypeArgumentsDescr identifierWithTypeArguments : identifierWithTypeArgumentsList) {
            if (identifierWithTypeArguments.getArguments() != null) {
                return false;
            }
        }
        return true;
    }

    public static Object[] isSimpleGeneric(TypeDescr typeDescr) {
        Object[] result = new Object[3];
        result[0] = false;
        result[1] = null;
        result[2] = null;

        if (!typeDescr.isClassOrInterfaceType()) {
            return result;
        }

        ClassOrInterfaceTypeDescr classOrInterfaceTypeDescr = typeDescr.getClassOrInterfaceType();
        List<IdentifierWithTypeArgumentsDescr> identifierWithTypeArgumentsList = classOrInterfaceTypeDescr.getIdentifierWithTypeArguments();

        if (identifierWithTypeArgumentsList == null || identifierWithTypeArgumentsList.size() == 0) {
            return result;
        }

        int i = 0;
        StringBuilder outerClassName = new StringBuilder();
        for (IdentifierWithTypeArgumentsDescr identifierWithTypeArguments : identifierWithTypeArgumentsList) {
            i++;
            if (i > 1) {
                outerClassName.append(".");
            }
            outerClassName.append(identifierWithTypeArguments.getIdentifier().getIdentifier());

            if (identifierWithTypeArguments.getArguments() != null) {
                if (identifierWithTypeArgumentsList.size() > i) {
                    return result;
                }
                TypeArgumentListDescr typeArgumentList = identifierWithTypeArguments.getArguments();
                List<TypeArgumentDescr> typeArguments = typeArgumentList != null ? typeArgumentList.getArguments() : null;
                if (typeArguments == null || typeArguments.size() != 1) {
                    return result;
                }

                TypeDescr type;
                if ((type = typeArguments.get(0).getType()) != null && isSimpleClass(type)) {
                    result[0] = true;
                    result[1] = outerClassName.toString();
                    result[2] = type;
                    return result;
                }
            }
        }
        return result;
    }

    public static boolean isArray(TypeDescr typeDescr) {
        return typeDescr.getDimensionsCount() > 0;
    }

    /**
     * @return Return true if the given type can be managed by the driver, and subsequently by the UI.
     * <p/>
     * E.g. of managed types are:
     * int, Integer, java.lang.Integer, org.kie.SomeClass, List<Integer>, java.util.List<org.kie.SomeClass>
     * <p/>
     * e.g. of not manged types are:
     * int[], java.util.List<List<String>>, List<Map<String, org.kie.SomeClass>>
     */
    public static boolean isManagedType(TypeDescr typeDescr, ClassTypeResolver classTypeResolver) throws ModelDriverException {

        if (isArray(typeDescr)) {
            return false;
        }

        if (isPrimitiveType(typeDescr) || isSimpleClass(typeDescr)) {
            return true;
        }

        Object[] simpleGenerics = isSimpleGeneric(typeDescr);

        if (Boolean.FALSE.equals(simpleGenerics[0])) {
            return false;
        } else {
            //try to guess if we have something in the form Collection<SomeClass>
            String collectionCandidate = simpleGenerics[1].toString();
            try {
                Class collectionCandidateClass = classTypeResolver.resolveType(collectionCandidate);
                return Collection.class.isAssignableFrom(collectionCandidateClass);
            } catch (ClassNotFoundException e) {
                throw new ModelDriverException("Class could not be resolved for name: " + collectionCandidate + ". " + e.getMessage(), e);
            }
        }
    }

    public static boolean equalsType(TypeDescr type, String fullClassName, boolean multiple, String fullBagClassName, ClassTypeResolver classTypeResolver) throws ClassNotFoundException {

        String currentClassName;
        String currentBag;

        if (isArray(type)) {
            return false;
        }

        if (type.isPrimitiveType()) {
            return !multiple && fullClassName.equals(type.getPrimitiveType().getName());
        }

        if (isSimpleClass(type)) {
            currentClassName = classTypeResolver.getFullTypeName(type.getClassOrInterfaceType().getClassName());
            return !multiple && fullClassName.equals(currentClassName);
        }

        Object[] simpleGenerics = isSimpleGeneric(type);

        if (Boolean.TRUE.equals(simpleGenerics[0]) && multiple) {

            currentBag = (String) simpleGenerics[1];
            currentBag = classTypeResolver.getFullTypeName(currentBag);

            currentClassName = ((TypeDescr) simpleGenerics[2]).getClassOrInterfaceType().getClassName();
            currentClassName = classTypeResolver.getFullTypeName(currentClassName);

            return fullBagClassName.equals(currentBag) && fullClassName.equals(currentClassName);
        }

        return false;
    }

    public static int buildModifierRepresentation(List<ModifierDescr> modifiers) {
        int result = 0x0;
        if (modifiers != null) {
            for (ModifierDescr modifier : modifiers) {
                if ("public".equals(modifier.getName())) {
                    result = result | Modifier.PUBLIC;
                }
                if ("protected".equals(modifier.getName())) {
                    result = result | Modifier.PROTECTED;
                }
                if ("private".equals(modifier.getName())) {
                    result = result | Modifier.PRIVATE;
                }
                if ("abstract".equals(modifier.getName())) {
                    result = result | Modifier.ABSTRACT;
                }
                if ("static".equals(modifier.getName())) {
                    result = result | Modifier.STATIC;
                }
                if ("final".equals(modifier.getName())) {
                    result = result | Modifier.FINAL;
                }
                if ("transient".equals(modifier.getName())) {
                    result = result | Modifier.TRANSIENT;
                }
                if ("volatile".equals(modifier.getName())) {
                    result = result | Modifier.VOLATILE;
                }
                if ("synchronized".equals(modifier.getName())) {
                    result = result | Modifier.SYNCHRONIZED;
                }
                if ("native".equals(modifier.getName())) {
                    result = result | Modifier.NATIVE;
                }
                if ("strictfp".equals(modifier.getName())) {
                    result = result | Modifier.STRICT;
                }
                if ("interface".equals(modifier.getName())) {
                    result = result | Modifier.INTERFACE;
                }
            }
        }
        return result;
    }
}