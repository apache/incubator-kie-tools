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
package org.uberfire.client.workbench;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EnabledByProperty;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.identity.User;
import org.slf4j.Logger;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.client.resources.i18n.WorkbenchConstants;
import org.uberfire.client.util.UserAgent;
import org.uberfire.client.workbench.events.ApplicationReadyEvent;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.rpc.impl.SessionInfoImpl;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.PermissionManager;

/**
 * Responsible for bootstrapping the client-side Workbench user interface by coordinating calls to the PanelManager and
 * PlaceManager. Normally this happens automatically with no need for assistance or interference from the application.
 * Thus, applications don't usually need to do anything with the Workbench class directly.
 * <p>
 * <h2>Delaying Workbench Startup</h2>
 * <p>
 * In special cases, applications may wish to delay the startup of the workbench. For example, an application that
 * relies on global variables (also known as singletons or Application Scoped beans) that are initialized based on
 * response data from the server doesn't want UberFire to start initializing its widgets until that server response has
 * come in.
 * <p>
 * To delay startup, add a <i>Startup Blocker</i> before Errai starts calling {@link AfterInitialization} methods. The
 * best place to do this is in the {@link PostConstruct} method of an {@link EntryPoint} bean. You would then remove the
 * startup blocker from within the callback from the server:
 * <p>
 * <pre>
 *   {@code @EntryPoint}
 *   public class MyMutableGlobal() {
 *     {@code @Inject private Workbench workbench;}
 *     {@code @Inject private Caller<MyRemoteService> remoteService;}
 *
 *     // set up by a server call. don't start the app until it's populated!
 *     {@code private MyParams params;}
 *
 *     {@code @PostConstruct}
 *     private void earlyInit() {
 *       workbench.addStartupBlocker(MyMutableGlobal.class);
 *     }
 *
 *     {@code @AfterInitialization}
 *     private void lateInit() {
 *       remoteService.call(new {@code RemoteCallback<MyParams>}{
 *         public void callback(MyParams params) {
 *           MyMutableGlobal.this.params = params;
 *           workbench.removeStartupBlocker(MyMutableGlobal.class);
 *         }
 *       }).fetchParameters();
 *     }
 *   }
 * </pre>
 */
@EntryPoint
@EnabledByProperty(value = "uberfire.plugin.mode.active", negated = true)
public class Workbench {

    /**
     * List of classes who want to do stuff (often server communication) before the workbench shows up.
     */
    private final Set<Class<?>> startupBlockers = new HashSet<>();
    private final Set<String> headersToKeep = new HashSet<>();
    /**
     * This indirection exists so we can ignore spurious WindowCloseEvents in IE10.
     * In all other cases, the {@link WorkbenchCloseHandler} simply executes whatever command we pass it.
     */
    private final WorkbenchCloseHandler workbenchCloseHandler = GWT.create(WorkbenchCloseHandler.class);
    @Inject
    LayoutSelection layoutSelection;
    /**
     * Fired when all startup blockers have cleared and just before the workbench starts to build its components.
     */
    @Inject
    private Event<ApplicationReadyEvent> appReady;
    private boolean isStandaloneMode = false;
    @Inject
    private ActivityBeansCache activityBeansCache;
    @Inject
    private SyncBeanManager iocManager;
    @Inject
    private PlaceManager placeManager;
    @Inject
    private PermissionManager permissionManager;
    @Inject
    private AuthorizationManager authorizationManager;
    @Inject
    private VFSServiceProxy vfsService;
    private WorkbenchLayout layout;
    @Inject
    private User identity;
    @Inject
    private ClientMessageBus bus;
    @Inject
    private Logger logger;
    private SessionInfo sessionInfo = null;
    @Inject
    private ManagedInstance<WorkbenchCustomStandalonePerspectiveDefinition> workbenchCustomStandalonePerspectiveDefinition;

    final ParameterizedCommand<Window.ClosingEvent> workbenchClosingCommand = (Window.ClosingEvent event) -> {
        if (!placeManager.canCloseAllPlaces()) {
            // Setting a non-null message will make the browser to present a confirmation dialog that asks the user
            // whether or not they wish to navigate away from the page. The message in the dialog, however,
            // is not customizable anymore mainly due to scammers using such a message to trick people.
            // Thus, each browser shows its own message. If the user has an outdated browser, then our custom
            // message might be shown.
            event.setMessage(WorkbenchConstants.INSTANCE.closingWindowMessage());
        }
    };

    final Command workbenchCloseCommand = () -> placeManager.closeAllPlaces(); // would be preferable to close current perspective, which should be recursive

    /**
     * Requests that the workbench does not attempt to create any UI parts until the given responsible party has
     * been removed as a startup blocker. Blockers are tracked as a set, so adding the same class more than once has no
     * effect.
     * @param responsibleParty any Class object; typically it will be the class making the call to this method.
     * Must not be null.
     */
    public void addStartupBlocker(Class<?> responsibleParty) {
        startupBlockers.add(responsibleParty);
        logger.info(responsibleParty.getName() + " is blocking workbench startup.");
    }

    /**
     * Causes the given responsible party to no longer block workbench initialization.
     * If the given responsible party was not already in the blocking set (either because
     * it was never added, or it has already been removed) then the method call has no effect.
     * <p>
     * After removing the blocker, if there are no more blockers left in the blocking set, the workbench UI is
     * bootstrapped immediately. If there are still one or more blockers left in the blocking set, the workbench UI
     * remains uninitialized.
     * @param responsibleParty any Class object that was previously passed to {@link #addStartupBlocker(Class)}.
     * Must not be null.
     */
    public void removeStartupBlocker(Class<?> responsibleParty) {
        if (startupBlockers.remove(responsibleParty)) {
            logger.info(responsibleParty.getName() + " is no longer blocking startup.");
        } else {
            logger.info(responsibleParty.getName() + " tried to unblock startup, but it wasn't blocking to begin with!");
        }
        startIfNotBlocked();
    }

    // package-private so tests can call in
    void startIfNotBlocked() {
        logger.info(startupBlockers.size() + " workbench startup blockers remain.");
        if (startupBlockers.isEmpty()) {
            bootstrap();
        }
    }

    @AfterInitialization
    private void afterInit() {
        removeStartupBlocker(Workbench.class);
    }

    @PostConstruct
    private void earlyInit() {
        layout = layoutSelection.get();
        WorkbenchResources.INSTANCE.CSS().ensureInjected();

        Map<String, List<String>> windowParamMap = Window.Location.getParameterMap();
        isStandaloneMode = windowParamMap.containsKey("standalone");
        List<String> headers = windowParamMap.getOrDefault("header", Collections.emptyList());
        headersToKeep.addAll(headers);
        addStartupBlocker(Workbench.class);
    }

    private void bootstrap() {
        logger.info("Starting workbench...");
        ((SessionInfoImpl) currentSession()).setId(bus.getSessionId());

        //Lookup PerspectiveProviders and if present launch it to set-up the Workbench
        if (!isStandaloneMode) {
            final PerspectiveActivity homePerspective = getHomePerspectiveActivity();
            appReady.fire(new ApplicationReadyEvent());
            if (homePerspective != null) {
                layout.setMarginWidgets(isStandaloneMode, headersToKeep);
                layout.onBootstrap();
                addLayoutToRootPanel(layout);
                placeManager.goTo(new DefaultPlaceRequest(homePerspective.getIdentifier()));
            } else {
                activityBeansCache.noOp();
                logger.warn("No home perspective available!");
            }
        } else {
            layout.setMarginWidgets(isStandaloneMode,
                                    headersToKeep);
            layout.onBootstrap();

            addLayoutToRootPanel(layout);
            handleStandaloneMode(Window.Location.getParameterMap());
        }

        addCloseHandler();

        // Resizing the Window should resize everything
        Window.addResizeHandler(event -> layout.resizeTo(event.getWidth(),
                                                         event.getHeight()));

        // Defer the initial resize call until widgets are rendered and sizes are available
        Scheduler.get().scheduleDeferred(() -> layout.onResize());

        notifyJSReady();
    }

    private void addCloseHandler() {
        if (UserAgent.isChrome()) {
            setupMessageForUnsavedChanges(); // only works on chrome
            Window.addCloseHandler(event -> workbenchCloseHandler.onWindowClose(workbenchCloseCommand));
        } else {
            // The Window.addCloseHandler does not work as expected for other browsers, thus we need to register the
            // workbenchCloseCommand in the Window.addWindowClosingHandler.
            Window.addWindowClosingHandler(event -> workbenchCloseHandler.onWindowClose(workbenchCloseCommand));
        }
    }

    private void setupMessageForUnsavedChanges() {
        Window.addWindowClosingHandler(event -> workbenchCloseHandler.onWindowClosing(workbenchClosingCommand, event));
    }

    private native void notifyJSReady() /*-{
        if ($wnd.appFormerGwtFinishedLoading) {
            $wnd.appFormerGwtFinishedLoading();
        }
    }-*/;

    // TODO add tests for standalone startup vs. full startup
    void handleStandaloneMode(final Map<String, List<String>> parameters) {
        if (parameters.containsKey("perspective") && !parameters.get("perspective").isEmpty()) {
            placeManager.goTo(new DefaultPlaceRequest(parameters.get("perspective").get(0)));
        } else if (parameters.containsKey("path") && !parameters.get("path").isEmpty()) {
            openStandaloneEditor(parameters);
        }
    }

    private void openStandaloneEditor(final Map<String, List<String>> parameters) {
        String standalonePerspective = "StandaloneEditorPerspective";
        boolean openEditor = true;

        if (!workbenchCustomStandalonePerspectiveDefinition.isUnsatisfied()) {
            final WorkbenchCustomStandalonePerspectiveDefinition workbenchCustomStandalonePerspectiveDefinition = this.workbenchCustomStandalonePerspectiveDefinition.get();
            standalonePerspective = workbenchCustomStandalonePerspectiveDefinition.getStandalonePerspectiveIdentifier();
            openEditor = workbenchCustomStandalonePerspectiveDefinition.openPathAutomatically();
        }

        placeManager.goTo(new DefaultPlaceRequest(standalonePerspective));
        if (openEditor) {
            vfsService.get(parameters.get("path").get(0),
                           path -> {
                               if (parameters.containsKey("editor") && !parameters.get("editor").isEmpty()) {
                                   openEditor(path, parameters.get("editor").get(0));
                               } else {
                                   openEditor(path);
                               }
                           });
        }
    }

    void openEditor(final Path path) {
        placeManager.goTo(new PathPlaceRequest(path));
    }

    void openEditor(final Path path,
                    final String editor) {
        placeManager.goTo(new PathPlaceRequest(path, editor));
    }

    /**
     * Get the home perspective defined at the workbench authorization policy.
     * <p>
     * <p>If no home is defined then the perspective marked as "{@code isDefault=true}" is taken.</p>
     * <p>
     * <p>Notice that access permission over the selected perspective is always required.</p>
     * @return A perspective instance or null if no perspective is found or access to it has been denied.
     */
    public PerspectiveActivity getHomePerspectiveActivity() {

        // Get the user's home perspective
        PerspectiveActivity homePerspective = null;
        AuthorizationPolicy authPolicy = permissionManager.getAuthorizationPolicy();
        String homePerspectiveId = authPolicy.getHomePerspective(identity);

        // Get the workbench's default perspective
        PerspectiveActivity defaultPerspective = null;
        final Collection<SyncBeanDef<PerspectiveActivity>> perspectives = iocManager.lookupBeans(PerspectiveActivity.class);

        for (final SyncBeanDef<PerspectiveActivity> perspective : perspectives) {
            final PerspectiveActivity instance = perspective.getInstance();

            if (homePerspectiveId != null && homePerspectiveId.equals(instance.getIdentifier())) {
                homePerspective = instance;
                if (defaultPerspective != null) {
                    iocManager.destroyBean(defaultPerspective);
                }
            } else if (instance.isDefault()) {
                defaultPerspective = instance;
            } else {
                iocManager.destroyBean(instance);
            }
        }
        // The home perspective has always priority over the default
        return homePerspective != null ? homePerspective : defaultPerspective;
    }

    @Produces
    @ApplicationScoped
    private SessionInfo currentSession() {
        if (sessionInfo == null) {
            sessionInfo = new SessionInfoImpl(identity);
        }
        return sessionInfo;
    }

    void addLayoutToRootPanel(final WorkbenchLayout layout) {
        RootLayoutPanel.get().add(layout.getRoot());
    }
}
