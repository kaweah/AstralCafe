<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.io.IOException" %>

<html>
<head>
<title>Astral Applet</title>
</head>

<%
String param;
String imageName;
boolean preload;

// JDBC database connection
Connection con = null;

if (request.getParameter("preloadStars") != null)
{
	preload = true;
}
else preload = false;

try 
{  
	con = com.kaweah.utilities.dbUtils.getJDBCConnection();
} 
catch (SQLException sqle) 
{
	out.println("<p>Error opening JDBC:</p>    <b> " + sqle + "</b>");
} 
catch (ClassNotFoundException cnfe) 
{
	out.println("<p>Error opening JDBC:</p>    <b>" + cnfe + "</b>");
}
%>

<body <% 
if ((param = request.getParameter("colorMode")) != null)
{
	if (param.equals("3"))
	{
		out.print("bgcolor=\"white\"");
		imageName = "AstralCafe.jpg";
	}
	else
	{
		out.print("bgcolor=\"#000000\" text=\"#0099ff\" link=\"#00FFFF\" vlink=\"#0066ff\" alink=\"#0033ff\"");
		imageName = "AstralCafe-dark.jpg";
	}
}
else
{
	out.print("bgcolor=\"#000000\" text=\"#0099ff\" link=\"#00FFFF\" vlink=\"#0066ff\" alink=\"#0033ff\"");
	imageName = "AstralCafe-dark.jpg";
}
%>>

<table width="364" border="0">
  <tr>
    <td><img src="<%= imageName %>"></td>
    <td><h1 align="left">Astral Applet</h1></td>
  </tr>
</table>
<p><em>This applet 
  <% if (preload) out.print("<b>preloads</b> primary star data"); else out.print("<b>receives sky projection data</b>"); %>
  and constellation data from Java servlets running on the Kaweah.com web server.</em></p>
<h4><em>Please allow a moment for the applet to be loaded and to acquire data.</em></h4>
<table border="1">
  <tr>
    <td>
    	<applet code="com.kaweah.astralcafe.AstralApplet.class"
			archive="AstralApplet.jar, download/log4j-1.2.8.jar"
			width=<% if ((param = request.getParameter("width")) != null) out.print(param); else out.print("550"); %>
			height=<% if ((param = request.getParameter("height")) != null) out.print(param); else out.print("400"); %>>
			
			<h3>Java is either unsupported or disabled on your web browser.</h3>
			<h3>This applet requires that Java be supported and enabled to operate.</h3>
			<param name=serverURL value="http://www.kaweah.com">
			<param name=servletPath value="/servlet/StarServer">
			<param name=constelServletPath value="/servlet/ConstellationServer">
<%--			<param name=constelData value="<%
try
{
	com.kaweah.utilities.ConstellationUtils.writeConstelData(con, response.getOutputStream());
}
catch (SQLException sqle)
{
	out.print(sqle.getMessage());
}
catch (IOException ioe)
{
	out.print(ioe.getMessage());
}
%>">
--%>
<%
if (preload)
{
	out.println("			<param name=preloadStars value=\"preloadStars\">");
}
if ((param = request.getParameter("dashBoard")) != null)
{
	out.println("			<param name=dashBoard value=\"" + param + "\">");
}
if ((param = request.getParameter("ra")) != null)
{
	out.println("			<param name=ra value=\"" + param + "\">");
}
if ((param = request.getParameter("dec")) != null)
{
	out.println("			<param name=dec value=\"" + param + "\">");
}
if ((param = request.getParameter("viewBreadthAngle")) != null)
{
	out.println("			<param name=viewBreadthAngle value=\"" + param + "\">");
}
if ((param = request.getParameter("colorMode")) != null)
{
	out.println("			<param name=colorMode value=\"" + param + "\">");
}
if ((param = request.getParameter("brightness")) != null)
{
	out.println("			<param name=brightness value=\"" + param + "\">");
}
%>
		</applet>
	</td>
  </tr>
</table>
<p><em>The objective of this project is to enable people to submit their own
	constellation patterns, to be stored on a database by means of another
	servlet.</em></p>
<h4>Return to the <a href="./">Astral Caf&eacute;</a> page.</h4>
<p>&nbsp;</p>
<h5>&nbsp; </h5>
</body>
</html>
