/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.drl.parser.antlr4;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

/**
 * Collection of static helper methods for DRLParser
 */
public class DRLParserHelper {

    private DRLParserHelper() {
    }

    public static DRL10Parser createDrlParser(String drl) {
        CharStream charStream = CharStreams.fromString(drl);
        return createDrlParser(charStream);
    }

    public static DRL10Parser createDrlParser(InputStream is) {
        try {
            CharStream charStream = CharStreams.fromStream(is);
            return createDrlParser(charStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static DRL10Parser createDrlParser(CharStream charStream) {
        DRL10Lexer drlLexer = new DRL10Lexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(drlLexer);
        return new DRL10Parser(commonTokenStream);
    }

    /**
     * Given a row and column of a caret in the input DRL, return the index of the matched token
     */
    public static Integer computeTokenIndex(DRL10Parser parser, int row, int col) {
        for (int i = 0; i < parser.getInputStream().size(); i++) {
            Token token = parser.getInputStream().get(i);
            int start = token.getCharPositionInLine();
            int stop = token.getCharPositionInLine() + token.getText().length();
            if (token.getLine() > row) {
                // if token is on the next line, we can stop searching. Take the previous token
                return token.getTokenIndex() - 1;
            } else if (token.getLine() == row && start >= col) {
                // if token is on the same line and positioned to the right of the caret, we can stop searching. Take the previous token
                return token.getTokenIndex() == 0 ? 0 : token.getTokenIndex() - 1;
            } else if (token.getLine() == row && start < col && stop >= col) {
                // if token is on the same line and the caret is inside the token or right after the token, we can stop searching. Take the current token
                return token.getTokenIndex();
            }
        }
        return null;
    }

    public static String getFirstRuleName(String drl) {
        DRL10Parser parser = createDrlParser(drl);
        DRL10Parser.CompilationUnitContext compilationUnitContext = parser.compilationUnit();
        return compilationUnitContext.drlStatementdef()
                .stream()
                .filter(def -> def.ruledef() != null)
                .map(def -> def.ruledef().name.getText())
                .findFirst()
                .orElse("");
    }
}
