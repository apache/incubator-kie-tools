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

package org.kie.workbench.common.screens.server.management.client.util;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.server.management.model.MergeMode;

public enum ClientMergeMode {
    MERGE_COLLECTIONS( Constants.ClientMergeMode_MergeCollections, MergeMode.MERGE_COLLECTIONS ),
    KEEP_ALL( Constants.ClientMergeMode_KeepAll, MergeMode.KEEP_ALL ),
    OVERRIDE_ALL( Constants.ClientMergeMode_OverrideAll, MergeMode.OVERRIDE_ALL ),
    OVERRIDE_EMPTY( Constants.ClientMergeMode_OverrideEmpty, MergeMode.OVERRIDE_EMPTY );

    private final String valueTranslationKey;
    private final MergeMode mergeMode;

    ClientMergeMode( final String valueTranslationKey,
                     final MergeMode mergeMode ) {
        this.valueTranslationKey = valueTranslationKey;
        this.mergeMode = mergeMode;
    }

    public String getValue( final TranslationService translationService ) {
        return translationService.format( valueTranslationKey );
    }

    public MergeMode getMergeMode() {
        return mergeMode;
    }

    public static ClientMergeMode convert( final MergeMode mergeMode ) {
        switch ( mergeMode ) {
            case KEEP_ALL:
                return ClientMergeMode.KEEP_ALL;
            case OVERRIDE_ALL:
                return ClientMergeMode.OVERRIDE_ALL;
            case OVERRIDE_EMPTY:
                return ClientMergeMode.OVERRIDE_EMPTY;
            case MERGE_COLLECTIONS:
                return ClientMergeMode.MERGE_COLLECTIONS;
        }
        throw new RuntimeException( "Invalid parameter" );
    }

    public static ClientMergeMode convert( final String mergeMode, final TranslationService translationService ) {
        for ( ClientMergeMode clientMergeMode : ClientMergeMode.values() ) {
            if ( mergeMode.equals( clientMergeMode.getValue( translationService ) ) ) {
                return clientMergeMode;
            }
        }

        return ClientMergeMode.KEEP_ALL;
    }

    public static List<String> listMergeModeValues( final TranslationService translationService ) {
        List<String> mergeModeValues = new ArrayList<String>();

        for ( ClientMergeMode clientMergeMode : ClientMergeMode.values() ) {
            mergeModeValues.add( clientMergeMode.getValue( translationService ) );
        }

        return mergeModeValues;
    }

}
