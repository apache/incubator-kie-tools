<%
	String queryString = request.getQueryString();
    String redirectURL = "org.drools.guvnor.jBPMShowcase/jBPM.html?"+(queryString==null?"":queryString);
    response.sendRedirect(redirectURL);
%>
