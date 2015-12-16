/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.CancellableRepeatingCommand;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.RowInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.Status;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

/**
 * This class handles processing the analysis itself. It supports "batched" processing
 * of different "chunks". State is a snapshot of the Checks at the time the RepeatingCommand
 * was instantiated.
 */
public class ChecksRepeatingCommand implements CancellableRepeatingCommand {

    private static final int BLOCK_SIZE = 10;

    private boolean isCancelled = false;
    private int startRowInspectorIndex = 0;
    private int endRowInspectorIndex = BLOCK_SIZE;

    private Map<RowInspector, Set<Check>> set = new HashMap<RowInspector, Set<Check>>();
    private Map<RowInspector, Set<Check>> rechecks = new HashMap<RowInspector, Set<Check>>();
    private List<RowInspector> rowInspectorsToCheck = new ArrayList<RowInspector>();

    private ParameterizedCommand<Status> onStatus;
    private Command onCompletion;

    public ChecksRepeatingCommand( final Map<RowInspector, Set<Check>> set,
                                   final Map<RowInspector, Set<Check>> rechecks,
                                   final ParameterizedCommand<Status> onStatus,
                                   final Command onCompletion ) {
        this.set.putAll( set );
        this.rechecks.putAll( rechecks );
        this.rowInspectorsToCheck.addAll( set.keySet() );
        this.onStatus = onStatus;
        this.onCompletion = onCompletion;
        this.startRowInspectorIndex = 0;
        this.endRowInspectorIndex = Math.min( rowInspectorsToCheck.size(),
                                              BLOCK_SIZE );
    }

    public boolean execute() {
        for ( int ri = startRowInspectorIndex; ri < endRowInspectorIndex; ri++ ) {
            if ( isCancelled() ) {
                return false;
            }

            final RowInspector rowInspector = rowInspectorsToCheck.get( ri );
            final Set<Check> rowInspectorChecks = set.get( rowInspector );

            if ( onStatus != null ) {
                onStatus.execute( new Status( startRowInspectorIndex,
                                              endRowInspectorIndex,
                                              rowInspectorsToCheck.size() ) );
            }

            for ( Check check : rowInspectorChecks ) {
                if ( isCancelled() ) {
                    return false;
                }

                if ( check instanceof OneToManyCheck ) {
                    final Set<Check> existingRechecks = rechecks.get( rowInspector );
                    if ( existingRechecks == null ) {
                        rechecks.put( rowInspector,
                                      new HashSet<Check>( rowInspectorChecks ) );
                    } else {
                        existingRechecks.addAll( rowInspectorChecks );
                    }
                }
            }
            final Set<Check> checksToRun = rechecks.get( rowInspector );
            for ( Check checkToRun : checksToRun ) {
                if ( isCancelled() ) {
                    return false;
                }

                checkToRun.check();
            }
        }
        startRowInspectorIndex = endRowInspectorIndex + 1;
        endRowInspectorIndex = endRowInspectorIndex + BLOCK_SIZE;
        if ( endRowInspectorIndex > rowInspectorsToCheck.size() - 1 ) {
            endRowInspectorIndex = rowInspectorsToCheck.size() - 1;
        }

        if ( startRowInspectorIndex > rowInspectorsToCheck.size() - 1 ) {
            complete();
            return false;
        }
        return true;
    }

    private boolean isCancelled() {
        if ( isCancelled ) {
            complete();
        }
        return isCancelled;
    }

    private void complete() {
        if ( onCompletion != null ) {
            onCompletion.execute();
        }
        rechecks.clear();
    }

    @Override
    public void cancel() {
        isCancelled = true;
    }

}
