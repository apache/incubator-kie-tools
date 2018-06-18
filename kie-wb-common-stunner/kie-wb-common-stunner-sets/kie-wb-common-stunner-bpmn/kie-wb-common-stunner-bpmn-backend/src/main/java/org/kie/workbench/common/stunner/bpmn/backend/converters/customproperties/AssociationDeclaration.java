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

package org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties;

public class AssociationDeclaration {

    private Direction direction;
    private Type type;
    private String source;
    private String target;

    public AssociationDeclaration(Direction direction, Type type, String source, String target) {
        this.direction = direction;
        this.type = type;
        this.source = source;
        this.target = target;
    }

    public static AssociationDeclaration fromString(String encoded) {
        return AssociationParser.parse(encoded);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return direction.prefix() +
                (type.equals(Type.FromTo) ?
                        (target + type.op() + source)
                        : (source + type.op() + target));
    }

    public enum Direction {
        Input("[din]"),
        Output("[dout]");

        private final String prefix;

        Direction(String prefix) {
            this.prefix = prefix;
        }

        public String prefix() {
            return prefix;
        }
    }

    public enum Type {
        FromTo("="),
        SourceTarget("->");

        private final String op;

        Type(String op) {
            this.op = op;
        }

        public String op() {
            return op;
        }
    }
}

class AssociationParser {

    public static AssociationDeclaration parse(String encoded) {
        for (AssociationDeclaration.Direction direction : AssociationDeclaration.Direction.values()) {
            if (encoded.startsWith(direction.prefix())) {
                String rest = encoded.substring(direction.prefix().length());
                return parseAssociation(direction, rest);
            }
        }

        throw new IllegalArgumentException("Cannot parse " + encoded);
    }

    private static AssociationDeclaration parseAssociation(AssociationDeclaration.Direction direction, String rest) {
        AssociationDeclaration.Type type;
        type = AssociationDeclaration.Type.SourceTarget;
        if (rest.contains(type.op())) {
            String[] association = rest.split(type.op());
            return new AssociationDeclaration(direction, type, association[0], association[1]);
        }
        type = AssociationDeclaration.Type.FromTo;
        if (rest.contains(type.op())) {
            String[] association = rest.split(type.op());
            return new AssociationDeclaration(direction, type, association[1], association[0]);
        }

        throw new IllegalArgumentException("Cannot parse " + rest);
    }
}
