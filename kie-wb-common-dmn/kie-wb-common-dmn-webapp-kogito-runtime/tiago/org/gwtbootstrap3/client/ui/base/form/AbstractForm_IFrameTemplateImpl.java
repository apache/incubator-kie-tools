package org.gwtbootstrap3.client.ui.base.form;

public class AbstractForm_IFrameTemplateImpl implements org.gwtbootstrap3.client.ui.base.form.AbstractForm.IFrameTemplate {
  
  public com.google.gwt.safehtml.shared.SafeHtml get(java.lang.String arg0) {
    StringBuilder sb = new java.lang.StringBuilder();
    sb.append("<iframe src=\"javascript:''\" name='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
    sb.append("' tabindex='-1' title='Form submit helper frame'style='position:absolute;width:0;height:0;border:0'>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}
}
