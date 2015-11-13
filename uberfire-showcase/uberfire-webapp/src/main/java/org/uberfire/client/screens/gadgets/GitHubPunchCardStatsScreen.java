/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
@WorkbenchScreen( identifier = "GitHubCommitDaysStats" )
public class GitHubPunchCardStatsScreen extends AbstractGitHubStatsScreen {

    @WorkbenchPartTitle
    public String getName() {
        return "Uberfire GitHub Commits per Day";
    }

    public void setGraph( final JavaScriptObject graph ) {
        this.graph = graph;
        onResize();
    }

    public native JavaScriptObject generateGraph( final String id )/*-{
        var that = this;

        $wnd.d3.json("https://api.github.com/repos/uberfire/uberfire/stats/punch_card", function (error, json_data) {
            var data = $wnd.d3.nest()
                .key(function (d) {
                    return d[0];
                })
                .rollup(function (leaves) {
                    return $wnd.d3.sum(leaves, function (d) {
                        return d[2];
                    });
                })
                .entries(json_data)
                .map(function (d) {
                    return [d.key, d.values];
                });

            var graph = $wnd.c3.generate({
                bindto: '#' + id,
                data: {
                    columns: data,
                    names: {
                        0: 'Sunday',
                        1: 'Monday',
                        2: 'Tuesday',
                        3: 'Wednesday',
                        4: 'Thursday',
                        5: 'Friday',
                        6: 'Saturday'
                    },
                    type: 'pie'
                },
                padding: {
                    top: 15
                },
                oninit: function () {
                    that.@org.uberfire.client.screens.gadgets.GitHubPunchCardStatsScreen::initGraph()();
                },
                color: {
                    pattern: ['#0088ce', '#00659c', '#3f9c35', '#ec7a08', '#cc0000']
                }
            });

            that.@org.uberfire.client.screens.gadgets.GitHubPunchCardStatsScreen::setGraph(Lcom/google/gwt/core/client/JavaScriptObject;)(graph);
        });

        return null;
    }-*/;
}

