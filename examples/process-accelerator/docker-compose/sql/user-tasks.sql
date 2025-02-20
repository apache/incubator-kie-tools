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
-- User tasks subsystem tables
--

-- TABLE jbpm_user_tasks: The entity that represents a User Task
-- id: The User Task ID. The scope of this ID is valid on the User Tasks subsystem only. Can't be used at the Workflow Engine level
-- user_task_id: The ID of the node associated with this User Task
-- status: The status name
-- termination_type: COMPLETED, ABORT, FAILED, EXITED, OBSOLETE, ERROR
-- external_reference_id: The ID of the Work Item associated with this user task. Can be used at Workflow Engine lever to refer to this User Task
CREATE TABLE jbpm_user_tasks (
    id varchar(50) NOT NULL,
    user_task_id varchar(255),
    task_priority varchar(50),
    actual_owner varchar(255),
    task_description varchar(255),
    status varchar(255),
    termination_type varchar(255),
    external_reference_id varchar(255),
    task_name varchar(255)
);

-- TABLE jbpm_user_tasks_potential_users
CREATE TABLE jbpm_user_tasks_potential_users (
    task_id varchar(50) NOT NULL,
    user_id varchar(255) NOT NULL
);

-- TABLE jbpm_user_tasks_potential_groups
CREATE TABLE jbpm_user_tasks_potential_groups (
    task_id varchar(50) NOT NULL,
    group_id varchar(255) NOT NULL
);

-- TABLE jbpm_user_tasks_admin_users
CREATE TABLE jbpm_user_tasks_admin_users (
    task_id varchar(50) NOT NULL,
    user_id varchar(255) NOT NULL
);

-- TABLE jbpm_user_tasks_admin_groups
CREATE TABLE jbpm_user_tasks_admin_groups (
    task_id varchar(50) NOT NULL,
    group_id varchar(255) NOT NULL
);

-- TABLE jbpm_user_tasks_excluded_users
CREATE TABLE jbpm_user_tasks_excluded_users (
    task_id varchar(50) NOT NULL,
    user_id varchar(255) NOT NULL
);

-- TABLE jbpm_user_tasks_attachments: An attachment is a reference to an external URIs containing information relevant to a related task, for example a screen snapshot.
-- url: The uri related to this attachment
CREATE TABLE jbpm_user_tasks_attachments (
    id varchar(50) NOT NULL,
    name varchar(255),
    updated_by varchar(255),
    updated_at timestamp(6),
    url varchar(255),
    task_id varchar(50) NOT NULL
);

-- TABLE jbpm_user_tasks_comments: A comment consists of a human readable text that will help to achieve a successful resolution of a task.
-- comment: The comment related to this attachment
CREATE TABLE jbpm_user_tasks_comments (
    id varchar(50) NOT NULL,
    updated_by varchar(255),
    updated_at timestamp(6),
    comment varchar(255),
    task_id varchar(50) NOT NULL
);

-- TABLE jbpm_user_tasks_inputs: Input parameters of a task which are passed as a pair (name, value), to be consumed by a human
CREATE TABLE jbpm_user_tasks_inputs (
    task_id varchar(50) NOT NULL,
    input_name varchar(255) NOT NULL,
    input_value bytea,
    java_type varchar(255)
);

-- TABLE jbpm_user_tasks_outputs: Output parameters of a User Task, which results in a set of properties in a pair (name, value) format.
CREATE TABLE jbpm_user_tasks_outputs (
    task_id varchar(50) NOT NULL,
    output_name varchar(255) NOT NULL,
    output_value bytea,
    java_type varchar(255)
);

-- TABLE jbpm_user_tasks_metadata: Global properties related the User Task
CREATE TABLE jbpm_user_tasks_metadata (
    task_id varchar(50),
    metadata_name varchar(255) NOT NULL,
    metadata_value varchar(512),
    java_type varchar(255)
);

-- TABLE jbpm_user_tasks_deadline:
CREATE TABLE jbpm_user_tasks_deadline (
    id int,
    task_id varchar(50) NOT NULL,
    notification_type varchar(255) NOT NULL,
    notification_value bytea,
    java_type varchar(255)
);

-- TABLE jbpm_user_tasks_reassignment:
CREATE TABLE jbpm_user_tasks_reassignment (
    id int,
    task_id varchar(50) NOT NULL,
    reassignment_type varchar(255) NOT NULL,
    reassignment_value bytea,
    java_type varchar(255)
);

-- TABLE jbpm_user_tasks_deadline_timer:
CREATE TABLE jbpm_user_tasks_deadline_timer (
    task_id varchar(50) NOT NULL,
    notification_job_id varchar(255) NOT NULL,
    notification_type varchar(255) NOT NULL,
    notification_value bytea,
    java_type varchar(255)
);

-- TABLE jbpm_user_tasks_reassignment_timer:
CREATE TABLE jbpm_user_tasks_reassignment_timer (
    task_id varchar(50) NOT NULL,
    reassignment_job_id varchar(255) NOT NULL,
    reassignment_type varchar(255) NOT NULL,
    reassignment_value bytea,
    java_type varchar(255)
);

CREATE SEQUENCE jbpm_user_tasks_deadline_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    OWNED BY jbpm_user_tasks_deadline.id;

CREATE SEQUENCE jbpm_user_tasks_reassignment_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    OWNED BY jbpm_user_tasks_reassignment.id;

ALTER TABLE ONLY jbpm_user_tasks
    ADD CONSTRAINT jbpm_user_tasks_pkey PRIMARY KEY (id);

ALTER TABLE ONLY jbpm_user_tasks_potential_users
    ADD CONSTRAINT jbpm_user_tasks_potential_users_pkey PRIMARY KEY (task_id, user_id);

ALTER TABLE ONLY jbpm_user_tasks_potential_groups
    ADD CONSTRAINT jbpm_user_tasks_potential_groups_pkey PRIMARY KEY (task_id, group_id);

ALTER TABLE ONLY jbpm_user_tasks_admin_users
    ADD CONSTRAINT jbpm_user_tasks_admin_users_pkey PRIMARY KEY (task_id, user_id);

ALTER TABLE ONLY jbpm_user_tasks_admin_groups
    ADD CONSTRAINT jbpm_user_tasks_admin_groups_pkey PRIMARY KEY (task_id, group_id);

ALTER TABLE ONLY jbpm_user_tasks_excluded_users
    ADD CONSTRAINT jbpm_user_tasks_excluded_users_pkey PRIMARY KEY (task_id, user_id);

ALTER TABLE ONLY jbpm_user_tasks_attachments
    ADD CONSTRAINT jbpm_user_tasks_attachments_pkey PRIMARY KEY (id);

ALTER TABLE ONLY jbpm_user_tasks_comments
    ADD CONSTRAINT jbpm_user_tasks_comments_pkey PRIMARY KEY (id);

ALTER TABLE ONLY jbpm_user_tasks_inputs
    ADD CONSTRAINT jbpm_user_tasks_inputs_pkey PRIMARY KEY (task_id, input_name);

ALTER TABLE ONLY jbpm_user_tasks_outputs
    ADD CONSTRAINT jbpm_user_tasks_outputs_pkey PRIMARY KEY (task_id, output_name);

ALTER TABLE ONLY jbpm_user_tasks_metadata
    ADD CONSTRAINT jbpm_user_tasks_metadata_pkey PRIMARY KEY (task_id, metadata_name);

ALTER TABLE ONLY jbpm_user_tasks_deadline
    ADD CONSTRAINT jbpm_user_tasks_deadline_pkey PRIMARY KEY (id);

ALTER TABLE ONLY jbpm_user_tasks_reassignment
    ADD CONSTRAINT jbpm_user_tasks_reassignment_pkey PRIMARY KEY (id);

ALTER TABLE ONLY jbpm_user_tasks_deadline_timer
    ADD CONSTRAINT jbpm_user_tasks_deadline_timer_pkey PRIMARY KEY (task_id, notification_job_id);

ALTER TABLE ONLY jbpm_user_tasks_reassignment_timer
    ADD CONSTRAINT jbpm_user_tasks_reassignment_timer_pkey PRIMARY KEY (task_id, reassignment_job_id);

ALTER TABLE ONLY jbpm_user_tasks_potential_users
    ADD CONSTRAINT fk_jbpm_user_fk_tasks_potential_users_tid FOREIGN KEY (task_id)  REFERENCES jbpm_user_tasks(id) ON DELETE CASCADE;

ALTER TABLE ONLY jbpm_user_tasks_potential_groups
    ADD CONSTRAINT fk_jbpm_user_tasks_potential_groups_tid FOREIGN KEY (task_id)  REFERENCES jbpm_user_tasks(id) ON DELETE CASCADE;

ALTER TABLE ONLY jbpm_user_tasks_admin_users
    ADD CONSTRAINT fk_jbpm_user_tasks_admin_users_tid FOREIGN KEY (task_id)  REFERENCES jbpm_user_tasks(id) ON DELETE CASCADE;

ALTER TABLE ONLY jbpm_user_tasks_admin_groups
    ADD CONSTRAINT fk_jbpm_user_tasks_admin_groups_tid FOREIGN KEY (task_id) REFERENCES jbpm_user_tasks(id) ON DELETE CASCADE;

ALTER TABLE ONLY jbpm_user_tasks_excluded_users
    ADD CONSTRAINT fk_jbpm_user_tasks_excluded_users_tid FOREIGN KEY (task_id) REFERENCES jbpm_user_tasks(id) ON DELETE CASCADE;

ALTER TABLE ONLY jbpm_user_tasks_attachments
    ADD CONSTRAINT fk_user_tasks_attachments_tid FOREIGN KEY (task_id) REFERENCES jbpm_user_tasks(id) ON DELETE CASCADE;

ALTER TABLE ONLY jbpm_user_tasks_comments
    ADD CONSTRAINT fk_user_tasks_comments_tid FOREIGN KEY (task_id) REFERENCES jbpm_user_tasks(id) ON DELETE CASCADE;

ALTER TABLE ONLY jbpm_user_tasks_inputs
    ADD CONSTRAINT fk_jbpm_user_tasks_inputs_tid FOREIGN KEY (task_id) REFERENCES jbpm_user_tasks(id) ON DELETE CASCADE;

ALTER TABLE ONLY jbpm_user_tasks_outputs
    ADD CONSTRAINT fk_jbpm_user_tasks_outputs_tid FOREIGN KEY (task_id) REFERENCES jbpm_user_tasks(id) ON DELETE CASCADE;

ALTER TABLE ONLY jbpm_user_tasks_metadata
    ADD CONSTRAINT fk_jbpm_user_tasks_metadata_tid FOREIGN KEY (task_id) REFERENCES jbpm_user_tasks(id) ON DELETE CASCADE;

ALTER TABLE ONLY jbpm_user_tasks_deadline
    ADD CONSTRAINT fk_jbpm_user_tasks_deadline_tid FOREIGN KEY (task_id) REFERENCES jbpm_user_tasks(id) ON DELETE CASCADE;

ALTER TABLE ONLY jbpm_user_tasks_reassignment
    ADD CONSTRAINT fk_jbpm_user_tasks_reassignment_tid FOREIGN KEY (task_id) REFERENCES jbpm_user_tasks(id) ON DELETE CASCADE;

ALTER TABLE ONLY jbpm_user_tasks_deadline_timer
    ADD CONSTRAINT fk_jbpm_user_tasks_deadline_timer_tid FOREIGN KEY (task_id) REFERENCES jbpm_user_tasks(id) ON DELETE CASCADE;

ALTER TABLE ONLY jbpm_user_tasks_reassignment_timer
    ADD CONSTRAINT fk_jbpm_user_tasks_reassignment_timer_tid FOREIGN KEY (task_id) REFERENCES jbpm_user_tasks(id) ON DELETE CASCADE;