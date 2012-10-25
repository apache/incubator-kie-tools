<%
  final String queryString = request.getQueryString();
  final String redirectURL = "org.uberfire.UberfireShowcase/Uberfire.html" + (queryString == null ? "" : "?" + queryString);
  response.sendRedirect(redirectURL);
%>
