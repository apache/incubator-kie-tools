/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.index;

import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Utils.*;

public class PatternBuilder {

    private Rule                rule;
    private Pattern52           pattern52;

    public PatternBuilder( final Rule rule,
                           final Pattern52 pattern52 ) {
        this.rule = rule;
        this.pattern52 = pattern52;
    }

    public Pattern build() {
        final Pattern pattern = new Pattern( pattern52.getBoundName(),
                                             resolveObjectType( rule,
                                                                pattern52.getFactType() ) );
        rule.getPatterns().add( pattern );

        return pattern;
    }

}

