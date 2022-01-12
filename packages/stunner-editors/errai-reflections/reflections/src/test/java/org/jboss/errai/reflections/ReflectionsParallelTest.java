/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.reflections;

import com.google.common.base.Predicate;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.jboss.errai.reflections.scanners.*;
import org.jboss.errai.reflections.util.ConfigurationBuilder;
import org.jboss.errai.reflections.util.ClasspathHelper;
import org.jboss.errai.reflections.util.FilterBuilder;

import static java.util.Arrays.asList;

/** */
@Ignore
public class ReflectionsParallelTest extends ReflectionsTest {

    @BeforeClass
    public static void init() {
        Predicate<String> filter = new FilterBuilder().include("org.jboss.errai.reflections.TestModel\\$.*");

        reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(asList(ClasspathHelper.forClass(TestModel.class)))
                .filterInputsBy(filter)
                .setScanners(
                        new SubTypesScanner().filterResultsBy(filter),
                        new TypeAnnotationsScanner().filterResultsBy(filter),
                        new FieldAnnotationsScanner().filterResultsBy(filter),
                        new MethodAnnotationsScanner().filterResultsBy(filter),
                        new ConvertersScanner().filterResultsBy(filter))
                .useParallelExecutor());
    }

    @Test
    public void testAll() {
        super.testAll();
    }
}
