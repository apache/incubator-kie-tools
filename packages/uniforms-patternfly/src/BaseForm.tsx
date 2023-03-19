/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
import { Form } from "@patternfly/react-core/dist/js/components/Form";
import { BaseForm, context } from "uniforms";

function Patternfly(parent: any): any {
  class _ extends parent {
    static Patternfly = Patternfly;

    static displayName = `Patternfly${parent.displayName}`;

    render() {
      return (
        <context.Provider value={this.getContext()}>
          <Form data-testid="base-form" {...this.getNativeFormProps()} />
        </context.Provider>
      );
    }
  }

  return _;
}

export default Patternfly(BaseForm);
