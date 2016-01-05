/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.screens.gadgets;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.JavaScriptObject;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;

/**
 * Created by Cristiano Nicolai.
 */
@ApplicationScoped
@WorkbenchScreen( identifier = "GitHubFrequencyStats" )
public class GitHubCodeFrequencyStatsScreen extends AbstractGitHubStatsScreen {

    @WorkbenchPartTitle
    public String getName() {
        return "Uberfire GitHub Code Frequency";
    }

    public native JavaScriptObject generateGraph( final String id )/*-{
        var that = this;
        return $wnd.c3.generate({
            bindto: '#' + id,
            data: {
                url: 'https://api.github.com/repos/uberfire/uberfire/stats/code_frequency',
                mimeType: 'json',
                keys: {
                    value: [1, 2]
                },
                names: {
                    1: 'Additions per Week',
                    2: 'Deletions per Week'
                }
            },
            padding: {
                top: 15
            },
            oninit: function () {
                that.@org.uberfire.client.screens.gadgets.GitHubCodeFrequencyStatsScreen::initGraph()();
            },
            color: {
                pattern: ['#0088ce', '#cc0000']
            },
            axis: {
                y: {
                    label: {
                        text: '# Lines Changed',
                        position: 'outer-top'
                    },
                    tick: {
                        format: $wnd.d3.format("s")
                    }
                },
                x: {
                    label: {
                        text: 'Week #',
                        position: 'outer-right'
                    }
                }
            }
        });
    }-*/;
}

