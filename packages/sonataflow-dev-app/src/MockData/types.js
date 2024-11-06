/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

const { gql } = require("apollo-server-express");
module.exports = typeDefs = gql`
  scalar DateTime
  scalar JSON

  schema {
    query: Query
  }

  type Mutation {
    JobExecute(id: String): String
  }

  type Query {
    ProcessInstances(
      where: ProcessInstanceArgument
      orderBy: ProcessInstanceOrderBy
      pagination: Pagination
    ): [ProcessInstance]
    ProcessDefinitions: [ProcessDefinition]
    Jobs(where: JobArgument, orderBy: JobOrderBy, pagination: Pagination): [Job]
  }

  type ProcessInstance {
    id: String!
    processId: String!
    processName: String
    parentProcessInstanceId: String
    parentProcessInstance: ProcessInstance
    rootProcessInstanceId: String
    rootProcessId: String
    roles: [String!]
    state: ProcessInstanceState!
    serviceUrl: String
    endpoint: String!
    nodes: [NodeInstance!]!
    nodeDefinitions: [Node!]
    milestones: [Milestones!]
    variables: JSON
    start: DateTime!
    end: DateTime
    businessKey: String
    childProcessInstances: [ProcessInstance!]
    error: ProcessInstanceError
    addons: [String!]
    source: String
    lastUpdate: DateTime!
    diagram: String
  }

  type ProcessDefinition {
    id: String!
    endpoint: String!
    serviceUrl: String!
  }

  type ProcessInstanceError {
    nodeDefinitionId: String!
    message: String
  }

  enum ProcessInstanceState {
    PENDING
    ACTIVE
    COMPLETED
    ABORTED
    SUSPENDED
    ERROR
  }

  type NodeInstance {
    id: String!
    name: String!
    type: String!
    enter: DateTime!
    exit: DateTime
    definitionId: String!
    nodeId: String!
  }

  type Node {
    id: String!
    nodeDefinitionId: String!
    name: String!
    type: String!
    uniqueId: String!
  }

  type Milestones {
    id: String!
    name: String!
    status: MilestoneStatus!
  }
  enum MilestoneStatus {
    ACTIVE
    AVAILABLE
    COMPLETED
  }

  input ProcessInstanceOrderBy {
    processId: OrderBy
    processName: OrderBy
    rootProcessId: OrderBy
    state: OrderBy
    start: OrderBy
    end: OrderBy
    error: ProcessInstanceErrorOrderBy
    lastUpdate: OrderBy
  }

  input ProcessInstanceErrorOrderBy {
    nodeDefinitionId: OrderBy
    message: OrderBy
  }

  input ProcessInstanceArgument {
    and: [ProcessInstanceArgument!]
    or: [ProcessInstanceArgument!]
    id: IdArgument
    processId: StringArgument
    processName: StringArgument
    parentProcessInstanceId: IdArgument
    rootProcessInstanceId: IdArgument
    rootProcessId: StringArgument
    state: ProcessInstanceStateArgument
    error: ProcessInstanceErrorArgument
    nodes: NodeInstanceArgument
    endpoint: StringArgument
    roles: StringArrayArgument
    start: DateArgument
    end: DateArgument
    addons: StringArrayArgument
    lastUpdate: DateArgument
    businessKey: StringArgument
  }

  input ProcessInstanceErrorArgument {
    nodeDefinitionId: StringArgument
    message: StringArgument
  }

  input ProcessInstanceMetaArgument {
    id: IdArgument
    processId: StringArgument
    processName: StringArgument
    parentProcessInstanceId: IdArgument
    rootProcessInstanceId: IdArgument
    rootProcessId: StringArgument
    state: ProcessInstanceStateArgument
    endpoint: StringArgument
    roles: StringArrayArgument
    start: DateArgument
    end: DateArgument
  }

  input NodeInstanceArgument {
    id: IdArgument
    name: StringArgument
    definitionId: StringArgument
    nodeId: StringArgument
    type: StringArgument
    enter: DateArgument
    exit: DateArgument
  }

  input StringArrayArgument {
    contains: String
    containsAll: [String!]
    containsAny: [String!]
    isNull: Boolean
  }

  input IdArgument {
    in: [String!]
    equal: String
    isNull: Boolean
  }

  input StringArgument {
    in: [String!]
    like: String
    isNull: Boolean
    equal: String
  }

  input BooleanArgument {
    isNull: Boolean
    equal: Boolean
  }

  input NumericArgument {
    in: [Int!]
    isNull: Boolean
    equal: Int
    greaterThan: Int
    greaterThanEqual: Int
    lessThan: Int
    lessThanEqual: Int
    between: NumericRange
  }

  input NumericRange {
    from: Int!
    to: Int!
  }

  input DateArgument {
    isNull: Boolean
    equal: DateTime
    greaterThan: DateTime
    greaterThanEqual: DateTime
    lessThan: DateTime
    lessThanEqual: DateTime
    between: DateRange
  }

  input DateRange {
    from: DateTime!
    to: DateTime!
  }

  input ProcessInstanceStateArgument {
    equal: ProcessInstanceState
    in: [ProcessInstanceState]
  }

  enum OrderBy {
    ASC
    DESC
  }

  input Pagination {
    limit: Int
    offset: Int
  }

  input JobArgument {
    and: [JobArgument!]
    or: [JobArgument!]
    id: IdArgument
    processId: StringArgument
    processInstanceId: IdArgument
    rootProcessInstanceId: IdArgument
    rootProcessId: StringArgument
    status: JobStatusArgument
    expirationTime: DateArgument
    priority: NumericArgument
    scheduledId: IdArgument
    lastUpdate: DateArgument
    endpoint: StringArgument
    nodeInstanceId: StringArgument
  }

  input JobOrderBy {
    processId: OrderBy
    rootProcessId: OrderBy
    status: OrderBy
    expirationTime: OrderBy
    priority: OrderBy
    retries: OrderBy
    lastUpdate: OrderBy
    executionCounter: OrderBy
  }

  input JobStatusArgument {
    equal: JobStatus
    in: [JobStatus]
  }

  type Job {
    id: String!
    processId: String
    processInstanceId: String
    rootProcessInstanceId: String
    rootProcessId: String
    status: JobStatus!
    expirationTime: DateTime
    priority: Int
    callbackEndpoint: String
    repeatInterval: Int
    repeatLimit: Int
    scheduledId: String
    retries: Int
    lastUpdate: DateTime
    executionCounter: Int
    endpoint: String
    nodeInstanceId: String
  }

  enum JobStatus {
    ERROR
    EXECUTED
    SCHEDULED
    RETRY
    CANCELED
  }
`;
