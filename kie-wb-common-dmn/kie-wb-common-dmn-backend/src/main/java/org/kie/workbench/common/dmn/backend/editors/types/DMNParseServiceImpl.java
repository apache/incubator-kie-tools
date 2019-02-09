/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.editors.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.ListNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.kie.workbench.common.dmn.api.editors.types.DMNParseService;
import org.kie.workbench.common.dmn.api.editors.types.RangeValue;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class DMNParseServiceImpl implements DMNParseService {

    @Override
    public List<String> parseFEELList(final String source) {
        return parse(source)
            .map(this::getTextElements)
            .orElseGet(ArrayList::new);
    }

    @Override
    public RangeValue parseRangeValue(final String source) {

        final FEEL_1_1Parser parser = makeParser(source);
        final BaseNode baseNode = makeVisitor().visit(parser.expression());
        final RangeValue rangeValue = new RangeValue();

        if (baseNode instanceof RangeNode) {
            final RangeNode rangeNode = (RangeNode) baseNode;
            rangeValue.setStartValue(rangeNode.getStart().getText());
            rangeValue.setIncludeStartValue(rangeNode.getLowerBound() == RangeNode.IntervalBoundary.CLOSED);
            rangeValue.setEndValue(rangeNode.getEnd().getText());
            rangeValue.setIncludeEndValue(rangeNode.getUpperBound() == RangeNode.IntervalBoundary.CLOSED);
        }

        return rangeValue;
    }

    private List<String> getTextElements(final ListNode list) {
        return list
            .getElements()
            .stream()
            .map(BaseNode::getText)
            .collect(Collectors.toList());
    }

    private Optional<ListNode> parse(final String source) {

        final FEEL_1_1Parser parser = makeParser(asListSource(source));
        final BaseNode baseNode = makeVisitor().visit(parser.expression());

        return Optional.ofNullable((ListNode) baseNode);
    }

    private FEEL_1_1Parser makeParser(final String source) {
        return FEELParser.parse(null, source, emptyMap(), emptyMap(), emptyList(), emptyList());
    }

    private ASTBuilderVisitor makeVisitor() {
        return new ASTBuilderVisitor(emptyMap());
    }

    private String asListSource(final String source) {
        return "[" + source + "]";
    }
}
