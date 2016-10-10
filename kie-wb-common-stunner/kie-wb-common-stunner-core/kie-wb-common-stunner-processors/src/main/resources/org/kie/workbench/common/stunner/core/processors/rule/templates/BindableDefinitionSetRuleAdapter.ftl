/*
* Copyright 2016 Red Hat, Inc. and/or its affiliates.
*  
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*  
*    http://www.apache.org/licenses/LICENSE-2.0
*  
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package ${packageName};

import ${parentAdapterClassName};
import org.kie.workbench.common.stunner.core.rule.Rule;
import org.kie.workbench.common.stunner.core.rule.CardinalityRule;
import org.kie.workbench.common.stunner.core.rule.EdgeCardinalityRule;
import org.kie.workbench.common.stunner.core.rule.ConnectionRule;
import org.kie.workbench.common.stunner.core.rule.impl.rules.ConnectionRuleImpl;
import org.kie.workbench.common.stunner.core.rule.impl.rules.ContainmentRuleImpl;
import org.kie.workbench.common.stunner.core.rule.impl.rules.EdgeCardinalityRuleImpl;
import org.kie.workbench.common.stunner.core.rule.impl.rules.CardinalityRuleImpl;
import org.kie.workbench.common.stunner.core.rule.impl.rules.*;

import javax.annotation.Generated;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Generated("${generatedByClassName}")
@ApplicationScoped
public class ${className} extends ${parentAdapterClassName}<${defSetClassName}> {

    <#list rules as rule>
        ${rule.content}
    </#list>

    private static final Set<Rule> rules = new HashSet<Rule>(${rulesSize});

    @PostConstruct
    public void init() {
        <#list ruleNames as ruleName>
            rules.add(${ruleName});
        </#list>
    }

    @Override
    public Collection<Rule> getRules( final ${defSetClassName} pojo ) {
        return rules;
    }

    @Override
    public boolean accepts( final Class<?> type ) {
        return ${defSetClassName}.class.equals( type );
    }

}
