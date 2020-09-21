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
import { Actions } from "./reducers/Actions";
import MockDataFieldUI from "./MockDataFieldUI";
import { Timestamp, Title } from "./PMMLEditor";
import { TypedUseSelectorHook, useDispatch, useSelector } from "react-redux";
import { DataDictionary, PMML } from "@kogito-tooling/pmml-editor-marshaller";

const style = {
  padding: "5px 5px 5px 5px"
};

const MockDataFieldsUI = () => {
  const [name, setName] = React.useState("");

  const dispatch = useDispatch();
  const typedUseSelector: TypedUseSelectorHook<PMML> = useSelector;
  const dataDictionary: DataDictionary = typedUseSelector(state => state.DataDictionary);

  const createDataField = () => {
    dispatch({
      type: Actions.CreateDataField,
      payload: {
        name: name
      }
    });
  };

  return (
    <div style={style}>
      <Title title="DataDictionary" />
      <input value={name} onChange={e => setName(e.target.value)} placeholder="Value" />
      <button onClick={() => createDataField()}>Create DataField</button>
      <div>
        {dataDictionary.DataField.map((field, index) => (
          <MockDataFieldUI key={index} index={index} field={field} />
        ))}
      </div>
      <Timestamp />
    </div>
  );
};

export default MockDataFieldsUI;
