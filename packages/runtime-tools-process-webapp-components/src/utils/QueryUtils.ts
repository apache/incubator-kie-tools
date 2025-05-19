/**
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
import { User } from "@kie-tools/runtime-tools-components/dist/contexts/KogitoAppContext";
import { GraphQL } from "@kie-tools/runtime-tools-process-gateway-api/dist/graphql/types";
import { TaskListQueryFilter, TaskListSortBy } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import _ from "lodash";

const createSearchTextArray = (taskNames: string[]) => {
  const formattedTextArray: { referenceName: { like: string } }[] = [];
  taskNames.forEach((word) => {
    formattedTextArray.push({
      referenceName: {
        like: `*${word}*`,
      },
    });
  });
  return {
    or: formattedTextArray,
  };
};

const createUserAssignmentClause = (currentUser?: User) => {
  if (!currentUser) {
    return {};
  }
  return {
    or: [
      { actualOwner: { equal: currentUser.id } },
      {
        and: [
          { actualOwner: { isNull: true } },
          {
            not: { excludedUsers: { contains: currentUser.id } },
          },
          {
            or: [
              { potentialUsers: { contains: currentUser.id } },
              { potentialGroups: { containsAny: currentUser.groups } },
            ],
          },
        ],
      },
    ],
  };
};

export const buildTaskListWhereArgument = (
  activeFilters: TaskListQueryFilter,
  currentUser?: User
): GraphQL.UserTaskInstanceArgument => {
  if (activeFilters) {
    const filtersClause: GraphQL.UserTaskInstanceArgument[] = [];
    if (activeFilters.taskStates.length > 0) {
      filtersClause.push({
        state: { in: activeFilters.taskStates },
      });
    }
    if (activeFilters.taskNames.length > 0) {
      filtersClause.push(createSearchTextArray(activeFilters.taskNames));
    }

    if (filtersClause.length > 0) {
      return {
        and: [
          createUserAssignmentClause(currentUser),
          {
            and: filtersClause,
          },
        ],
      };
    }
  }
  return createUserAssignmentClause(currentUser);
};

export const getOrderByObject = (sortBy: TaskListSortBy) => {
  if (!_.isEmpty(sortBy)) {
    return _.set({}, sortBy.property, sortBy.direction.toUpperCase());
  }
  return {
    lastUpdate: "DESC",
  };
};
