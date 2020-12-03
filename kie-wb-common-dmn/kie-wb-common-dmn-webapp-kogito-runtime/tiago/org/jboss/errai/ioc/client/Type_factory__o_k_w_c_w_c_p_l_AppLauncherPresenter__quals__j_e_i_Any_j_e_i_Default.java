package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherPresenter;
import org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherView;
import org.kie.workbench.common.widgets.client.popups.launcher.events.AppLauncherAddEvent;
import org.kie.workbench.common.widgets.client.popups.launcher.events.AppLauncherRemoveEvent;
import org.kie.workbench.common.widgets.client.popups.launcher.events.AppLauncherUpdatedEvent;

public class Type_factory__o_k_w_c_w_c_p_l_AppLauncherPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<AppLauncherPresenter> { private class Type_factory__o_k_w_c_w_c_p_l_AppLauncherPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends AppLauncherPresenter implements Proxy<AppLauncherPresenter> {
    private final ProxyHelper<AppLauncherPresenter> proxyHelper = new ProxyHelperImpl<AppLauncherPresenter>("Type_factory__o_k_w_c_w_c_p_l_AppLauncherPresenter__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final AppLauncherPresenter instance) {

    }

    public AppLauncherPresenter asBeanType() {
      return this;
    }

    public void setInstance(final AppLauncherPresenter instance) {
      proxyHelper.setInstance(instance);
    }

    public void clearInstance() {
      proxyHelper.clearInstance();
    }

    public void setProxyContext(final Context context) {
      proxyHelper.setProxyContext(context);
    }

    public Context getProxyContext() {
      return proxyHelper.getProxyContext();
    }

    public Object unwrap() {
      return proxyHelper.getInstance(this);
    }

    public boolean equals(Object obj) {
      obj = Factory.maybeUnwrapProxy(obj);
      return proxyHelper.getInstance(this).equals(obj);
    }

    @Override public IsWidget getView() {
      if (proxyHelper != null) {
        final AppLauncherPresenter proxiedInstance = proxyHelper.getInstance(this);
        final IsWidget retVal = proxiedInstance.getView();
        return retVal;
      } else {
        return super.getView();
      }
    }

    @Override public void onAppLauncherRemoveEvent(AppLauncherRemoveEvent event) {
      if (proxyHelper != null) {
        final AppLauncherPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onAppLauncherRemoveEvent(event);
      } else {
        super.onAppLauncherRemoveEvent(event);
      }
    }

    @Override public void onAppLauncherAddEvent(AppLauncherAddEvent event) {
      if (proxyHelper != null) {
        final AppLauncherPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onAppLauncherAddEvent(event);
      } else {
        super.onAppLauncherAddEvent(event);
      }
    }

    @Override public boolean isAppLauncherEmpty() {
      if (proxyHelper != null) {
        final AppLauncherPresenter proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isAppLauncherEmpty();
        return retVal;
      } else {
        return super.isAppLauncherEmpty();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final AppLauncherPresenter proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_w_c_p_l_AppLauncherPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AppLauncherPresenter.class, "Type_factory__o_k_w_c_w_c_p_l_AppLauncherPresenter__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AppLauncherPresenter.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.kie.workbench.common.widgets.client.popups.launcher.events.AppLauncherRemoveEvent", new AbstractCDIEventCallback<AppLauncherRemoveEvent>() {
      public void fireEvent(final AppLauncherRemoveEvent event) {
        final AppLauncherPresenter instance = Factory.maybeUnwrapProxy((AppLauncherPresenter) context.getInstance("Type_factory__o_k_w_c_w_c_p_l_AppLauncherPresenter__quals__j_e_i_Any_j_e_i_Default"));
        instance.onAppLauncherRemoveEvent(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.widgets.client.popups.launcher.events.AppLauncherRemoveEvent []";
      }
    });
    CDI.subscribeLocal("org.kie.workbench.common.widgets.client.popups.launcher.events.AppLauncherAddEvent", new AbstractCDIEventCallback<AppLauncherAddEvent>() {
      public void fireEvent(final AppLauncherAddEvent event) {
        final AppLauncherPresenter instance = Factory.maybeUnwrapProxy((AppLauncherPresenter) context.getInstance("Type_factory__o_k_w_c_w_c_p_l_AppLauncherPresenter__quals__j_e_i_Any_j_e_i_Default"));
        instance.onAppLauncherAddEvent(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.widgets.client.popups.launcher.events.AppLauncherAddEvent []";
      }
    });
  }

  public AppLauncherPresenter createInstance(final ContextManager contextManager) {
    final AppLauncherPresenter instance = new AppLauncherPresenter();
    setIncompleteInstance(instance);
    final AppLauncherView AppLauncherPresenter_view = (AppLauncherView) contextManager.getInstance("Type_factory__o_k_w_c_w_c_p_l_AppLauncherView__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, AppLauncherPresenter_view);
    AppLauncherPresenter_AppLauncherView_view(instance, AppLauncherPresenter_view);
    final Event AppLauncherPresenter_updatedEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { AppLauncherUpdatedEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, AppLauncherPresenter_updatedEvent);
    AppLauncherPresenter_Event_updatedEvent(instance, AppLauncherPresenter_updatedEvent);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<AppLauncherPresenter> proxyImpl = new Type_factory__o_k_w_c_w_c_p_l_AppLauncherPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherPresenter.AppLauncherView AppLauncherPresenter_AppLauncherView_view(AppLauncherPresenter instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherPresenter::view;
  }-*/;

  native static void AppLauncherPresenter_AppLauncherView_view(AppLauncherPresenter instance, org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherPresenter.AppLauncherView value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherPresenter::view = value;
  }-*/;

  native static Event AppLauncherPresenter_Event_updatedEvent(AppLauncherPresenter instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherPresenter::updatedEvent;
  }-*/;

  native static void AppLauncherPresenter_Event_updatedEvent(AppLauncherPresenter instance, Event<AppLauncherUpdatedEvent> value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherPresenter::updatedEvent = value;
  }-*/;
}