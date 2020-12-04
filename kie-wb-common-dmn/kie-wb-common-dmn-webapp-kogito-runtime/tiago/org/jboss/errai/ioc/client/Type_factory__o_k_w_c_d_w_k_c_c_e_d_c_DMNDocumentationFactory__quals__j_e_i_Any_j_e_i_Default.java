package org.jboss.errai.ioc.client;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentation;
import org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationDRDsFactory;
import org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationI18n;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editors.documentation.common.DMNDocumentationFactory;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.rpc.SessionInfo;

public class Type_factory__o_k_w_c_d_w_k_c_c_e_d_c_DMNDocumentationFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDocumentationFactory> { private class Type_factory__o_k_w_c_d_w_k_c_c_e_d_c_DMNDocumentationFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNDocumentationFactory implements Proxy<DMNDocumentationFactory> {
    private final ProxyHelper<DMNDocumentationFactory> proxyHelper = new ProxyHelperImpl<DMNDocumentationFactory>("Type_factory__o_k_w_c_d_w_k_c_c_e_d_c_DMNDocumentationFactory__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_w_k_c_c_e_d_c_DMNDocumentationFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null, null, null);
    }

    public void initProxyProperties(final DMNDocumentationFactory instance) {

    }

    public DMNDocumentationFactory asBeanType() {
      return this;
    }

    public void setInstance(final DMNDocumentationFactory instance) {
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

    @Override protected String getCurrentUserName() {
      if (proxyHelper != null) {
        final DMNDocumentationFactory proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = DMNDocumentationFactory_getCurrentUserName(proxiedInstance);
        return retVal;
      } else {
        return super.getCurrentUserName();
      }
    }

    @Override public DMNDocumentation create(Diagram diagram) {
      if (proxyHelper != null) {
        final DMNDocumentationFactory proxiedInstance = proxyHelper.getInstance(this);
        final DMNDocumentation retVal = proxiedInstance.create(diagram);
        return retVal;
      } else {
        return super.create(diagram);
      }
    }

    @Override protected List getDrds(Diagram diagram) {
      if (proxyHelper != null) {
        final DMNDocumentationFactory proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = DMNDocumentationFactory_getDrds_Diagram(proxiedInstance, diagram);
        return retVal;
      } else {
        return super.getDrds(diagram);
      }
    }

    @Override protected String getNamespace(Diagram diagram) {
      if (proxyHelper != null) {
        final DMNDocumentationFactory proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = DMNDocumentationFactory_getNamespace_Diagram(proxiedInstance, diagram);
        return retVal;
      } else {
        return super.getNamespace(diagram);
      }
    }

    @Override protected boolean hasGraphNodes(Diagram diagram) {
      if (proxyHelper != null) {
        final DMNDocumentationFactory proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = DMNDocumentationFactory_hasGraphNodes_Diagram(proxiedInstance, diagram);
        return retVal;
      } else {
        return super.hasGraphNodes(diagram);
      }
    }

    @Override protected String getDiagramImage() {
      if (proxyHelper != null) {
        final DMNDocumentationFactory proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = DMNDocumentationFactory_getDiagramImage(proxiedInstance);
        return retVal;
      } else {
        return super.getDiagramImage();
      }
    }

    @Override protected List getDataTypes(Diagram diagram) {
      if (proxyHelper != null) {
        final DMNDocumentationFactory proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = DMNDocumentationFactory_getDataTypes_Diagram(proxiedInstance, diagram);
        return retVal;
      } else {
        return super.getDataTypes(diagram);
      }
    }

    @Override protected String getCurrentDate() {
      if (proxyHelper != null) {
        final DMNDocumentationFactory proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = DMNDocumentationFactory_getCurrentDate(proxiedInstance);
        return retVal;
      } else {
        return super.getCurrentDate();
      }
    }

    @Override protected String getDiagramName(Diagram diagram) {
      if (proxyHelper != null) {
        final DMNDocumentationFactory proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = DMNDocumentationFactory_getDiagramName_Diagram(proxiedInstance, diagram);
        return retVal;
      } else {
        return super.getDiagramName(diagram);
      }
    }

    @Override protected String getDiagramDescription(Diagram diagram) {
      if (proxyHelper != null) {
        final DMNDocumentationFactory proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = DMNDocumentationFactory_getDiagramDescription_Diagram(proxiedInstance, diagram);
        return retVal;
      } else {
        return super.getDiagramDescription(diagram);
      }
    }

    @Override protected DMNDocumentationI18n getDocumentationI18n() {
      if (proxyHelper != null) {
        final DMNDocumentationFactory proxiedInstance = proxyHelper.getInstance(this);
        final DMNDocumentationI18n retVal = DMNDocumentationFactory_getDocumentationI18n(proxiedInstance);
        return retVal;
      } else {
        return super.getDocumentationI18n();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNDocumentationFactory proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_w_k_c_c_e_d_c_DMNDocumentationFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDocumentationFactory.class, "Type_factory__o_k_w_c_d_w_k_c_c_e_d_c_DMNDocumentationFactory__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDocumentationFactory.class, org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory.class, Object.class });
  }

  public DMNDocumentationFactory createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_1 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final CanvasFileExport _canvasFileExport_0 = (CanvasFileExport) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_u_CanvasFileExport__quals__j_e_i_Any_j_e_i_Default");
    final DMNDocumentationDRDsFactory _drdsFactory_2 = (DMNDocumentationDRDsFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_d_c_DMNDocumentationDRDsFactory__quals__j_e_i_Any_j_e_i_Default");
    final SessionInfo _sessionInfo_3 = (SessionInfo) contextManager.getInstance("Producer_factory__o_u_r_SessionInfo__quals__j_e_i_Any_j_e_i_Default");
    final DMNGraphUtils _graphUtils_4 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final DMNDocumentationFactory instance = new DMNDocumentationFactory(_canvasFileExport_0, _translationService_1, _drdsFactory_2, _sessionInfo_3, _graphUtils_4);
    registerDependentScopedReference(instance, _translationService_1);
    registerDependentScopedReference(instance, _drdsFactory_2);
    registerDependentScopedReference(instance, _graphUtils_4);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_w_k_c_c_e_d_c_DMNDocumentationFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.webapp.kogito.common.client.editors.documentation.common.DMNDocumentationFactory an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.webapp.kogito.common.client.editors.documentation.common.DMNDocumentationFactory ([org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport, org.jboss.errai.ui.client.local.spi.TranslationService, org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationDRDsFactory, org.uberfire.rpc.SessionInfo, org.kie.workbench.common.dmn.client.graph.DMNGraphUtils])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNDocumentationFactory> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static boolean DMNDocumentationFactory_hasGraphNodes_Diagram(org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory instance, Diagram a0) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory::hasGraphNodes(Lorg/kie/workbench/common/stunner/core/diagram/Diagram;)(a0);
  }-*/;

  public native static List DMNDocumentationFactory_getDrds_Diagram(org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory instance, Diagram a0) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory::getDrds(Lorg/kie/workbench/common/stunner/core/diagram/Diagram;)(a0);
  }-*/;

  public native static String DMNDocumentationFactory_getDiagramName_Diagram(org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory instance, Diagram a0) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory::getDiagramName(Lorg/kie/workbench/common/stunner/core/diagram/Diagram;)(a0);
  }-*/;

  public native static DMNDocumentationI18n DMNDocumentationFactory_getDocumentationI18n(org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory::getDocumentationI18n()();
  }-*/;

  public native static String DMNDocumentationFactory_getNamespace_Diagram(org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory instance, Diagram a0) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory::getNamespace(Lorg/kie/workbench/common/stunner/core/diagram/Diagram;)(a0);
  }-*/;

  public native static String DMNDocumentationFactory_getCurrentDate(org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory::getCurrentDate()();
  }-*/;

  public native static String DMNDocumentationFactory_getCurrentUserName(DMNDocumentationFactory instance) /*-{
    return instance.@org.kie.workbench.common.dmn.webapp.kogito.common.client.editors.documentation.common.DMNDocumentationFactory::getCurrentUserName()();
  }-*/;

  public native static List DMNDocumentationFactory_getDataTypes_Diagram(org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory instance, Diagram a0) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory::getDataTypes(Lorg/kie/workbench/common/stunner/core/diagram/Diagram;)(a0);
  }-*/;

  public native static String DMNDocumentationFactory_getDiagramDescription_Diagram(org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory instance, Diagram a0) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory::getDiagramDescription(Lorg/kie/workbench/common/stunner/core/diagram/Diagram;)(a0);
  }-*/;

  public native static String DMNDocumentationFactory_getDiagramImage(org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory::getDiagramImage()();
  }-*/;
}