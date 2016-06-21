/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.action;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.HumanReadable;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsConflicting;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsRedundant;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsSubsuming;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Action;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

public class ActionInspector
        implements IsRedundant,
                   IsSubsuming,
                   IsConflicting,
                   HumanReadable {

    private static final String DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();
    private static final DateTimeFormat DATE_FORMATTER = DateTimeFormat.getFormat( DATE_FORMAT );

    private Action action;

    public ActionInspector( final Action action ) {
        this.action = action;
    }

    @Override
    public boolean isRedundant( final Object other ) {
        if ( other instanceof ActionInspector ) {
            if ( !areFieldsEqual( ( ActionInspector ) other ) ) {
                return false;
            } else {
            return isValueRedundant( (( ActionInspector ) other).action.getValue() );
            }
        } else {
            return false;
        }
    }

    private boolean isValueRedundant( final Comparable other ) {
        if ( action.getValue().equals( other ) ) {
            return true;
        } else if ( action.getValue() instanceof Date ) {
            return areDatesEqual( ( Date ) action.getValue(),
                                  other );
        } else if ( other instanceof Date ) {
            return areDatesEqual( ( Date ) other,
                                  action.getValue() );
        } else {
            return action.getValue().toString().equals( other.toString() );
        }
    }

    private boolean areDatesEqual( final Date value,
                                   final Comparable other ) {
        if ( other instanceof String ) {
            return format( value ).toString().equals( other );
        } else {
            return false;
        }
    }

    protected String format( final Date dateValue ) {
        return DATE_FORMATTER.format( dateValue );
    }

    @Override
    public boolean conflicts( final Object other ) {
        if ( other instanceof ActionInspector ) {
            final ActionInspector otherActionInspector = ( ActionInspector ) other;
            if ( areFieldsEqual( otherActionInspector ) ) {
                return !isValueRedundant( otherActionInspector.action.getValue() );
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean subsumes( final Object other ) {
        // At the moment we are not smart enough to figure out subsumption in the RHS.
        // So redundancy == subsumption in this case.
        return isRedundant( other );
    }

    public String toHumanReadableString() {
        return action.getField().getName() + " = " + action.getValue();
    }

    public boolean hasValue() {
        return action.getValue() != null;
    }

    private boolean areFieldsEqual( final ActionInspector other ) {
        return action.getField().equals( other.action.getField() );
    }
}
