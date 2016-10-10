/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.basicset.definition.icon.statics;

import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.shapes.def.icon.statics.Icons;

public class StaticIcons {

    public static String getIconDefinitionId( final Icons icon ) {
        Class<?> type = null;
        switch ( icon ) {
            case BUSINESS_RULE:
                type = BusinessRuleIcon.class;
                break;
            case USER:
                type = UserIcon.class;
                break;
            case SCRIPT:
                type = ScriptIcon.class;
                break;
            case TIMER:
                type = TimerIcon.class;
                break;

        }
        if ( null != type ) {
            return getDefinitionId( type );

        }
        return null;

    }

    private static String getDefinitionId( final Class<?> type ) {
        return BindableAdapterUtils.getDefinitionId( type );
    }

}
