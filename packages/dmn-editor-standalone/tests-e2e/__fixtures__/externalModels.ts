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

import path from "path";
import { readFileSync } from "fs";

export const emptyDrd = readFileSync(path.join(__dirname, "files/empty-drd.dmn"), "utf8");

export const emptyDmn = readFileSync(path.join(__dirname, "files/empty.dmn"), "utf8");

export const loanPreQualificationDmn = readFileSync(path.join(__dirname, "files/loan-pre-qualification.dmn"), "utf8");

export const canDriveDmn = readFileSync(path.join(__dirname, "files/can-drive.dmn"), "utf8");

export const findEmployeesDmn = readFileSync(path.join(__dirname, "files/find-employees.dmn"), "utf8");

export const typesDmn = readFileSync(path.join(__dirname, "files/types.dmn"), "utf8");

export const scorecardPmml = readFileSync(path.join(__dirname, "files/scorecard.pmml"), "utf8");
