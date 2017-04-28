/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.refactoring.backend.server.impact;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.Instance;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.test.objects.Person;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.drl.TestDrlFileIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.drl.TestDrlFileTypeDefinition;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.PackageDescrIndexVisitor;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindAllChangeImpactQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindResourceReferencesQuery;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.impact.QueryOperationRequest;
import org.uberfire.java.nio.file.Path;

/**
 * This test is focused on making sure that the {@link PackageDescrIndexVisitor} is able to find and collect the data that it needs to.
 * </p>
 * Eventually, when other changes have also been made to drools-compiler, it may be important to make sure that the
 * overlap of the {@link PackageDescrIndexVisitor} logic and the drools-compiler logic completely, complimentary and uniquely covers
 * retrieving information about compiled rules.
 * </p>
 * By "uniquely covers", I mean that there are a few cases in which references to parts/resources can be retrieved either via compilation
 * or via the {@link PackageDescrIndexVisitor} visitor logic -- it's important that only one of the two retrieve that informaiton
 * (clarity of code, maintenance, etc.. ).
 * </p>
 * HOWEVER, there is definitely information that can ONLY be retrieved via the {@link PackageDescrIndexVisitor} logic, and not
 * via the drools-compiler logic!
 * </p>
 * Ignored at the moment: can be turned on/finished when the drools-compiler logic has also been added
 */
@Ignore
public class PackageDescrIndexVisitorIndexingTest
        extends BaseIndexingTest<TestDrlFileTypeDefinition> {

    protected Set<NamedQuery> getQueries() {
        return new HashSet<NamedQuery>() {{
            add( new FindResourceReferencesQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new DefaultResponseBuilder( ioService() );
                }
            } );
            add( new FindAllChangeImpactQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new DefaultResponseBuilder( ioService() );
                }
            } );
        }};
    }

    private int drlIdGen = 0;

    @Test
    public void allPackageDescrIndexVisitorMethodsTest() throws IOException, InterruptedException {
        //Add test files
        Path path = basePath.resolve( getUniqueDrlFileName() );

        String accFunctionName = "testAcc";
        String accFunctionClass = "org.kie.test.objects.TestAccumulator";
        String entryPointName = "testEntryPoint";
        String entryPointAnno = "testAnno";
        String windowName = "TestWindow";
        String funcDeclName = "myFunction";
        String rule1Name = "ruleOne";
        String rule2Name = "windowRule";
        String rule3Name = "condBranchRule";

        String drlSource =
                "package org.kie.indexing;\n" +
                "import org.kie.test.objects.*;\n" +
                "import accumulate " + accFunctionClass + " " + accFunctionName + "\n\n" +
                // 3
                "declare entry-point '" + entryPointName + "' @" + entryPointAnno + " end\n\n" +
                // 5
                "declare enum Workload\n" + // this info (local reference) can *not* be retrieved via compilation!
                "  LIGHT( 4 ),\n" +
                "  MEDIUM( 8 ),\n" +
                "  HEAVY( 12 );\n" +
                "  hours   : int\n" +
                "end\n\n" +
                // 12
                "declare TestWork\n" +
                "  load  : WorkLoad\n" +
                "  num   : int = 111\n" +
                "  pers  : Person \n" +
                "end\n\n" +
                // NOTE: it looks like Window.pattern object type name resolution requires compile time information
                // (Trace why this test fails, to see why.. )
                 "declare window " + windowName + "\n" +
                 "Person( name == 'mark' )\n" +
                 "  over window:length( 10 )\n" +
                 "  from entry-point " + entryPointName +"\n" +
                 "end\n\n" +
                 // 18

                 "function String " + funcDeclName + "(Cheese cheese) {\n" +
                 "  return 'Hello' cheese.type\n" +
                 "}\n\n" +

                 "rule EnumRule\n" +
                 "when\n" +
                 "  TestWork( WorkLoad.LIGHT, $h : load.hours )\n" + // Workload.LIGHT reference info must be retrieved via compilation!
                 "then\n" +
                 "  list.add( $h );\n" +
                 "end\n" +

                "rule " + rule1Name + "\n" +
                "when\n" +
                "  Person( name == \"mark\", cheese.(price == 10, type.(length == 10) ) )\n" +
                "then\n" +
                "end\n\n" +
                 // 28
                "rule " + rule2Name + "\n" +
                "when\n" +
                "  accumulate( Person( name == 'mark' ) from window TestWindow, $cnt : " + accFunctionName +"(1) )\n" +
                "then\n" +
                "  // there has been $cnt RHT ticks over the last 10 ticks\n" +
                "end\n\n" +
                 // 35
                "rule " + rule3Name  + "\n" +
                "when\n" +
                "  $customer : Customer( age > 60 )\n" +
                "  if ( type == Golden ) do[giveDiscount]\n" +
                "  $car : Car ( owner == $customer )\n" +
                "then\n" +
                "  modify($car) { setFreeParking( true ) }\n" +
                "then[giveDiscount]\n" +
                "  modify($customer) { setDiscount( 0.1 ) }\n" +
                "end\n";

        ioService().write( path, drlSource );

        path = basePath.resolve( getUniqueDrlFileName() );
        drlSource = loadText("accumulate.drl");
        ioService().write( path, drlSource );

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        String className = Person.class.getName();
        QueryOperationRequest refOpRequest = QueryOperationRequest.referencesPart(className, "setName(String)", PartType.METHOD )
                .inAllProjects()
                .onAllBranches();

        List<RefactoringPageRow> response = service.queryToList(refOpRequest);

        assertNotNull("Null PageResonse", response);
        assertNotNull("Null PageRefactoringRow list", response );
        assertEquals("Objects referencing " + className, 1, response.size() );

        Object pageRowValue = response.get(0).getValue();
        assertTrue( "Expected a " + org.uberfire.backend.vfs.Path.class.getName() + ", not a " + pageRowValue.getClass().getSimpleName(),
                    org.uberfire.backend.vfs.Path.class.isAssignableFrom(pageRowValue.getClass()) );
        String fileName = ((org.uberfire.backend.vfs.Path) pageRowValue).getFileName();
        assertTrue( "File does not end with '.java'", fileName.endsWith(".java"));
        assertEquals( "File name", className, fileName.subSequence(0, fileName.indexOf(".java")));
    }

    private String getUniqueDrlFileName() {
        return "drl" + drlIdGen++ + ".drl";
    }

    @Override
    protected TestIndexer<TestDrlFileTypeDefinition> getIndexer() {
        return new TestDrlFileIndexer();
    }

    @Override
    protected TestDrlFileTypeDefinition getResourceTypeDefinition() {
        return new TestDrlFileTypeDefinition();
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }

}
