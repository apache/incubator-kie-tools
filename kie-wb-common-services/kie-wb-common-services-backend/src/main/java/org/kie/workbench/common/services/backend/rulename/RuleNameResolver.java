/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.rulename;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleNameResolver {

    private HashSet<String> ruleNames = new HashSet<String>();

    private String drl;

    private boolean currentRuleNameHasDoubleQuotes = false;
    private boolean currentRuleNameHasSingleQuotes = false;
    private String packageName;

    private Pattern pattern = Pattern.compile("^.*(rule\\s).*", Pattern.MULTILINE);

    private Matcher matcher;

    public RuleNameResolver(String drl) {
        this.drl = drl;

        stripComments();

        matcher = pattern.matcher(this.drl);

        resolve();
    }

    private Set<String> resolve() {

        findPackage();

        while (matcher.find()) {
            String ruleLine = matcher.group();

            stripNameFromLine(ruleNames, ruleLine);

            drl = drl.substring(drl.indexOf(ruleLine) + ruleLine.length());
        }

        return ruleNames;
    }

    private void stripComments() {
        while (drl.contains("//")) {
            int commentStart = drl.indexOf("//");
            int endOfComment = drl.indexOf("\n", commentStart);
            if (endOfComment >= 0) {
                drl = drl.substring(0, commentStart) + drl.substring(endOfComment);
            } else {
                drl = drl.substring(0, commentStart);
            }
        }

        while (hasMultiLineComment()) {
            clearMultiLineComment();
        }
    }

    private void findPackage() {
        if (drl.contains("package ")) {
            String text = drl.substring(drl.indexOf("package") + "package".length()).trim();
            int endIndex = getEndOfPackageLine(text);

            packageName = text.substring(0, endIndex).trim();
        } else {
            packageName = "";
        }
    }

    private int getEndOfPackageLine(String text) {
        int endIndex = 0;
        int semiColonIndex = text.indexOf(";");
        int eofIndex = text.indexOf("\n");

        if (semiColonIndex == -1 && eofIndex == -1) {
            return text.length();
        } else if (semiColonIndex == -1 && eofIndex != -1) {
            endIndex = eofIndex;
        } else if (semiColonIndex != -1 && eofIndex == -1) {
            endIndex = semiColonIndex;
        } else if (semiColonIndex > eofIndex) {
            endIndex = eofIndex;
        } else if (semiColonIndex < eofIndex) {
            endIndex = semiColonIndex;
        }
        return endIndex;
    }

    private void clearMultiLineComment() {
        int startIndexOfTheComment = drl.indexOf("/*");

        drl = drl.substring(0, startIndexOfTheComment) + drl.substring(getEndOfComment());
    }

    private int getEndOfComment() {
        int endOfComment = drl.indexOf("*/") + "*/".length();

        while (endOfNextComment(endOfComment) >= 0) {
            int endOfNextComment = endOfNextComment(endOfComment) + "*/".length();

            String substring = drl.substring(endOfComment, endOfNextComment);
            if (endOfNextComment >= 0 && !hasMultiLineComment(substring)) {
                endOfComment = endOfNextComment;
            } else {
                break;
            }
        }

        return endOfComment;
    }

    private boolean hasMultiLineComment(String substring) {
        return substring.contains("*/")
                && substring.contains("/*")
                && substring.indexOf("/*") < substring.indexOf("*/");
    }

    private int endOfNextComment(int endOfComment) {
        return drl.indexOf("*/", endOfComment);
    }

    private boolean hasMultiLineComment() {
        return drl.indexOf("/*") >= 0;
    }

    private void checkQuotes(String ruleLine) {
        int doubleQuotes = ruleLine.indexOf("rule \"");
        int singleQuotes = ruleLine.indexOf("rule '");

        if (singleQuotes == -1 && doubleQuotes == -1) {
            currentRuleNameHasSingleQuotes = false;
            currentRuleNameHasDoubleQuotes = false;
            return;
        } else if (singleQuotes == -1 || (doubleQuotes <= singleQuotes && doubleQuotes >= 0)) {
            currentRuleNameHasSingleQuotes = false;
            currentRuleNameHasDoubleQuotes = true;
        } else {
            currentRuleNameHasDoubleQuotes = false;
            currentRuleNameHasSingleQuotes = true;
        }
    }

    private void stripNameFromLine(HashSet<String> ruleNames,
            String ruleLine) {
        checkQuotes(ruleLine);

        ruleLine = removeTheWorldRuleAndWhiteSpacesFromTheBeginning(ruleLine);

        int endIndex = getRuleNameEndIndex(ruleLine);
        if (endIndex >= 0) {
            ruleNames.add(ruleLine.substring(0, endIndex));
        } else {
            ruleNames.add(ruleLine);
        }
    }

    private String removeTheWorldRuleAndWhiteSpacesFromTheBeginning(String ruleLine) {
        return ruleLine.substring(getRuleFrontBitLength()).trim();
    }

    private int getRuleNameEndIndex(String ruleLine) {
        if (currentRuleNameHasDoubleQuotes) {
            return ruleLine.indexOf("\"");
        } else if (currentRuleNameHasSingleQuotes) {
            return ruleLine.indexOf("'");
        } else {
            return ruleLine.indexOf(" ");
        }
    }

    private int getRuleFrontBitLength() {
        if (currentRuleNameHasQuotes()) {
            return "rule \"".length();
        } else {
            return "rule".length();
        }
    }

    private boolean currentRuleNameHasQuotes() {
        return currentRuleNameHasDoubleQuotes || currentRuleNameHasSingleQuotes;
    }

    public Set<String> getRuleNames() {
        return new HashSet<String>(ruleNames);
    }

    public String getPackageName() {
        return packageName;
    }
}
