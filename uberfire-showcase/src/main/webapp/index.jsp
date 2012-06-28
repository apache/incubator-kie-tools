<%
	String queryString = request.getQueryString();
    String redirectURL = "org.drools.guvnor.GuvnorNGShowcase/Guvnor.jsp?"+(queryString==null?"":queryString);
    response.sendRedirect(redirectURL);
%>
