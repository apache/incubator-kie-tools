<%
  final String queryString = request.getQueryString();
  final String redirectURL = "${package}.${capitalizedRootArtifactId}Showcase/${capitalizedRootArtifactId}.html" + (queryString == null ? "" : "?" + queryString);
  response.sendRedirect(redirectURL);
%>
