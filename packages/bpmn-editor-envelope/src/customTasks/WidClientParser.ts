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

// TypeScript equivalent of WorkItemDefinitionClientParser
import { MvelDataType } from "./WidDataTypes.js";

export class IconDefinition {
  private uri: string = "";
  private iconData: string = "";

  public getUri(): string {
    return this.uri;
  }

  public setUri(uri: string): void {
    this.uri = uri;
  }

  public getIconData(): string {
    return this.iconData;
  }

  public setIconData(iconData: string): void {
    this.iconData = iconData;
  }
}

/**
 * Work Item Definition (WID)
 */
export class WorkItemDefinition {
  private name: string = "";
  private displayName: string = "";
  private category: string = "";
  private description: string = "";
  private documentation: string = "";
  private uri: string = "";
  private parameters: Map<string, string> = new Map();
  private results: Map<string, string> = new Map();
  private defaultHandler: string = "";
  private iconDefinition: IconDefinition = new IconDefinition();

  public getName(): string {
    return this.name;
  }

  public setName(name: string): void {
    this.name = name;
  }

  public getDisplayName(): string {
    return this.displayName;
  }

  public setDisplayName(displayName: string): void {
    this.displayName = displayName;
  }

  public getCategory(): string {
    return this.category;
  }

  public setCategory(category: string): void {
    this.category = category;
  }

  public getDescription(): string {
    return this.description;
  }

  public setDescription(description: string): void {
    this.description = description;
  }

  public getDocumentation(): string {
    return this.documentation;
  }

  public setDocumentation(documentation: string): void {
    this.documentation = documentation;
  }

  public getUri(): string {
    return this.uri;
  }

  public setUri(uri: string): void {
    this.uri = uri;
  }

  public getParameters(): Map<string, string> {
    return this.parameters;
  }

  public setParameters(parameters: Map<string, string>): void {
    this.parameters = parameters;
  }

  public getResults(): Map<string, string> {
    return this.results;
  }

  public setResults(results: Map<string, string>): void {
    this.results = results;
  }

  public getDefaultHandler(): string {
    return this.defaultHandler;
  }

  public setDefaultHandler(defaultHandler: string): void {
    this.defaultHandler = defaultHandler;
  }

  public getIconDefinition(): IconDefinition {
    return this.iconDefinition;
  }

  public setIconDefinition(iconDefinition: IconDefinition): void {
    this.iconDefinition = iconDefinition;
  }
}

interface MapEntry<K, V> {
  key: K;
  value: V;
}

export class WorkItemDefinitionClientParser {
  private readonly NAME = "name";
  private readonly DISPLAY_NAME = "displayName";
  private readonly ICON = "icon";
  private readonly PARAMETERS = "parameters";
  private readonly RESULTS = "results";
  private readonly CATEGORY = "category";
  private readonly DOCUMENTATION = "documentation";
  private readonly DEFAULT_HANDLER = "defaultHandler";
  private readonly DESCRIPTION = "description";

  // KOGITO-4372. Properties supported by the engine but not supported by BPMN Editor
  private readonly CUSTOM_EDITOR = "customEditor";
  private readonly PARAMETER_VALUES = "parameterValues";
  private readonly DEPENDENCIES = "dependencies";
  private readonly VERSION = "version";
  private readonly MAVEN_DEPENDENCIES = "mavenDependencies";

  private readonly ICON_PREFIX = "data:image";
  private readonly NEW = "new";

  private index: number;
  // Waiting for KOGITO-3846 before we can use it
  private lineNumber: number;
  private widString: string;

  public parse(widStr: string): WorkItemDefinition[] {
    if (widStr == null || isEmpty(widStr.trim())) {
      return [];
    }

    this.index = 0;
    this.lineNumber = 1;
    this.widString = widStr;
    try {
      return this.parseWid();
    } catch (ex) {
      this.reportAnError((ex as Error).message);
      return [];
    }
  }

  private parseWid(): WorkItemDefinition[] {
    this.skipToObjectStart();
    this.findNextToken();

    const widFile: Map<string, any>[] = [];
    while (this.isObjectStart(this.skipWhitespaceAndComments())) {
      const token = this.findNextToken();
      switch (token) {
        case '"':
          widFile.push(this.getMapEntries());
          break;
        case "]":
          // WID is empty, just skip it
          break;
        default:
          // If current WID file is incorrect return all already parsed and skip others
          return this.convertMvelToWid(widFile);
      }
      if (this.isElementSeparator(this.skipWhitespaceAndComments())) {
        this.findNextToken();
      }
    }

    return this.convertMvelToWid(widFile);
  }

  private convertMvelToWid(widFile: Map<string, any>[]): WorkItemDefinition[] {
    const wids: WorkItemDefinition[] = [];
    for (const widItem of widFile) {
      const wid = this.emptyWid();

      if (widItem.get(this.NAME) != null) {
        wid.setName(widItem.get(this.NAME).toString());
      }
      if (widItem.get(this.DISPLAY_NAME) != null) {
        wid.setDisplayName(widItem.get(this.DISPLAY_NAME).toString());
      }
      if (widItem.get(this.ICON) != null) {
        if (widItem.get(this.ICON).toString().startsWith(this.ICON_PREFIX)) {
          wid.getIconDefinition().setIconData(widItem.get(this.ICON).toString());
        } else {
          wid.getIconDefinition().setUri(widItem.get(this.ICON).toString());
        }
      }
      if (widItem.get(this.DOCUMENTATION) != null) {
        wid.setDocumentation(widItem.get(this.DOCUMENTATION).toString());
      }
      if (widItem.get(this.CATEGORY) != null) {
        wid.setCategory(widItem.get(this.CATEGORY).toString());
      }
      if (isEmpty(wid.getCategory())) {
        wid.setCategory("CustomTasks"); // Hard-coded on purpose.
      }

      if (widItem.get(this.DEFAULT_HANDLER) != null) {
        // It has no visual representation in BPMN Editor so far but model already done, so parsing it
        wid.setDefaultHandler(widItem.get(this.DEFAULT_HANDLER).toString());
      }

      if (widItem.get(this.DESCRIPTION) != null) {
        wid.setDescription(widItem.get(this.DESCRIPTION).toString());
      }
      if (widItem.get(this.PARAMETERS) != null) {
        wid.setParameters(this.retrieveParameters(widItem.get(this.PARAMETERS) as Map<string, any>));
      }
      if (widItem.get(this.RESULTS) != null) {
        wid.setResults(this.retrieveParameters(widItem.get(this.RESULTS) as Map<string, any>));
      }

      wids.push(wid);
    }
    return wids;
  }

  public emptyWid(): WorkItemDefinition {
    const wid = new WorkItemDefinition();
    wid.setIconDefinition(new IconDefinition());
    wid.getIconDefinition().setUri("");
    wid.getIconDefinition().setIconData("");
    wid.setUri("");
    wid.setName("");
    wid.setCategory("");
    wid.setDescription("");
    wid.setDocumentation("");
    wid.setDisplayName("");
    wid.setResults(new Map());
    wid.setDefaultHandler("");
    wid.setParameters(new Map());
    return wid;
  }

  private retrieveParameters(params: Map<string, any>): Map<string, string> {
    params.forEach((value, key) => {
      params.set(key, this.updateMvelType({ key, value }));
    });

    return params;
  }

  private updateMvelType(entry: MapEntry<string, any>): any {
    const paramType = entry.value
      .toString()
      .trim()
      .replace(new RegExp(this.NEW, "g"), "")
      .replace(/,/g, "")
      .replace(/\(\)/g, "")
      .trim();
    return MvelDataType.getJavaTypeByMvelType(paramType);
  }

  private getMapEntries(): Map<string, any> {
    const params = new Map<string, any>();
    while (this.notObjectEnd(this.skipWhitespaceAndComments())) {
      if (this.isElementSeparator(this.getCurrentSymbol())) {
        this.index++;
        continue;
      }
      if (this.isAttributeWrapper(this.getCurrentSymbol())) {
        const entry = this.getEntry();
        params.set(entry.key, entry.value);
        continue;
      }
      throw new Error(`Invalid parameter line: ${this.lineNumber}, file position: ${this.index}`);
    }
    this.index++;

    return params;
  }

  private isElementSeparator(symbol: string): boolean {
    return symbol === ",";
  }

  private getEntry(): MapEntry<string, any> {
    this.skipWhitespaceAndComments();
    const name = this.parseString();
    if (this.isObjectEnd(this.skipWhitespaceAndComments())) {
      return { key: name, value: null };
    }
    if (this.isElementSeparator(this.skipWhitespaceAndComments())) {
      return { key: name, value: null };
    }
    if (this.notParameterDivider(this.skipWhitespaceAndComments())) {
      throw new Error("Invalid parameter");
    }

    this.index++;

    const currentToken = this.skipWhitespaceAndComments();

    let parameterValue: any;

    switch (currentToken) {
      // String
      case '"':
      case "'":
        parameterValue = this.parseString();
        break;
      case "[":
        // Object Start
        this.findNextToken();
        parameterValue = this.getMapEntries();
        this.index++;
        break;
      default:
        // Literal (type, numeric, Floating Point, BigInteger, BigDecimal, boolean, null)
        parameterValue = this.parseLiteral();
        break;
    }

    return { key: name, value: parameterValue };
  }

  private reportAnError(message: string): void {
    // Show error message here. You can use message and lineNumber variables.
    // See KOGITO-3846 for more details
    console.error(`Error at line ${this.lineNumber}: ${message}`);
  }

  private findNextToken(): string {
    this.index++;
    return this.skipWhitespaceAndComments();
  }

  private getCurrentSymbol(): string {
    return this.widString.charAt(this.index);
  }

  private skipWhitespaceAndComments(): string {
    while (this.isWhitespace(this.getCurrentSymbol()) || this.isComment(this.getCurrentSymbol())) {
      if (this.isComment(this.getCurrentSymbol())) {
        this.skipComment();
        continue;
      }

      if (this.getCurrentSymbol() === "\n") {
        this.lineNumber++;
      }
      this.index++;
    }

    return this.getCurrentSymbol();
  }

  private isWhitespace(symbol: string): boolean {
    switch (symbol) {
      case " ":
      case "\n":
      case "\r":
      case "\t":
        return true;
      default:
        return false;
    }
  }

  private isComment(symbol: string): boolean {
    if (this.isSlash(symbol)) {
      if (this.isSlash(this.widString.charAt(this.index + 1)) || this.isStar(this.widString.charAt(this.index + 1))) {
        return true;
      }
    }

    return false;
  }

  private skipComment(): void {
    this.index++;
    if (this.isStar(this.getCurrentSymbol())) {
      this.skipMultiLineComment();
    } else {
      this.skipSingleLineComment();
    }
  }

  private skipMultiLineComment(): void {
    do {
      this.index++;
      if (this.isStar(this.getCurrentSymbol()) && this.isSlash(this.widString.charAt(this.index + 1))) {
        this.index += 2;
        break;
      }
    } while (this.index < this.widString.length - 1);
  }

  private skipSingleLineComment(): void {
    do {
      this.index++;
      if (this.getCurrentSymbol() === "\n") {
        break;
      }
    } while (this.index < this.widString.length - 1);
  }

  private parseString(): string {
    const wrapper = this.getCurrentSymbol();
    this.index++;
    if (this.notAttributeWrapper(wrapper)) {
      throw new Error("Invalid wrapper symbol");
    }

    const name: string[] = [];
    for (; this.nonStringEnd(wrapper); this.index++) {
      name.push(this.getCurrentSymbol());
    }

    this.index++;
    return name.join("");
  }

  private parseLiteral(): string {
    const literal: string[] = [];
    for (; this.widString.length > this.index && this.isLiteralSymbol(this.getCurrentSymbol()); this.index++) {
      literal.push(this.getCurrentSymbol());
    }

    return literal.join("");
  }

  private nonStringEnd(endOfString: string): boolean {
    if (this.isEscape(this.getCurrentSymbol())) {
      this.index++;
      return true;
    }
    return this.getCurrentSymbol() !== endOfString;
  }

  private isLiteralSymbol(symbol: string): boolean {
    return !this.notLiteralSymbol(symbol);
  }

  private notLiteralSymbol(symbol: string): boolean {
    switch (symbol) {
      case "\n":
      case "\r":
      case "\t":
      case '"':
      case "'":
      case ",":
      case "/":
      case "]":
        return true;
      default:
        return false;
    }
  }

  private isSlash(symbol: string): boolean {
    return symbol === "/";
  }

  private isStar(symbol: string): boolean {
    return symbol === "*";
  }

  private isObjectStart(symbol: string): boolean {
    return symbol === "[";
  }

  private isAttributeWrapper(symbol: string): boolean {
    return symbol === '"' || symbol === "'";
  }

  private isEscape(symbol: string): boolean {
    return symbol === "\\";
  }

  private isParameterDivider(symbol: string): boolean {
    return symbol === ":";
  }

  private notParameterDivider(symbol: string): boolean {
    return !this.isParameterDivider(symbol);
  }

  private notAttributeWrapper(symbol: string): boolean {
    return !this.isAttributeWrapper(symbol);
  }

  private isObjectEnd(symbol: string): boolean {
    return symbol === "]";
  }

  private notObjectEnd(symbol: string): boolean {
    return !this.isObjectEnd(symbol);
  }

  private skipToObjectStart(): void {
    this.skipWhitespaceAndComments();
    while (this.notObjectStart(this.getCurrentSymbol())) {
      this.index++;
      this.skipWhitespaceAndComments();
    }
  }

  private notObjectStart(symbol: string): boolean {
    return !this.isObjectStart(symbol);
  }
}

/**
 * Utility functions for Work Item Definition parsing
 */

/**
 * Checks if a string is empty, null, or undefined
 * @param str - The string to check
 * @returns true if the string is empty, null, or undefined
 */
export function isEmpty(str: string | null | undefined): boolean {
  return str == null || str.trim().length === 0;
}

/**
 * Checks if a string is not empty
 * @param str - The string to check
 * @returns true if the string is not empty, null, or undefined
 */
export function isNotEmpty(str: string | null | undefined): boolean {
  return !isEmpty(str);
}

/**
 * Gets the default icon data for work items
 * This returns a base64-encoded default icon that can be used when no specific icon is provided
 * @returns Base64-encoded default icon data
 */
export function getDefaultIconData(): string {
  // This is a simple default icon - a small gray square as base64 PNG
  // In a real implementation, you might want to use a more sophisticated default icon
  return "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAACxGlDQ1BJQ0MgUHJvZmlsZQAAOI2NlEtr1FAUx/8ZU1qwCIVaSxEJClKkrbE+sIjamb4YW8dh+tAWQWYyd2ZiM5mYZPqiuOjGpdUvIGoXLvwAXbhwZTdKhVrdFOm2ilIsdCNlPDfJTDLU14Ukv3se/3NucnOBmqNJw9BCEpDXbTMxEJFujU9ItVuog4AGnEZXUrGMcDw+BBrlZ/XY/UixNNbaudZ+/1/HwTSzFECoIy6kLSVPPAOEfiiGaQMi1zsxbRucF4kbTWqQeIlz1uVlzimX3zoxI4ke4k3iQ0oumSbeJm5LBezZALs9OKNxgOnMVBWJv4u4WcioGgu0+w/3f468VizXa6Crnumjw/Q8Seu+z6w+j4WFdLJ3kLiD+Eua9fbxNRC/yKj9UeJWujYyZv+oy8Ip1Y6OuBwa07XYkMfNeip2w9MUDTuSKOtbU8N9Zfvd5LU4cQtxYrIwyGOaKLd7Ljdy02VBm8v1xDz+YBYTvO5xilkyNGdfUG+hNxiHBgYVOt11SEhgABG0w4CJAjLkUSlCJStzYkxiC5O/jdQQD7BE/i3K2XJy7qFI2TxrDJEYFtoqCpL8Sf4mr8tP5SX562JLsdX3LJh3VGX14Tbp8splXc/r9eTqK1Q3TF4NWbLmK2uyAp0Gusvoiy2+Eq/BHsR2A6tkgUrtSDlKk06lKcdn0T0cqPant0exK/Ovmv1a6+Ly7bX6lfmqd1XYtypWWVX13I8L5jPxmHhGjIoXxS5I4lWxW7wi9tLskjhUyRglFRXTjpKFJGXpmCWvUfXN3FjYbMbmG76nYMyaajZnS2E6LZgU1ZWONqlT7jwP8LPH/S12Es6ZIjSt+jb7OnD5O3DgvW+bKAIvLeDIOd/WSnv48BNg+YJSNKe8/0wQ3gFW5mynO6unjmo+l0o7tPdrHwN7j0qln89Kpb3npL8BvNZ+AdoD9izImpg/AAAAsmVYSWZNTQAqAAAACAAHARIAAwAAAAEAAQAAARoABQAAAAEAAABiARsABQAAAAEAAABqASgAAwAAAAEAAwAAATEAAgAAAA0AAAByATIAAgAAABQAAACAh2kABAAAAAEAAACUAAAAAAAAAEgAAAABAAAASAAAAAFHSU1QIDIuMTAuMjIAADIwMjE6MDI6MjQgMTA6NDE6NTAAAAKgAgAEAAAAAQAAABSgAwAEAAAAAQAAABQAAAAAwFOzXAAAAAlwSFlzAAALEwAACxMBAJqcGAAADIZpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IlhNUCBDb3JlIDYuMC4wIj4KICAgPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4KICAgICAgPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIKICAgICAgICAgICAgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIgogICAgICAgICAgICB4bWxuczpHSU1QPSJodHRwOi8vd3d3LmdpbXAub3JnL3htcC8iCiAgICAgICAgICAgIHhtbG5zOnRpZmY9Imh0dHA6Ly9ucy5hZG9iZS5jb20vdGlmZi8xLjAvIgogICAgICAgICAgICB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIKICAgICAgICAgICAgeG1sbnM6c3RFdnQ9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZUV2ZW50IyIKICAgICAgICAgICAgeG1sbnM6cGx1cz0iaHR0cDovL25zLnVzZXBsdXMub3JnL2xkZi94bXAvMS4wLyIKICAgICAgICAgICAgeG1sbnM6SXB0YzR4bXBFeHQ9Imh0dHA6Ly9pcHRjLm9yZy9zdGQvSXB0YzR4bXBFeHQvMjAwOC0wMi0yOS8iCiAgICAgICAgICAgIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyI+CiAgICAgICAgIDx4bXA6Q3JlYXRvclRvb2w+R0lNUCAyLjEwLjIyPC94bXA6Q3JlYXRvclRvb2w+CiAgICAgICAgIDx4bXA6TW9kaWZ5RGF0ZT4yMDIxLTAyLTI0VDEwOjQxOjUwPC94bXA6TW9kaWZ5RGF0ZT4KICAgICAgICAgPEdJTVA6QVBJPjIuMDwvR0lNUDpBUEk+CiAgICAgICAgIDxHSU1QOlZlcnNpb24+Mi4xMC4yMjwvR0lNUDpWZXJzaW9uPgogICAgICAgICA8R0lNUDpQbGF0Zm9ybT5MaW51eDwvR0lNUDpQbGF0Zm9ybT4KICAgICAgICAgPEdJTVA6VGltZVN0YW1wPjE2MTQxNzQxMTIzMjIyNDk8L0dJTVA6VGltZVN0YW1wPgogICAgICAgICA8dGlmZjpSZXNvbHV0aW9uVW5pdD4zPC90aWZmOlJlc29sdXRpb25Vbml0PgogICAgICAgICA8dGlmZjpPcmllbnRhdGlvbj4xPC90aWZmOk9yaWVudGF0aW9uPgogICAgICAgICA8dGlmZjpZUmVzb2x1dGlvbj41NjcvMjA8L3RpZmY6WVJlc29sdXRpb24+CiAgICAgICAgIDx0aWZmOlhSZXNvbHV0aW9uPjU2Ny8yMDwvdGlmZjpYUmVzb2x1dGlvbj4KICAgICAgICAgPHhtcE1NOkhpc3Rvcnk+CiAgICAgICAgICAgIDxyZGY6U2VxPgogICAgICAgICAgICAgICA8cmRmOmxpIHJkZjpwYXJzZVR5cGU9IlJlc291cmNlIj4KICAgICAgICAgICAgICAgICAgPHN0RXZ0OmNoYW5nZWQ+Lzwvc3RFdnQ6Y2hhbmdlZD4KICAgICAgICAgICAgICAgICAgPHN0RXZ0OnNvZnR3YXJlQWdlbnQ+R2ltcCAyLjEwIChMaW51eCk8L3N0RXZ0OnNvZnR3YXJlQWdlbnQ+CiAgICAgICAgICAgICAgICAgIDxzdEV2dDp3aGVuPi0wMzowMDwvc3RFdnQ6d2hlbj4KICAgICAgICAgICAgICAgICAgPHN0RXZ0Omluc3RhbmNlSUQ+eG1wLmlpZDo3YzUwYTU5ZS05NGQwLTQ5OWQtYjQxZC1lMmM3OThiNzU5NTk8L3N0RXZ0Omluc3RhbmNlSUQ+CiAgICAgICAgICAgICAgICAgIDxzdEV2dDphY3Rpb24+c2F2ZWQ8L3N0RXZ0OmFjdGlvbj4KICAgICAgICAgICAgICAgPC9yZGY6bGk+CiAgICAgICAgICAgICAgIDxyZGY6bGkgcmRmOnBhcnNlVHlwZT0iUmVzb3VyY2UiPgogICAgICAgICAgICAgICAgICA8c3RFdnQ6Y2hhbmdlZD4vPC9zdEV2dDpjaGFuZ2VkPgogICAgICAgICAgICAgICAgICA8c3RFdnQ6c29mdHdhcmVBZ2VudD5HaW1wIDIuMTAgKExpbnV4KTwvc3RFdnQ6c29mdHdhcmVBZ2VudD4KICAgICAgICAgICAgICAgICAgPHN0RXZ0OndoZW4+LTAzOjAwPC9zdEV2dDp3aGVuPgogICAgICAgICAgICAgICAgICA8c3RFdnQ6aW5zdGFuY2VJRD54bXAuaWlkOjZhZTAwNjBlLWZmZjEtNDk5ZS05MDZmLWNmMWJiMmUzYWU2Zjwvc3RFdnQ6aW5zdGFuY2VJRD4KICAgICAgICAgICAgICAgICAgPHN0RXZ0OmFjdGlvbj5zYXZlZDwvc3RFdnQ6YWN0aW9uPgogICAgICAgICAgICAgICA8L3JkZjpsaT4KICAgICAgICAgICAgPC9yZGY6U2VxPgogICAgICAgICA8L3htcE1NOkhpc3Rvcnk+CiAgICAgICAgIDx4bXBNTTpPcmlnaW5hbERvY3VtZW50SUQ+eG1wLmRpZDoyOTk4MTI3OS1hOTk3LTQ2ZDMtOTE5OC05YmY0MDRiODY2OGE8L3htcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD4KICAgICAgICAgPHhtcE1NOkluc3RhbmNlSUQ+eG1wLmlpZDpkNjdjNTdiZi0yMGYwLTQ2ODYtOTdlZi1mYmIwZmVlN2IyOGE8L3htcE1NOkluc3RhbmNlSUQ+CiAgICAgICAgIDx4bXBNTTpEb2N1bWVudElEPmdpbXA6ZG9jaWQ6Z2ltcDoyNDViZTI2NC1mNDQ2LTRiM2MtYmE4Zi1hNDZiNTAzODk1NGE8L3htcE1NOkRvY3VtZW50SUQ+CiAgICAgICAgIDxwbHVzOkNvcHlyaWdodE93bmVyPgogICAgICAgICAgICA8cmRmOlNlcS8+CiAgICAgICAgIDwvcGx1czpDb3B5cmlnaHRPd25lcj4KICAgICAgICAgPHBsdXM6SW1hZ2VTdXBwbGllcj4KICAgICAgICAgICAgPHJkZjpTZXEvPgogICAgICAgICA8L3BsdXM6SW1hZ2VTdXBwbGllcj4KICAgICAgICAgPHBsdXM6SW1hZ2VDcmVhdG9yPgogICAgICAgICAgICA8cmRmOlNlcS8+CiAgICAgICAgIDwvcGx1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";
}

/**
 * Sanitizes a string by removing unwanted characters
 * @param str - The string to sanitize
 * @returns The sanitized string
 */
export function sanitizeString(str: string): string {
  if (isEmpty(str)) {
    return "";
  }
  return str
    .trim()
    .replace(/[\r\n\t]/g, " ")
    .replace(/\s+/g, " ");
}

/**
 * Converts a string to camelCase
 * @param str - The string to convert
 * @returns The camelCase string
 */
export function toCamelCase(str: string): string {
  if (isEmpty(str)) {
    return "";
  }

  return str.toLowerCase().replace(/[^a-zA-Z0-9]+(.)/g, (_, char) => char.toUpperCase());
}

/**
 * Converts a string to PascalCase
 * @param str - The string to convert
 * @returns The PascalCase string
 */
export function toPascalCase(str: string): string {
  const camelCase = toCamelCase(str);
  if (isEmpty(camelCase)) {
    return "";
  }

  return camelCase.charAt(0).toUpperCase() + camelCase.slice(1);
}

/**
 * Validates if a string is a valid identifier (for names, etc.)
 * @param str - The string to validate
 * @returns true if the string is a valid identifier
 */
export function isValidIdentifier(str: string): boolean {
  if (isEmpty(str)) {
    return false;
  }

  // Must start with letter or underscore, followed by letters, numbers, or underscores
  return /^[a-zA-Z_][a-zA-Z0-9_]*$/.test(str.trim());
}

/**
 * Escapes special characters in a string for use in regular expressions
 * @param str - The string to escape
 * @returns The escaped string
 */
export function escapeRegExp(str: string): string {
  if (isEmpty(str)) {
    return "";
  }

  return str.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}

/**
 * Truncates a string to a maximum length, adding ellipsis if needed
 * @param str - The string to truncate
 * @param maxLength - The maximum length
 * @returns The truncated string
 */
export function truncateString(str: string, maxLength: number): string {
  if (isEmpty(str) || str.length <= maxLength) {
    return str || "";
  }

  return str.substring(0, maxLength - 3) + "...";
}

/**
 * Checks if a string represents a boolean value
 * @param str - The string to check
 * @returns true if the string represents a boolean
 */
export function isBoolean(str: string): boolean {
  if (isEmpty(str)) {
    return false;
  }

  const lower = str.toLowerCase().trim();
  return lower === "true" || lower === "false";
}

/**
 * Checks if a string represents a numeric value
 * @param str - The string to check
 * @returns true if the string represents a number
 */
export function isNumeric(str: string): boolean {
  if (isEmpty(str)) {
    return false;
  }

  return !isNaN(Number(str.trim()));
}

/**
 * Parses a string to its appropriate type (string, number, boolean)
 * @param str - The string to parse
 * @returns The parsed value
 */
export function parseValue(str: string): string | number | boolean {
  if (isEmpty(str)) {
    return str || "";
  }

  const trimmed = str.trim();

  if (isBoolean(trimmed)) {
    return trimmed.toLowerCase() === "true";
  }

  if (isNumeric(trimmed)) {
    const num = Number(trimmed);
    return Number.isInteger(num) ? parseInt(trimmed, 10) : parseFloat(trimmed);
  }

  return str;
}
