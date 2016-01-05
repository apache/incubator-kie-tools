/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.lifecycle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Methods annotated with this are called by the Workbench before methods
 * annotated with {@code @OnOpen}. WorkbenchParts should perform any
 * initialisation activities here (for example load their content from a
 * persistent store).
 * </p>
 * <p>
 * For {@code @WorkbenchEditor}'s the method should take a single argument of
 * type {@code org.drools.guvnor.vfs.Path}. The Path specifies the URI for the
 * resource to be edited. For {@code @WorkbenchScreen} 's the method should have
 * zero arguments. The method should return void.
 * </p>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface OnStartup {

}
