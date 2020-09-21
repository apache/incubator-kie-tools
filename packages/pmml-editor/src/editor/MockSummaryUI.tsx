/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import * as React from "react";
import { Timestamp, Title } from "./PMMLEditor";
import { TypedUseSelectorHook, useSelector } from "react-redux";
import { PMML } from "@kogito-tooling/pmml-editor-marshaller";

const style = {
  padding: "5px 5px 5px 5px"
};

const MockSummaryUI = () => {
  const typedUseSelector: TypedUseSelectorHook<PMML> = useSelector;
  const pmml: PMML = typedUseSelector(state => state);

  return (
    <div style={style}>
      <Title title="JSON" />
      <pre>{`${JSON.stringify(pmml, undefined, 2)}`}</pre>
      <Timestamp />
    </div>
  );
};

export default MockSummaryUI;
