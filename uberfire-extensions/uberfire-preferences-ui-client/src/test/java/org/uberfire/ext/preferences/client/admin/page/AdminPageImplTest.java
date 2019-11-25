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

import com.google.common.collect.Sets;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.preferences.client.admin.AdminPagePerspective;
import org.uberfire.ext.preferences.client.central.PreferencesCentralPerspective;
import org.uberfire.ext.preferences.client.event.PreferencesCentralInitializationEvent;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.UsernameProvider;
import org.uberfire.preferences.shared.impl.DefaultPreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.impl.DefaultPreferenceScopeTypes;
import org.uberfire.preferences.shared.impl.PreferenceScopeFactoryImpl;
import org.uberfire.preferences.shared.impl.PreferenceScopeImpl;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

import static java.util.Collections.singleton;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdminPageImplTest {

    @Mock
    private PlaceManager placeManager;

    @Mock
    private Event<PreferencesCentralInitializationEvent> preferencesCentralInitializationEvent;

    @Mock
    private UberfireBreadcrumbs uberfireBreadcrumbs;

    @Mock
    private TranslationService translationService;

    private PreferenceScopeResolutionStrategy resolutionStrategy;

    private AdminPageImpl adminPage;

    @Before
    public void setup() {
        final UsernameProvider usernameProvider = mock(UsernameProvider.class);
        final DefaultPreferenceScopeTypes scopeTypes = new DefaultPreferenceScopeTypes(usernameProvider);
        final PreferenceScopeFactoryImpl scopeFactory = new PreferenceScopeFactoryImpl(scopeTypes);
        resolutionStrategy = new DefaultPreferenceScopeResolutionStrategy(scopeFactory,
                                                                          null);
        adminPage = new AdminPageImpl(placeManager,
                                      preferencesCentralInitializationEvent,
                                      resolutionStrategy,
                                      uberfireBreadcrumbs,
                                      translationService);
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
                          singleton("iconCss1"),
                          "category1",
                          () -> {
                          });
        adminPage.addTool("screen1",
                          "title2",
                          singleton("iconCss2"),
                          "category1",
                          () -> {
                          });
        adminPage.addTool("screen1",
                          "title3",
                          singleton("iconCss3"),
                          "category2",
                          () -> {
                          });
        adminPage.addTool("screen2",
                          "title4",
                          singleton("iconCss4"),
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
        assertEquals(1,
                     category1Tools.get(0).getIconCss().size());
        assertThat(category1Tools.get(0).getIconCss(),
                   hasItem("iconCss1"));
        assertEquals("title2",
                     category1Tools.get(1).getTitle());
        assertEquals(1,
                     category1Tools.get(1).getIconCss().size());
        assertThat(category1Tools.get(1).getIconCss(),
                   hasItem("iconCss2"));

        final List<AdminTool> category2Tools = toolsByCategory1.get("category2");
        assertEquals(1,
                     category2Tools.size());
        assertEquals("title3",
                     category2Tools.get(0).getTitle());
        assertThat(category2Tools.get(0).getIconCss(),
                   hasItem("iconCss3"));

        final Map<String, List<AdminTool>> toolsByCategory2 = adminPage.getToolsByCategory("screen2");

        assertNotNull(toolsByCategory2);
        assertEquals(1,
                     toolsByCategory2.size());

        final List<AdminTool> category3Tools = toolsByCategory2.get("category3");
        assertEquals(1,
                     category3Tools.size());
        assertEquals("title4",
                     category3Tools.get(0).getTitle());
        assertThat(category3Tools.get(0).getIconCss(),
                   hasItem("iconCss4"));
    }

    @Test(expected = RuntimeException.class)
    public void addToolWithNullScreenTest() {
        adminPage.addTool(null,
                          "title",
                          singleton("iconCss"),
                          null,
                          () -> {
                          });
    }

    @Test(expected = RuntimeException.class)
    public void addToolWithNullCategoryTest() {
        adminPage.addTool("screen",
                          "title",
                          singleton("iconCss"),
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
                                Sets.newHashSet("fa",
                                                "fa-map"),
                                "category1");

        final Map<String, List<AdminTool>> toolsByCategory1 = adminPage.getToolsByCategory("screen1");

        assertNotNull(toolsByCategory1);
        assertEquals(1,
                     toolsByCategory1.size());

        final List<AdminTool> category1Tools = toolsByCategory1.get("category1");
        assertEquals(1,
                     category1Tools.size());
        assertEquals("My Preference",
                     category1Tools.get(0).getTitle());
        assertEquals(2,
                     category1Tools.get(0).getIconCss().size());
        assertThat(category1Tools.get(0).getIconCss(),
                   hasItems("fa",
                            "fa-map"));

        category1Tools.get(0).getOnClickCommand().execute();

        verify(placeManager).goTo(eq(new DefaultPlaceRequest(PreferencesCentralPerspective.IDENTIFIER)));
        verify(preferencesCentralInitializationEvent).fire(eq(new PreferencesCentralInitializationEvent("MyPreference",
                                                                                                        null,
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
                                Sets.newHashSet("fa",
                                                "fa-map"),
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
        assertThat(category1Tools.get(0).getIconCss(),
                   hasItems("fa",
                            "fa-map"));

        category1Tools.get(0).getOnClickCommand().execute();

        verify(placeManager).goTo(eq(new DefaultPlaceRequest(PreferencesCentralPerspective.IDENTIFIER)));
        verify(preferencesCentralInitializationEvent).fire(eq(new PreferencesCentralInitializationEvent("MyPreference",
                                                                                                        scopeResolutionStrategyInfoSupplier.get(),
                                                                                                        null)));
    }

    @Test
    public void addPreferenceWithCustomScopeParameterTest() {
        final PreferenceScopeImpl preferenceScope = new PreferenceScopeImpl("all-users",
                                                                            "all-users",
                                                                            new PreferenceScopeImpl("entire-application",
                                                                                                    "entire-application",
                                                                                                    null));

        adminPage.addScreen("screen1",
                            "Screen 1");
        adminPage.addPreference("screen1",
                                "MyPreference",
                                "My Preference",
                                Sets.newHashSet("fa",
                                                "fa-map"),
                                "category1",
                                preferenceScope);

        final Map<String, List<AdminTool>> toolsByCategory1 = adminPage.getToolsByCategory("screen1");

        assertNotNull(toolsByCategory1);
        assertEquals(1,
                     toolsByCategory1.size());

        final List<AdminTool> category1Tools = toolsByCategory1.get("category1");
        assertEquals(1,
                     category1Tools.size());
        assertEquals("My Preference",
                     category1Tools.get(0).getTitle());
        assertThat(category1Tools.get(0).getIconCss(),
                   hasItems("fa",
                            "fa-map"));

        category1Tools.get(0).getOnClickCommand().execute();

        verify(placeManager).goTo(eq(new DefaultPlaceRequest(PreferencesCentralPerspective.IDENTIFIER)));
        verify(preferencesCentralInitializationEvent).fire(eq(new PreferencesCentralInitializationEvent("MyPreference",
                                                                                                        null,
                                                                                                        preferenceScope)));
    }

    @Test
    public void addPreferenceWithBreadcrumbsTest() {
        adminPage.addScreen("screen1",
                            "Screen 1");
        adminPage.addPreference("screen1",
                                "MyPreference",
                                "My Preference",
                                Sets.newHashSet("fa",
                                                "fa-map"),
                                "category1",
                                AdminPageOptions.WITH_BREADCRUMBS);

        final Map<String, List<AdminTool>> toolsByCategory1 = adminPage.getToolsByCategory("screen1");

        assertNotNull(toolsByCategory1);
        assertEquals(1,
                     toolsByCategory1.size());

        final List<AdminTool> category1Tools = toolsByCategory1.get("category1");
        assertEquals(1,
                     category1Tools.size());
        assertEquals("My Preference",
                     category1Tools.get(0).getTitle());
        assertThat(category1Tools.get(0).getIconCss(),
                   hasItems("fa",
                            "fa-map"));

        category1Tools.get(0).getOnClickCommand().execute();

        verify(placeManager).goTo(eq(new DefaultPlaceRequest(PreferencesCentralPerspective.IDENTIFIER)));
        verify(preferencesCentralInitializationEvent).fire(eq(new PreferencesCentralInitializationEvent("MyPreference",
                                                                                                        null,
                                                                                                        null)));
        verify(uberfireBreadcrumbs).clearBreadcrumbs(PreferencesCentralPerspective.IDENTIFIER);
        verify(uberfireBreadcrumbs).addBreadCrumb(eq(PreferencesCentralPerspective.IDENTIFIER),
                                                  anyString(),
                                                  eq(new DefaultPlaceRequest(AdminPagePerspective.IDENTIFIER)),
                                                  any(Command.class));
        verify(uberfireBreadcrumbs).addBreadCrumb(eq(PreferencesCentralPerspective.IDENTIFIER),
                                                  anyString(),
                                                  any(Command.class));
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
