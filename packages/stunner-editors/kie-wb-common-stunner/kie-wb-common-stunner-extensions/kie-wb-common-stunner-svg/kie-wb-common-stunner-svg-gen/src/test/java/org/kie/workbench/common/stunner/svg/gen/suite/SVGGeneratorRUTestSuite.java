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


package org.kie.workbench.common.stunner.svg.gen.suite;

import java.util.Locale;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.kie.workbench.common.stunner.svg.gen.codegen.impl.CircleDefinitionGeneratorTest;
import org.kie.workbench.common.stunner.svg.gen.codegen.impl.GroupDefinitionGeneratorTest;
import org.kie.workbench.common.stunner.svg.gen.codegen.impl.MultiPathDefinitionGeneratorTest;
import org.kie.workbench.common.stunner.svg.gen.codegen.impl.RectangleDefinitionGeneratorTest;
import org.kie.workbench.common.stunner.svg.gen.codegen.impl.SVGViewDefinitionGeneratorTest;
import org.kie.workbench.common.stunner.svg.gen.impl.SVGGeneratorImplTest;

/**
 * Just suite that tests the different generators but using a different locale.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        CircleDefinitionGeneratorTest.class,
        GroupDefinitionGeneratorTest.class,
        MultiPathDefinitionGeneratorTest.class,
        RectangleDefinitionGeneratorTest.class,
        SVGViewDefinitionGeneratorTest.class,
        SVGGeneratorImplTest.class,
})
public class SVGGeneratorRUTestSuite {

    private static Locale locale;

    @BeforeClass
    public static void setLocale() {
        locale = Locale.getDefault();
        Locale.setDefault(new Locale("ru",
                                     "RU"));
    }

    @AfterClass
    public static void restoreLocale() {
        Locale.setDefault(locale);
    }
}
