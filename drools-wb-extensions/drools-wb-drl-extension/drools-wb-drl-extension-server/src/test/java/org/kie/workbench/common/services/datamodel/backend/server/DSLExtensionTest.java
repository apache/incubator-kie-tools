/*
* Copyright 2017 Red Hat, Inc. and/or its affiliates.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.kie.workbench.common.services.datamodel.backend.server;

import java.util.List;

import org.drools.workbench.models.datamodel.oracle.DSLActionSentence;
import org.drools.workbench.models.datamodel.oracle.DSLConditionSentence;
import org.junit.Test;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.soup.project.datamodel.oracle.ExtensionKind;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.cache.DSLExtension;
import org.kie.workbench.common.services.datamodel.spi.DataModelExtension.ExtensionMapping;

import static junit.framework.Assert.*;

public class DSLExtensionTest {

    private DSLExtension extension = new DSLExtension();

    @Test
    public void testAddConditionDSLSentence() {
        List<ExtensionMapping<?>> mappings = extension.getExtensions(null,
                                                                     "[when]There is a Smurf=Smurf()");

        assertEquals(1,
                     totalValues(mappings));
        assertEquals(1,
                     valuesOfKind(mappings, DSLConditionSentence.INSTANCE));
    }

    @Test
    public void testAddActionDSLSentence() {
        List<ExtensionMapping<?>> mappings = extension.getExtensions(null,
                                                                     "[then]Greet Smurf=System.out.println(\"Hello Smurf\");");

        assertEquals(1,
                     totalValues(mappings));
        assertEquals(1,
                     valuesOfKind(mappings, DSLActionSentence.INSTANCE));
    }

    @Test
    public void testAddMultipleConditionDSLSentence() {
        List<ExtensionMapping<?>> mappings = extension.getExtensions(null,
                                                                     "[when]There is a Smurf=Smurf()\n"
                                                                             + "[when]There is Happy Smurf=Smurf( nature = HAPPY )");

        assertEquals(2,
                     totalValues(mappings));
        assertEquals(2,
                     valuesOfKind(mappings, DSLConditionSentence.INSTANCE));
    }

    @Test
    public void testAddMultipleActionDSLSentence() {
        List<ExtensionMapping<?>> mappings = extension.getExtensions(null,
                                                                     "[then]Report Smurfs=System.out.println(\"There is a Smurf\");\n"
                                                                             + "[then]Greet Happy Smurf=System.out.println(\"Hello Happy Smurf\");");

        assertEquals(2,
                     totalValues(mappings));
        assertEquals(2,
                     valuesOfKind(mappings, DSLActionSentence.INSTANCE));
    }

    private static int totalValues(final List<ExtensionMapping<?>> mappings) {
        return (int) mappings
                .stream()
                .flatMap(em -> em.getValues().stream())
                .count();
    }

    private static int valuesOfKind(final List<ExtensionMapping<?>> mappings, final ExtensionKind kind) {
        return (int) mappings
                .stream()
                .filter(em -> kind.equals(em.getKind()))
                .flatMap(em -> em.getValues().stream())
                .count();
    }
}
