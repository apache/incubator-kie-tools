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

package ${packageName};

import ${parentAdapterClassName};
import org.kie.workbench.common.stunner.core.rule.Rule;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleSetImpl;
import org.kie.workbench.common.stunner.core.rule.context.*;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.impl.CanConnect;
import org.kie.workbench.common.stunner.core.rule.impl.CanContain;
import org.kie.workbench.common.stunner.core.rule.impl.CanDock;
import org.kie.workbench.common.stunner.core.rule.impl.EdgeOccurrences;
import org.kie.workbench.common.stunner.core.rule.impl.Occurrences;

import javax.annotation.Generated;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Generated("${generatedByClassName}")
@ApplicationScoped
public class ${className} extends ${parentAdapterClassName}<${defSetClassName}> {

    <#list rules as rule>
        ${rule.content}
    </#list>

    private static final Set<Rule> rules = new HashSet<Rule>(${rulesSize});
    private RuleSet ruleSet;

    @PostConstruct
    public void init() {
        <#list ruleNames as ruleName>
            rules.add(${ruleName});
        </#list>
        ruleSet = new RuleSetImpl("${ruleSetName}", rules);
    }

    @Override
    public RuleSet getRuleSet( final ${defSetClassName} pojo ) {
        return ruleSet;
    }

    @Override
    public boolean accepts( final Class<?> type ) {
        return ${defSetClassName}.class.equals( type );
    }

}
