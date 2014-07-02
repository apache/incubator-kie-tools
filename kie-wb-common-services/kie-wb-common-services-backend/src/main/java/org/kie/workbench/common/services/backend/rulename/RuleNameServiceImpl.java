/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.services.backend.rulename;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValuePackageNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueProjectRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class RuleNameServiceImpl
        implements RuleNamesService {

    @Inject
    private RefactoringQueryService queryService;

    @Inject
    private ProjectService projectService;

    @Override
    public Collection<String> getRuleNames( final Path path,
                                            final String packageName ) {
        final Project project = projectService.resolveProject( path );

        if ( project == null ) {
            return Collections.emptyList();
        } else {
            return queryRuleNames( project,
                                   packageName );
        }
    }

    private List<String> queryRuleNames( final Project project,
                                         final String packageName ) {
        //Query for Rule Names
        final List<RefactoringPageRow> results = queryService.query( "FindRulesByProjectQuery",
                                                                     new HashSet<ValueIndexTerm>() {{
                                                                         add( new ValueProjectRootPathIndexTerm( project.getRootPath().toURI() ) );
                                                                         add( new ValuePackageNameIndexTerm( packageName ) );
                                                                     }},
                                                                     false );

        //Convert into response for RuleNameService
        final List<String> ruleNames = new ArrayList<String>();
        for ( RefactoringPageRow row : results ) {
            ruleNames.add( (String) row.getValue() );
        }
        Collections.sort( ruleNames );
        return ruleNames;
    }

}
