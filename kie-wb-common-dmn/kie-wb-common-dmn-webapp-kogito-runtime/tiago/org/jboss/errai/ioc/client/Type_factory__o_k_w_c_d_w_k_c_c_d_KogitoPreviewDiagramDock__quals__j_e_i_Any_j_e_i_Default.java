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
import org.kie.workbench.common.dmn.webapp.common.client.docks.preview.PreviewDiagramDock;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.docks.KogitoPreviewDiagramDock;
import org.kie.workbench.common.stunner.kogito.api.docks.DiagramEditorDock;
import org.uberfire.client.docks.UberfireDocksImpl;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

public class Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoPreviewDiagramDock__quals__j_e_i_Any_j_e_i_Default extends Factory<KogitoPreviewDiagramDock> { private class Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoPreviewDiagramDock__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends KogitoPreviewDiagramDock implements Proxy<KogitoPreviewDiagramDock> {
    private final ProxyHelper<KogitoPreviewDiagramDock> proxyHelper = new ProxyHelperImpl<KogitoPreviewDiagramDock>("Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoPreviewDiagramDock__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final KogitoPreviewDiagramDock instance) {

    }

    public KogitoPreviewDiagramDock asBeanType() {
      return this;
    }

    public void setInstance(final KogitoPreviewDiagramDock instance) {
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
        final KogitoPreviewDiagramDock proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init(owningPerspectiveId);
      } else {
        super.init(owningPerspectiveId);
      }
    }

    @Override public void open() {
      if (proxyHelper != null) {
        final KogitoPreviewDiagramDock proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.open();
      } else {
        super.open();
      }
    }

    @Override public void close() {
      if (proxyHelper != null) {
        final KogitoPreviewDiagramDock proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.close();
      } else {
        super.close();
      }
    }

    @Override public void destroy() {
      if (proxyHelper != null) {
        final KogitoPreviewDiagramDock proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.destroy();
      } else {
        super.destroy();
      }
    }

    @Override protected boolean isOpened() {
      if (proxyHelper != null) {
        final KogitoPreviewDiagramDock proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = PreviewDiagramDock_isOpened(proxiedInstance);
        return retVal;
      } else {
        return super.isOpened();
      }
    }

    @Override protected UberfireDock makeUberfireDock() {
      if (proxyHelper != null) {
        final KogitoPreviewDiagramDock proxiedInstance = proxyHelper.getInstance(this);
        final UberfireDock retVal = PreviewDiagramDock_makeUberfireDock(proxiedInstance);
        return retVal;
      } else {
        return super.makeUberfireDock();
      }
    }

    @Override protected UberfireDockPosition position() {
      if (proxyHelper != null) {
        final KogitoPreviewDiagramDock proxiedInstance = proxyHelper.getInstance(this);
        final UberfireDockPosition retVal = PreviewDiagramDock_position(proxiedInstance);
        return retVal;
      } else {
        return super.position();
      }
    }

    @Override protected String icon() {
      if (proxyHelper != null) {
        final KogitoPreviewDiagramDock proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = PreviewDiagramDock_icon(proxiedInstance);
        return retVal;
      } else {
        return super.icon();
      }
    }

    @Override protected DefaultPlaceRequest placeRequest() {
      if (proxyHelper != null) {
        final KogitoPreviewDiagramDock proxiedInstance = proxyHelper.getInstance(this);
        final DefaultPlaceRequest retVal = PreviewDiagramDock_placeRequest(proxiedInstance);
        return retVal;
      } else {
        return super.placeRequest();
      }
    }

    @Override protected String owningPerspectiveId() {
      if (proxyHelper != null) {
        final KogitoPreviewDiagramDock proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = PreviewDiagramDock_owningPerspectiveId(proxiedInstance);
        return retVal;
      } else {
        return super.owningPerspectiveId();
      }
    }

    @Override protected UberfireDock getUberfireDock() {
      if (proxyHelper != null) {
        final KogitoPreviewDiagramDock proxiedInstance = proxyHelper.getInstance(this);
        final UberfireDock retVal = PreviewDiagramDock_getUberfireDock(proxiedInstance);
        return retVal;
      } else {
        return super.getUberfireDock();
      }
    }

    @Override protected String dockLabel() {
      if (proxyHelper != null) {
        final KogitoPreviewDiagramDock proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = PreviewDiagramDock_dockLabel(proxiedInstance);
        return retVal;
      } else {
        return super.dockLabel();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final KogitoPreviewDiagramDock proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoPreviewDiagramDock__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KogitoPreviewDiagramDock.class, "Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoPreviewDiagramDock__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KogitoPreviewDiagramDock.class, PreviewDiagramDock.class, Object.class, DiagramEditorDock.class });
  }

  public KogitoPreviewDiagramDock createInstance(final ContextManager contextManager) {
    final UberfireDocks _uberfireDocks_0 = (UberfireDocksImpl) contextManager.getInstance("Type_factory__o_u_c_d_UberfireDocksImpl__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_1 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final KogitoPreviewDiagramDock instance = new KogitoPreviewDiagramDock(_uberfireDocks_0, _translationService_1);
    registerDependentScopedReference(instance, _translationService_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<KogitoPreviewDiagramDock> proxyImpl = new Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoPreviewDiagramDock__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static UberfireDockPosition PreviewDiagramDock_position(PreviewDiagramDock instance) /*-{
    return instance.@org.kie.workbench.common.dmn.webapp.common.client.docks.preview.PreviewDiagramDock::position()();
  }-*/;

  public native static String PreviewDiagramDock_icon(PreviewDiagramDock instance) /*-{
    return instance.@org.kie.workbench.common.dmn.webapp.common.client.docks.preview.PreviewDiagramDock::icon()();
  }-*/;

  public native static UberfireDock PreviewDiagramDock_makeUberfireDock(PreviewDiagramDock instance) /*-{
    return instance.@org.kie.workbench.common.dmn.webapp.common.client.docks.preview.PreviewDiagramDock::makeUberfireDock()();
  }-*/;

  public native static boolean PreviewDiagramDock_isOpened(PreviewDiagramDock instance) /*-{
    return instance.@org.kie.workbench.common.dmn.webapp.common.client.docks.preview.PreviewDiagramDock::isOpened()();
  }-*/;

  public native static UberfireDock PreviewDiagramDock_getUberfireDock(PreviewDiagramDock instance) /*-{
    return instance.@org.kie.workbench.common.dmn.webapp.common.client.docks.preview.PreviewDiagramDock::getUberfireDock()();
  }-*/;

  public native static String PreviewDiagramDock_dockLabel(PreviewDiagramDock instance) /*-{
    return instance.@org.kie.workbench.common.dmn.webapp.common.client.docks.preview.PreviewDiagramDock::dockLabel()();
  }-*/;

  public native static String PreviewDiagramDock_owningPerspectiveId(PreviewDiagramDock instance) /*-{
    return instance.@org.kie.workbench.common.dmn.webapp.common.client.docks.preview.PreviewDiagramDock::owningPerspectiveId()();
  }-*/;

  public native static DefaultPlaceRequest PreviewDiagramDock_placeRequest(PreviewDiagramDock instance) /*-{
    return instance.@org.kie.workbench.common.dmn.webapp.common.client.docks.preview.PreviewDiagramDock::placeRequest()();
  }-*/;
}