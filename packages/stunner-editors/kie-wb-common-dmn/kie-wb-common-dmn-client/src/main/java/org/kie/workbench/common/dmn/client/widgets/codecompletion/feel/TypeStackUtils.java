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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.GWT;
import org.kie.dmn.feel.gwt.functions.api.FunctionOverrideVariation;
import org.kie.dmn.feel.gwt.functions.client.FEELFunctionProvider;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.FEELLanguageService.Position;

@ApplicationScoped
public class TypeStackUtils {

    public final List<String> ALLOWED_TYPE_NAMES = Arrays.stream(BuiltInType.values()).flatMap(builtInType -> Arrays.stream(builtInType.getNames())).collect(Collectors.toList());

    private final Map<String, Type> typeByFunctionName = new HashMap<>();

    public List<Type> getTypeStack(final ASTNode node,
                                   final Position position) {
        return getTypeStack(node, null, position, false, false);
    }

    private List<Type> getTypeStack(final ASTNode currentNode,
                                    final ASTNode nextNode,
                                    final Position position,
                                    final boolean isListOfParameters,
                                    final boolean isParentEligible) {

        final List<Type> typeStack = new ArrayList<>();

        if (currentNode == null) {
            return typeStack;
        }

        final Type type = getType(currentNode);
        final boolean isNextListOfParameters = currentNode instanceof FunctionInvocationNode;
        final boolean isEligibleType = isEligibleType(currentNode, nextNode, position, isParentEligible) && !isListOfParameters;

        if (isEligibleType) {
            typeStack.add(type);
        }

        try {
            forEach(getChildren(currentNode), (current, next) -> {
                final boolean isParentEligibleType = isListOfParameters ? isParentEligible : isEligibleType;
                typeStack.addAll(getTypeStack(current, next, position, isNextListOfParameters, isParentEligibleType));
            });
        } catch (final Exception e) {
            // Ignore errors during node inspection.
        }

        return typeStack;
    }

    public Type getType(final ASTNode astNode) {
        if (astNode instanceof FunctionInvocationNode) {
            final String[] split = astNode.getText().split("\\(");
            return getTypeByFunctionName().get(split[0]);
        }
        return astNode.getResultType();
    }

    private Map<String, Type> getTypeByFunctionName() {
        if (typeByFunctionName.isEmpty()) {
            for (final FunctionOverrideVariation definition : getFunctionOverrideVariations()) {
                typeByFunctionName.put(definition.getFunctionName(), definition.getReturnType());
            }
        }
        return typeByFunctionName;
    }

    private List<FunctionOverrideVariation> getFunctionOverrideVariations() {
        final FEELFunctionProvider functionProvider = GWT.create(FEELFunctionProvider.class);
        return functionProvider.getDefinitions();
    }

    private boolean isEligibleType(final ASTNode node,
                                   final ASTNode next,
                                   final Position position,
                                   final boolean isParentEligibleType) {

        final boolean isAllowedType = isAllowedType(node);
        final boolean isEligibleLine = isEligibleLine(node, position.line);
        final boolean isEligibleColumn = isEligibleColumn(node, next, position.column, isParentEligibleType);
        return isAllowedType && isEligibleLine && isEligibleColumn;
    }

    private boolean isAllowedType(final ASTNode node) {
        return ALLOWED_TYPE_NAMES.contains(getType(node).getName());
    }

    private boolean isEligibleLine(final ASTNode node,
                                   final int line) {

        final int stop = node.getEndLine();
        final int start = node.getStartLine();

        return line >= start && line <= stop;
    }

    private boolean isEligibleColumn(final ASTNode node,
                                     final ASTNode next,
                                     final int column,
                                     final boolean isParentEligibleType) {

        return isEligibleColumnStart(node, column) &&
                isEligibleColumnEnd(node, next, column, isParentEligibleType);
    }

    private boolean isEligibleColumnEnd(final ASTNode node,
                                        final ASTNode next,
                                        final int column,
                                        final boolean isParentEligibleType) {

        final boolean hasNextNode = next != null;
        final int startNextNodeColumn = hasNextNode ? next.getStartColumn() : 0;
        final int endNodeColumn = node.getEndColumn() + countExtraChar(node);

        final boolean hasGapBetweenNodes = startNextNodeColumn - endNodeColumn > 0;
        final int stop = hasGapBetweenNodes ? startNextNodeColumn : endNodeColumn;

        return column <= stop || !isParentEligibleType && !hasNextNode;
    }

    private boolean isEligibleColumnStart(final ASTNode node,
                                          final int column) {

        final int startNodeColumn = node.getStartColumn();
        final boolean isMultiline = node.getEndLine() > node.getStartLine();
        final int start = isMultiline ? 0 : startNodeColumn;

        return column >= start;
    }

    private int countExtraChar(final ASTNode node) {
        final Type resultType = getType(node);
        if (resultType.conformsTo(BuiltInType.LIST) || resultType.conformsTo(BuiltInType.RANGE)) {
            return 1;
        }
        return 0;
    }

    private Iterator<ASTNode> getChildren(final ASTNode currentNode) {
        return Arrays.stream(currentNode.getChildrenNode()).iterator();
    }

    private void forEach(final Iterator<ASTNode> iterator,
                         final BiConsumer<ASTNode, ASTNode> consumer) {

        if (!iterator.hasNext()) {
            return;
        }

        ASTNode current = iterator.next();

        while (iterator.hasNext()) {
            final ASTNode next = iterator.next();
            consumer.accept(current, next);
            current = next;
        }

        consumer.accept(current, null);
    }
}
