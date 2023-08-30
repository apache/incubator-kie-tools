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


package org.jboss.errai.databinding.rebind;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.meta.HasAnnotations;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ioc.client.api.IOCExtension;
import org.jboss.errai.ioc.client.container.RefHolder;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.AbstractBodyGenerator;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.FactoryBodyGenerator;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.IOCProcessingContext;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCExtensionConfigurator;
import org.jboss.errai.ioc.rebind.ioc.graph.api.CustomFactoryInjectable;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraph;
import org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.InjectableType;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;
import org.jboss.errai.ioc.rebind.ioc.graph.api.InjectionSite;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.DefaultCustomFactoryInjectable;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.FactoryNameGenerator;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.InjectableHandle;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectableProvider;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;
import org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType;
import org.jboss.errai.ui.shared.api.annotations.Model;

import static org.jboss.errai.codegen.meta.MetaClassFactory.parameterizedAs;
import static org.jboss.errai.codegen.meta.MetaClassFactory.typeParametersOf;
import static org.jboss.errai.codegen.util.Stmt.declareFinalVariable;
import static org.jboss.errai.codegen.util.Stmt.invokeStatic;
import static org.jboss.errai.codegen.util.Stmt.loadVariable;

/**
 * The purpose of this IOC extension is to provide bean instances of bindable types that are qualified with
 * {@link Model} and to expose the {@link DataBinder}s that manage these model instances using {@link RefHolder} s.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Mike Brock
 */
@IOCExtension
public class DataBindingIOCExtension implements IOCExtensionConfigurator {

  @Override
  public void configure(final IOCProcessingContext context, final InjectionContext injectionContext) {
  }

  @Override
  public void afterInitialization(final IOCProcessingContext context, final InjectionContext injectionContext) {

    final Collection<MetaClass> allBindableTypes = DataBindingUtil.getAllBindableTypes(context.getGeneratorContext());
    final Model anno = new Model() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return Model.class;
      }
    };

    for (final MetaClass modelBean : allBindableTypes) {
      final InjectableHandle handle = new InjectableHandle(modelBean,
              injectionContext.getQualifierFactory().forSource(new HasAnnotations() {

                @Override
                public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
                  return Model.class.equals(annotation);
                }

                @Override
                public Annotation[] getAnnotations() {
                  return new Annotation[] {
                      anno
                  };
                }

                @SuppressWarnings("unchecked")
                @Override
                public <A extends Annotation> A getAnnotation(Class<A> annotation) {
                  if (isAnnotationPresent(annotation)) {
                    return (A) anno;
                  }
                  else {
                    return null;
                  }
                }
              }));

      injectionContext.registerInjectableProvider(handle, new InjectableProvider() {

        private CustomFactoryInjectable provided;

        @Override
        public CustomFactoryInjectable getInjectable(final InjectionSite injectionSite, final FactoryNameGenerator nameGenerator) {
          if (injectionSite.isAnnotationPresent(Model.class)) {
            if (provided == null) {
              final FactoryBodyGenerator generator = new AbstractBodyGenerator() {

                @Override
                protected List<Statement> generateCreateInstanceStatements(
                        final ClassStructureBuilder<?> bodyBlockBuilder, final Injectable injectable,
                        final DependencyGraph graph, final InjectionContext injectionContext) {
                  final List<Statement> createInstanceStmts = new ArrayList<Statement>();
                  final MetaClass binderClass = parameterizedAs(DataBinder.class, typeParametersOf(modelBean));
                  final String dataBinderVar = "dataBinder";
                  final String modelVar = "model";

                  createInstanceStmts.add(declareFinalVariable(dataBinderVar, binderClass,
                          invokeStatic(DataBinder.class, "forType", modelBean)));
                  createInstanceStmts
                  .add(declareFinalVariable(modelVar, modelBean, loadVariable(dataBinderVar).invoke("getModel")));
                  createInstanceStmts.add(loadVariable("this").invoke("setReference", loadVariable(modelVar),
                          DataBindingUtil.BINDER_VAR_NAME, loadVariable(dataBinderVar)));
                  createInstanceStmts.add(loadVariable(modelVar).returnValue());

                  return createInstanceStmts;
                }
              };

              provided = new DefaultCustomFactoryInjectable(handle.getType(), handle.getQualifier(),
                      nameGenerator.generateFor(handle.getType(), handle.getQualifier(),
                              InjectableType.ExtensionProvided),
                      Dependent.class, Collections.singletonList(WiringElementType.DependentBean), generator);
            }

            return provided;
          }
          else {
            throw new IllegalStateException("The " + DataBindingIOCExtension.class.getSimpleName()
                    + " was used to satisfy an injection site without the @" + Model.class.getName() + " annotation.");
          }
        }
      });
    }
  }
}
