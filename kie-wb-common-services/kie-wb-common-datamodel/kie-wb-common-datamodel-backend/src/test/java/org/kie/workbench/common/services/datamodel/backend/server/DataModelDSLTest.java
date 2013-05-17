/*
* Copyright 2010 JBoss Inc
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

import org.junit.Test;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracle;

import static junit.framework.Assert.assertEquals;

public class DataModelDSLTest {

    @Test
    public void testAddConditionDSLSentence() {
        final PackageDataModelOracle dmo = PackageDataModelOracleBuilder.newPackageOracleBuilder()
                .addDsl( "[when]There is a Smurf=Smurf()" )
                .build();

        assertEquals( 1,
                      dmo.getDSLConditions().size() );
        assertEquals( 0,
                      dmo.getDSLActions().size() );
    }

    @Test
    public void testAddActionDSLSentence() {
        final PackageDataModelOracle dmo = PackageDataModelOracleBuilder.newPackageOracleBuilder()
                .addDsl( "[then]Greet Smurf=System.out.println(\"Hello Smurf\");" )
                .build();

        assertEquals( 0,
                      dmo.getDSLConditions().size() );
        assertEquals( 1,
                      dmo.getDSLActions().size() );
    }

    @Test
    public void testAddMultipleConditionDSLSentence() {
        final PackageDataModelOracle dmo = PackageDataModelOracleBuilder.newPackageOracleBuilder()
                .addDsl( "[when]There is a Smurf=Smurf()" )
                .addDsl( "[when]There is Happy Smurf=Smurf( nature = HAPPY )" )
                .build();

        assertEquals( 2,
                      dmo.getDSLConditions().size() );
        assertEquals( 0,
                      dmo.getDSLActions().size() );
    }

    @Test
    public void testAddMultipleActionDSLSentence() {
        final PackageDataModelOracle dmo = PackageDataModelOracleBuilder.newPackageOracleBuilder()
                .addDsl( "[then]Report Smurfs=System.out.println(\"There is a Smurf\");" )
                .addDsl( "[then]Greet Happy Smurf=System.out.println(\"Hello Happy Smurf\");" )
                .build();

        assertEquals( 0,
                      dmo.getDSLConditions().size() );
        assertEquals( 2,
                      dmo.getDSLActions().size() );
    }

    @Test
    public void testAddMultipleConditionDSLSentenceCombined() {
        final PackageDataModelOracle dmo = PackageDataModelOracleBuilder.newPackageOracleBuilder()
                .addDsl( "[when]There is a Smurf=Smurf()\n[when]There is Happy Smurf=Smurf( nature = HAPPY )" )
                .build();

        assertEquals( 2,
                      dmo.getDSLConditions().size() );
        assertEquals( 0,
                      dmo.getDSLActions().size() );
    }

    @Test
    public void testAddMultipleActionDSLSentenceCombined() {
        final PackageDataModelOracle dmo = PackageDataModelOracleBuilder.newPackageOracleBuilder()
                .addDsl( "[then]Report Smurfs=System.out.println(\"There is a Smurf\");\n[then]Greet Happy Smurf=System.out.println(\"Hello Happy Smurf\");" )
                .build();

        assertEquals( 0,
                      dmo.getDSLConditions().size() );
        assertEquals( 2,
                      dmo.getDSLActions().size() );
    }

}
