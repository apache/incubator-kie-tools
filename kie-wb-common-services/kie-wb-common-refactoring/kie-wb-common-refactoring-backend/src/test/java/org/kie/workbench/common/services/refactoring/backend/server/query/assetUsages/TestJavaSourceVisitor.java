/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.refactoring.backend.server.query.assetUsages;

import java.util.List;

import org.jboss.forge.roaster.model.Type;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.EnumConstantSource.Body;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.Import;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.kie.workbench.common.services.refactoring.Resource;
import org.kie.workbench.common.services.refactoring.ResourceReference;
import org.kie.workbench.common.services.refactoring.backend.server.impact.ResourceReferenceCollector;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;

public class TestJavaSourceVisitor extends ResourceReferenceCollector {

    private final JavaSource javaSource;
    private final Resource resParts;

    public TestJavaSourceVisitor(JavaSource javaSource,
                                 Resource resParts) {
        this.javaSource = javaSource;
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
        } else if (javaSource instanceof JavaClassSource) {
            visit((JavaClassSource) javaSource);
        }
    }

    public void visit(AnnotationSource<? extends JavaClassSource> annoSource) {
        if (annoSource.getQualifiedName().equals(AssetsUsageServiceImplTest.REFERENCED)) {
            ResourceReference reference = addResourceReference(annoSource.getStringValue("resourceFQN"),
                                                               ResourceType.JAVA);
            reference.addPartReference(annoSource.getStringValue("part"),
                                       PartType.FIELD);
        }
    }

    public void visit(Body body) {
        for (AnnotationSource annoSource : body.getAnnotations()) {
            visit(annoSource);
        }
        for (FieldSource fieldSource : body.getFields()) {
            visit(fieldSource);
        }
    }

    public void visit(FieldSource<? extends JavaSource> fieldSource) {
        Type fieldType = fieldSource.getType();
        String fieldClassName;

        // the javadoc for Named.getName() is misleading:
        // the FieldSource.getName() (which is implemented by FieldImpl.getName())
        // returns the (fully-qualified!) name of the field
        String fieldName = fieldSource.getName();
        resParts.addPart(fieldName,
                         PartType.FIELD);

        if (fieldType.isPrimitive()) {
            fieldClassName = fieldType.getName();
        } else {
            fieldClassName = fieldType.getQualifiedName();
        }
        addJavaResourceReference(fieldClassName);

        // Field annotations
        for (AnnotationSource annoSource : fieldSource.getAnnotations()) {
            visit(annoSource);
        }
    }

    public void visit(JavaClassSource javaClassSource) {
        for (AnnotationSource<? extends JavaClassSource> annoSource : javaClassSource.getAnnotations()) {
            visit(annoSource);
        }
        for (FieldSource<? extends JavaClassSource> fieldSource : javaClassSource.getFields()) {
            visit(fieldSource);
        }
    }

    public void visit(Import javaImport) {
        String refClassName = javaImport.getQualifiedName();
        addJavaResourceReference(refClassName);
    }

    public void addJavaResourceReference(String fullyQualifiedName) {
        super.addResourceReference(fullyQualifiedName,
                                   ResourceType.JAVA);
    }
}
