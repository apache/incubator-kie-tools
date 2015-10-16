/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.kie.workbench.common.services.refactoring.backend.server.query.response;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringStringPageRow;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.paging.PageResponse;

/**
 * Custom ResponseBuilder to return a String Rule Names
 */
public class RuleNameResponseBuilder
        implements ResponseBuilder {

    @Override
    public PageResponse<RefactoringPageRow> buildResponse( final int pageSize,
                                                           final int startRow,
                                                           final List<KObject> kObjects ) {
        final int hits = kObjects.size();
        final PageResponse<RefactoringPageRow> response = new PageResponse<RefactoringPageRow>();
        final List<RefactoringPageRow> result = buildResponse( kObjects );
        response.setTotalRowSize( hits );
        response.setPageRowList( result );
        response.setTotalRowSizeExact( true );
        response.setStartRowIndex( startRow );
        response.setLastPage( (pageSize * startRow + 2) >= hits );

        return response;
    }

    @Override
    public List<RefactoringPageRow> buildResponse( final List<KObject> kObjects ) {
        //Both "child" rule and "parent" rule (when one extends another) are stored
        //in the index. We therefore need to build a set of unique Rule Names
        final List<RefactoringPageRow> result = new ArrayList<RefactoringPageRow>( kObjects.size() );
        final Set<String> uniqueRuleNames = new HashSet<String>();
        for (final KObject kObject : kObjects) {
            final Set<String> ruleNames = getRuleNamesFromKObject( kObject );
            uniqueRuleNames.addAll( ruleNames );
        }

        for (String ruleName : uniqueRuleNames) {
            final RefactoringStringPageRow row = new RefactoringStringPageRow();
            row.setValue( ruleName );
            result.add( row );
        }

        return result;
    }

    private Set<String> getRuleNamesFromKObject( final KObject kObject ) {
        //Some resources (e.g. Decision Tables etc) contain multiple rule names so add them all
        final Set<String> ruleNames = new HashSet<String>();
        if ( kObject == null ) {
            return ruleNames;
        }
        for (KProperty property : kObject.getProperties()) {
            if ( property.getName().equals( RuleIndexTerm.TERM ) ) {
                ruleNames.add( property.getValue().toString() );
            }
        }
        return ruleNames;
    }

}
