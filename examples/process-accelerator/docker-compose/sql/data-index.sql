-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements.  See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership.  The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License.  You may obtain a copy of the License at
--
--  http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied.  See the License for the
-- specific language governing permissions and limitations
-- under the License.

--
-- Data index uses below tables
--

-- TABLE attachments: user task instance attachments
CREATE TABLE attachments (
    id character varying(255) NOT NULL,
    content character varying(255),
    name character varying(255),
    updated_at timestamp without time zone,
    updated_by character varying(255),
    task_id character varying(255) NOT NULL
);

-- TABLE comments: user task instance comments
CREATE TABLE comments (
    id character varying(255) NOT NULL,
    content character varying(1000),
    updated_at timestamp without time zone,
    updated_by character varying(255),
    task_id character varying(255) NOT NULL
);

-- TABLE definitions: process definitions that has been deployed
CREATE TABLE definitions (
    id character varying(255) NOT NULL,
    version character varying(255) NOT NULL,
    name character varying(255),
    type character varying(255),
    source bytea,
    endpoint character varying(255),
    description character varying(255),
    metadata jsonb
);

-- TABLE definitions_addons: addons those process definitions were deployed with
CREATE TABLE definitions_addons (
    process_id character varying(255) NOT NULL,
    process_version character varying(255) NOT NULL,
    addon character varying(255) NOT NULL
);

-- TABLE definitions_annotations
CREATE TABLE definitions_annotations (
    annotation character varying(255) NOT NULL,
    process_id character varying(255) NOT NULL,
    process_version character varying(255) NOT NULL
);

-- TABLE definitions_nodes: last definitions of node executed by a process instance
CREATE TABLE definitions_nodes (
    id character varying(255) NOT NULL,
    name character varying(255),
    unique_id character varying(255),
    type character varying(255),
    process_id character varying(255) NOT NULL,
    process_version character varying(255) NOT NULL
);

-- TABLE definitions_nodes_metadata
CREATE TABLE definitions_nodes_metadata (
    node_id character varying(255) NOT NULL,
    process_id character varying(255) NOT NULL,
    process_version character varying(255) NOT NULL,
    meta_value character varying(255),
    name character varying(255) NOT NULL
);

-- TABLE definitions_roles
CREATE TABLE definitions_roles (
    process_id character varying(255) NOT NULL,
    process_version character varying(255) NOT NULL,
    role character varying(255) NOT NULL
);

-- TABLE jobs: timers created by runtime
CREATE TABLE jobs (
    id character varying(255) NOT NULL,
    callback_endpoint character varying(255),
    endpoint character varying(255),
    execution_counter integer,
    expiration_time timestamp without time zone,
    last_update timestamp without time zone,
    node_instance_id character varying(255),
    priority integer,
    process_id character varying(255),
    process_instance_id character varying(255),
    repeat_interval bigint,
    repeat_limit integer,
    retries integer,
    root_process_id character varying(255),
    root_process_instance_id character varying(255),
    scheduled_id character varying(255),
    status character varying(255)
);

-- TABLE jobs: kogito_data_cache
CREATE TABLE kogito_data_cache (
    key character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    json_value jsonb
);

-- TABLE milestones: special type of node that is completed through a condition (comes from cmmn)
CREATE TABLE milestones (
    id character varying(255) NOT NULL,
    process_instance_id character varying(255) NOT NULL,
    name character varying(255),
    status character varying(255)
);

-- TABLE nodes: nodes executed by the process instance
CREATE TABLE nodes (
    id character varying(255) NOT NULL,
    definition_id character varying(255),
    enter timestamp without time zone,
    exit timestamp without time zone,
    name character varying(255),
    node_id character varying(255),
    type character varying(255),
    process_instance_id character varying(255) NOT NULL,
    sla_due_date timestamp without time zone
);

-- TABLE processes: last state of the process instance
CREATE TABLE processes (
    id character varying(255) NOT NULL,
    business_key character varying(255),
    end_time timestamp without time zone,
    endpoint character varying(255),
    message character varying(65535),
    node_definition_id character varying(255),
    last_update_time timestamp without time zone,
    parent_process_instance_id character varying(255),
    process_id character varying(255),
    process_name character varying(255),
    root_process_id character varying(255),
    root_process_instance_id character varying(255),
    start_time timestamp without time zone,
    state integer,
    variables jsonb,
    version character varying(255),
    created_by character varying,
    updated_by character varying,
    sla_due_date timestamp without time zone
);

-- TABLE processes_addons: addons this process instance is being executed with
CREATE TABLE processes_addons (
    process_id character varying(255) NOT NULL,
    addon character varying(255) NOT NULL
);

-- TABLE processes_roles: roles this process instance requires
CREATE TABLE processes_roles (
    process_id character varying(255) NOT NULL,
    role character varying(255) NOT NULL
);

-- TABLE tasks: user task instance last state
CREATE TABLE tasks (
    id character varying(255) NOT NULL,
    actual_owner character varying(255),
    completed timestamp without time zone,
    description character varying(255),
    endpoint character varying(255),
    inputs jsonb,
    last_update timestamp without time zone,
    name character varying(255),
    outputs jsonb,
    priority character varying(255),
    process_id character varying(255),
    process_instance_id character varying(255),
    reference_name character varying(255),
    root_process_id character varying(255),
    root_process_instance_id character varying(255),
    started timestamp without time zone,
    state character varying(255),
    external_reference_id character varying(4000),
    sla_due_date timestamp without time zone
);

-- TABLE tasks_admin_groups: user task instance admin groups assigned
CREATE TABLE tasks_admin_groups (
    task_id character varying(255) NOT NULL,
    group_id character varying(255) NOT NULL
);

-- TABLE tasks_admin_users: user task instance admin user assigned
CREATE TABLE tasks_admin_users (
    task_id character varying(255) NOT NULL,
    user_id character varying(255) NOT NULL
);

-- TABLE tasks_excluded_users: user task instance excluded users
CREATE TABLE tasks_excluded_users (
    task_id character varying(255) NOT NULL,
    user_id character varying(255) NOT NULL
);

-- TABLE tasks_potential_groups: user task instance potential groups
CREATE TABLE tasks_potential_groups (
    task_id character varying(255) NOT NULL,
    group_id character varying(255) NOT NULL
);

-- TABLE tasks_potential_users: user task instance potential users
CREATE TABLE tasks_potential_users (
    task_id character varying(255) NOT NULL,
    user_id character varying(255) NOT NULL
);

ALTER TABLE ONLY attachments
    ADD CONSTRAINT attachments_pkey PRIMARY KEY (id);

ALTER TABLE ONLY comments
    ADD CONSTRAINT comments_pkey PRIMARY KEY (id);

ALTER TABLE ONLY definitions_addons
    ADD CONSTRAINT definitions_addons_pkey PRIMARY KEY (process_id, process_version, addon);

ALTER TABLE ONLY definitions_annotations
    ADD CONSTRAINT definitions_annotations_pkey PRIMARY KEY (annotation, process_id, process_version);

ALTER TABLE ONLY definitions_nodes_metadata
    ADD CONSTRAINT definitions_nodes_metadata_pkey PRIMARY KEY (node_id, process_id, process_version, name);

ALTER TABLE ONLY definitions_nodes
    ADD CONSTRAINT definitions_nodes_pkey PRIMARY KEY (id, process_id, process_version);

ALTER TABLE ONLY definitions
    ADD CONSTRAINT definitions_pkey PRIMARY KEY (id, version);

ALTER TABLE ONLY definitions_roles
    ADD CONSTRAINT definitions_roles_pkey PRIMARY KEY (process_id, process_version, role);

ALTER TABLE ONLY jobs
    ADD CONSTRAINT jobs_pkey PRIMARY KEY (id);

ALTER TABLE ONLY kogito_data_cache
    ADD CONSTRAINT kogito_data_cache_pkey PRIMARY KEY (key, name);

ALTER TABLE ONLY milestones
    ADD CONSTRAINT milestones_pkey PRIMARY KEY (id, process_instance_id);

ALTER TABLE ONLY nodes
    ADD CONSTRAINT nodes_pkey PRIMARY KEY (id);

ALTER TABLE ONLY processes_addons
    ADD CONSTRAINT processes_addons_pkey PRIMARY KEY (process_id, addon);

ALTER TABLE ONLY processes
    ADD CONSTRAINT processes_pkey PRIMARY KEY (id);

ALTER TABLE ONLY processes_roles
    ADD CONSTRAINT processes_roles_pkey PRIMARY KEY (process_id, role);

ALTER TABLE ONLY tasks_admin_groups
    ADD CONSTRAINT tasks_admin_groups_pkey PRIMARY KEY (task_id, group_id);

ALTER TABLE ONLY tasks_admin_users
    ADD CONSTRAINT tasks_admin_users_pkey PRIMARY KEY (task_id, user_id);

ALTER TABLE ONLY tasks_excluded_users
    ADD CONSTRAINT tasks_excluded_users_pkey PRIMARY KEY (task_id, user_id);

ALTER TABLE ONLY tasks
    ADD CONSTRAINT tasks_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tasks_potential_groups
    ADD CONSTRAINT tasks_potential_groups_pkey PRIMARY KEY (task_id, group_id);

ALTER TABLE ONLY tasks_potential_users
    ADD CONSTRAINT tasks_potential_users_pkey PRIMARY KEY (task_id, user_id);

CREATE INDEX idx_attachments_tid ON attachments USING btree (task_id);

CREATE INDEX idx_comments_tid ON comments USING btree (task_id);

CREATE INDEX idx_definitions_addons_pid_pv ON definitions_addons USING btree (process_id, process_version);

CREATE INDEX idx_definitions_annotations_pid_pv ON definitions_annotations USING btree (process_id, process_version);

CREATE INDEX idx_definitions_nodes_metadata_pid_pv ON definitions_nodes_metadata USING btree (process_id, process_version);

CREATE INDEX idx_definitions_nodes_pid_pv ON definitions_nodes USING btree (process_id, process_version);

CREATE INDEX idx_definitions_roles_pid_pv ON definitions_roles USING btree (process_id, process_version);

CREATE INDEX idx_milestones_piid ON milestones USING btree (process_instance_id);

CREATE INDEX idx_nodes_piid ON nodes USING btree (process_instance_id);

CREATE INDEX idx_processes_addons_pid ON processes_addons USING btree (process_id);

CREATE INDEX idx_processes_roles_pid ON processes_roles USING btree (process_id);

CREATE INDEX idx_tasks_admin_groups_tid ON tasks_admin_groups USING btree (task_id);

CREATE INDEX idx_tasks_admin_users_tid ON tasks_admin_users USING btree (task_id);

CREATE INDEX idx_tasks_excluded_users_tid ON tasks_excluded_users USING btree (task_id);

CREATE INDEX idx_tasks_potential_groups_tid ON tasks_potential_groups USING btree (task_id);

CREATE INDEX idx_tasks_potential_users_tid ON tasks_potential_users USING btree (task_id);

ALTER TABLE ONLY attachments
    ADD CONSTRAINT fk_attachments_tasks FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE;

ALTER TABLE ONLY comments
    ADD CONSTRAINT fk_comments_tasks FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE;

ALTER TABLE ONLY definitions_addons
    ADD CONSTRAINT fk_definitions_addons_definitions FOREIGN KEY (process_id, process_version) REFERENCES definitions(id, version) ON DELETE CASCADE;

ALTER TABLE ONLY definitions_annotations
    ADD CONSTRAINT fk_definitions_annotations FOREIGN KEY (process_id, process_version) REFERENCES definitions(id, version) ON DELETE CASCADE;

ALTER TABLE ONLY definitions_nodes
    ADD CONSTRAINT fk_definitions_nodes_definitions FOREIGN KEY (process_id, process_version) REFERENCES definitions(id, version) ON DELETE CASCADE;

ALTER TABLE ONLY definitions_nodes_metadata
    ADD CONSTRAINT fk_definitions_nodes_metadata_definitions_nodes FOREIGN KEY (node_id, process_id, process_version) REFERENCES definitions_nodes(id, process_id, process_version) ON DELETE CASCADE;

ALTER TABLE ONLY definitions_roles
    ADD CONSTRAINT fk_definitions_roles_definitions FOREIGN KEY (process_id, process_version) REFERENCES definitions(id, version) ON DELETE CASCADE;

ALTER TABLE ONLY milestones
    ADD CONSTRAINT fk_milestones_process FOREIGN KEY (process_instance_id) REFERENCES processes(id) ON DELETE CASCADE;

ALTER TABLE ONLY nodes
    ADD CONSTRAINT fk_nodes_process FOREIGN KEY (process_instance_id) REFERENCES processes(id) ON DELETE CASCADE;

ALTER TABLE ONLY processes_addons
    ADD CONSTRAINT fk_processes_addons_processes FOREIGN KEY (process_id) REFERENCES processes(id) ON DELETE CASCADE;

ALTER TABLE ONLY processes_roles
    ADD CONSTRAINT fk_processes_roles_processes FOREIGN KEY (process_id) REFERENCES processes(id) ON DELETE CASCADE;

ALTER TABLE ONLY tasks_admin_groups
    ADD CONSTRAINT fk_tasks_admin_groups_tasks FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE;

ALTER TABLE ONLY tasks_admin_users
    ADD CONSTRAINT fk_tasks_admin_users_tasks FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE;

ALTER TABLE ONLY tasks_excluded_users
    ADD CONSTRAINT fk_tasks_excluded_users_tasks FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE;

ALTER TABLE ONLY tasks_potential_groups
    ADD CONSTRAINT fk_tasks_potential_groups_tasks FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE;

ALTER TABLE ONLY tasks_potential_users
    ADD CONSTRAINT fk_tasks_potential_users_tasks FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE;
