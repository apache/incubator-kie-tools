package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.ext.editor.commons.client.file.exports.FileExportProducer;
import org.uberfire.ext.editor.commons.client.file.exports.ImageFileExport;
import org.uberfire.ext.editor.commons.client.file.exports.PdfFileExport;
import org.uberfire.ext.editor.commons.client.file.exports.TextFileExport;
import org.uberfire.ext.editor.commons.client.file.exports.jso.FileExportScriptInjector;
import org.uberfire.ext.editor.commons.client.file.exports.svg.SvgFileExport;

public class Type_factory__o_u_e_e_c_c_f_e_FileExportProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<FileExportProducer> { private class Type_factory__o_u_e_e_c_c_f_e_FileExportProducer__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends FileExportProducer implements Proxy<FileExportProducer> {
    private final ProxyHelper<FileExportProducer> proxyHelper = new ProxyHelperImpl<FileExportProducer>("Type_factory__o_u_e_e_c_c_f_e_FileExportProducer__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final FileExportProducer instance) {

    }

    public FileExportProducer asBeanType() {
      return this;
    }

    public void setInstance(final FileExportProducer instance) {
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

    @Override public void init() {
      if (proxyHelper != null) {
        final FileExportProducer proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public TextFileExport forText() {
      if (proxyHelper != null) {
        final FileExportProducer proxiedInstance = proxyHelper.getInstance(this);
        final TextFileExport retVal = proxiedInstance.forText();
        return retVal;
      } else {
        return super.forText();
      }
    }

    @Override public PdfFileExport forPDF() {
      if (proxyHelper != null) {
        final FileExportProducer proxiedInstance = proxyHelper.getInstance(this);
        final PdfFileExport retVal = proxiedInstance.forPDF();
        return retVal;
      } else {
        return super.forPDF();
      }
    }

    @Override public ImageFileExport forImage() {
      if (proxyHelper != null) {
        final FileExportProducer proxiedInstance = proxyHelper.getInstance(this);
        final ImageFileExport retVal = proxiedInstance.forImage();
        return retVal;
      } else {
        return super.forImage();
      }
    }

    @Override public SvgFileExport forSvg() {
      if (proxyHelper != null) {
        final FileExportProducer proxiedInstance = proxyHelper.getInstance(this);
        final SvgFileExport retVal = proxiedInstance.forSvg();
        return retVal;
      } else {
        return super.forSvg();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final FileExportProducer proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_e_c_c_f_e_FileExportProducer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FileExportProducer.class, "Type_factory__o_u_e_e_c_c_f_e_FileExportProducer__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FileExportProducer.class, Object.class });
  }

  public FileExportProducer createInstance(final ContextManager contextManager) {
    final FileExportScriptInjector _fsScriptInjector_0 = (FileExportScriptInjector) contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_e_j_FileExportScriptInjector__quals__j_e_i_Any_j_e_i_Default");
    final FileExportProducer instance = new FileExportProducer(_fsScriptInjector_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final FileExportProducer instance) {
    instance.init();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<FileExportProducer> proxyImpl = new Type_factory__o_u_e_e_c_c_f_e_FileExportProducer__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}