package org.kie.workbench.common.screens.server.management.client.util;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.server.management.model.MergeMode;

/**
 * TODO: update me
 */
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
