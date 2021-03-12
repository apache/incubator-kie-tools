/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.services.verifier.plugin.client;

import java.util.Set;

import org.drools.verifier.api.Status;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Issues;
import org.drools.verifier.core.checks.base.CheckRunner;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.Index;
import org.drools.verifier.core.main.Analyzer;
import org.drools.verifier.core.main.Reporter;
import org.drools.workbench.services.verifier.plugin.client.api.DeleteColumns;
import org.drools.workbench.services.verifier.plugin.client.api.DrlInitialize;
import org.drools.workbench.services.verifier.plugin.client.api.MakeRule;
import org.drools.workbench.services.verifier.plugin.client.api.NewColumn;
import org.drools.workbench.services.verifier.plugin.client.api.RemoveRule;
import org.drools.workbench.services.verifier.plugin.client.api.SortTable;
import org.drools.workbench.services.verifier.plugin.client.api.Update;
import org.drools.workbench.services.verifier.plugin.client.builders.BuildException;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.verifier.api.client.api.RequestStatus;
import org.kie.workbench.common.services.verifier.api.client.api.WebWorkerException;

public class Receiver {

    private Analyzer analyzer;
    private Poster poster;
    private CheckRunner checkRunner;
    private Issues latestReport;
    private Index index;
    private AnalyzerConfiguration configuration;

    public Receiver(final Poster poster,
                    final CheckRunner checkRunner) {
        this.poster = PortablePreconditions.checkNotNull("poster",
                                                         poster);
        this.checkRunner = PortablePreconditions.checkNotNull("checkRunner",
                                                              checkRunner);
    }

    public void received(final Object o) {
        if (o instanceof DrlInitialize) {
            init((DrlInitialize) o);
        } else if (o instanceof RequestStatus) {
            requestStatus();
        } else if (o instanceof RemoveRule) {
            removeRule((RemoveRule) o);
        } else if (o instanceof Update) {
            update((Update) o);
        } else if (o instanceof DeleteColumns) {
            deleteColumns((DeleteColumns) o);
        } else if (o instanceof MakeRule) {
            makeRule((MakeRule) o);
        } else if (o instanceof NewColumn) {
            newColumn((NewColumn) o);
        } else if (o instanceof SortTable) {
            sortTable((SortTable) o);
        }
    }

    private void sortTable(final SortTable sortTable) {
        try {
            getUpdateManager().sort(sortTable.getRowOrder());
        } catch (final Exception e) {
            poster.post(new WebWorkerException("Failed to sort the table: " +
                                                       e.getMessage()));
        }
    }

    private void removeRule(final RemoveRule removeRule) {
        try {
            getUpdateManager().removeRule(removeRule.getDeletedRow());
        } catch (final Exception e) {
            poster.post(new WebWorkerException("Failed to remove a rule: " +
                                                       e.getMessage()));
        }
    }

    private void deleteColumns(final DeleteColumns deleteColumns) {
        try {
            getUpdateManager().deleteColumns(deleteColumns.getFirstColumnIndex(),
                                             deleteColumns.getNumberOfColumns());
        } catch (final Exception e) {
            poster.post(new WebWorkerException("Deleting columns failed: " +
                                                       e.getMessage()));
        }
    }

    private void update(final Update update) {
        try {
            getUpdateManager().update(update.getModel(),
                                      update.getCoordinates());
        } catch (final Exception e) {
            poster.post(new WebWorkerException("Dtable update failed: " +
                                                       e.getMessage()));
        }
    }

    private void requestStatus() {
        if (latestReport != null) {
            poster.post(latestReport);
        }
    }

    private void newColumn(final NewColumn newColumn) {
        try {
            getUpdateManager().newColumn(newColumn.getModel(),
                                         newColumn.getHeaderMetaData(),
                                         newColumn.getFactTypes(),
                                         newColumn.getColumnIndex());
        } catch (final BuildException buildException) {
            poster.post(new WebWorkerException("Adding a new column failed: " +
                                                       buildException.getMessage()));
        }
    }

    private void makeRule(final MakeRule makeRule) {
        try {
            getUpdateManager().makeRule(makeRule.getModel(),
                                        makeRule.getHeaderMetaData(),
                                        makeRule.getFactTypes(),
                                        makeRule.getIndex());
        } catch (final BuildException buildException) {
            poster.post(new WebWorkerException("Rule Creation failed: " +
                                                       buildException.getMessage()));
        }
    }

    private DTableUpdateManager getUpdateManager() {
        return new DTableUpdateManager(index,
                                       analyzer,
                                       configuration);
    }

    private void init(final DrlInitialize initialize) {
        try {
            final AnalyzerBuilder analyzerBuilder = new AnalyzerBuilder()
                    .with(initialize)
                    .with(checkRunner)
                    .with(new Reporter() {
                        @Override
                        public void sendReport(final Set<Issue> issues) {
                            latestReport = new Issues(initialize.getUuid(),
                                                      issues);
                            poster.post(latestReport);
                        }

                        @Override
                        public void sendStatus(final Status status) {
                            poster.post(status);
                        }
                    });

            analyzer = analyzerBuilder.buildAnalyzer();
            index = analyzerBuilder.getIndex();
            configuration = analyzerBuilder.getConfiguration();

            analyzer.resetChecks();
            analyzer.analyze();
        } catch (final Exception e) {
            poster.post(new WebWorkerException("Initialization failed: " +
                                                       e.getMessage()));
        } 
    }
}
