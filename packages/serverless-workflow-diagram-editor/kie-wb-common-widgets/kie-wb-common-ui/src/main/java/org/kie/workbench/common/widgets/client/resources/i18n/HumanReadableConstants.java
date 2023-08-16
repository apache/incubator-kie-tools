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


package org.kie.workbench.common.widgets.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface HumanReadableConstants
        extends Messages {

    public static final HumanReadableConstants INSTANCE = GWT.create(HumanReadableConstants.class);

    String isEqualTo();

    String isNotEqualTo();

    String isEqualToNull();

    String isNotEqualToNull();

    String isLessThan();

    String lessThanOrEqualTo();

    String greaterThan();

    String greaterThanOrEqualTo();

    String orEqualTo();

    String orNotEqualTo();

    String andNotEqualTo();

    String andGreaterThan();

    String orGreaterThan();

    String orLessThan();

    String andLessThan();

    String orGreaterThanOrEqualTo();

    String orLessThanOrEqualTo();

    String andGreaterThanOrEqualTo();

    String andLessThanOrEqualTo();

    String andContains();

    String orContains();

    String andMatches();

    String orMatches();

    String orExcludes();

    String andExcludes();

    String soundsLike();

    String ThereIsNo();

    String ThereExists();

    String orAfter();

    String orBefore();

    String orCoincides();

    String andAfter();

    String andBefore();

    String andCoincides();

    String orDuring();

    String orFinishes();

    String orFinishedBy();

    String orIncludes();

    String orMeets();

    String orMetBy();

    String orOverlaps();

    String orOverlappedBy();

    String orStarts();

    String orStartedBy();

    String addDuring();

    String andFinishes();

    String andFinishedBy();

    String andIncluded();

    String andMeets();

    String andMetBy();

    String andOverlaps();

    String andOverlappedBy();

    String andStarts();

    String andStartedBy();

    String AnyOf1();

    String isContainedInTheFollowingList();

    String isNotContainedInTheFollowingList();

    String OverCEPWindow();

    String OverCEPWindowTime();

    String OverCEPWindowLength();

    String noCEPWindow();

    String From();

    String FromAccumulate();

    String FromCollect();

    String FromEntryPoint();

    String Insert();

    String LogicallyInsert();

    String Retract();

    String Set();

    String CallMethod();

    String Modify();
}
