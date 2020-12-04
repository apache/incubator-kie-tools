package org.jboss.errai.ioc.client;

import elemental2.dom.HTMLElement;
import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenu;
import org.kie.workbench.common.dmn.client.editors.drd.DRDContextMenu;
import org.kie.workbench.common.dmn.client.editors.drd.DRDContextMenuService;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.graph.Node;

public class Type_factory__o_k_w_c_d_c_e_d_DRDContextMenu__quals__j_e_i_Any_j_e_i_Default extends Factory<DRDContextMenu> { private class Type_factory__o_k_w_c_d_c_e_d_DRDContextMenu__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DRDContextMenu implements Proxy<DRDContextMenu> {
    private final ProxyHelper<DRDContextMenu> proxyHelper = new ProxyHelperImpl<DRDContextMenu>("Type_factory__o_k_w_c_d_c_e_d_DRDContextMenu__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_e_d_DRDContextMenu__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null, null);
    }

    public void initProxyProperties(final DRDContextMenu instance) {

    }

    public DRDContextMenu asBeanType() {
      return this;
    }

    public void setInstance(final DRDContextMenu instance) {
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

    @Override public String getTitle() {
      if (proxyHelper != null) {
        final DRDContextMenu proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getTitle();
        return retVal;
      } else {
        return super.getTitle();
      }
    }

    @Override public void appendContextMenuToTheDOM(double x, double y) {
      if (proxyHelper != null) {
        final DRDContextMenu proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.appendContextMenuToTheDOM(x, y);
      } else {
        super.appendContextMenuToTheDOM(x, y);
      }
    }

    @Override public void show(Collection selectedNodes) {
      if (proxyHelper != null) {
        final DRDContextMenu proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.show(selectedNodes);
      } else {
        super.show(selectedNodes);
      }
    }

    @Override protected void setDRDContextMenuHandler(ContextMenu contextMenu, Collection selectedNodes) {
      if (proxyHelper != null) {
        final DRDContextMenu proxiedInstance = proxyHelper.getInstance(this);
        DRDContextMenu_setDRDContextMenuHandler_ContextMenu_Collection(proxiedInstance, contextMenu, selectedNodes);
      } else {
        super.setDRDContextMenuHandler(contextMenu, selectedNodes);
      }
    }

    @Override public HTMLElement getElement() {
      if (proxyHelper != null) {
        final DRDContextMenu proxiedInstance = proxyHelper.getInstance(this);
        final HTMLElement retVal = proxiedInstance.getElement();
        return retVal;
      } else {
        return super.getElement();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DRDContextMenu proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_d_DRDContextMenu__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DRDContextMenu.class, "Type_factory__o_k_w_c_d_c_e_d_DRDContextMenu__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DRDContextMenu.class, Object.class });
  }

  public DRDContextMenu createInstance(final ContextManager contextManager) {
    final ClientTranslationService _translationService_1 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final DRDContextMenuService _drdContextMenuService_2 = (DRDContextMenuService) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_d_DRDContextMenuService__quals__j_e_i_Any_j_e_i_Default");
    final DMNDiagramsSession _dmnDiagramsSession_3 = (DMNDiagramsSession) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default");
    final ContextMenu _contextMenu_0 = (ContextMenu) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_c_ContextMenu__quals__j_e_i_Any_j_e_i_Default");
    final DRDContextMenu instance = new DRDContextMenu(_contextMenu_0, _translationService_1, _drdContextMenuService_2, _dmnDiagramsSession_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_e_d_DRDContextMenu__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.editors.drd.DRDContextMenu an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.editors.drd.DRDContextMenu ([org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenu, org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService, org.kie.workbench.common.dmn.client.editors.drd.DRDContextMenuService, org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DRDContextMenu> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void DRDContextMenu_setDRDContextMenuHandler_ContextMenu_Collection(DRDContextMenu instance, ContextMenu a0, Collection<Node> a1) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.drd.DRDContextMenu::setDRDContextMenuHandler(Lorg/kie/workbench/common/dmn/client/editors/contextmenu/ContextMenu;Ljava/util/Collection;)(a0, a1);
  }-*/;
}