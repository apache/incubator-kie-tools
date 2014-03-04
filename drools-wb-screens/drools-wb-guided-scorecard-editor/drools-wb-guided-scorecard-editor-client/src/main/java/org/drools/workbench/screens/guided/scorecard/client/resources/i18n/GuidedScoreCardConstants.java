/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.screens.guided.scorecard.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * Explorer I18N constants
 */
public interface GuidedScoreCardConstants
        extends
        Messages {

    public static final GuidedScoreCardConstants INSTANCE = GWT.create( GuidedScoreCardConstants.class );

    public String scorecard();

    public String newGuidedScoreCardDescription();

    public String scorecardCharacteristics();

    public String scoreCardTitle0( final String scoreCardName );

    public String setupParameters();

    public String characteristics();

    public String facts();

    public String resultantScoreField();

    public String initialScore();

    public String useReasonCodes();

    public String resultantReasonCodesField();

    public String reasonCodesAlgorithm();

    public String baselineScore();

    public String addCharacteristic();

    public String addAttribute();

    public String untitled();

    public String promptDeleteCharacteristic0( final String characteristicName );

    public String promptDeleteAttribute();

    public String notApplicable();

    public String removeCharacteristic();

    public String name();

    public String fact();

    public String characteristic();

    public String reasonCode();

    public String remove();

    public String operator();

    public String value();

    public String partialScore();

    public String actions();

    public String ScoreCardEditorTitle();

    String guidedScoreCardResourceTypeDescription();
}
