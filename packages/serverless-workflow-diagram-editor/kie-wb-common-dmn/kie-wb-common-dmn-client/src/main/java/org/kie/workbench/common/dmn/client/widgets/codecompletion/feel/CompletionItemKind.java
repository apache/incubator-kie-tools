/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.codecompletion.feel;

public enum CompletionItemKind {
    Method(0),
    Function(1),
    Constructor(2),
    Field(3),
    Variable(4),
    Class(5),
    Struct(6),
    Interface(7),
    Module(8),
    Property(9),
    Event(10),
    Operator(11),
    Unit(12),
    Value(13),
    Constant(14),
    Enum(15),
    EnumMember(16),
    Keyword(17),
    Text(18),
    Color(19),
    File(20),
    Reference(21),
    Customcolor(22),
    Folder(23),
    TypeParameter(24),
    User(25),
    Issue(26),
    Snippet(27);

    private final int value;

    CompletionItemKind(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
