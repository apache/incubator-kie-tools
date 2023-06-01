/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import "cypress-file-upload";
import "cypress-iframe";
import "./commands";

// temporary workaround, will be removed with https://issues.redhat.com/browse/KOGITO-6355
Cypress.on("uncaught:exception", (err, runnable) => {
  // returning false here prevents Cypress from
  // failing the test
  return false;
});

/**
 * Delete all indexed DBs
 */
export const deleteAllIndexedDBs = async () => {
  (await window.indexedDB.databases())
    .filter((db) => db.name)
    .forEach((db) => window.indexedDB.deleteDatabase(db.name!));
};
