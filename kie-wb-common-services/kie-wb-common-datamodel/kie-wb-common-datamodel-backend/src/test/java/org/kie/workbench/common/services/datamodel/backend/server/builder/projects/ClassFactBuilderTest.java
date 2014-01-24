package org.kie.workbench.common.services.datamodel.backend.server.builder.projects;

import org.drools.workbench.models.commons.backend.oracle.ProjectDataModelOracleImpl;
import org.drools.workbench.models.datamodel.oracle.TypeSource;
import org.junit.Test;
import org.kie.workbench.common.services.datamodel.backend.server.testclasses.superclasses.PapaSmurf;

import static org.junit.Assert.*;

public class ClassFactBuilderTest {

    @Test
    public void testSuperTypes() throws Exception {
        final ProjectDataModelOracleBuilder builder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
        final ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder(builder,
                PapaSmurf.class,
                false,
                TypeSource.JAVA_PROJECT);
        cb.build(oracle);

        assertEquals(2, oracle.getProjectSuperTypes().get(PapaSmurf.class.getName()).size());
    }
}
