package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.appformer.client.context.EditorContextProvider;
import org.appformer.kogito.bridge.client.context.impl.KogitoEditorContextProviderImpl;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.docks.KogitoDecisionNavigatorDock;
import org.kie.workbench.common.stunner.kogito.api.docks.DiagramEditorDock;
import org.uberfire.client.docks.UberfireDocksImpl;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

public class Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoDecisionNavigatorDock__quals__j_e_i_Any_j_e_i_Default extends Factory<KogitoDecisionNavigatorDock> { private class Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoDecisionNavigatorDock__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends KogitoDecisionNavigatorDock implements Proxy<KogitoDecisionNavigatorDock> {
    private final ProxyHelper<KogitoDecisionNavigatorDock> proxyHelper = new ProxyHelperImpl<KogitoDecisionNavigatorDock>("Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoDecisionNavigatorDock__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final KogitoDecisionNavigatorDock instance) {

    }

    public KogitoDecisionNavigatorDock asBeanType() {
      return this;
    }

    public void setInstance(final KogitoDecisionNavigatorDock instance) {
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

    @Override public void init(String perspective) {
      if (proxyHelper != null) {
        final KogitoDecisionNavigatorDock proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init(perspective);
      } else {
        super.init(perspective);
      }
    }

    @Override public void open() {
      if (proxyHelper != null) {
        final KogitoDecisionNavigatorDock proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.open();
      } else {
        super.open();
      }
    }

    @Override public void close() {
      if (proxyHelper != null) {
        final KogitoDecisionNavigatorDock proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.close();
      } else {
        super.close();
      }
    }

    @Override protected UberfireDockPosition position() {
      if (proxyHelper != null) {
        final KogitoDecisionNavigatorDock proxiedInstance = proxyHelper.getInstance(this);
        final UberfireDockPosition retVal = KogitoDecisionNavigatorDock_position(proxiedInstance);
        return retVal;
      } else {
        return super.position();
      }
    }

    @Override public void destroy() {
      if (proxyHelper != null) {
        final KogitoDecisionNavigatorDock proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.destroy();
      } else {
        super.destroy();
      }
    }

    @Override public void reload() {
      if (proxyHelper != null) {
        final KogitoDecisionNavigatorDock proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.reload();
      } else {
        super.reload();
      }
    }

    @Override public void resetContent() {
      if (proxyHelper != null) {
        final KogitoDecisionNavigatorDock proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.resetContent();
      } else {
        super.resetContent();
      }
    }

    @Override protected boolean isOpened() {
      if (proxyHelper != null) {
        final KogitoDecisionNavigatorDock proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = DecisionNavigatorDock_isOpened(proxiedInstance);
        return retVal;
      } else {
        return super.isOpened();
      }
    }

    @Override protected void setOpened(boolean opened) {
      if (proxyHelper != null) {
        final KogitoDecisionNavigatorDock proxiedInstance = proxyHelper.getInstance(this);
        DecisionNavigatorDock_setOpened_boolean(proxiedInstance, opened);
      } else {
        super.setOpened(opened);
      }
    }

    @Override protected UberfireDock makeUberfireDock() {
      if (proxyHelper != null) {
        final KogitoDecisionNavigatorDock proxiedInstance = proxyHelper.getInstance(this);
        final UberfireDock retVal = DecisionNavigatorDock_makeUberfireDock(proxiedInstance);
        return retVal;
      } else {
        return super.makeUberfireDock();
      }
    }

    @Override protected String perspective() {
      if (proxyHelper != null) {
        final KogitoDecisionNavigatorDock proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = DecisionNavigatorDock_perspective(proxiedInstance);
        return retVal;
      } else {
        return super.perspective();
      }
    }

    @Override protected UberfireDock getUberfireDock() {
      if (proxyHelper != null) {
        final KogitoDecisionNavigatorDock proxiedInstance = proxyHelper.getInstance(this);
        final UberfireDock retVal = DecisionNavigatorDock_getUberfireDock(proxiedInstance);
        return retVal;
      } else {
        return super.getUberfireDock();
      }
    }

    @Override protected String icon() {
      if (proxyHelper != null) {
        final KogitoDecisionNavigatorDock proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = DecisionNavigatorDock_icon(proxiedInstance);
        return retVal;
      } else {
        return super.icon();
      }
    }

    @Override protected String dockLabel() {
      if (proxyHelper != null) {
        final KogitoDecisionNavigatorDock proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = DecisionNavigatorDock_dockLabel(proxiedInstance);
        return retVal;
      } else {
        return super.dockLabel();
      }
    }

    @Override protected DefaultPlaceRequest placeRequest() {
      if (proxyHelper != null) {
        final KogitoDecisionNavigatorDock proxiedInstance = proxyHelper.getInstance(this);
        final DefaultPlaceRequest retVal = DecisionNavigatorDock_placeRequest(proxiedInstance);
        return retVal;
      } else {
        return super.placeRequest();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final KogitoDecisionNavigatorDock proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoDecisionNavigatorDock__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KogitoDecisionNavigatorDock.class, "Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoDecisionNavigatorDock__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KogitoDecisionNavigatorDock.class, DecisionNavigatorDock.class, Object.class, DiagramEditorDock.class });
  }

  public KogitoDecisionNavigatorDock createInstance(final ContextManager contextManager) {
    final UberfireDocks _uberfireDocks_0 = (UberfireDocksImpl) contextManager.getInstance("Type_factory__o_u_c_d_UberfireDocksImpl__quals__j_e_i_Any_j_e_i_Default");
    final EditorContextProvider _context_3 = (KogitoEditorContextProviderImpl) contextManager.getInstance("Type_factory__o_a_k_b_c_c_i_KogitoEditorContextProviderImpl__quals__j_e_i_Any_j_e_i_Default");
    final DecisionNavigatorPresenter _decisionNavigatorPresenter_1 = (DecisionNavigatorPresenter) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorPresenter__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_2 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final KogitoDecisionNavigatorDock instance = new KogitoDecisionNavigatorDock(_uberfireDocks_0, _decisionNavigatorPresenter_1, _translationService_2, _context_3);
    registerDependentScopedReference(instance, _translationService_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<KogitoDecisionNavigatorDock> proxyImpl = new Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoDecisionNavigatorDock__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static boolean DecisionNavigatorDock_isOpened(DecisionNavigatorDock instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock::isOpened()();
  }-*/;

  public native static UberfireDock DecisionNavigatorDock_makeUberfireDock(DecisionNavigatorDock instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock::makeUberfireDock()();
  }-*/;

  public native static UberfireDock DecisionNavigatorDock_getUberfireDock(DecisionNavigatorDock instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock::getUberfireDock()();
  }-*/;

  public native static String DecisionNavigatorDock_dockLabel(DecisionNavigatorDock instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock::dockLabel()();
  }-*/;

  public native static String DecisionNavigatorDock_icon(DecisionNavigatorDock instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock::icon()();
  }-*/;

  public native static void DecisionNavigatorDock_setOpened_boolean(DecisionNavigatorDock instance, boolean a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock::setOpened(Z)(a0);
  }-*/;

  public native static DefaultPlaceRequest DecisionNavigatorDock_placeRequest(DecisionNavigatorDock instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock::placeRequest()();
  }-*/;

  public native static String DecisionNavigatorDock_perspective(DecisionNavigatorDock instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock::perspective()();
  }-*/;

  public native static UberfireDockPosition KogitoDecisionNavigatorDock_position(KogitoDecisionNavigatorDock instance) /*-{
    return instance.@org.kie.workbench.common.dmn.webapp.kogito.common.client.docks.KogitoDecisionNavigatorDock::position()();
  }-*/;
}