/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import * as jsonata from "jsonata";

export type StateBlockOffset = {
  start: number;
  end: number;
};

export type StateOffsets = {
  stateNameOffset: number;
  offset: StateBlockOffset;
};

export type StatesOffsets = {
  [key: string]: StateOffsets;
};

/**
 * type for the object with the coordinates of the full document
 */
export type FullTextOffsets = {
  states: StatesOffsets;
};

/**
 * Api to work with offsets
 */
export abstract class SwfOffsetsApi {
  protected fullText: string;
  protected fullTextOffsets: FullTextOffsets | undefined;

  constructor(protected astTransformQuery: string, public documentUri = "") {}

  /**
   * Parses the content of a file
   *
   * @param fullText -
   * @returns this for chaining
   */
  parseContent(fullText: string): SwfOffsetsApi {
    if (!fullText) {
      this.fullText = "";
      this.fullTextOffsets = undefined;
      return this;
    }

    if (this.fullText === fullText) {
      return this;
    }

    this.fullText = fullText;
    this.fullTextOffsets = this.getAllOffsets();

    return this;
  }

  /**
   * Get the full AST object from the fullText
   */
  abstract getFullAST(): any;

  /**
   * Get All Offsets for easy access.
   *
   * @returns the resulting FullTextOffsets
   */
  getAllOffsets(): FullTextOffsets {
    if (!this.fullText) {
      return { states: {} };
    }

    const fullAST = this.getFullAST();

    if (!fullAST) {
      return { states: {} };
    }

    try {
      return jsonata(this.astTransformQuery).evaluate(fullAST);
    } catch (e) {
      return { states: {} };
    }
  }

  /**
   * Get the Offset of a State Name.
   *
   * @param stateName
   * @returns the offset found, -1 otherwise
   */
  getStateNameOffset(stateName: string): number | undefined {
    if (!stateName) {
      return undefined;
    }

    return this.fullTextOffsets?.states[stateName]?.stateNameOffset || undefined;
  }

  /**
   * Get State Name from a given Offset, checking the whole state block.
   *
   * @param offset
   * @returns the stateName found, null otherwise
   */
  getStateNameFromOffset(offset: number): string | null {
    if (!offset) {
      return null;
    }

    for (const stateName in this.fullTextOffsets?.states) {
      const blockOffset = this.fullTextOffsets!.states[stateName].offset;
      if (offset >= blockOffset.start && offset <= blockOffset.end) {
        return stateName;
      }
    }

    return null;
  }
}
