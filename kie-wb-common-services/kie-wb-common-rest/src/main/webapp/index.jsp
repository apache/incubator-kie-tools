<%
  final String queryString = request.getQueryString();
  final String redirectURL = "org.drools.workbench.DroolsWorkbench/DroolsWorkbench.html" + (queryString == null ? "" : "?" + queryString);
  response.sendRedirect(redirectURL);
%>
