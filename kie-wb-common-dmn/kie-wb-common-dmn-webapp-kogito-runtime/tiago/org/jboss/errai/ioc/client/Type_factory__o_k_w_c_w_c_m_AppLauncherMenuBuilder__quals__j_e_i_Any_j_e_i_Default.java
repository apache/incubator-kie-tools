package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.widgets.client.menu.AppLauncherMenuBuilder;
import org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherPresenter;
import org.kie.workbench.common.widgets.client.popups.launcher.events.AppLauncherUpdatedEvent;
import org.uberfire.workbench.model.menu.MenuFactory.CustomMenuBuilder;
import org.uberfire.workbench.model.menu.MenuItem;

public class Type_factory__o_k_w_c_w_c_m_AppLauncherMenuBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<AppLauncherMenuBuilder> { private class Type_factory__o_k_w_c_w_c_m_AppLauncherMenuBuilder__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends AppLauncherMenuBuilder implements Proxy<AppLauncherMenuBuilder> {
    private final ProxyHelper<AppLauncherMenuBuilder> proxyHelper = new ProxyHelperImpl<AppLauncherMenuBuilder>("Type_factory__o_k_w_c_w_c_m_AppLauncherMenuBuilder__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final AppLauncherMenuBuilder instance) {

    }

    public AppLauncherMenuBuilder asBeanType() {
      return this;
    }

    public void setInstance(final AppLauncherMenuBuilder instance) {
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

    @Override public void init() {
      if (proxyHelper != null) {
        final AppLauncherMenuBuilder proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public void push(CustomMenuBuilder element) {
      if (proxyHelper != null) {
        final AppLauncherMenuBuilder proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.push(element);
      } else {
        super.push(element);
      }
    }

    @Override public MenuItem build() {
      if (proxyHelper != null) {
        final AppLauncherMenuBuilder proxiedInstance = proxyHelper.getInstance(this);
        final MenuItem retVal = proxiedInstance.build();
        return retVal;
      } else {
        return super.build();
      }
    }

    @Override public void onAppLauncherUpdatedEvent(AppLauncherUpdatedEvent event) {
      if (proxyHelper != null) {
        final AppLauncherMenuBuilder proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onAppLauncherUpdatedEvent(event);
      } else {
        super.onAppLauncherUpdatedEvent(event);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final AppLauncherMenuBuilder proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_w_c_m_AppLauncherMenuBuilder__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AppLauncherMenuBuilder.class, "Type_factory__o_k_w_c_w_c_m_AppLauncherMenuBuilder__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AppLauncherMenuBuilder.class, Object.class, CustomMenuBuilder.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.kie.workbench.common.widgets.client.popups.launcher.events.AppLauncherUpdatedEvent", new AbstractCDIEventCallback<AppLauncherUpdatedEvent>() {
      public void fireEvent(final AppLauncherUpdatedEvent event) {
        final AppLauncherMenuBuilder instance = Factory.maybeUnwrapProxy((AppLauncherMenuBuilder) context.getInstance("Type_factory__o_k_w_c_w_c_m_AppLauncherMenuBuilder__quals__j_e_i_Any_j_e_i_Default"));
        instance.onAppLauncherUpdatedEvent(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.widgets.client.popups.launcher.events.AppLauncherUpdatedEvent []";
      }
    });
  }

  public AppLauncherMenuBuilder createInstance(final ContextManager contextManager) {
    final AppLauncherMenuBuilder instance = new AppLauncherMenuBuilder();
    setIncompleteInstance(instance);
    final AppLauncherPresenter AppLauncherMenuBuilder_appLauncher = (AppLauncherPresenter) contextManager.getInstance("Type_factory__o_k_w_c_w_c_p_l_AppLauncherPresenter__quals__j_e_i_Any_j_e_i_Default");
    AppLauncherMenuBuilder_AppLauncherPresenter_appLauncher(instance, AppLauncherMenuBuilder_appLauncher);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final AppLauncherMenuBuilder instance) {
    instance.init();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<AppLauncherMenuBuilder> proxyImpl = new Type_factory__o_k_w_c_w_c_m_AppLauncherMenuBuilder__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static AppLauncherPresenter AppLauncherMenuBuilder_AppLauncherPresenter_appLauncher(AppLauncherMenuBuilder instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.menu.AppLauncherMenuBuilder::appLauncher;
  }-*/;

  native static void AppLauncherMenuBuilder_AppLauncherPresenter_appLauncher(AppLauncherMenuBuilder instance, AppLauncherPresenter value) /*-{
    instance.@org.kie.workbench.common.widgets.client.menu.AppLauncherMenuBuilder::appLauncher = value;
  }-*/;
}