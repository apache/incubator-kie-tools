<%
  final String queryString = request.getQueryString();
  final String redirectURL = "org.uberfire.UberfireShowcase/Uberfire.html?gwt.codesvr=127.0.0.1:9997";
  response.sendRedirect(redirectURL);
%>