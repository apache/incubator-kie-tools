<%
  final String queryString = request.getQueryString();
  final String redirectURL = "org.uberfire.UberThora/Uberthora.html" + (queryString == null ? "" : "?" + queryString);
  response.sendRedirect(redirectURL);
%>
