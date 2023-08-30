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

package org.kie.workbench.common.dmn.showcase.client.backward.compatibility;

import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.workbench.common.dmn.showcase.client.common.DMNDesignerBaseIT;

import static java.util.Arrays.asList;

public class DMNBackwardCompatibilityIT extends DMNDesignerBaseIT {

    @Test
    public void testDMN11Assets() throws Exception {
        runAssets("dmn11", dmn11Assets());
    }

    @Test
    public void testDMN12Assets() throws Exception {
        runAssets("dmn12", dmn12Assets());
    }

    @Test
    public void testDMN13Assets() throws Exception {
        runAssets("dmn13", dmn13Assets());
    }

    @Test
    @Ignore("KOGITO-3836")
    public void testDMN13FeelInDMN() throws Exception {
        executeDMNTestCase("dmn13", "0072-feel-in.dmn", "KOGITO-3836 reproducer");
    }

    private void runAssets(final String assertDirectory, final List<String> dmn13Assets) throws IOException {

        final int numberOfAssets = dmn13Assets.size();
        final int batchSize = 10;

        for (int i = 0; i < numberOfAssets; i++) {
            final String currentAsset = dmn13Assets.get(i);
            final int currentAssetNumber = i + 1;

            if (currentAssetNumber % batchSize == 0) {
                resetPage();
            }

            executeDMNTestCase(assertDirectory,
                               currentAsset,
                               getLogMessage(assertDirectory, currentAsset, currentAssetNumber, numberOfAssets));
        }
    }

    private String getLogMessage(final String prefix,
                                 final String currentAssetName,
                                 final int currentAssetNumber,
                                 final int numberOfAssets) {
        return String.format("[%s] (%d/%d) Running: %s",
                             prefix.toUpperCase(),
                             currentAssetNumber,
                             numberOfAssets,
                             currentAssetName);
    }

    private List<String> dmn11Assets() {
        return asList(
                "0001-filter.dmn",
                "0001-input-data-string.dmn",
                "0002-input-data-number.dmn",
                "0002-string-functions.dmn",
                "0003-input-data-string-allowed-values.dmn",
                "0003-iteration.dmn",
                "0004-lending.dmn",
                "0004-simpletable-U.dmn",
                "0005-literal-invocation.dmn",
                "0005-simpletable-A.dmn",
                "0006-join.dmn",
                "0006-simpletable-P1.dmn",
                "0007-date-time.dmn",
                "0007-simpletable-P2.dmn",
                "0008-listGen.dmn",
                "0008-LX-arithmetic.dmn",
                "0009-append-flatten.dmn",
                "0009-invocation-arithmetic.dmn",
                "0010-concatenate.dmn",
                "0010-multi-output-U.dmn",
                "0011-insert-remove.dmn",
                "0012-list-functions.dmn",
                "0013-sort.dmn",
                "0014-loan-comparison.dmn",
                "0015-all-any.dmn",
                "0016-some-every.dmn",
                "0017-tableTests.dmn",
                "0019-flight-rebooking.dmn"
        );
    }

    private List<String> dmn12Assets() {
        return asList(
                "0001-filter.dmn",
                "0001-input-data-string.dmn",
                "0002-input-data-number.dmn",
                "0002-string-functions.dmn",
                "0003-input-data-string-allowed-values.dmn",
                "0003-iteration.dmn",
                "0004-lending.dmn",
                "0004-simpletable-U.dmn",
                "0005-literal-invocation.dmn",
                "0005-simpletable-A.dmn",
                "0006-join.dmn",
                "0006-simpletable-P1.dmn",
                "0007-date-time.dmn",
                "0007-simpletable-P2.dmn",
                "0008-listGen.dmn",
                "0008-LX-arithmetic.dmn",
                "0009-append-flatten.dmn",
                "0009-invocation-arithmetic.dmn",
                "0010-concatenate.dmn",
                "0010-multi-output-U.dmn",
                "0011-insert-remove.dmn",
                "0012-list-functions.dmn",
                "0013-sort.dmn",
                "0014-loan-comparison.dmn",
                "0016-some-every.dmn",
                "0017-tableTests.dmn",
                "0020-vacation-days.dmn",
                "0021-singleton-list.dmn",
                "0030-user-defined-functions.dmn",
                "0031-user-defined-functions.dmn",
                "0032-conditionals.dmn",
                "0033-for-loops.dmn",
                "0034-drg-scopes.dmn",
                "0035-test-structure-output.dmn",
                "0036-dt-variable-input.dmn",
                "0037-dt-on-bkm-implicit-params.dmn",
                "0038-dt-on-bkm-explicit-params.dmn",
                "0039-dt-list-semantics.dmn",
                "0040-singlenestedcontext.dmn",
                "0041-multiple-nestedcontext.dmn",
                "0100-feel-constants.dmn",
                "0101-feel-constants.dmn",
                "0102-feel-constants.dmn",
                "0105-feel-math.dmn",
                "0106-feel-ternary-logic.dmn",
                "0107-feel-ternary-logic-not.dmn",
                "0108-first-hitpolicy.dmn",
                "0109-ruleOrder-hitpolicy.dmn",
                "0110-outputOrder-hitpolicy.dmn",
                "0111-first-hitpolicy-singleoutputcol.dmn",
                "0112-ruleOrder-hitpolicy-singleinoutcol.dmn",
                "0113-outputOrder-hitpolicy-singleinoutcol.dmn",
                "0114-min-collect-hitpolicy.dmn",
                "0115-sum-collect-hitpolicy.dmn",
                "0116-count-collect-hitpolicy.dmn",
                "0117-multi-any-hitpolicy.dmn",
                "0118-multi-priority-hitpolicy.dmn",
                "0119-multi-collect-hitpolicy.dmn",
                "1100-feel-decimal-function.dmn",
                "1101-feel-floor-function.dmn",
                "1102-feel-ceiling-function.dmn",
                "1103-feel-substring-function.dmn",
                "1104-feel-string-length-function.dmn",
                "1105-feel-upper-case-function.dmn",
                "1106-feel-lower-case-function.dmn",
                "1107-feel-substring-before-function.dmn",
                "1108-feel-substring-after-function.dmn",
                "1109-feel-replace-function.dmn",
                "1110-feel-contains-function.dmn",
                "1115-feel-date-function.dmn",
                "1116-feel-time-function.dmn",
                "1117-feel-date-and-time-function.dmn",
                "1120-feel-duration-function.dmn",
                "1121-feel-years-and-months-duration-function.dmn"
        );
    }

    private List<String> dmn13Assets() {
        return asList(
                "0001-filter.dmn",
                "0001-input-data-string.dmn",
                "0002-input-data-number.dmn",
                "0002-string-functions.dmn",
                "0003-input-data-string-allowed-values.dmn",
                "0003-iteration.dmn",
                "0004-lending.dmn",
                "0004-simpletable-U.dmn",
                "0005-literal-invocation.dmn",
                "0005-simpletable-A.dmn",
                "0006-join.dmn",
                "0006-simpletable-P1.dmn",
                "0007-date-time.dmn",
                "0007-simpletable-P2.dmn",
                "0008-listGen.dmn",
                "0008-LX-arithmetic.dmn",
                "0009-append-flatten.dmn",
                "0009-invocation-arithmetic.dmn",
                "0010-concatenate.dmn",
                "0010-multi-output-U.dmn",
                "0011-insert-remove.dmn",
                "0012-list-functions.dmn",
                "0013-sort.dmn",
                "0014-loan-comparison.dmn",
                "0016-some-every.dmn",
                "0017-tableTests.dmn",
                "0020-vacation-days.dmn",
                "0021-singleton-list.dmn",
                "0030-user-defined-functions.dmn",
                "0031-user-defined-functions.dmn",
                "0032-conditionals.dmn",
                "0033-for-loops.dmn",
                "0034-drg-scopes.dmn",
                "0035-test-structure-output.dmn",
                "0036-dt-variable-input.dmn",
                "0037-dt-on-bkm-implicit-params.dmn",
                "0038-dt-on-bkm-explicit-params.dmn",
                "0039-dt-list-semantics.dmn",
                "0040-singlenestedcontext.dmn",
                "0041-multiple-nestedcontext.dmn",
                "0050-feel-abs-function.dmn",
                "0051-feel-sqrt-function.dmn",
                "0052-feel-exp-function.dmn",
                "0053-feel-log-function.dmn",
                "0054-feel-even-function.dmn",
                "0055-feel-odd-function.dmn",
                "0056-feel-modulo-function.dmn",
                "0057-feel-context.dmn",
                "0058-feel-number-function.dmn",
                "0059-feel-all-function.dmn",
                "0060-feel-any-function.dmn",
                "0061-feel-median-function.dmn",
                "0062-feel-mode-function.dmn",
                "0063-feel-stddev-function.dmn",
                "0064-feel-conjunction.dmn",
                "0065-feel-disjunction.dmn",
                "0066-feel-negation.dmn",
                "0067-feel-split-function.dmn",
                "0068-feel-equality.dmn",
                "0069-feel-list.dmn",
                "0070-feel-instance-of.dmn",
                "0071-feel-between.dmn",
                "0073-feel-comments.dmn",
                "0074-feel-properties.dmn",
                "0075-feel-exponent.dmn",
                "0076-feel-external-java.dmn",
                "0077-feel-nan.dmn",
                "0078-feel-infinity.dmn",
                "0080-feel-getvalue-function.dmn",
                "0081-feel-getentries-function.dmn",
                "0082-feel-coercion.dmn",
                "0083-feel-unicode.dmn",
                "0084-feel-for-loops.dmn",
                "0085-decision-services.dmn",
                "0086-import.dmn",
                "0087-chapter-11-example.dmn",
                "0088-no-decision-logic.dmn",
                "0089-nested-inputdata-imports.dmn",
                "0090-feel-paths.dmn",
                "0100-feel-constants.dmn",
                "0101-feel-constants.dmn",
                "0102-feel-constants.dmn",
                "0105-feel-math.dmn",
                "0106-feel-ternary-logic.dmn",
                "0107-feel-ternary-logic-not.dmn",
                "0108-first-hitpolicy.dmn",
                "0109-ruleOrder-hitpolicy.dmn",
                "0110-outputOrder-hitpolicy.dmn",
                "0111-first-hitpolicy-singleoutputcol.dmn",
                "0112-ruleOrder-hitpolicy-singleinoutcol.dmn",
                "0113-outputOrder-hitpolicy-singleinoutcol.dmn",
                "0114-min-collect-hitpolicy.dmn",
                "0115-sum-collect-hitpolicy.dmn",
                "0116-count-collect-hitpolicy.dmn",
                "0117-multi-any-hitpolicy.dmn",
                "0118-multi-priority-hitpolicy.dmn",
                "0119-multi-collect-hitpolicy.dmn",
                "1100-feel-decimal-function.dmn",
                "1101-feel-floor-function.dmn",
                "1102-feel-ceiling-function.dmn",
                "1103-feel-substring-function.dmn",
                "1104-feel-string-length-function.dmn",
                "1105-feel-upper-case-function.dmn",
                "1106-feel-lower-case-function.dmn",
                "1107-feel-substring-before-function.dmn",
                "1108-feel-substring-after-function.dmn",
                "1109-feel-replace-function.dmn",
                "1110-feel-contains-function.dmn",
                "1115-feel-date-function.dmn",
                "1116-feel-time-function.dmn",
                "1117-feel-date-and-time-function.dmn",
                "1120-feel-duration-function.dmn",
                "1121-feel-years-and-months-duration-function.dmn",
                "Imported_Model.dmn",
                "Model_B.dmn",
                "Model_B2.dmn",
                "Say_hello_1ID1D.dmn"
        );
    }
}
