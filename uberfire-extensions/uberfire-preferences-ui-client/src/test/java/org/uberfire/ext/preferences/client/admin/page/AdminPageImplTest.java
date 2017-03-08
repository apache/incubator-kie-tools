/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.preferences.client.admin.page;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.preferences.client.central.PreferencesCentralPerspective;
import org.uberfire.ext.preferences.client.event.PreferencesCentralInitializationEvent;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdminPageImplTest {

    @Mock
    private PlaceManager placeManager;

    @Mock
    private Event<PreferencesCentralInitializationEvent> preferencesCentralInitializationEvent;

    private AdminPageImpl adminPage;

    @Before
    public void setup() {
        adminPage = new AdminPageImpl(placeManager,
                                      preferencesCentralInitializationEvent);
    }

    @Test
    public void addValidScreen() {
        adminPage.addScreen("screen",
                            "title");
        assertEquals("title",
                     adminPage.getScreenTitle("screen"));
    }

    @Test(expected = RuntimeException.class)
    public void addScreenWithNullIdentifierTest() {
        adminPage.addScreen(null,
                            "title");
    }

    @Test
    public void addToolTest() {
        adminPage.addScreen("screen1",
                            "Screen 1");
        adminPage.addScreen("screen2",
                            "Screen 2");

        adminPage.addTool("screen1",
                          "title1",
                          "iconCss1",
                          "category1",
                          () -> {
                          });
        adminPage.addTool("screen1",
                          "title2",
                          "iconCss2",
                          "category1",
                          () -> {
                          });
        adminPage.addTool("screen1",
                          "title3",
                          "iconCss3",
                          "category2",
                          () -> {
                          });
        adminPage.addTool("screen2",
                          "title4",
                          "iconCss4",
                          "category3",
                          () -> {
                          });

        final Map<String, List<AdminTool>> toolsByCategory1 = adminPage.getToolsByCategory("screen1");

        assertNotNull(toolsByCategory1);
        assertEquals(2,
                     toolsByCategory1.size());

        final List<AdminTool> category1Tools = toolsByCategory1.get("category1");
        assertEquals(2,
                     category1Tools.size());
        assertEquals("title1",
                     category1Tools.get(0).getTitle());
        assertEquals("iconCss1",
                     category1Tools.get(0).getIconCss());
        assertEquals("title2",
                     category1Tools.get(1).getTitle());
        assertEquals("iconCss2",
                     category1Tools.get(1).getIconCss());

        final List<AdminTool> category2Tools = toolsByCategory1.get("category2");
        assertEquals(1,
                     category2Tools.size());
        assertEquals("title3",
                     category2Tools.get(0).getTitle());
        assertEquals("iconCss3",
                     category2Tools.get(0).getIconCss());

        final Map<String, List<AdminTool>> toolsByCategory2 = adminPage.getToolsByCategory("screen2");

        assertNotNull(toolsByCategory2);
        assertEquals(1,
                     toolsByCategory2.size());

        final List<AdminTool> category3Tools = toolsByCategory2.get("category3");
        assertEquals(1,
                     category3Tools.size());
        assertEquals("title4",
                     category3Tools.get(0).getTitle());
        assertEquals("iconCss4",
                     category3Tools.get(0).getIconCss());
    }

    @Test(expected = RuntimeException.class)
    public void addToolWithNullScreenTest() {
        adminPage.addTool(null,
                          "title",
                          "iconCss",
                          null,
                          () -> {
                          });
    }

    @Test(expected = RuntimeException.class)
    public void addToolWithNullCategoryTest() {
        adminPage.addTool("screen",
                          "title",
                          "iconCss",
                          null,
                          () -> {
                          });
    }

    @Test
    public void addPreferenceTest() {
        adminPage.addScreen("screen1",
                            "Screen 1");
        adminPage.addPreference("screen1",
                                "MyPreference",
                                "My Preference",
                                "fa-map",
                                "category1",
                                null);

        final Map<String, List<AdminTool>> toolsByCategory1 = adminPage.getToolsByCategory("screen1");

        assertNotNull(toolsByCategory1);
        assertEquals(1,
                     toolsByCategory1.size());

        final List<AdminTool> category1Tools = toolsByCategory1.get("category1");
        assertEquals(1,
                     category1Tools.size());
        assertEquals("My Preference",
                     category1Tools.get(0).getTitle());
        assertEquals("fa-map",
                     category1Tools.get(0).getIconCss());

        category1Tools.get(0).getOnClickCommand().execute();

        verify(placeManager).goTo(eq(new DefaultPlaceRequest(PreferencesCentralPerspective.IDENTIFIER)));
        verify(preferencesCentralInitializationEvent).fire(eq(new PreferencesCentralInitializationEvent("MyPreference",
                                                                                                        null)));
    }

    @Test
    public void addPreferenceWithCustomScopeResolutionStrategyParameterTest() {
        Supplier<PreferenceScopeResolutionStrategyInfo> scopeResolutionStrategyInfoSupplier = new Supplier<PreferenceScopeResolutionStrategyInfo>() {
            PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo = null;

            @Override
            public PreferenceScopeResolutionStrategyInfo get() {
                if (scopeResolutionStrategyInfo == null) {
                    scopeResolutionStrategyInfo = mock(PreferenceScopeResolutionStrategyInfo.class);
                }

                return scopeResolutionStrategyInfo;
            }
        };

        adminPage.addScreen("screen1",
                            "Screen 1");
        adminPage.addPreference("screen1",
                                "MyPreference",
                                "My Preference",
                                "fa-map",
                                "category1",
                                scopeResolutionStrategyInfoSupplier);

        final Map<String, List<AdminTool>> toolsByCategory1 = adminPage.getToolsByCategory("screen1");

        assertNotNull(toolsByCategory1);
        assertEquals(1,
                     toolsByCategory1.size());

        final List<AdminTool> category1Tools = toolsByCategory1.get("category1");
        assertEquals(1,
                     category1Tools.size());
        assertEquals("My Preference",
                     category1Tools.get(0).getTitle());
        assertEquals("fa-map",
                     category1Tools.get(0).getIconCss());

        category1Tools.get(0).getOnClickCommand().execute();

        verify(placeManager).goTo(eq(new DefaultPlaceRequest(PreferencesCentralPerspective.IDENTIFIER)));
        verify(preferencesCentralInitializationEvent).fire(eq(new PreferencesCentralInitializationEvent("MyPreference",
                                                                                                        scopeResolutionStrategyInfoSupplier.get())));
    }

    @Test
    public void setDefaultScreen() {
        adminPage.setDefaultScreen("screen1");
        assertEquals("screen1",
                     adminPage.getDefaultScreen());

        adminPage.setDefaultScreen("screen2");
        assertEquals("screen2",
                     adminPage.getDefaultScreen());
    }
}
