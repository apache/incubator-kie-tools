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
import { CSSProperties, useState } from "react";
import { Actions } from "./reducers/Actions";
import { HistoryService } from "./history/HistoryProvider";
import { Title } from "./PMMLEditor";
import { useDispatch } from "react-redux";

const style: CSSProperties = {
  padding: "5px 5px 5px 5px"
};

const StateButtons = () => {
  const dispatch = useDispatch();

  const doUndo = () => {
    dispatch({
      type: Actions.Undo
    });
  };

  const doRedo = () => {
    dispatch({
      type: Actions.Redo
    });
  };

  return (
    <div style={style}>
      <button onClick={doUndo}>Undo</button>
      <button onClick={doRedo}>Redo</button>
    </div>
  );
};

interface HistoryLogProps {
  service: HistoryService;
}

const HistoryLog = (props: HistoryLogProps) => {
  const [state, setState] = useState(true);

  return (
    <div style={style}>
      <Title title={"History"} />
      <button onClick={() => setState(!state)}>Refresh</button>
      <div>
        <sub>This is a hack just to support showing the undo/redo log...</sub>
      </div>
      <pre>{`${JSON.stringify(
        {
          index: props.service.index(),
          changes: [props.service.changes()].reverse()
        },
        undefined,
        2
      )}`}</pre>
    </div>
  );
};

export { StateButtons, HistoryLog };
