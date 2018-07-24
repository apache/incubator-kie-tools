/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.model;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepository;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.Test;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;

import static org.jgroups.util.Util.assertTrue;
import static org.junit.Assert.*;

public class ProjectScreenModelTest {

    @Test
    public void testHashCode() {
        final ProjectScreenModel model = new ProjectScreenModel();
        model.setPOM(new POM("test",
                             "test",
                             "url",
                             new GAV("groupID",
                                     "artifactID",
                                     "version")));
        final MavenRepository repository = new MavenRepository();
        repository.setId("guvnor-m2-repo");
        repository.setName("Guvnor M2 Repo");
        repository.setUrl("http://localhost/maven2/");
        model.getPOM().addRepository(repository);
        model.setPOMMetaData(new Metadata());

        model.setKModule(new KModuleModel());
        model.setKModuleMetaData(new Metadata());

        model.setProjectTagsMetaData(new Metadata());

        model.setProjectImports(new ProjectImports());
        model.setProjectImportsMetaData(new Metadata());

        final int hashCode1 = model.hashCode();
        assertTrue(hashCode1 <= Integer.MAX_VALUE);
        assertTrue(hashCode1 >= Integer.MIN_VALUE);

        model.getProjectImports().getImports().addImport(new Import("java.lang.List"));

        final int hashCode2 = model.hashCode();
        assertTrue(hashCode2 <= Integer.MAX_VALUE);
        assertTrue(hashCode2 >= Integer.MIN_VALUE);

        assertNotEquals(hashCode1,
                        hashCode2);
    }
}
