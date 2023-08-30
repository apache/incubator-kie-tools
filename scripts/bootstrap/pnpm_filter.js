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

const { filterPackages } = require("@pnpm/filter-workspace-packages");
const findWorkspacePackages = require("@pnpm/find-workspace-packages").default;

const workspaceDir = ".";

module.exports = {
  pnpmFilter: async (pnpmFilterString, { alwaysIncludeRoot }) => {
    const packages = await findWorkspacePackages(workspaceDir);

    const parsedFilterString = !pnpmFilterString
      ? []
      : pnpmFilterString
          .split(" ")
          .filter((f) => f !== "-F")
          .filter((f) => f !== "--filter");

    const filters = parsedFilterString.map((f) => ({
      filter: `${f}`,
      followProdDepsOnly: false,
    }));

    const filteredPackages = (await filterPackages(packages, filters, { prefix: "", workspaceDir }))
      .selectedProjectsGraph;

    if (!alwaysIncludeRoot) {
      return filteredPackages;
    }

    return {
      [workspaceDir]: {
        package: packages.filter((p) => p.dir === workspaceDir)[0],
        dependencies: [],
      },
      ...filteredPackages,
    };
  },
};
