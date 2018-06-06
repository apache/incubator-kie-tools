/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.screens.datamodeller.backend.server.indexing;

import java.util.List;

import org.jboss.forge.roaster.model.Type;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.EnumConstantSource.Body;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.Import;
import org.jboss.forge.roaster.model.source.JavaAnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaEnumSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.source.ParameterSource;
import org.kie.soup.project.datamodel.commons.types.ClassTypeResolver;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;
import org.kie.workbench.common.services.refactoring.Resource;
import org.kie.workbench.common.services.refactoring.backend.server.impact.ResourceReferenceCollector;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public class JavaSourceVisitor extends ResourceReferenceCollector {

    private static final Logger logger = LoggerFactory.getLogger(JavaSourceVisitor.class);

    private final JavaSource javaSource;
    private final ClassTypeResolver classTypeResolver;
    private final Resource resParts;

    public JavaSourceVisitor(JavaSource javaSource, ClassLoader classLoader, Resource resParts) {
        this.javaSource = javaSource;
        this.classTypeResolver = DriverUtils.createClassTypeResolver(javaSource, classLoader);
        this.resParts = resParts;
    }

    public void visit(JavaSource javaSource) {

        // Imports
        List<Import> imports = javaSource.getImports();
        for (Import javaImport : imports) {
            visit(javaImport);
        }

        if (javaSource instanceof Body) {
            visit((Body) javaSource);
        } else if (javaSource instanceof JavaAnnotationSource) {
            visit((JavaAnnotationSource) javaSource);
        } else if (javaSource instanceof JavaClassSource) {
            visit((JavaClassSource) javaSource);
        } else if (javaSource instanceof JavaEnumSource) {
            visit((JavaEnumSource) javaSource);
        } else if (javaSource instanceof JavaInterfaceSource) {
            visit((JavaInterfaceSource) javaSource);
        }
    }

    public void visit(AnnotationSource<? extends JavaClassSource> annoSource) {
        addJavaResourceReference(annoSource.getQualifiedName());
    }

    public void visit(Body body) {
        for (AnnotationSource annoSource : body.getAnnotations()) {
            visit(annoSource);
        }
        for (FieldSource fieldSource : body.getFields()) {
            visit(fieldSource);
        }
        for (MethodSource methodSource : body.getMethods()) {
            visit(methodSource);
        }
    }

    public void visit(FieldSource<? extends JavaSource> fieldSource) {
        Type fieldType = fieldSource.getType();
        String fieldClassName;

        // the javadoc for Named.getName() is misleading:
        // the FieldSource.getName() (which is implemented by FieldImpl.getName())
        // returns the (fully-qualified!) name of the field
        String fieldName = fieldSource.getName();
        resParts.addPart(fieldName, PartType.FIELD);

        try {
            if (DriverUtils.isManagedType(fieldType, classTypeResolver)) {
                if (fieldType.isPrimitive()) {
                    fieldClassName = fieldType.getName();
                } else if (DriverUtils.isSimpleClass(fieldType)) {
                    fieldClassName = classTypeResolver.getFullTypeName(fieldType.getName());
                } else {
                    //if this point was reached, we know it's a Collection.
                    // Managed type check was done previously.
                    Type elementsType = ((List<Type>) fieldType.getTypeArguments()).get(0);
                    fieldClassName = classTypeResolver.getFullTypeName(elementsType.getName());
                }
            } else {
                // mriet: not complete sure why we don't just do this instead of using DriverUtils?
                fieldClassName = fieldType.getQualifiedName();
            }
            addJavaResourceReference(fieldClassName);
        } catch (Exception e) {
            logger.error("Unable to index java class field for class: "
                                 + javaSource.getQualifiedName()
                                 + ", fieldName: " + fieldName
                                 + ", fieldType: " + fieldType, e);
        }

        // Field annotations
        for (AnnotationSource annoSource : fieldSource.getAnnotations()) {
            visit(annoSource);
        }
    }

    public void visit(JavaAnnotationSource javaAnnoSource) {
        for (AnnotationSource annoSource : javaAnnoSource.getAnnotations()) {
            visit(annoSource);
        }
    }

    public void visit(JavaClassSource javaClassSource) {
        if (javaClassSource.getSuperType() != null) {
            try {
                String superClass = classTypeResolver.getFullTypeName(javaClassSource.getSuperType());
                addJavaResourceReference(superClass);
                // TODO: add relationship information ( child )
            } catch (ClassNotFoundException e) {
                logger.error("Unable to index superclass name for class: "
                                     + javaClassSource.getQualifiedName()
                                     + ", superclass: " + javaClassSource.getSuperType(), e);
            }
        }

        List<String> implementedInterfaces = javaClassSource.getInterfaces();
        if (implementedInterfaces != null) {
            for (String implementedInterface : implementedInterfaces) {
                try {
                    implementedInterface = classTypeResolver.getFullTypeName(implementedInterface);
                    addJavaResourceReference(implementedInterface);
                    // TODO: add relationship information ( implements )
                } catch (ClassNotFoundException e) {
                    logger.error("Unable to index implemented interface qualified name for class: "
                                         + javaClassSource.getQualifiedName()
                                         + ", interface: " + implementedInterface, e);
                }
            }
        }

        for (AnnotationSource<? extends JavaClassSource> annoSource : javaClassSource.getAnnotations()) {
            visit(annoSource);
        }
        for (FieldSource<? extends JavaClassSource> fieldSource : javaClassSource.getFields()) {
            visit(fieldSource);
        }
        for (MethodSource<? extends JavaClassSource> methodSource : javaClassSource.getMethods()) {
            visit(methodSource);
        }
    }

    public void visit(JavaEnumSource javaEnumSource) {
        for (AnnotationSource annoSource : javaEnumSource.getAnnotations()) {
            visit(annoSource);
        }
        for (FieldSource fieldSource : javaEnumSource.getFields()) {
            visit(fieldSource);
        }
        for (MethodSource methodSource : javaEnumSource.getMethods()) {
            visit(methodSource);
        }
    }

    public void visit(Import javaImport) {
        String refClassName = javaImport.getQualifiedName();
        if (javaImport.isWildcard()) {
            logger.warn("Import " + javaImport.getQualifiedName() + " used in class.");
        } else {
            addJavaResourceReference(refClassName);
        }
    }

    public void visit(JavaInterfaceSource interfaceSource) {
        for (AnnotationSource annoSource : interfaceSource.getAnnotations()) {
            visit(annoSource);
        }
        for (FieldSource<JavaInterfaceSource> fieldSource : interfaceSource.getFields()) {
            visit(fieldSource);
        }
        for (MethodSource methodSource : interfaceSource.getMethods()) {
            visit(methodSource);
        }
    }

    public void visit(MethodSource<? extends JavaClassSource> methodSource) {
        for (ParameterSource<? extends JavaClassSource> paramSource : methodSource.getParameters()) {
            // Method parameters
            addJavaResourceReference(paramSource.getType().getQualifiedName());
            // Method parameter annotations
            for (AnnotationSource<? extends JavaClassSource> annoSource : paramSource.getAnnotations()) {
                visit(annoSource);
            }
        }

        Type<? extends JavaClassSource> returnType = methodSource.getReturnType();
        if (returnType != null) {
            String returnTypeQualifiedName = returnType.getQualifiedName();
            if (!returnTypeQualifiedName.endsWith(".void")) {
                addJavaResourceReference(returnTypeQualifiedName);
            }
        }

        // method annotations
        for (AnnotationSource<? extends JavaClassSource> annoSource : methodSource.getAnnotations()) {
            visit(annoSource);
        }
    }

    public void addJavaResourceReference(String fullyQualifiedName) {
        super.addResourceReference(fullyQualifiedName, ResourceType.JAVA);
    }
}
