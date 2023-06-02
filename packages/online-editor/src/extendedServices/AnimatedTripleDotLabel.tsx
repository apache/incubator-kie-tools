/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { useEffect, useState } from "react";

interface Props {
  label: string;
  interval?: number;
}

export function AnimatedTripleDotLabel({ label, interval = 1000 }: Props) {
  const [dots, setDots] = useState("");

  useEffect(() => {
    const timeout = setTimeout(() => {
      if (dots.length === 3) {
        setDots("");
      } else {
        setDots(dots + ".");
      }
    }, interval);

    return () => {
      clearTimeout(timeout);
    };
  }, [interval, dots]);

  return (
    <p>
      {label}
      <span data-testid="animated-triple-dot-label">{dots}</span>
    </p>
  );
}
