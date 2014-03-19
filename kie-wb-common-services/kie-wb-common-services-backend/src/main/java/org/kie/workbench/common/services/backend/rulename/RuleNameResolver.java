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

import java.util.ArrayList;
import java.util.List;

public class RuleNameResolver {

    private ArrayList<String> ruleNames = new ArrayList<String>();

    private String drl;

    private boolean currentRuleNameHasQuotes = false;
    private String packageName;

    public RuleNameResolver(String drl) {
        this.drl = drl;

        resolve();
    }

    private List<String> resolve() {

        // Strip comments
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

        findPackage();

        while (hasRule()) {
            String ruleLine = getNextRuleHeaderLine();

            stripNameFromLine(ruleNames, ruleLine);

            drl = drl.substring(drl.indexOf(ruleLine) + ruleLine.length());
        }

        return ruleNames;
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
        int colonIndex = text.indexOf(";");
        int eofIndex = text.indexOf("\n");

        if (colonIndex == -1) {
            endIndex = eofIndex;
        } else if (colonIndex > eofIndex) {
            endIndex = eofIndex;
        } else if (colonIndex < eofIndex) {
            endIndex = colonIndex;
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

    private String getNextRuleHeaderLine() {
        int beginIndex = getRuleIndex();
        return drl.substring(beginIndex, drl.indexOf("\n", beginIndex));
    }

    private int getRuleIndex() {
        int ruleIndex = drl.indexOf("rule");

        int beginIndexWithQuotes = drl.indexOf("rule \"");

        if (ruleIndex >= beginIndexWithQuotes && beginIndexWithQuotes >= 0) {
            currentRuleNameHasQuotes = true;
            return beginIndexWithQuotes;
        } else {
            currentRuleNameHasQuotes = false;
            return ruleIndex;
        }
    }

    private boolean hasRule() {
        return getRuleIndex() >= 0;
    }

    private void stripNameFromLine(ArrayList<String> ruleNames, String ruleLine) {
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
        if (currentRuleNameHasQuotes) {
            return ruleLine.indexOf("\"");
        } else {
            return ruleLine.indexOf(" ");
        }
    }

    private int getRuleFrontBitLength() {
        if (currentRuleNameHasQuotes) {
            return "rule \"".length();
        } else {
            return "rule".length();
        }
    }

    public ArrayList<String> getRuleNames() {
        return new ArrayList<String>(ruleNames);
    }

    public String getPackageName() {
        return packageName;
    }
}
