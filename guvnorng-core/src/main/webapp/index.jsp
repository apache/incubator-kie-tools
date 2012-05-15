<%
	String queryString = request.getQueryString();
    String redirectURL = "org.drools.guvnor.GuvnorNGCore/Guvnor.jsp?"+(queryString==null?"":queryString);
    response.sendRedirect(redirectURL);
%>
