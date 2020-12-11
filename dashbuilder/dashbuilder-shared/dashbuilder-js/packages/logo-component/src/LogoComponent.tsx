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

import { ComponentController } from "@dashbuilder-js/component-api";
import * as React from "react";
import { useState, useEffect } from "react";
import { Logo, LogoProps } from "./Logo";
const SRC_PROP = "src";
const WIDTH_PROP = "width";
const HEIGHT_PROP = "height";

interface Props {
  controller: ComponentController;
}
export function LogoComponent(props: Props) {
  const [logoProps, setLogoProps] = useState<LogoProps>({
    src: ""
  });

  useEffect(() => {
    props.controller.setOnInit(componentProps => {
      setLogoProps({
        src: (componentProps.get(SRC_PROP) as string) || "",
        width: componentProps.get(WIDTH_PROP) as string,
        height: componentProps.get(HEIGHT_PROP) as string
      });
    });
  }, []);

  return <Logo {...logoProps} />;
}
