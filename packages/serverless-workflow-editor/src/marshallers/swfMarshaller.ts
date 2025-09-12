/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { WorkflowValidator, ValidationError, Specification } from "@severlessworkflow/sdk-typescript";

export interface Marshaller {
  setContent: (content: string) => Marshaller;
  getContent: () => string | undefined;
  getPastValidContentType: () => "yaml" | "json" | undefined;
  getPastValidContent: () => string | undefined;
  getPastValidModel: () => Specification.Workflow | undefined;
  getContentType: () => "yaml" | "json" | undefined;
  getModel: () => Specification.Workflow | undefined;
  isValid: () => boolean | undefined;
  getErrors: () => string[] | undefined;
  reset: () => void;
}

export class MarshallerImpl implements Marshaller {
  /**
   * Last valid model, used to render the last valid view of the workflow
   */
  pastModel: Specification.Workflow | undefined = undefined;
  /**
   * past content type JSON/YAML
   */
  pastContentType: "yaml" | "json" | undefined = undefined;
  /**
   * Last valid content
   */
  pastContent: string | undefined = undefined;
  /**
   * Curent content
   */
  rawContent: string | undefined = undefined;
  /**
   * Curent content type JSON/YAmL
   */
  contentType: "yaml" | "json" | undefined = undefined;
  /**
   * Curent model
   */
  model: Specification.Workflow | undefined = undefined;
  /**
   * Flag indicating if the last content is valid, therefore renderable
   */
  valid: boolean | undefined = undefined;
  /**
   * Errors from deserealization and/or validation
   */
  errors: string[] = [];

  setContent(content: string): Marshaller {
    // Clean-ups
    this.clearCurrent();

    // set curent content
    this.rawContent = content;
    this.contentType = getRawContentType(this.rawContent);
    this.model = deserializeSwf(this.rawContent, this.errors);

    // Deserialization error
    if (!this.model || this.errors.length > 0) {
      this.setCurrentContentInvalid();
      return this;
    }

    // Validation errors
    this.errors = validateModel(this.model!);

    if (this.errors.length > 0) {
      this.setCurrentContentInvalid();
    } else {
      this.setCurrentContentValid();
    }

    return this;
  }

  isValid(): boolean | undefined {
    return this.valid;
  }

  getErrors(): string[] | undefined {
    return this.errors;
  }

  getContentType(): "yaml" | "json" | undefined {
    return this.contentType;
  }

  getContent(): string | undefined {
    if (!this.model && !this.contentType) {
      return serializeSwf(this.model!, this.contentType!);
    }

    return undefined;
  }

  getModel(): Specification.Workflow | undefined {
    return this.model;
  }

  getPastValidContentType(): "yaml" | "json" | undefined {
    return this.pastContentType;
  }

  getPastValidContent(): string | undefined {
    return this.pastContent;
  }

  getPastValidModel(): Specification.Workflow | undefined {
    return this.pastModel;
  }

  reset() {
    this.rawContent = undefined;
    this.contentType = undefined;
    this.model = undefined;
    this.valid = undefined;
    this.errors = [];
    this.pastContent = undefined;
    this.pastModel = undefined;
    this.pastContentType = undefined;
  }

  setCurrentContentInvalid() {
    this.model = undefined;
    this.valid = false;
  }

  setCurrentContentValid() {
    this.valid = true;
    this.errors = [];
    this.pastContent = this.rawContent;
    this.pastModel = this.model;
    this.pastContentType = this.contentType;
  }

  clearCurrent() {
    this.rawContent = undefined;
    this.contentType = undefined;
    this.model = undefined;
    this.valid = undefined;
    this.errors = [];
  }
}

export function getMarshaller(): Marshaller {
  return new MarshallerImpl();
}

export function validateModel(model: Specification.Workflow): string[] {
  const validator = new WorkflowValidator(model);

  if (!validator.isValid) {
    const errors: string[] = [];
    validator.errors.forEach((error) => errors.push((error as ValidationError).message));

    return errors;
  }

  return [];
}

export function deserializeSwf(content: string, errors: string[]): Specification.Workflow | undefined {
  if (!content) {
    return undefined;
  }

  try {
    const swf = Specification.Workflow.fromSource(content);

    return swf;
  } catch (e) {
    errors.push(e.message);
    return undefined;
  }
}

export function serializeSwf(swf: Specification.Workflow, format: "yaml" | "json"): string | undefined {
  try {
    switch (format) {
      case "json": {
        return Specification.Workflow.toJson(swf);
      }
      default: {
        return Specification.Workflow.toYaml(swf);
      }
    }
  } catch (e) {
    console.error("SWF serializeSwf: ", e);
    return undefined;
  }
}

export function getRawContentType(content: string): "yaml" | "json" | undefined {
  if (!content) {
    return undefined;
  }

  if (content.trim().startsWith("{")) {
    return "json";
  }

  return "yaml";
}
