package org.jboss.errai.ioc.client;

import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.ext.preferences.client.admin.AdminPagePerspective;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;

public class Type_factory__o_u_e_p_c_a_AdminPagePerspective__quals__j_e_i_Any_j_e_i_Default extends Factory<AdminPagePerspective> { private class Type_factory__o_u_e_p_c_a_AdminPagePerspective__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends AdminPagePerspective implements Proxy<AdminPagePerspective> {
    private final ProxyHelper<AdminPagePerspective> proxyHelper = new ProxyHelperImpl<AdminPagePerspective>("Type_factory__o_u_e_p_c_a_AdminPagePerspective__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final AdminPagePerspective instance) {

    }

    public AdminPagePerspective asBeanType() {
      return this;
    }

    public void setInstance(final AdminPagePerspective instance) {
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

    @Override public PerspectiveDefinition getPerspective() {
      if (proxyHelper != null) {
        final AdminPagePerspective proxiedInstance = proxyHelper.getInstance(this);
        final PerspectiveDefinition retVal = proxiedInstance.getPerspective();
        return retVal;
      } else {
        return super.getPerspective();
      }
    }

    @Override public void onStartup(PlaceRequest placeRequest) {
      if (proxyHelper != null) {
        final AdminPagePerspective proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onStartup(placeRequest);
      } else {
        super.onStartup(placeRequest);
      }
    }

    @Override public void getMenus(Consumer menusConsumer) {
      if (proxyHelper != null) {
        final AdminPagePerspective proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.getMenus(menusConsumer);
      } else {
        super.getMenus(menusConsumer);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final AdminPagePerspective proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_p_c_a_AdminPagePerspective__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AdminPagePerspective.class, "Type_factory__o_u_e_p_c_a_AdminPagePerspective__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AdminPagePerspective.class, Object.class });
  }

  public AdminPagePerspective createInstance(final ContextManager contextManager) {
    final AdminPagePerspective instance = new AdminPagePerspective();
    setIncompleteInstance(instance);
    final TranslationService AdminPagePerspective_translationService = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, AdminPagePerspective_translationService);
    AdminPagePerspective_TranslationService_translationService(instance, AdminPagePerspective_translationService);
    final PlaceManagerImpl AdminPagePerspective_placeManager = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    AdminPagePerspective_PlaceManager_placeManager(instance, AdminPagePerspective_placeManager);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<AdminPagePerspective> proxyImpl = new Type_factory__o_u_e_p_c_a_AdminPagePerspective__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static PlaceManager AdminPagePerspective_PlaceManager_placeManager(AdminPagePerspective instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.admin.AdminPagePerspective::placeManager;
  }-*/;

  native static void AdminPagePerspective_PlaceManager_placeManager(AdminPagePerspective instance, PlaceManager value) /*-{
    instance.@org.uberfire.ext.preferences.client.admin.AdminPagePerspective::placeManager = value;
  }-*/;

  native static TranslationService AdminPagePerspective_TranslationService_translationService(AdminPagePerspective instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.admin.AdminPagePerspective::translationService;
  }-*/;

  native static void AdminPagePerspective_TranslationService_translationService(AdminPagePerspective instance, TranslationService value) /*-{
    instance.@org.uberfire.ext.preferences.client.admin.AdminPagePerspective::translationService = value;
  }-*/;
}