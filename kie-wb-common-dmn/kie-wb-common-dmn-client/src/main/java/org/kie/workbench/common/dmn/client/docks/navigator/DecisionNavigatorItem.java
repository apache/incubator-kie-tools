/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.docks.navigator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

import org.uberfire.mvp.Command;

public class DecisionNavigatorItem implements Comparable {

    private String uuid;

    private String label;

    private Type type;

    private Command onClick;

    private String parentUUID;

    private TreeSet<DecisionNavigatorItem> children = new TreeSet<>();

    public DecisionNavigatorItem(final String uuid,
                                 final String label,
                                 final Type type,
                                 final Command onClick,
                                 final String parentUUID) {
        this.uuid = uuid;
        this.label = label;
        this.type = type;
        this.onClick = onClick;
        this.parentUUID = parentUUID;
    }

    public DecisionNavigatorItem(final String uuid) {
        this.uuid = uuid;
    }

    public String getUUID() {
        return uuid;
    }

    public String getLabel() {
        return label;
    }

    public Type getType() {
        return type;
    }

    public TreeSet<DecisionNavigatorItem> getChildren() {
        return children;
    }

    public void removeChild(final DecisionNavigatorItem item) {
        getChildren().removeIf(i -> i.getUUID().equals(item.getUUID()));
    }

    public void addChild(final DecisionNavigatorItem item) {
        removeChild(item);
        getChildren().add(item);
    }

    public void onClick() {
        onClick.execute();
    }

    public Command getOnClick() {
        return onClick;
    }

    public String getParentUUID() {
        return parentUUID;
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final DecisionNavigatorItem item = (DecisionNavigatorItem) o;

        return Objects.equals(uuid, item.uuid) &&
                Objects.equals(label, item.label) &&
                Objects.equals(parentUUID, item.parentUUID) &&
                type == item.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, label, type, parentUUID);
    }

    @Override
    public int compareTo(final Object o) {

        if (o == null || getClass() != o.getClass()) {
            return 1;
        }

        final DecisionNavigatorItem that = (DecisionNavigatorItem) o;

        if (this.equals(that)) {
            return 0;
        } else {
            return getOrderingName().compareTo(that.getOrderingName());
        }
    }

    String getOrderingName() {
        final String orderingLabel = getLabel() + getUUID();
        return orderingLabel.toLowerCase();
    }

    public enum Type {
        ROOT("Root"),
        ITEM("Item"),
        CONTEXT("Context"),
        DECISION_TABLE("DecisionTable"),
        FUNCTION_DEFINITION("FunctionDefinition"),
        INVOCATION("Invocation"),
        LIST("List"),
        LITERAL_EXPRESSION("LiteralExpression"),
        RELATION("Relation"),
        TEXT_ANNOTATION("TextAnnotation"),
        BUSINESS_KNOWLEDGE_MODEL("BusinessKnowledgeModel"),
        INPUT_DATA("InputData"),
        DECISION_SERVICE("DecisionService"),
        KNOWLEDGE_SOURCE("KnowledgeSource"),
        DECISION("Decision");

        private static final Map<String, Type> BY_CLASS_NAME = new HashMap<>();

        static {
            for (final Type type : values()) {
                BY_CLASS_NAME.put(type.navigatorItemClassName, type);
            }
        }

        private final String navigatorItemClassName;

        Type(final String navigatorItemClassName) {
            this.navigatorItemClassName = navigatorItemClassName;
        }

        public static Type ofExpressionNodeClassName(final String nodeClassName) {
            return BY_CLASS_NAME.getOrDefault(nodeClassName, ITEM);
        }
    }
}
