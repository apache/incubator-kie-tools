package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentPalette;
import org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPaletteGroupProvider;
import org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPalettePresenter;
import org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPalettePresenter.View;
import org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPaletteView;
import org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupPresenter;

public class Type_factory__o_u_e_l_e_c_w_LayoutComponentPalettePresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutComponentPalettePresenter> { private class Type_factory__o_u_e_l_e_c_w_LayoutComponentPalettePresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends LayoutComponentPalettePresenter implements Proxy<LayoutComponentPalettePresenter> {
    private final ProxyHelper<LayoutComponentPalettePresenter> proxyHelper = new ProxyHelperImpl<LayoutComponentPalettePresenter>("Type_factory__o_u_e_l_e_c_w_LayoutComponentPalettePresenter__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final LayoutComponentPalettePresenter instance) {

    }

    public LayoutComponentPalettePresenter asBeanType() {
      return this;
    }

    public void setInstance(final LayoutComponentPalettePresenter instance) {
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

    @Override public UberElement getView() {
      if (proxyHelper != null) {
        final LayoutComponentPalettePresenter proxiedInstance = proxyHelper.getInstance(this);
        final UberElement retVal = proxiedInstance.getView();
        return retVal;
      } else {
        return super.getView();
      }
    }

    @Override public Map getLayoutDragComponentGroups() {
      if (proxyHelper != null) {
        final LayoutComponentPalettePresenter proxiedInstance = proxyHelper.getInstance(this);
        final Map retVal = proxiedInstance.getLayoutDragComponentGroups();
        return retVal;
      } else {
        return super.getLayoutDragComponentGroups();
      }
    }

    @Override public void clear() {
      if (proxyHelper != null) {
        final LayoutComponentPalettePresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.clear();
      } else {
        super.clear();
      }
    }

    @Override public void addDraggableGroups(Collection groupProviders) {
      if (proxyHelper != null) {
        final LayoutComponentPalettePresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addDraggableGroups(groupProviders);
      } else {
        super.addDraggableGroups(groupProviders);
      }
    }

    @Override public void addDraggableGroup(LayoutComponentPaletteGroupProvider groupProvider) {
      if (proxyHelper != null) {
        final LayoutComponentPalettePresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addDraggableGroup(groupProvider);
      } else {
        super.addDraggableGroup(groupProvider);
      }
    }

    @Override public void removeDraggableGroup(String groupName) {
      if (proxyHelper != null) {
        final LayoutComponentPalettePresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.removeDraggableGroup(groupName);
      } else {
        super.removeDraggableGroup(groupName);
      }
    }

    @Override public boolean hasDraggableGroup(String groupName) {
      if (proxyHelper != null) {
        final LayoutComponentPalettePresenter proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.hasDraggableGroup(groupName);
        return retVal;
      } else {
        return super.hasDraggableGroup(groupName);
      }
    }

    @Override public void addDraggableComponent(String groupName, String componentId, LayoutDragComponent component) {
      if (proxyHelper != null) {
        final LayoutComponentPalettePresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addDraggableComponent(groupName, componentId, component);
      } else {
        super.addDraggableComponent(groupName, componentId, component);
      }
    }

    @Override public void removeDraggableComponent(String groupName, String componentId) {
      if (proxyHelper != null) {
        final LayoutComponentPalettePresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.removeDraggableComponent(groupName, componentId);
      } else {
        super.removeDraggableComponent(groupName, componentId);
      }
    }

    @Override public boolean hasDraggableComponent(String groupName, String componentId) {
      if (proxyHelper != null) {
        final LayoutComponentPalettePresenter proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.hasDraggableComponent(groupName, componentId);
        return retVal;
      } else {
        return super.hasDraggableComponent(groupName, componentId);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final LayoutComponentPalettePresenter proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_l_e_c_w_LayoutComponentPalettePresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutComponentPalettePresenter.class, "Type_factory__o_u_e_l_e_c_w_LayoutComponentPalettePresenter__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LayoutComponentPalettePresenter.class, Object.class, LayoutDragComponentPalette.class });
  }

  public LayoutComponentPalettePresenter createInstance(final ContextManager contextManager) {
    final ManagedInstance<LayoutDragComponentGroupPresenter> _layoutDragComponentGroupInstance_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { LayoutDragComponentGroupPresenter.class }, new Annotation[] { });
    final View _view_0 = (LayoutComponentPaletteView) contextManager.getInstance("Type_factory__o_u_e_l_e_c_w_LayoutComponentPaletteView__quals__j_e_i_Any_j_e_i_Default");
    final LayoutComponentPalettePresenter instance = new LayoutComponentPalettePresenter(_view_0, _layoutDragComponentGroupInstance_1);
    registerDependentScopedReference(instance, _layoutDragComponentGroupInstance_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<LayoutComponentPalettePresenter> proxyImpl = new Type_factory__o_u_e_l_e_c_w_LayoutComponentPalettePresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}