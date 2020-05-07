/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { EditorType } from "../../common/EditorTypes"

describe("EditorTypes::definitions",
    () => {
        test("Should contain DMN",
            () => {
                expect(Object.keys(EditorType)).toContain("DMN");
                expect(EditorType["DMN"]).toEqual("dmn");
            });

        test("Should contain BPMN",
            () => {
                expect(Object.keys(EditorType)).toContain("BPMN");
                expect(EditorType["BPMN"]).toEqual("bpmn");
            });

        test("Should contain SCESIM",
            () => {
                expect(Object.keys(EditorType)).toContain("SCESIM");
                expect(EditorType["SCESIM"]).toEqual("scesim");
            });
    });