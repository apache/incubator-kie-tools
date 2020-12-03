package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.docks.KogitoPropertiesDock;
import org.kie.workbench.common.stunner.kogito.api.docks.DiagramEditorDock;
import org.kie.workbench.common.stunner.kogito.client.docks.BaseDiagramEditorDock;
import org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPropertiesDock;
import org.uberfire.client.docks.UberfireDocksImpl;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

public class Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoPropertiesDock__quals__j_e_i_Any_j_e_i_Default extends Factory<KogitoPropertiesDock> { private class Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoPropertiesDock__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends KogitoPropertiesDock implements Proxy<KogitoPropertiesDock> {
    private final ProxyHelper<KogitoPropertiesDock> proxyHelper = new ProxyHelperImpl<KogitoPropertiesDock>("Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoPropertiesDock__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final KogitoPropertiesDock instance) {

    }

    public KogitoPropertiesDock asBeanType() {
      return this;
    }

    public void setInstance(final KogitoPropertiesDock instance) {
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

    @Override public void init(String owningPerspectiveId) {
      if (proxyHelper != null) {
        final KogitoPropertiesDock proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init(owningPerspectiveId);
      } else {
        super.init(owningPerspectiveId);
      }
    }

    @Override public void open() {
      if (proxyHelper != null) {
        final KogitoPropertiesDock proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.open();
      } else {
        super.open();
      }
    }

    @Override public void close() {
      if (proxyHelper != null) {
        final KogitoPropertiesDock proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.close();
      } else {
        super.close();
      }
    }

    @Override public void destroy() {
      if (proxyHelper != null) {
        final KogitoPropertiesDock proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.destroy();
      } else {
        super.destroy();
      }
    }

    @Override public boolean isOpened() {
      if (proxyHelper != null) {
        final KogitoPropertiesDock proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isOpened();
        return retVal;
      } else {
        return super.isOpened();
      }
    }

    @Override protected UberfireDockPosition position() {
      if (proxyHelper != null) {
        final KogitoPropertiesDock proxiedInstance = proxyHelper.getInstance(this);
        final UberfireDockPosition retVal = BaseDiagramEditorDock_position(proxiedInstance);
        return retVal;
      } else {
        return super.position();
      }
    }

    @Override protected String owningPerspectiveId() {
      if (proxyHelper != null) {
        final KogitoPropertiesDock proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = BaseDiagramEditorDock_owningPerspectiveId(proxiedInstance);
        return retVal;
      } else {
        return super.owningPerspectiveId();
      }
    }

    @Override protected UberfireDock getUberfireDock() {
      if (proxyHelper != null) {
        final KogitoPropertiesDock proxiedInstance = proxyHelper.getInstance(this);
        final UberfireDock retVal = BaseDiagramEditorDock_getUberfireDock(proxiedInstance);
        return retVal;
      } else {
        return super.getUberfireDock();
      }
    }

    @Override protected UberfireDock makeUberfireDock() {
      if (proxyHelper != null) {
        final KogitoPropertiesDock proxiedInstance = proxyHelper.getInstance(this);
        final UberfireDock retVal = BaseDiagramEditorDock_makeUberfireDock(proxiedInstance);
        return retVal;
      } else {
        return super.makeUberfireDock();
      }
    }

    @Override protected DefaultPlaceRequest placeRequest() {
      if (proxyHelper != null) {
        final KogitoPropertiesDock proxiedInstance = proxyHelper.getInstance(this);
        final DefaultPlaceRequest retVal = BaseDiagramEditorDock_placeRequest(proxiedInstance);
        return retVal;
      } else {
        return super.placeRequest();
      }
    }

    @Override protected String dockLabel() {
      if (proxyHelper != null) {
        final KogitoPropertiesDock proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = BaseDiagramEditorDock_dockLabel(proxiedInstance);
        return retVal;
      } else {
        return super.dockLabel();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final KogitoPropertiesDock proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoPropertiesDock__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KogitoPropertiesDock.class, "Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoPropertiesDock__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KogitoPropertiesDock.class, DiagramEditorPropertiesDock.class, BaseDiagramEditorDock.class, Object.class, DiagramEditorDock.class });
  }

  public KogitoPropertiesDock createInstance(final ContextManager contextManager) {
    final UberfireDocks _uberfireDocks_0 = (UberfireDocksImpl) contextManager.getInstance("Type_factory__o_u_c_d_UberfireDocksImpl__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_1 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final KogitoPropertiesDock instance = new KogitoPropertiesDock(_uberfireDocks_0, _translationService_1);
    registerDependentScopedReference(instance, _translationService_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<KogitoPropertiesDock> proxyImpl = new Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoPropertiesDock__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static String BaseDiagramEditorDock_owningPerspectiveId(BaseDiagramEditorDock instance) /*-{
    return instance.@org.kie.workbench.common.stunner.kogito.client.docks.BaseDiagramEditorDock::owningPerspectiveId()();
  }-*/;

  public native static UberfireDock BaseDiagramEditorDock_getUberfireDock(BaseDiagramEditorDock instance) /*-{
    return instance.@org.kie.workbench.common.stunner.kogito.client.docks.BaseDiagramEditorDock::getUberfireDock()();
  }-*/;

  public native static UberfireDockPosition BaseDiagramEditorDock_position(BaseDiagramEditorDock instance) /*-{
    return instance.@org.kie.workbench.common.stunner.kogito.client.docks.BaseDiagramEditorDock::position()();
  }-*/;

  public native static UberfireDock BaseDiagramEditorDock_makeUberfireDock(BaseDiagramEditorDock instance) /*-{
    return instance.@org.kie.workbench.common.stunner.kogito.client.docks.BaseDiagramEditorDock::makeUberfireDock()();
  }-*/;

  public native static DefaultPlaceRequest BaseDiagramEditorDock_placeRequest(BaseDiagramEditorDock instance) /*-{
    return instance.@org.kie.workbench.common.stunner.kogito.client.docks.BaseDiagramEditorDock::placeRequest()();
  }-*/;

  public native static String BaseDiagramEditorDock_dockLabel(BaseDiagramEditorDock instance) /*-{
    return instance.@org.kie.workbench.common.stunner.kogito.client.docks.BaseDiagramEditorDock::dockLabel()();
  }-*/;
}