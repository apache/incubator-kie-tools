package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
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
import org.uberfire.client.menu.SplashScreenMenuPresenter;
import org.uberfire.client.menu.SplashScreenMenuPresenter.View;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.views.pfly.menu.SplashScreenMenuView;
import org.uberfire.client.workbench.events.NewSplashScreenActiveEvent;

public class Type_factory__o_u_c_m_SplashScreenMenuPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<SplashScreenMenuPresenter> { private class Type_factory__o_u_c_m_SplashScreenMenuPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends SplashScreenMenuPresenter implements Proxy<SplashScreenMenuPresenter> {
    private final ProxyHelper<SplashScreenMenuPresenter> proxyHelper = new ProxyHelperImpl<SplashScreenMenuPresenter>("Type_factory__o_u_c_m_SplashScreenMenuPresenter__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final SplashScreenMenuPresenter instance) {

    }

    public SplashScreenMenuPresenter asBeanType() {
      return this;
    }

    public void setInstance(final SplashScreenMenuPresenter instance) {
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

    @Override public Widget asWidget() {
      if (proxyHelper != null) {
        final SplashScreenMenuPresenter proxiedInstance = proxyHelper.getInstance(this);
        final Widget retVal = proxiedInstance.asWidget();
        return retVal;
      } else {
        return super.asWidget();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final SplashScreenMenuPresenter proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_m_SplashScreenMenuPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SplashScreenMenuPresenter.class, "Type_factory__o_u_c_m_SplashScreenMenuPresenter__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SplashScreenMenuPresenter.class, Object.class, IsWidget.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.uberfire.client.workbench.events.NewSplashScreenActiveEvent", new AbstractCDIEventCallback<NewSplashScreenActiveEvent>() {
      public void fireEvent(final NewSplashScreenActiveEvent event) {
        final SplashScreenMenuPresenter instance = Factory.maybeUnwrapProxy((SplashScreenMenuPresenter) context.getInstance("Type_factory__o_u_c_m_SplashScreenMenuPresenter__quals__j_e_i_Any_j_e_i_Default"));
        SplashScreenMenuPresenter_onNewSplashScreen_NewSplashScreenActiveEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.NewSplashScreenActiveEvent []";
      }
    });
  }

  public SplashScreenMenuPresenter createInstance(final ContextManager contextManager) {
    final View _view_1 = (SplashScreenMenuView) contextManager.getInstance("Type_factory__o_u_c_v_p_m_SplashScreenMenuView__quals__j_e_i_Any_j_e_i_Default");
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final SplashScreenMenuPresenter instance = new SplashScreenMenuPresenter(_placeManager_0, _view_1);
    registerDependentScopedReference(instance, _view_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<SplashScreenMenuPresenter> proxyImpl = new Type_factory__o_u_c_m_SplashScreenMenuPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void SplashScreenMenuPresenter_onNewSplashScreen_NewSplashScreenActiveEvent(SplashScreenMenuPresenter instance, NewSplashScreenActiveEvent a0) /*-{
    instance.@org.uberfire.client.menu.SplashScreenMenuPresenter::onNewSplashScreen(Lorg/uberfire/client/workbench/events/NewSplashScreenActiveEvent;)(a0);
  }-*/;
}