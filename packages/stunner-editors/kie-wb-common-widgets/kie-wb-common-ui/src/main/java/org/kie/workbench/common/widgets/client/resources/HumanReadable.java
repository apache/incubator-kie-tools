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


package org.kie.workbench.common.widgets.client.resources;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.widgets.client.resources.i18n.HumanReadableConstants;

/**
 * This contains some simple mappings between operators, conditional elements and the human readable
 * equivalent.
 * <p/>
 * Yes, I am making the presumption that programmers are not human,
 * but I think they (we) are cool with that.
 */
public class HumanReadable {

    public static       Map<String, String> operatorDisplayMap          = new HashMap<String, String>();
    public static       Map<String, String> operatorExtensionDisplayMap = new HashMap<String, String>();
    public static       Map<String, String> ceDisplayMap                = new HashMap<String, String>();
    public static       Map<String, String> actionDisplayMap            = new HashMap<String, String>();
    public static final String[]            CONDITIONAL_ELEMENTS        = new String[]{ "not", "exists", "or" };
    public static final String[]            FROM_CONDITIONAL_ELEMENTS   = new String[]{ "from", "from accumulate", "from collect", "from entry-point" };

    static {
        operatorDisplayMap.put( "==", HumanReadableConstants.INSTANCE.isEqualTo() );
        operatorDisplayMap.put( "!=", HumanReadableConstants.INSTANCE.isNotEqualTo() );
        operatorDisplayMap.put( "<", HumanReadableConstants.INSTANCE.isLessThan() );
        operatorDisplayMap.put( "<=", HumanReadableConstants.INSTANCE.lessThanOrEqualTo() );
        operatorDisplayMap.put( ">", HumanReadableConstants.INSTANCE.greaterThan() );
        operatorDisplayMap.put( ">=", HumanReadableConstants.INSTANCE.greaterThanOrEqualTo() );
        operatorDisplayMap.put( "|| ==", HumanReadableConstants.INSTANCE.orEqualTo() );
        operatorDisplayMap.put( "|| !=", HumanReadableConstants.INSTANCE.orNotEqualTo() );
        operatorDisplayMap.put( "&& !=", HumanReadableConstants.INSTANCE.andNotEqualTo() );
        operatorDisplayMap.put( "&& >", HumanReadableConstants.INSTANCE.andGreaterThan() );
        operatorDisplayMap.put( "&& <", HumanReadableConstants.INSTANCE.andLessThan() );
        operatorDisplayMap.put( "|| >", HumanReadableConstants.INSTANCE.orGreaterThan() );
        operatorDisplayMap.put( "|| <", HumanReadableConstants.INSTANCE.orLessThan() );
        operatorDisplayMap.put( "&& <", HumanReadableConstants.INSTANCE.andLessThan() );
        operatorDisplayMap.put( "|| >=", HumanReadableConstants.INSTANCE.orGreaterThanOrEqualTo() );
        operatorDisplayMap.put( "|| <=", HumanReadableConstants.INSTANCE.orLessThanOrEqualTo() );
        operatorDisplayMap.put( "&& >=", HumanReadableConstants.INSTANCE.andGreaterThanOrEqualTo() );
        operatorDisplayMap.put( "&& <=", HumanReadableConstants.INSTANCE.andLessThanOrEqualTo() );
        operatorDisplayMap.put( "&& contains", HumanReadableConstants.INSTANCE.andContains() );
        operatorDisplayMap.put( "|| contains", HumanReadableConstants.INSTANCE.orContains() );
        operatorDisplayMap.put( "&& matches", HumanReadableConstants.INSTANCE.andMatches() );
        operatorDisplayMap.put( "|| matches", HumanReadableConstants.INSTANCE.orMatches() );
        operatorDisplayMap.put( "|| excludes", HumanReadableConstants.INSTANCE.orExcludes() );
        operatorDisplayMap.put( "&& excludes", HumanReadableConstants.INSTANCE.andExcludes() );
        operatorDisplayMap.put( "soundslike", HumanReadableConstants.INSTANCE.soundsLike() );
        operatorDisplayMap.put( "in", HumanReadableConstants.INSTANCE.isContainedInTheFollowingList() );
        operatorDisplayMap.put( "not in", HumanReadableConstants.INSTANCE.isNotContainedInTheFollowingList() );
        operatorDisplayMap.put( "== null", HumanReadableConstants.INSTANCE.isEqualToNull() );
        operatorDisplayMap.put( "!= null", HumanReadableConstants.INSTANCE.isNotEqualToNull() );

        operatorDisplayMap.put( "|| after", HumanReadableConstants.INSTANCE.orAfter() );
        operatorDisplayMap.put( "|| before", HumanReadableConstants.INSTANCE.orBefore() );
        operatorDisplayMap.put( "|| coincides", HumanReadableConstants.INSTANCE.orCoincides() );
        operatorDisplayMap.put( "&& after", HumanReadableConstants.INSTANCE.andAfter() );
        operatorDisplayMap.put( "&& before", HumanReadableConstants.INSTANCE.andBefore() );
        operatorDisplayMap.put( "&& coincides", HumanReadableConstants.INSTANCE.andCoincides() );
        operatorDisplayMap.put( "|| during", HumanReadableConstants.INSTANCE.orDuring() );
        operatorDisplayMap.put( "|| finishes", HumanReadableConstants.INSTANCE.orFinishes() );
        operatorDisplayMap.put( "|| finishedby", HumanReadableConstants.INSTANCE.orFinishedBy() );
        operatorDisplayMap.put( "|| includes", HumanReadableConstants.INSTANCE.orIncludes() );
        operatorDisplayMap.put( "|| meets", HumanReadableConstants.INSTANCE.orMeets() );
        operatorDisplayMap.put( "|| metby", HumanReadableConstants.INSTANCE.orMetBy() );
        operatorDisplayMap.put( "|| overlaps", HumanReadableConstants.INSTANCE.orOverlaps() );
        operatorDisplayMap.put( "|| overlappedby", HumanReadableConstants.INSTANCE.orOverlappedBy() );
        operatorDisplayMap.put( "|| starts", HumanReadableConstants.INSTANCE.orStarts() );
        operatorDisplayMap.put( "|| startedby", HumanReadableConstants.INSTANCE.orStartedBy() );
        operatorDisplayMap.put( "&& during", HumanReadableConstants.INSTANCE.addDuring() );
        operatorDisplayMap.put( "&& finishes", HumanReadableConstants.INSTANCE.andFinishes() );
        operatorDisplayMap.put( "&& finishedby", HumanReadableConstants.INSTANCE.andFinishedBy() );
        operatorDisplayMap.put( "&& includes", HumanReadableConstants.INSTANCE.andIncluded() );
        operatorDisplayMap.put( "&& meets", HumanReadableConstants.INSTANCE.andMeets() );
        operatorDisplayMap.put( "&& metby", HumanReadableConstants.INSTANCE.andMetBy() );
        operatorDisplayMap.put( "&& overlaps", HumanReadableConstants.INSTANCE.andOverlaps() );
        operatorDisplayMap.put( "&& overlappedby", HumanReadableConstants.INSTANCE.andOverlappedBy() );
        operatorDisplayMap.put( "&& starts", HumanReadableConstants.INSTANCE.andStarts() );
        operatorDisplayMap.put( "&& startedby", HumanReadableConstants.INSTANCE.andStartedBy() );
        operatorDisplayMap.put( "over window:time", HumanReadableConstants.INSTANCE.OverCEPWindowTime() );
        operatorDisplayMap.put( "over window:length", HumanReadableConstants.INSTANCE.OverCEPWindowLength() );

        ceDisplayMap.put( "not", HumanReadableConstants.INSTANCE.ThereIsNo() );
        ceDisplayMap.put( "exists", HumanReadableConstants.INSTANCE.ThereExists() );
        ceDisplayMap.put( "or", HumanReadableConstants.INSTANCE.AnyOf1() );
        ceDisplayMap.put( "from", HumanReadableConstants.INSTANCE.From() );
        ceDisplayMap.put( "from accumulate", HumanReadableConstants.INSTANCE.FromAccumulate() );
        ceDisplayMap.put( "from collect", HumanReadableConstants.INSTANCE.FromCollect() );
        ceDisplayMap.put( "from entry-point", HumanReadableConstants.INSTANCE.FromEntryPoint() );
        ceDisplayMap.put( "from entry-point", HumanReadableConstants.INSTANCE.FromEntryPoint() );

        actionDisplayMap.put( "assert", HumanReadableConstants.INSTANCE.Insert() );
        actionDisplayMap.put( "assertLogical", HumanReadableConstants.INSTANCE.LogicallyInsert() );
        actionDisplayMap.put( "retract", HumanReadableConstants.INSTANCE.Retract() );
        actionDisplayMap.put( "set", HumanReadableConstants.INSTANCE.Set() );
        actionDisplayMap.put( "modify", HumanReadableConstants.INSTANCE.Modify() );
        actionDisplayMap.put( "call", HumanReadableConstants.INSTANCE.CallMethod() );

    }

    public static String getOperatorDisplayName( String op ) {
        return lookup( op, operatorDisplayMap );
    }

    public static String getCEDisplayName( String ce ) {
        return lookup( ce, ceDisplayMap );
    }

    private static String lookup( String ce,
                                  Map<String, String> map ) {
        String ret = map.get( ce );
        return ret == null ? ce : ret;
    }

    public static String getActionDisplayName( String action ) {
        return lookup( action, actionDisplayMap );
    }
}
