/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

export type ASTDocument = {
  root?: ASTNode;
  getNodeFromOffset(offset: number, includeRightBound?: boolean): ASTNode | undefined;
};

export interface ASTNode {
  readonly type: "object" | "array" | "property" | "string" | "number" | "boolean" | "null";
  readonly parent?: ASTNode;
  readonly offset: number;
  readonly length: number;
  readonly children?: ASTNode[];
  readonly value?: string | boolean | number | null;
  location: string;
  getNodeFromOffsetEndInclusive(offset: number): ASTNode | undefined;
}

export interface ObjectASTNode extends ASTNode {
  readonly type: "object";
  readonly properties: PropertyASTNode[];
  readonly children: ASTNode[];
}
export interface PropertyASTNode extends ASTNode {
  readonly type: "property";
  readonly keyNode: StringASTNode;
  readonly valueNode?: ASTNode;
  readonly colonOffset?: number;
  readonly children: ASTNode[];
}
export interface ArrayASTNode extends ASTNode {
  readonly type: "array";
  readonly items: ASTNode[];
  readonly children: ASTNode[];
}
export interface StringASTNode extends ASTNode {
  readonly type: "string";
  readonly value: string;
}
export interface NumberASTNode extends ASTNode {
  readonly type: "number";
  readonly value: number;
  readonly isInteger: boolean;
}
export interface BooleanASTNode extends ASTNode {
  readonly type: "boolean";
  readonly value: boolean;
}
export interface NullASTNode extends ASTNode {
  readonly type: "null";
  readonly value: null;
}

export abstract class ASTNodeImpl {
  public abstract readonly type: "object" | "property" | "array" | "number" | "boolean" | "null" | "string";

  public offset: number;
  public length: number;
  public readonly parent: ASTNode | undefined;
  public location: string;

  constructor(parent: ASTNode | undefined, offset: number, length?: number) {
    this.offset = offset;
    this.parent = parent;
    if (length) {
      this.length = length;
    }
  }

  public getNodeFromOffsetEndInclusive(offset: number): ASTNode | undefined {
    const collector: ASTNode[] = [];
    const findNode = (node: ASTNode): ASTNode | undefined => {
      if (offset >= node.offset && offset <= node.offset + node.length) {
        const children = node.children;
        if (children) {
          for (let i = 0; i < children.length && children[i].offset <= offset; i++) {
            const item = findNode(children[i]);
            if (item) {
              collector.push(item);
            }
          }
          return node;
        }
      }
      return undefined;
    };
    const foundNode = findNode(this as ASTNode);
    let currMinDist = Number.MAX_VALUE;
    let currMinNode = null;
    for (const possibleNode in collector) {
      const currNode = collector[possibleNode];
      const minDist = currNode.length + currNode.offset - offset + (offset - currNode.offset);
      if (minDist < currMinDist) {
        currMinNode = currNode;
        currMinDist = minDist;
      }
    }
    return currMinNode || foundNode;
  }

  public get children(): ASTNode[] {
    return [];
  }

  public toString(): string {
    return (
      "type: " +
      this.type +
      " (" +
      this.offset +
      "/" +
      this.length +
      ")" +
      (this.parent ? " parent: {" + this.parent.toString() + "}" : "")
    );
  }
}

export class ArrayASTNodeImpl extends ASTNodeImpl implements ArrayASTNode {
  public type: "array" = "array";
  public items: ASTNode[];

  constructor(parent: ASTNode | undefined, offset: number, length?: number) {
    super(parent, offset, length);
    this.items = [];
  }

  public get children(): ASTNode[] {
    return this.items;
  }
}

export class NullASTNodeImpl extends ASTNodeImpl implements NullASTNode {
  public type: "null" = "null";
  public value = null;
  constructor(parent: ASTNode | undefined, offset: number, length?: number) {
    super(parent, offset, length);
  }
}

export class BooleanASTNodeImpl extends ASTNodeImpl implements BooleanASTNode {
  public type: "boolean" = "boolean";
  public value: boolean;

  constructor(parent: ASTNode | undefined, boolValue: boolean, offset: number, length?: number) {
    super(parent, offset, length);
    this.value = boolValue;
  }
}

export class NumberASTNodeImpl extends ASTNodeImpl implements NumberASTNode {
  public type: "number" = "number";
  public isInteger: boolean;
  public value: number;

  constructor(parent: ASTNode | undefined, offset: number, length?: number) {
    super(parent, offset, length);
    this.isInteger = true;
    this.value = Number.NaN;
  }
}

export class StringASTNodeImpl extends ASTNodeImpl implements StringASTNode {
  public type: "string" = "string";
  public value: string;

  constructor(parent: ASTNode | undefined, offset: number, length?: number) {
    super(parent, offset, length);
    this.value = "";
  }
}

export class PropertyASTNodeImpl extends ASTNodeImpl implements PropertyASTNode {
  public type: "property" = "property";
  public keyNode: StringASTNode;
  public valueNode: ASTNode;
  public colonOffset: number;

  constructor(parent: ObjectASTNode | undefined, offset: number, length?: number) {
    super(parent, offset, length);
    this.colonOffset = -1;
  }

  public get children(): ASTNode[] {
    return this.valueNode ? [this.keyNode, this.valueNode] : [this.keyNode];
  }
}

export class ObjectASTNodeImpl extends ASTNodeImpl implements ObjectASTNode {
  public type: "object" = "object";
  public properties: PropertyASTNode[];

  constructor(parent: ASTNode | undefined, offset: number, length?: number) {
    super(parent, offset, length);

    this.properties = [];
  }

  public get children(): ASTNode[] {
    return this.properties;
  }
}
