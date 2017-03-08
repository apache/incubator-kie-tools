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

import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.ioc.client.api.CodeDecorator;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCDecoratorExtension;
import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.FactoryController;
import org.uberfire.security.annotations.PermissionCheck;
import org.uberfire.security.client.authz.AuthorizationManagerHelper;

/**
 * <p>Given a method where a specific permission is required like, for instance:</p>
 * <p>
 * <pre>
 * {@code @PermissionCheck("featureX")
 *    private void enableFeatureX() {
 *        ...
 *    }
 * }
 * </pre>
 * <p>
 * <p>This processor will append the required security check code to ensure the method body
 * is only executed when the user is granted with the proper permission rights.</p>
 * </pre>
 */
@CodeDecorator
public class PermissionCheckProcessor extends IOCDecoratorExtension<PermissionCheck> {

    public PermissionCheckProcessor(Class<PermissionCheck> decoratesWith) {
        super(decoratesWith);
    }

    @Override
    public void generateDecorator(final Decorable decorable,
                                  final FactoryController controller) {
        MetaMethod metaMethod = decorable.getAsMethod();
        PermissionCheck securedResource = metaMethod.getAnnotation(PermissionCheck.class);
        String permission = securedResource.value();
        String onGranted = securedResource.onGranted();
        String onDenied = securedResource.onDenied();
        String declaringClass = metaMethod.getDeclaringClassName();

        // The method must return void
        if (!metaMethod.getReturnType().getName().equals("void")) {
            throw new RuntimeException("The @PermissionCheck annotated method \"" +
                                               declaringClass + "#" + metaMethod.getName() + "\" must return void");
        }

        // Permission check
        if (permission == null || permission.trim().length() == 0) {
            Statement stmt = createPermissionCheck(permission,
                                                   onGranted,
                                                   onDenied);
            controller.addInvokeBefore(metaMethod,
                                       stmt);
        }
    }

    public Statement createPermissionCheck(String permission,
                                           String onGranted,
                                           String onDenied) {
        return ResourceCheckProcessor.buildCheckStatement(
                Stmt.invokeStatic(AuthorizationManagerHelper.class,
                                  "authorize",
                                  permission),
                onGranted,
                onDenied);
    }
}