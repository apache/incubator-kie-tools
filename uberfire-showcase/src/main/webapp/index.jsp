<%
	String queryString = request.getQueryString();
    String redirectURL = "org.uberfire.UberfireShowcase/Uberfire.jsp?"+(queryString==null?"":queryString);
    response.sendRedirect(redirectURL);
%>
