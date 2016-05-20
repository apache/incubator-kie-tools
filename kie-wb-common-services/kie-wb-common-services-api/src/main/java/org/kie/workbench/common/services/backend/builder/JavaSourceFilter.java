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
package org.kie.workbench.common.services.backend.builder;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.function.Predicate;

import javax.inject.Qualifier;

import org.kie.internal.builder.InternalKieBuilder;

/**
 * A qualifier for predicates that filter Java source files from compilation in the {@link InternalKieBuilder}.
 * {@link Predicate} beans with this qualifier will be combined such that if any single predicate tests false
 * for a Java source file, that file will not be compiled by the {@link InternalKieBuilder}.
 */
@Documented
@Qualifier
@Retention( RUNTIME )
@Target( { TYPE, METHOD, FIELD, PARAMETER } )
public @interface JavaSourceFilter {

}
