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
import { CSSProperties, useContext } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Actions } from "../../reducers/Actions";
import { Timestamp, Title } from "../../utils/UIUtils";
import { PMML } from "@kogito-tooling/pmml-editor-marshaller";
import { HistoryContext, HistoryService } from "../../history/HistoryProvider";

const style: CSSProperties = {
  padding: "5px 5px 5px 5px"
};

const MockVersionUI = () => {
  const dispatch = useDispatch();
  const version: string = useSelector<PMML, string>(state => state.version);
  const service: HistoryService = useContext(HistoryContext).service;

  const setVersion = () => {
    dispatch({
      type: Actions.SetVersion,
      payload: {
        service: service,
        version: Math.random()
          .toString(36)
          .replace(/[^a-z]+/g, "")
          .substr(0, 5)
      }
    });
  };

  return (
    <div style={style}>
      <Title title={"PMML"} />
      <div>{version}</div>
      <button onClick={setVersion}>Set version</button>
      <Timestamp />
    </div>
  );
};

export default MockVersionUI;
