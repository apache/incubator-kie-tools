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

package org.uberfire.ext.widgets.common.client.diff2html;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Composite;

public class Diff2Html extends Composite {

    private String containerId;

    private DiffOutputFormat outputFormat;

    private String diffText;

    private JavaScriptObject viewer;

    public Diff2Html(final String containerId,
                     final DiffOutputFormat outputFormat,
                     final String diffText) {
        this.diffText = diffText;
        this.outputFormat = outputFormat;
        this.setupContainerId(containerId);
        this.initialize();
    }

    private void setupContainerId(String containerId) {
        this.containerId = "#" + containerId;
    }

    private native void initialize()/*-{
        var diffText = this.@org.uberfire.ext.widgets.common.client.diff2html.Diff2Html::diffText;

        this.@org.uberfire.ext.widgets.common.client.diff2html.Diff2Html::viewer = new $wnd.Diff2HtmlUI({diff: diffText});
    }-*/;

    public native void draw() /*-{
        // the format of the input data: 'diff' or 'json', default is 'diff'
        var inputFormat = "diff";
        // 'lines' for matching lines, 'words' for matching lines and words or 'none', default is none (avoids OOM)
        var matching = "none";
        // show a file list before the diff: true or false, default is false
        var showFiles = false;
        // the id of the div in which the diff will be drawn
        var containerId = this.@org.uberfire.ext.widgets.common.client.diff2html.Diff2Html::containerId;
        // the format of the output data: 'line-by-line' or 'side-by-side', default is 'line-by-line'
        var outputFormat = this.@org.uberfire.ext.widgets.common.client.diff2html.Diff2Html::outputFormat.toString();
        // similarity threshold for word matching, default is 0.25
        var matchWordsThreshold = 0.25;
        // perform at most this much comparisons for line matching a block of changes, default is 2500
        var matchingMaxComparisons = 2500;
        // maximum number os characters of the bigger line in a block to apply comparison, default is 200
        var maxLineSizeInBlockForComparison = 200;
        // only perform diff changes highlight if lines are smaller than this, default is 10000
        var maxLineLengthHighlight = 10000;
        // render nothing if the diff shows no change in its comparison: true or false, default is false
        var renderNothingWhenEmpty = false;
        // scroll both panes in side-by-side mode: true or false, default is false
        var synchronisedScroll = true;

        var viewer = this.@org.uberfire.ext.widgets.common.client.diff2html.Diff2Html::viewer;

        viewer.draw(containerId, {
            inputFormat: inputFormat,
            showFiles: showFiles,
            matching: matching,
            outputFormat: outputFormat,
            matchWordsThreshold: matchWordsThreshold,
            matchingMaxComparisons: matchingMaxComparisons,
            maxLineSizeInBlockForComparison: maxLineSizeInBlockForComparison,
            maxLineLengthHighlight: maxLineLengthHighlight,
            renderNothingWhenEmpty: renderNothingWhenEmpty,
            synchronisedScroll: synchronisedScroll
        });
    }-*/;

    public native void highlightCode() /*-{
        var viewer = this.@org.uberfire.ext.widgets.common.client.diff2html.Diff2Html::viewer;
        var containerId = this.@org.uberfire.ext.widgets.common.client.diff2html.Diff2Html::containerId;
        viewer.highlightCode(containerId);
    }-*/;

    public native void configContainerHeight(final int maxHeight) /*-{
        var containerId = this.@org.uberfire.ext.widgets.common.client.diff2html.Diff2Html::containerId;
        var outputFormat = this.@org.uberfire.ext.widgets.common.client.diff2html.Diff2Html::outputFormat.toString();

        var rowHeight = 18;
        var tableRowsCount;

        if (outputFormat === "line-by-line") {
            tableRowsCount = $wnd.jQuery(containerId + " .d2h-file-diff .d2h-diff-table >tbody >tr").length;
        } else {
            tableRowsCount = $wnd.jQuery(containerId + " .d2h-file-side-diff .d2h-diff-table >tbody >tr").length / 2;
        }

        if (maxHeight <= tableRowsCount * rowHeight) {
            $wnd.jQuery(containerId).find('.d2h-wrapper').css({"height": maxHeight + "px"});
        }
    }-*/;
}
