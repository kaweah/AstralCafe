<html>
<head>
<title>Astral Applet</title>
</head>

<?php
$preloadStars = $_GET["preloadStars"];
$preload = true;
$colorMode = $_GET["colorMode"];

if ($preloadStars != null)
{
	$preload = false;
}

// JDBC database connection
$con = null;
$bodyParamsString = '';

if ($colorMode != null)
{
	if ($colorMode == 3)
	{
		$bodyParamsString = "bgcolor=\"white\"";
		$imageName = "AstralCafe.jpg";
	}
	else
	{
		$bodyParamsString = "bgcolor=\"#000000\" text=\"#0099ff\" link=\"#00FFFF\" vlink=\"#0066ff\" alink=\"#0033ff\"";
		$imageName = "AstralCafe-dark.jpg";
	}
}
else
{
	$bodyParamsString = "bgcolor=\"#000000\" text=\"#0099ff\" link=\"#00FFFF\" vlink=\"#0066ff\" alink=\"#0033ff\"";
	$imageName = "AstralCafe-dark.jpg";
}

$width=550;
$height=400;
if (($param = $_GET["width"]) != null) $width = $param;
if (($param = $_GET["height"]) != null) $height = $param;


?>

<body <?= $bodyParamsString ?>>

<table width="364" border="0">
  <tr>
    <td><img src="AstralCafe.jpg"></td>
    <td><h1 align="left">Astral Applet</h1></td>
  </tr>
</table>
<p><em>This applet 

<?php
if ($preload)
    echo "<b>preloads</b> primary star data";
else
    echo "<b>receives sky projection data</b>";
?>

  and constellation data from Java servlets running on the Kaweah.com web server.</em></p>
<h4><em>Please allow a moment for the applet to be loaded and to acquire data.</em></h4>
<table border="1">
  <tr>
    <td>
    	<applet code="com.kaweah.astralcafe.applet.AstralApplet.class"
			archive="AstralApplet.jar"
			width="<?= $width ?>" height="<?= $height ?>">
			<h3>Java is either unsupported or disabled on your web browser.</h3>
			<h3>This applet requires that Java be supported and enabled to operate.</h3>
			<param name=serverURL value="http://www.kaweah.com">
			<param name=servletPath value="/software/astralcafe/stars3k.ser">
			<param name=constelServletPath value="/software/astralcafe/links.ser">
<?php
if ($preload)
{
	echo "			<param name=preloadStars value=\"preloadStars\">";
}
if (($param = $_GET["dashBoard"]) != null)
{
	echo "			<param name=dashBoard value=\"{$param}\">";
}
if (($param = $_GET["ra"]) != null)
{
	echo "			<param name=ra value=\"{$param}\">";
}
if (($param = $_GET["dec"]) != null)
{
	echo "			<param name=dec value=\"{$param}\">";
}
if (($param = $_GET["viewBreadthAngle"]) != null)
{
	echo "			<param name=viewBreadthAngle value=\"{$param}\">";
}
if (($param = $_GET["colorMode"]) != null)
{
	echo "			<param name=colorMode value=\"{$param}\">";
}
if (($param = $_GET["brightness"]) != null)
{
	echo "			<param name=brightness value=\"{$param}\">";
}
?>
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
