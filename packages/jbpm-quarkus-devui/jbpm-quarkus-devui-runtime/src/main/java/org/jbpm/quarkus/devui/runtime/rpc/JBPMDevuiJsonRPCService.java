/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jbpm.quarkus.devui.runtime.rpc;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import io.smallrye.mutiny.Uni;
import io.quarkus.arc.profile.IfBuildProfile;
import io.smallrye.mutiny.Multi;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jbpm.quarkus.devui.runtime.forms.FormsStorage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@IfBuildProfile("dev")
public class JBPMDevuiJsonRPCService {
    private static final String DATA_INDEX_URL = "kogito.data-index.url";

    private static final Logger LOGGER = LoggerFactory.getLogger(JBPMDevuiJsonRPCService.class);

    public static final String PROCESS_INSTANCES = "ProcessInstances";
    public static final String USER_TASKS = "UserTaskInstances";
    public static final String JOBS = "Jobs";

    public static final String ALL_TASKS_IDS_QUERY = "{ \"operationName\": \"getAllTasksIds\", \"query\": \"query getAllTasksIds{  UserTaskInstances{ id } }\" }";
    public static final String ALL_PROCESS_INSTANCES_IDS_QUERY = "{ \"operationName\": \"getAllProcessesIds\", \"query\": \"query getAllProcessesIds{  ProcessInstances{ id } }\" }";
    public static final String ALL_JOBS_IDS_QUERY = "{ \"operationName\": \"getAllJobsIds\", \"query\": \"query getAllJobsIds{  Jobs{ id } }\" }";

    private WebClient dataIndexWebClient;

    private final Vertx vertx;
    private final JBPMDevUIEventPublisher eventPublisher;
    private final FormsStorage formsStorage;
    private DataIndexCounter processesCounter;
    private DataIndexCounter tasksCounter;
    private DataIndexCounter jobsCounter;

    @Inject
    public JBPMDevuiJsonRPCService(Vertx vertx, JBPMDevUIEventPublisher eventPublisher, FormsStorage formsStorage) {
        this.vertx = vertx;
        this.eventPublisher = eventPublisher;
        this.formsStorage = formsStorage;
    }

    @PostConstruct
    public void init() {
        Optional<String> dataIndexURL = ConfigProvider.getConfig().getOptionalValue(DATA_INDEX_URL, String.class);
        dataIndexURL.ifPresent(this::initDataIndexWebClient);
    }

    private void initDataIndexWebClient(String dataIndexURL) {
        try {
            URL url = new URL(dataIndexURL);
            this.dataIndexWebClient = WebClient.create(vertx, buildWebClientOptions(url));

            String contextPath = url.getPath();
            this.processesCounter = new DataIndexCounter(ALL_PROCESS_INSTANCES_IDS_QUERY, PROCESS_INSTANCES,
                    contextPath, vertx, dataIndexWebClient);
            this.tasksCounter = new DataIndexCounter(ALL_TASKS_IDS_QUERY, USER_TASKS, contextPath, vertx, dataIndexWebClient);
            this.jobsCounter = new DataIndexCounter(ALL_JOBS_IDS_QUERY, JOBS, contextPath, vertx, dataIndexWebClient);

            this.eventPublisher.setOnProcessEventListener(processesCounter::refresh);
            this.eventPublisher.setOnTaskEventListener(tasksCounter::refresh);
            this.eventPublisher.setOnJobEventListener(jobsCounter::refresh);
        } catch (Exception ex) {
            LOGGER.warn("Cannot configure dataIndexWebClient with 'kogito.data-index.url'='{}':", dataIndexURL, ex);
        }
    }

    protected WebClientOptions buildWebClientOptions(URL dataIndexURL) throws MalformedURLException {
        return new WebClientOptions()
                .setDefaultHost(dataIndexURL.getHost())
                .setDefaultPort((dataIndexURL.getPort() != -1 ? dataIndexURL.getPort() : dataIndexURL.getDefaultPort()))
                .setSsl(dataIndexURL.getProtocol().compareToIgnoreCase("https") == 0);
    }

    public Multi<String> queryProcessInstancesCount() {
        return processesCounter.getMulti();
    }

    public Multi<String> queryTasksCount() {
        return tasksCounter.getMulti();
    }

    public Multi<String> queryJobsCount() {
        return jobsCounter.getMulti();
    }

    public Uni<String> getFormsCount() {
        return Uni.createFrom().item(String.valueOf(this.formsStorage.getFormsCount()));
    }

    @PreDestroy
    public void destroy() {
        processesCounter.stop();
        tasksCounter.stop();
        jobsCounter.stop();
    }
}