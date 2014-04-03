package org.drools.workbench.screens.testscenario.backend.server;

import java.util.HashSet;
import java.util.List;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.FixtureList;
import org.drools.workbench.models.testscenarios.shared.FixturesMap;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;

public class TestScenarioModelVisitor {

    private final PackageDataModelOracleBaselinePayload dmo;
    private final Scenario scenario;
    private HashSet<String> fqcNames = new HashSet<String>();

    public TestScenarioModelVisitor( PackageDataModelOracleBaselinePayload dmo,
                                     Scenario scenario ) {
        this.dmo = dmo;
        this.scenario = scenario;
    }

    public HashSet<String> visit() {

        visit( scenario.getFixtures() );

        return fqcNames;

    }

    private void visit( List<Fixture> fixtures ) {
        for ( Fixture fixture : fixtures ) {
            visit( fixture );
        }
    }

    private void visit( Fixture fixture ) {
        //TODO: -Rikkola-
        //        /CallFixtureMap?
        // Expectation?
        if ( fixture instanceof FixtureList ) {
            for ( Fixture child : ( (FixtureList) fixture ) ) {
                visit( child );
            }
        } else if ( fixture instanceof FixturesMap ) {
            for ( Fixture child : ( (FixturesMap) fixture ).values() ) {
                visit( child );
            }
        } else if ( fixture instanceof FactData ) {
            convertToFullyQualifiedClassName( ( (FactData) fixture ).getType() );
        }

    }

    //Get the fully qualified class name of the fact type
    private void convertToFullyQualifiedClassName( final String factType ) {
        if ( factType.contains( "." ) ) {
            fqcNames.add( factType );
            return;
        }
        String fullyQualifiedClassName = null;
        for ( Import imp : scenario.getImports().getImports() ) {
            if ( imp.getType().endsWith( factType ) ) {
                fullyQualifiedClassName = imp.getType();
                break;
            }
        }
        if ( fullyQualifiedClassName == null ) {
            fullyQualifiedClassName = scenario.getPackageName() + "." + factType;
        }
        fqcNames.add( fullyQualifiedClassName );
    }
}
