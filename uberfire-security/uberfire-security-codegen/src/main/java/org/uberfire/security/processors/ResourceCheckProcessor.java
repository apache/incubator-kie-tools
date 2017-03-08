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
package org.uberfire.security.processors;

import org.jboss.errai.codegen.BooleanExpression;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.builder.ElseBlockBuilder;
import org.jboss.errai.codegen.builder.StatementEnd;
import org.jboss.errai.codegen.builder.impl.BooleanExpressionBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameter;
import org.jboss.errai.codegen.util.If;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.ioc.client.api.CodeDecorator;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCDecoratorExtension;
import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.FactoryController;
import org.uberfire.security.Resource;
import org.uberfire.security.annotations.ResourceCheck;
import org.uberfire.security.client.authz.AuthorizationManagerHelper;

/**
 * <p>Given a method where "project" creation permissions are required like, for instance:</p>
 * <p>
 * <pre>
 * {@code @SecuredAction(type="project", action="create")
 *    private void enableProjectCreation() {
 *        creationButton.setEnabled(true);
 *    }
 * }
 * </pre>
 * <p>
 * <p>This processor class will append the required security check code to ensure the method body
 * is only executed when the user is granted with the proper permission rights.</p>
 * <p>
 * <p>For resource instance specific checks a parameter of a class implementing {@link Resource}
 * is required. For instance:</p>
 * <pre>
 * {@code @SecuredAction(action="create")
 *    private void addProjectToView(Project project) {
 *        view,addProject(project.getName());
 *    }
 * }
 * </pre>
 */
@CodeDecorator
public class ResourceCheckProcessor extends IOCDecoratorExtension<ResourceCheck> {

    public ResourceCheckProcessor(Class<ResourceCheck> decoratesWith) {
        super(decoratesWith);
    }

    public static Statement buildCheckStatement(ContextualStatementBuilder authzCall,
                                                String onGranted,
                                                String onDenied) {
        BooleanExpression boolExpr = BooleanExpressionBuilder.create(authzCall).negate();
        BlockBuilder<ElseBlockBuilder> builder = If.cond(boolExpr);
        if (onDenied != null && onDenied.trim().length() > 0) {
            builder.append(Stmt.loadVariable("this").invoke(onDenied));
        }
        BlockBuilder<StatementEnd> endBuilder = builder.append(Stmt.returnVoid()).finish().else_();
        if (onGranted != null && onGranted.trim().length() > 0) {
            endBuilder.append(Stmt.loadVariable("this").invoke(onGranted));
        }
        return endBuilder.finish();
    }

    @Override
    public void generateDecorator(final Decorable decorable,
                                  final FactoryController controller) {
        MetaMethod metaMethod = decorable.getAsMethod();
        ResourceCheck securedResource = metaMethod.getAnnotation(ResourceCheck.class);
        String resourceType = securedResource.type();
        String resourceAction = securedResource.action();
        String onGranted = securedResource.onGranted();
        String onDenied = securedResource.onDenied();
        String declaringClass = metaMethod.getDeclaringClassName();
        int paramCount = metaMethod.getParameters().length;

        // The method must return void
        if (!metaMethod.getReturnType().getName().equals("void")) {
            throw new RuntimeException("The @ResourceCheck annotated method \"" +
                                               declaringClass + "#" + metaMethod.getName() + "\" must return void");
        }

        // Infer the check type: global action or resource check
        boolean resourceCheck = false;
        if (paramCount > 0) {
            MetaParameter resourceParameter = metaMethod.getParameters()[0];
            resourceCheck = implementsResource(resourceParameter.getType());
        }

        // Resource instance check
        if (resourceCheck) {
            MetaParameter p1 = metaMethod.getParameters()[0];
            Statement stmt = createResourceActionCheck(p1.getName(),
                                                       resourceAction,
                                                       onGranted,
                                                       onDenied);
            controller.addInvokeBefore(metaMethod,
                                       stmt);
        }
        // Global action check
        else {
            // The resource type is mandatory
            if (resourceType == null || resourceType.trim().length() == 0) {
                throw new RuntimeException("The @ResourceCheck parameter named \"type\" is missing " +
                                                   "\"" + declaringClass + "#" + metaMethod.getName() + "\"");
            }
            Statement stmt = createGlobalActionCheck(resourceType,
                                                     resourceAction,
                                                     onGranted,
                                                     onDenied);
            controller.addInvokeBefore(metaMethod,
                                       stmt);
        }
    }

    public boolean implementsResource(MetaClass metaClass) {
        for (MetaClass iface : metaClass.getInterfaces()) {
            if (iface.asClass().equals(Resource.class) || implementsResource(iface)) {
                return true;
            }
        }
        return false;
    }

    public Statement createResourceActionCheck(String resourceName,
                                               String resourceAction,
                                               String onGranted,
                                               String onDenied) {
        return buildCheckStatement(Stmt.invokeStatic(AuthorizationManagerHelper.class,
                                                     "authorize",
                                                     Stmt.loadVariable(resourceName),
                                                     resourceAction),
                                   onGranted,
                                   onDenied);
    }

    public Statement createGlobalActionCheck(String resourceType,
                                             String resourceAction,
                                             String onGranted,
                                             String onDenied) {
        return buildCheckStatement(Stmt.invokeStatic(AuthorizationManagerHelper.class,
                                                     "authorize",
                                                     resourceType,
                                                     resourceAction),
                                   onGranted,
                                   onDenied);
    }
}