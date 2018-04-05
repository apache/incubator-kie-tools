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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Package;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.kie.workbench.common.screens.javaeditor.type.JavaResourceTypeDefinition;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.refactoring.Resource;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.AbstractFileIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.IndexBuilder;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

/**
 * The following information is being indexed for java files.
 * <p/>
 * Java type definition minimal information:
 * <p/>
 * (java_type, {class|enum|interface|annotation})
 * (java_type_name, qualifiedClassName)
 * <p/>
 * Java type definition inheritance information:
 * <p/>
 * (java_type_parent, superClassQualifiedName)
 * (java_type_interface, implementedInterface1QualifiedName)
 * (java_type_interface, implementedInterface2QualifiedName)
 * <p/>
 * Java type definition fields information:
 * <p/>
 * (field_name, theField1Name)
 * (field_type:theField1Name, field1TypeQualifiedName)
 * <p/>
 * (field_name, theField2Name)
 * (field_type:theField2Name, field2TypeQualifiedName)
 * <p/>
 * References to types used by this .java class definition, uses the Type references standard used by the other assets:
 * <p/>
 * (type_name, superClassQualifiedName)
 * (type_name, implementedInterface1)
 * (type_name, implementedInterface2)
 * (type_name, field1TypeQualifiedName)
 * (type_name, field2TypeQualifiedName)
 */
@ApplicationScoped
public class JavaFileIndexer extends AbstractFileIndexer {

    private static final Logger logger = LoggerFactory.getLogger(JavaFileIndexer.class);

    @Inject
    protected JavaResourceTypeDefinition javaResourceTypeDefinition;

    @Inject
    @Any
    protected Instance<JavaFileIndexerExtension> javaFileIndexerExtensions;

    @Inject
    ModuleClassLoaderHelper classLoaderHelper;

    @Override
    public boolean supportsPath(final Path path) {
        return javaResourceTypeDefinition.accept(Paths.convert(path));
    }

    @Override
    public IndexBuilder fillIndexBuilder(final Path path) throws Exception {
        // create indexbuilder
        final KieModule module = getModule(path);

        if (module == null) {
            logger.error("Unable to index " + path.toUri().toString() + ": module could not be resolved.");
            return null;
        }

        final Package pkg = getPackage(path);
        if (pkg == null) {
            logger.error("Unable to index " + path.toUri().toString() + ": package could not be resolved.");
            return null;
        }

        // responsible for basic index info: module name, branch, etc
        final DefaultIndexBuilder builder = new DefaultIndexBuilder(Paths.convert(path).getFileName(),
                                                                    module,
                                                                    pkg);

        // visit/index java source
        final String javaSource = ioService.readAllString(path);
        org.jboss.forge.roaster.model.JavaType<?> javaType = Roaster.parse(javaSource);
        if (javaType.getSyntaxErrors() == null || javaType.getSyntaxErrors().isEmpty()) {

            if (javaFileIndexerExtensions != null) {
                for (JavaFileIndexerExtension javaFileIndexerExtension : javaFileIndexerExtensions) {
                    javaFileIndexerExtension.process(builder,
                                                     javaType);
                }
            }

            String pkgName = pkg.getPackageName();
            pkgName = javaType.getPackage();
            if (pkgName == null) {
                pkgName = "";
            }
            // use Java class package name, not Package name
            builder.setPackageName(pkgName);

            String javaTypeName = javaType.getQualifiedName();
            Resource resParts = new Resource(javaTypeName,
                                             ResourceType.JAVA);

            if (javaType instanceof JavaSource) {
                ClassLoader moduleClassLoader = getModuleClassLoader(module);
                JavaSourceVisitor visitor = new JavaSourceVisitor((JavaSource) javaType,
                                                                  moduleClassLoader,
                                                                  resParts);
                visitor.visit((JavaSource) javaType);
                addReferencedResourcesToIndexBuilder(builder,
                                                     visitor);
            }

            builder.addGenerator(resParts);
        }

        return builder;
    }

    /*
     * Present in order to be overridden in tests
     */
    protected ClassLoader getModuleClassLoader(final KieModule module) {
        return classLoaderHelper.getModuleClassLoader(module);
    }

    /*
     * Present in order to be overridden in tests
     */
    protected KieModule getModule(final Path path) {
        return moduleService.resolveModule(Paths.convert(path));
    }

    /*
     * Present in order to be overridden in tests
     */
    protected Package getPackage(final Path path) {
        return moduleService.resolvePackage(Paths.convert(path));
    }
}
