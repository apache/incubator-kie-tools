package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.export.LienzoCanvasExport;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.export.CanvasExport;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.uberfire.ext.editor.commons.client.file.exports.FileExport;
import org.uberfire.ext.editor.commons.client.file.exports.ImageDataUriContent;
import org.uberfire.ext.editor.commons.client.file.exports.ImageFileExport;
import org.uberfire.ext.editor.commons.client.file.exports.PdfDocument;
import org.uberfire.ext.editor.commons.client.file.exports.PdfFileExport;
import org.uberfire.ext.editor.commons.client.file.exports.svg.SvgFileExport;
import org.uberfire.ext.editor.commons.file.exports.FileExportsPreferences;
import org.uberfire.ext.editor.commons.file.exports.FileExportsPreferencesBeanGeneratedImpl;

public class Type_factory__o_k_w_c_s_c_c_c_u_CanvasFileExport__quals__j_e_i_Any_j_e_i_Default extends Factory<CanvasFileExport> { private class Type_factory__o_k_w_c_s_c_c_c_u_CanvasFileExport__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends CanvasFileExport implements Proxy<CanvasFileExport> {
    private final ProxyHelper<CanvasFileExport> proxyHelper = new ProxyHelperImpl<CanvasFileExport>("Type_factory__o_k_w_c_s_c_c_c_u_CanvasFileExport__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final CanvasFileExport instance) {

    }

    public CanvasFileExport asBeanType() {
      return this;
    }

    public void setInstance(final CanvasFileExport instance) {
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

    @Override public void exportToSvg(AbstractCanvasHandler canvasHandler, String fileName) {
      if (proxyHelper != null) {
        final CanvasFileExport proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.exportToSvg(canvasHandler, fileName);
      } else {
        super.exportToSvg(canvasHandler, fileName);
      }
    }

    @Override public String exportToSvg(AbstractCanvasHandler canvasHandler) {
      if (proxyHelper != null) {
        final CanvasFileExport proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.exportToSvg(canvasHandler);
        return retVal;
      } else {
        return super.exportToSvg(canvasHandler);
      }
    }

    @Override public String exportToPng(AbstractCanvasHandler canvasHandler) {
      if (proxyHelper != null) {
        final CanvasFileExport proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.exportToPng(canvasHandler);
        return retVal;
      } else {
        return super.exportToPng(canvasHandler);
      }
    }

    @Override public void exportToPng(AbstractCanvasHandler canvasHandler, String fileName) {
      if (proxyHelper != null) {
        final CanvasFileExport proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.exportToPng(canvasHandler, fileName);
      } else {
        super.exportToPng(canvasHandler, fileName);
      }
    }

    @Override public void exportToJpg(AbstractCanvasHandler canvasHandler, String fileName) {
      if (proxyHelper != null) {
        final CanvasFileExport proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.exportToJpg(canvasHandler, fileName);
      } else {
        super.exportToJpg(canvasHandler, fileName);
      }
    }

    @Override public void exportToPdf(AbstractCanvasHandler canvasHandler, String fileName) {
      if (proxyHelper != null) {
        final CanvasFileExport proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.exportToPdf(canvasHandler, fileName);
      } else {
        super.exportToPdf(canvasHandler, fileName);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final CanvasFileExport proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_c_u_CanvasFileExport__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CanvasFileExport.class, "Type_factory__o_k_w_c_s_c_c_c_u_CanvasFileExport__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CanvasFileExport.class, Object.class });
  }

  public CanvasFileExport createInstance(final ContextManager contextManager) {
    final SvgFileExport _svgFileExport_4 = (SvgFileExport) contextManager.getInstance("Producer_factory__o_u_e_e_c_c_f_e_s_SvgFileExport__quals__j_e_i_Any_j_e_i_Default");
    final CanvasExport<AbstractCanvasHandler> _canvasExport_0 = (LienzoCanvasExport) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_e_LienzoCanvasExport__quals__j_e_i_Any_j_e_i_Default");
    final FileExport<PdfDocument> _pdfFileExport_2 = (PdfFileExport) contextManager.getInstance("Producer_factory__o_u_e_e_c_c_f_e_PdfFileExport__quals__j_e_i_Any_j_e_i_Default");
    final Event<CanvasClearSelectionEvent> _clearSelectionEvent_5 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasClearSelectionEvent.class }, new Annotation[] { });
    final FileExport<ImageDataUriContent> _imageFileExport_1 = (ImageFileExport) contextManager.getInstance("Producer_factory__o_u_e_e_c_c_f_e_ImageFileExport__quals__j_e_i_Any_j_e_i_Default");
    final FileExportsPreferences _preferences_3 = (FileExportsPreferencesBeanGeneratedImpl) contextManager.getInstance("Type_factory__o_u_e_e_c_f_e_FileExportsPreferencesBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default");
    final CanvasFileExport instance = new CanvasFileExport(_canvasExport_0, _imageFileExport_1, _pdfFileExport_2, _preferences_3, _svgFileExport_4, _clearSelectionEvent_5);
    registerDependentScopedReference(instance, _svgFileExport_4);
    registerDependentScopedReference(instance, _pdfFileExport_2);
    registerDependentScopedReference(instance, _clearSelectionEvent_5);
    registerDependentScopedReference(instance, _imageFileExport_1);
    registerDependentScopedReference(instance, _preferences_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<CanvasFileExport> proxyImpl = new Type_factory__o_k_w_c_s_c_c_c_u_CanvasFileExport__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}