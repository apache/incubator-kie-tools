<%
	String queryString = request.getQueryString();
    String redirectURL = "org.drools.guvnor.UberfireShowcase/Uberfire.jsp?"+(queryString==null?"":queryString);
    response.sendRedirect(redirectURL);
%>
