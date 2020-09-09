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
import { AppContext } from "./PMMLEditorContextProvider";
import { Actions } from "./reducers/Actions";
import { Timestamp, Title } from "./PMMLEditor";

const style = {
  padding: "5px 5px 5px 5px"
};

const MockHeaderUI = () => {
  const { state, dispatch } = React.useContext(AppContext);

  const setHeaderDescription = (description: string) => {
    dispatch({
      type: Actions.SetHeaderDescription,
      payload: {
        description: description
      }
    });
  };

  return (
    <div style={style}>
      <Title title="Header" />
      <input value={state.Header.description} onChange={e => setHeaderDescription(e.target.value)} placeholder="Name" />
      <Timestamp />
    </div>
  );
};

export default MockHeaderUI;
