/*
 * Copyright 2011 JBoss Inc
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
package org.drools.workbench.screens.guided.dtable.client.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.workbench.models.datamodel.oracle.OperatorsOracle;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactFieldsPattern;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;

/**
 * Guided Decision Table Wizard validator
 */
public class Validator {

    private List<CompositeColumn<? extends BaseColumn>> patternsConditions;
    private List<Pattern52> patternsActions;
    private Map<Pattern52, List<ActionSetFieldCol52>> patternToActionSetFieldsMap;
    private Map<ActionInsertFactFieldsPattern, List<ActionInsertFactCol52>> patternToActionInsertFactFieldsMap;

    public Validator() {
        this( new ArrayList<CompositeColumn<? extends BaseColumn>>() );
    }

    public Validator( List<CompositeColumn<? extends BaseColumn>> patterns ) {
        this.patternsConditions = patterns;
        this.patternsActions = new ArrayList<Pattern52>();
    }

    public void addActionPattern( Pattern52 pattern ) {
        this.patternsActions.add( pattern );
    }

    public void removeActionPattern( Pattern52 pattern ) {
        this.patternsActions.remove( pattern );
    }

    public boolean arePatternBindingsUnique() {

        boolean hasUniqueBindings = true;

        //Store Patterns by their binding
        Map<String, List<Pattern52>> bindings = new HashMap<String, List<Pattern52>>();
        for ( CompositeColumn<? extends BaseColumn> cc : patternsConditions ) {
            if ( cc instanceof Pattern52 ) {
                Pattern52 p = (Pattern52) cc;
                String binding = p.getBoundName();
                if ( binding != null && !binding.equals( "" ) ) {
                    List<Pattern52> ps = bindings.get( binding );
                    if ( ps == null ) {
                        ps = new ArrayList<Pattern52>();
                        bindings.put( binding,
                                      ps );
                    }
                    ps.add( p );
                }
            }
        }
        for ( Pattern52 p : patternsActions ) {
            String binding = p.getBoundName();
            if ( binding != null && !binding.equals( "" ) ) {
                List<Pattern52> ps = bindings.get( binding );
                if ( ps == null ) {
                    ps = new ArrayList<Pattern52>();
                    bindings.put( binding,
                                  ps );
                }
                ps.add( p );
            }
        }

        //Check if any bindings have multiple Patterns
        for ( List<Pattern52> pws : bindings.values() ) {
            if ( pws.size() > 1 ) {
                hasUniqueBindings = false;
                break;
            }
        }
        return hasUniqueBindings;
    }

    public boolean isPatternBindingUnique( Pattern52 pattern ) {
        String binding = pattern.getBoundName();
        if ( binding == null || binding.equals( "" ) ) {
            return true;
        }
        for ( CompositeColumn<? extends BaseColumn> cc : patternsConditions ) {
            if ( cc instanceof Pattern52 ) {
                Pattern52 p = (Pattern52) cc;
                if ( p != pattern ) {
                    if ( p.getBoundName() != null && p.getBoundName().equals( binding ) ) {
                        return false;
                    }
                }
            }
        }
        for ( Pattern52 p : patternsActions ) {
            if ( p != pattern ) {
                if ( p.getBoundName() != null && p.getBoundName().equals( binding ) ) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isPatternValid( Pattern52 p ) {
        return !( p.getBoundName() == null || p.getBoundName().equals( "" ) );
    }

    public boolean isConditionValid( ConditionCol52 c ) {
        return isConditionHeaderValid( c )
                && isConditionOperatorValid( c )
                && isConditionLimitedEntryValueValid( c );
    }

    public boolean isConditionHeaderValid( ConditionCol52 c ) {
        return !( c.getHeader() == null || c.getHeader().equals( "" ) );
    }

    public boolean isConditionOperatorValid( ConditionCol52 c ) {
        if ( c.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE ) {
            return true;
        }
        return !( c.getOperator() == null || c.getOperator().equals( "" ) );
    }

    public boolean isActionValid( ActionCol52 c ) {
        return isActionHeaderValid( c );
    }

    public boolean isActionHeaderValid( ActionCol52 a ) {
        return !( a.getHeader() == null || a.getHeader().equals( "" ) );
    }

    public void setPatternToActionSetFieldsMap( Map<Pattern52, List<ActionSetFieldCol52>> patternToActionSetFieldsMap ) {
        this.patternToActionSetFieldsMap = patternToActionSetFieldsMap;
    }

    public boolean arePatternActionSetFieldsValid( Pattern52 p ) {
        if ( patternToActionSetFieldsMap == null ) {
            return true;
        }
        List<ActionSetFieldCol52> actions = patternToActionSetFieldsMap.get( p );
        if ( actions == null ) {
            return true;
        }
        for ( ActionSetFieldCol52 a : actions ) {
            if ( !isActionValid( a ) ) {
                return false;
            }
        }
        return true;
    }

    public void setPatternToActionInsertFactFieldsMap( Map<ActionInsertFactFieldsPattern, List<ActionInsertFactCol52>> patternToActionInsertFactFieldsMap ) {
        this.patternToActionInsertFactFieldsMap = patternToActionInsertFactFieldsMap;
    }

    public boolean arePatternActionInsertFactFieldsValid( Pattern52 p ) {
        if ( patternToActionInsertFactFieldsMap == null ) {
            return true;
        }
        List<ActionInsertFactCol52> actions = patternToActionInsertFactFieldsMap.get( p );
        if ( actions == null ) {
            return true;
        }
        for ( ActionInsertFactCol52 a : actions ) {
            if ( !isActionValid( a ) ) {
                return false;
            }
        }
        return true;
    }

    public boolean doesOperatorNeedValue( ConditionCol52 c ) {
        String operator = c.getOperator();
        if ( operator == null || operator.equals( "" ) ) {
            return false;
        }
        return !( operator.equals( "== null" ) || operator.equals( "!= null" ) );
    }

    public boolean doesOperatorAcceptValueList( ConditionCol52 c ) {
        String operator = c.getOperator();
        if ( operator == null || operator.equals( "" ) ) {
            return false;
        }
        return !( operator.equals( "== null" ) || operator.equals( "!= null" ) );
    }

    public boolean doesOperatorAcceptCommaSeparatedValues( ConditionCol52 c ) {
        final List<String> ops = Arrays.asList( OperatorsOracle.EXPLICIT_LIST_OPERATORS );
        return ops.contains( c.getOperator() );
    }

    public boolean isConditionLimitedEntryValueValid( ConditionCol52 c ) {
        if ( !( c instanceof LimitedEntryConditionCol52 ) ) {
            return true;
        }
        LimitedEntryConditionCol52 lec = (LimitedEntryConditionCol52) c;
        boolean doesOperatorNeedValue = doesOperatorNeedValue( lec );
        boolean hasValue = hasValue( lec );
        return ( doesOperatorNeedValue && hasValue ) || ( !doesOperatorNeedValue && !hasValue );
    }

    public boolean canPatternBeRemoved( final Pattern52 pattern ) {
        List<ActionSetFieldCol52> actionSetFields = patternToActionSetFieldsMap.get( pattern );
        return ( actionSetFields == null || actionSetFields.isEmpty() );
    }

    public boolean isTypeUsed( final String fqcn ) {
        String simpleType = fqcn;
        int dotIndex = simpleType.lastIndexOf( "." );
        if ( dotIndex > 0 ) {
            simpleType = simpleType.substring( dotIndex + 1 );
        }
        for ( CompositeColumn<? extends BaseColumn> cc : patternsConditions ) {
            if ( cc instanceof Pattern52 ) {
                Pattern52 p = (Pattern52) cc;
                if ( p.getFactType().equals( simpleType ) ) {
                    return true;
                }
            }
        }
        for ( Pattern52 p : patternsActions ) {
            if ( p.getFactType().equals( simpleType ) ) {
                return true;
            }
        }
        return false;
    }

    private boolean hasValue( LimitedEntryConditionCol52 lec ) {
        if ( lec.getValue() == null ) {
            return false;
        }
        return lec.getValue().hasValue();
    }

}
