package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.DefaultBeanFactory;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.dnd.CompassDropController;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.menu.Menus;

public class Type_factory__o_u_c_w_DefaultBeanFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultBeanFactory> { private class Type_factory__o_u_c_w_DefaultBeanFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DefaultBeanFactory implements Proxy<DefaultBeanFactory> {
    private final ProxyHelper<DefaultBeanFactory> proxyHelper = new ProxyHelperImpl<DefaultBeanFactory>("Type_factory__o_u_c_w_DefaultBeanFactory__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DefaultBeanFactory instance) {

    }

    public DefaultBeanFactory asBeanType() {
      return this;
    }

    public void setInstance(final DefaultBeanFactory instance) {
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

    @Override public WorkbenchPartPresenter newWorkbenchPart(Menus menus, String title, IsWidget titleDecoration, PartDefinition definition, Class partType) {
      if (proxyHelper != null) {
        final DefaultBeanFactory proxiedInstance = proxyHelper.getInstance(this);
        final WorkbenchPartPresenter retVal = proxiedInstance.newWorkbenchPart(menus, title, titleDecoration, definition, partType);
        return retVal;
      } else {
        return super.newWorkbenchPart(menus, title, titleDecoration, definition, partType);
      }
    }

    @Override public WorkbenchPanelPresenter newRootPanel(PerspectiveActivity activity, PanelDefinition root) {
      if (proxyHelper != null) {
        final DefaultBeanFactory proxiedInstance = proxyHelper.getInstance(this);
        final WorkbenchPanelPresenter retVal = proxiedInstance.newRootPanel(activity, root);
        return retVal;
      } else {
        return super.newRootPanel(activity, root);
      }
    }

    @Override public WorkbenchPanelPresenter newWorkbenchPanel(PanelDefinition definition) {
      if (proxyHelper != null) {
        final DefaultBeanFactory proxiedInstance = proxyHelper.getInstance(this);
        final WorkbenchPanelPresenter retVal = proxiedInstance.newWorkbenchPanel(definition);
        return retVal;
      } else {
        return super.newWorkbenchPanel(definition);
      }
    }

    @Override public CompassDropController newDropController(WorkbenchPanelView view) {
      if (proxyHelper != null) {
        final DefaultBeanFactory proxiedInstance = proxyHelper.getInstance(this);
        final CompassDropController retVal = proxiedInstance.newDropController(view);
        return retVal;
      } else {
        return super.newDropController(view);
      }
    }

    @Override public void destroy(Object o) {
      if (proxyHelper != null) {
        final DefaultBeanFactory proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.destroy(o);
      } else {
        super.destroy(o);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DefaultBeanFactory proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_w_DefaultBeanFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultBeanFactory.class, "Type_factory__o_u_c_w_DefaultBeanFactory__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultBeanFactory.class, Object.class, BeanFactory.class });
  }

  public DefaultBeanFactory createInstance(final ContextManager contextManager) {
    final DefaultBeanFactory instance = new DefaultBeanFactory();
    setIncompleteInstance(instance);
    final SyncBeanManager DefaultBeanFactory_iocManager = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DefaultBeanFactory_iocManager);
    DefaultBeanFactory_SyncBeanManager_iocManager(instance, DefaultBeanFactory_iocManager);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DefaultBeanFactory> proxyImpl = new Type_factory__o_u_c_w_DefaultBeanFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static SyncBeanManager DefaultBeanFactory_SyncBeanManager_iocManager(DefaultBeanFactory instance) /*-{
    return instance.@org.uberfire.client.workbench.DefaultBeanFactory::iocManager;
  }-*/;

  native static void DefaultBeanFactory_SyncBeanManager_iocManager(DefaultBeanFactory instance, SyncBeanManager value) /*-{
    instance.@org.uberfire.client.workbench.DefaultBeanFactory::iocManager = value;
  }-*/;
}