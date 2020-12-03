package org.guvnor.messageconsole.client.console;

public class HyperLinkCell_HyperLinkTemplateImpl implements org.guvnor.messageconsole.client.console.HyperLinkCell.HyperLinkTemplate {
  
  public com.google.gwt.safehtml.shared.SafeHtml hyperLink(com.google.gwt.safehtml.shared.SafeHtml arg0,java.lang.String arg1) {
    StringBuilder sb = new java.lang.StringBuilder();
    sb.append("<a title=\"");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg1));
    sb.append("\" href=\"#\">");
    sb.append(arg0.asString());
    sb.append("</a>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}
}
