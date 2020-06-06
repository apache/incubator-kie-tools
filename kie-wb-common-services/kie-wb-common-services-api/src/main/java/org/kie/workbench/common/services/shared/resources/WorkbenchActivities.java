package org.kie.workbench.common.services.shared.resources;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.workbench.model.AppFormerActivities;

import static org.kie.workbench.common.services.shared.resources.EditorIds.CASE_MODELLER;
import static org.kie.workbench.common.services.shared.resources.EditorIds.GUIDED_DECISION_TREE;
import static org.kie.workbench.common.services.shared.resources.EditorIds.GUIDED_SCORE_CARD;
import static org.kie.workbench.common.services.shared.resources.EditorIds.SCENARIO_SIMULATION_DESIGNER;
import static org.kie.workbench.common.services.shared.resources.EditorIds.STUNNER_DESIGNER;
import static org.kie.workbench.common.services.shared.resources.EditorIds.XLS_SCORE_CARD;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.ADMIN;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.ADMINISTRATION;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.APPS;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.ARCHETYPE_MANAGEMENT;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.CONTENT_MANAGEMENT;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.DATASET_AUTHORING;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.DATASOURCE_MANAGEMENT;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.DATA_TRANSFER;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.DROOLS_ADMIN;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.EXECUTION_ERRORS;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.EXPERIMENTAL_FEATURES;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.GUVNOR_M2REPO;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.HOME;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.JOBS;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.LIBRARY;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.PLANNER_ADMIN;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.PLUGIN_AUTHORING;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.PROCESS_DASHBOARD;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.PROCESS_DEFINITIONS;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.PROCESS_INSTANCES;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.PROVISIONING;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.SECURITY_MANAGEMENT;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.SERVER_MANAGEMENT;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.SERVICE_TASK_ADMIN;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.SSH_KEYS_EDITOR;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.TASKS;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.TASKS_ADMIN;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.TASK_DASHBOARD;

@ApplicationScoped
public class WorkbenchActivities implements AppFormerActivities {

    @Override
    public List<String> getAllEditorIds() {
        return Arrays.asList(GUIDED_DECISION_TREE, GUIDED_SCORE_CARD, XLS_SCORE_CARD, STUNNER_DESIGNER, CASE_MODELLER, SCENARIO_SIMULATION_DESIGNER);
    }

    @Override
    public List<String> getAllPerpectivesIds() {
        return Arrays.asList(HOME, SECURITY_MANAGEMENT, GUVNOR_M2REPO, ADMINISTRATION, LIBRARY, DROOLS_ADMIN, PLANNER_ADMIN, PROCESS_DEFINITIONS, PROCESS_INSTANCES, PLUGIN_AUTHORING, APPS, DATASET_AUTHORING, PROVISIONING, SERVER_MANAGEMENT, JOBS, EXECUTION_ERRORS, TASKS, TASKS_ADMIN, PROCESS_DASHBOARD, TASK_DASHBOARD, CONTENT_MANAGEMENT, DATASOURCE_MANAGEMENT, ADMIN, EXPERIMENTAL_FEATURES, SSH_KEYS_EDITOR, SERVICE_TASK_ADMIN, DATA_TRANSFER, ARCHETYPE_MANAGEMENT);
    }
}

