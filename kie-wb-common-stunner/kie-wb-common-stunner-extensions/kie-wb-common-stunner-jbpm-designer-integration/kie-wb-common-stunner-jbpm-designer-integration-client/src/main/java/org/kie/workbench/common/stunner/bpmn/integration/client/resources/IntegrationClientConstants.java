/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.integration.client.resources;

import org.jboss.errai.ui.shared.api.annotations.TranslationKey;

public interface IntegrationClientConstants {

    @TranslationKey(defaultValue = "Migrate Diagram")
    String MigrateAction = "integration.actions.migrate";

    @TranslationKey(defaultValue = "Migrate Diagram")
    String MigrateActionTitle = "integration.actions.migrateTitle";

    @TranslationKey(defaultValue = "Warning this action cannot be undone")
    String MigrateToJBPMDesignerActionWarning = "integration.actions.migrate.toJBPMDesigner.migrateWarning";

    @TranslationKey(defaultValue = "Confirm Migrate")
    String MigrateToJBPMDesignerConfirmAction = "integration.actions.migrate.toJBPMDesigner.migrateConfirmAction";

    @TranslationKey(defaultValue = "Confirm Migrate")
    String MigrateToStunnerConfirmAction = "integration.actions.migrate.toStunner.migrateConfirmAction";

    @TranslationKey(defaultValue = "File {0} was migrated from Stunner to jBPM designer")
    String MigrateToJBPMDesignerCommitMessage = "integration.actions.migrate.toJBPMDesigner.migrateCommitMessage";

    @TranslationKey(defaultValue = "File {0} was migrated from jBPM designer to Stunner")
    String MigrateToStunnerCommitMessage = "integration.actions.migrate.toStunner.migrateCommitMessage";

    @TranslationKey(defaultValue = "An error was produced during migration")
    String MigrateErrorGeneric = "integration.actions.migrate.migrateErrorGeneric";

    @TranslationKey(defaultValue = "")
    String MigrateToStunnerErrorsProducedMessage = "integration.actions.migrate.toStunner.migrateErrorsProducedMessage";

    @TranslationKey(defaultValue = "")
    String MigrateToStunnerInfoWarningsProducedMessage = "integration.actions.migrate.toStunner.migrateInfoWarningsProducedMessage";

    @TranslationKey(defaultValue = "")
    String MigrateDiagramSuccessfullyMigratedMessage = "integration.actions.migrate.message.MigrateDiagramSuccessfullyMigratedMessage";

    @TranslationKey(defaultValue = "Information")
    String MigrateActionConfirmSaveInformationTitle = "integration.actions.confirmSave.informationTitle";

    @TranslationKey(defaultValue = "")
    String MigrateActionConfirmSaveMessage = "integration.actions.confirmSave.confirmMessage";

    @TranslationKey(defaultValue = "")
    String MigrateActionUnexpectedErrorMessage = "integration.actions.migrate.message.unExpectedErrorMessage";

    @TranslationKey(defaultValue = "No diagram has been returned by the migration process")
    String MigrateToStunnerNoDiagramHasBeenReturned = "integration.actions.migrate.toStunner.migrateNoDiagramHasBeenReturned";
}
