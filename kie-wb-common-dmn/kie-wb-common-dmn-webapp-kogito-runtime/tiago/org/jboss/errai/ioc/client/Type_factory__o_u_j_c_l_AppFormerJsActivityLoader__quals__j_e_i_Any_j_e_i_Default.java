package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.ActivityManagerImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.mvp.PlaceManagerImpl.AppFormerActivityLoader;
import org.uberfire.client.promise.Promises;
import org.uberfire.jsbridge.client.editor.JsWorkbenchEditorActivity;
import org.uberfire.jsbridge.client.loading.ActivityLazyLoaded;
import org.uberfire.jsbridge.client.loading.AppFormerComponentsRegistry;
import org.uberfire.jsbridge.client.loading.AppFormerJsActivityLoader;
import org.uberfire.jsbridge.client.loading.AppFormerJsActivityLoader.Shadowed;
import org.uberfire.jsbridge.client.loading.LazyLoadingScreen;

public class Type_factory__o_u_j_c_l_AppFormerJsActivityLoader__quals__j_e_i_Any_j_e_i_Default extends Factory<AppFormerJsActivityLoader> { public Type_factory__o_u_j_c_l_AppFormerJsActivityLoader__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AppFormerJsActivityLoader.class, "Type_factory__o_u_j_c_l_AppFormerJsActivityLoader__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { AppFormerJsActivityLoader.class, Object.class, AppFormerActivityLoader.class });
  }

  public AppFormerJsActivityLoader createInstance(final ContextManager contextManager) {
    final Instance<JsWorkbenchEditorActivity> _jsWorkbenchEditorActivityInstance_6 = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { JsWorkbenchEditorActivity.class }, new Annotation[] { new Shadowed() {
        public Class annotationType() {
          return Shadowed.class;
        }
        public String toString() {
          return "@org.uberfire.jsbridge.client.loading.AppFormerJsActivityLoader.Shadowed()";
        }
    } });
    final ActivityManager _activityManager_1 = (ActivityManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_ActivityManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final Event<ActivityLazyLoaded> _activityLazyLoadedEvent_5 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ActivityLazyLoaded.class }, new Annotation[] { });
    final Promises _promises_0 = (Promises) contextManager.getInstance("Type_factory__o_u_c_p_Promises__quals__j_e_i_Any_j_e_i_Default");
    final LazyLoadingScreen _lazyLoadingScreen_4 = (LazyLoadingScreen) contextManager.getInstance("Type_factory__o_u_j_c_l_LazyLoadingScreen__quals__j_e_i_Any_j_e_i_Default");
    final PlaceManager _placeManager_3 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ActivityBeansCache _activityBeansCache_2 = (ActivityBeansCache) contextManager.getInstance("Type_factory__o_u_c_m_ActivityBeansCache__quals__j_e_i_Any_j_e_i_Default");
    final AppFormerComponentsRegistry _appFormerComponentsRegistry_7 = (AppFormerComponentsRegistry) contextManager.getInstance("Type_factory__o_u_j_c_l_AppFormerComponentsRegistry__quals__j_e_i_Any_j_e_i_Default");
    final AppFormerJsActivityLoader instance = new AppFormerJsActivityLoader(_promises_0, _activityManager_1, _activityBeansCache_2, _placeManager_3, _lazyLoadingScreen_4, _activityLazyLoadedEvent_5, _jsWorkbenchEditorActivityInstance_6, _appFormerComponentsRegistry_7);
    registerDependentScopedReference(instance, _jsWorkbenchEditorActivityInstance_6);
    registerDependentScopedReference(instance, _activityLazyLoadedEvent_5);
    registerDependentScopedReference(instance, _promises_0);
    registerDependentScopedReference(instance, _lazyLoadingScreen_4);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}