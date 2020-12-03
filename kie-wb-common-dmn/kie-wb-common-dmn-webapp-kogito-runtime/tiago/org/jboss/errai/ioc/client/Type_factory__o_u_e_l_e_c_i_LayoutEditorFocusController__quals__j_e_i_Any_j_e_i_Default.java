package org.jboss.errai.ioc.client;

import elemental2.dom.HTMLElement;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.ext.layout.editor.client.components.container.Container.View;
import org.uberfire.ext.layout.editor.client.infra.LayoutEditorFocusController;

public class Type_factory__o_u_e_l_e_c_i_LayoutEditorFocusController__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutEditorFocusController> { private class Type_factory__o_u_e_l_e_c_i_LayoutEditorFocusController__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends LayoutEditorFocusController implements Proxy<LayoutEditorFocusController> {
    private final ProxyHelper<LayoutEditorFocusController> proxyHelper = new ProxyHelperImpl<LayoutEditorFocusController>("Type_factory__o_u_e_l_e_c_i_LayoutEditorFocusController__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final LayoutEditorFocusController instance) {

    }

    public LayoutEditorFocusController asBeanType() {
      return this;
    }

    public void setInstance(final LayoutEditorFocusController instance) {
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

    @Override public void setTargetContainerView(View view) {
      if (proxyHelper != null) {
        final LayoutEditorFocusController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setTargetContainerView(view);
      } else {
        super.setTargetContainerView(view);
      }
    }

    @Override public void recordFocus() {
      if (proxyHelper != null) {
        final LayoutEditorFocusController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.recordFocus();
      } else {
        super.recordFocus();
      }
    }

    @Override public void restoreFocus() {
      if (proxyHelper != null) {
        final LayoutEditorFocusController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.restoreFocus();
      } else {
        super.restoreFocus();
      }
    }

    @Override public boolean isDirty() {
      if (proxyHelper != null) {
        final LayoutEditorFocusController proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isDirty();
        return retVal;
      } else {
        return super.isDirty();
      }
    }

    @Override protected HTMLElement findScrollableParent(HTMLElement element) {
      if (proxyHelper != null) {
        final LayoutEditorFocusController proxiedInstance = proxyHelper.getInstance(this);
        final HTMLElement retVal = LayoutEditorFocusController_findScrollableParent_HTMLElement(proxiedInstance, element);
        return retVal;
      } else {
        return super.findScrollableParent(element);
      }
    }

    @Override protected void setScrollableElement(HTMLElement element) {
      if (proxyHelper != null) {
        final LayoutEditorFocusController proxiedInstance = proxyHelper.getInstance(this);
        LayoutEditorFocusController_setScrollableElement_HTMLElement(proxiedInstance, element);
      } else {
        super.setScrollableElement(element);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final LayoutEditorFocusController proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_l_e_c_i_LayoutEditorFocusController__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutEditorFocusController.class, "Type_factory__o_u_e_l_e_c_i_LayoutEditorFocusController__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LayoutEditorFocusController.class, Object.class });
  }

  public LayoutEditorFocusController createInstance(final ContextManager contextManager) {
    final LayoutEditorFocusController instance = new LayoutEditorFocusController();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<LayoutEditorFocusController> proxyImpl = new Type_factory__o_u_e_l_e_c_i_LayoutEditorFocusController__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void LayoutEditorFocusController_setScrollableElement_HTMLElement(LayoutEditorFocusController instance, HTMLElement a0) /*-{
    instance.@org.uberfire.ext.layout.editor.client.infra.LayoutEditorFocusController::setScrollableElement(Lelemental2/dom/HTMLElement;)(a0);
  }-*/;

  public native static HTMLElement LayoutEditorFocusController_findScrollableParent_HTMLElement(LayoutEditorFocusController instance, HTMLElement a0) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.infra.LayoutEditorFocusController::findScrollableParent(Lelemental2/dom/HTMLElement;)(a0);
  }-*/;
}