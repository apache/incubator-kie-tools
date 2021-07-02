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

import * as React from "react";
import { useCallback, useLayoutEffect, useMemo, useState } from "react";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { useEditorEnvelopeI18nContext } from "../i18n";

export function LoadingScreen(props: { loading: boolean }) {
  const [mustRender, setMustRender] = useState(true);
  const { i18n } = useEditorEnvelopeI18nContext();

  const onAnimationEnd = useCallback((e: React.AnimationEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setMustRender(false);
  }, []);

  const loadingScreenClassName = useMemo(() => {
    if (props.loading) {
      return "";
    }
    return "loading-finished";
  }, [props.loading]);

  useLayoutEffect(() => {
    if (props.loading) {
      setMustRender(true);
    }
  }, [props.loading]);

  return (
    (mustRender && (
      <div id="loading-screen" className="kogito-tooling--loading-screen">
        <div
          className={`kogito-tooling--loading-screen ${loadingScreenClassName}`}
          onAnimationEnd={onAnimationEnd}
          data-testid={"loading-screen-div"}
        >
          <Bullseye>
            <div className={"kogito-tooling--loading-screen-spinner"}>
              <div>
                <Spinner />
              </div>
              <Title headingLevel={"h5"}>{i18n.loadingScreen.loading}</Title>
            </div>
          </Bullseye>
        </div>
      </div>
    )) || <></>
  );
}
