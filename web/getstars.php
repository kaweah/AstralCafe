<html>
<head>
<title>Get Stars</title>
</head>
<body>
<?php
$filename = 'stars3k.txt';
$starfile = fopen($filename,'r');
if ($starfile === NULL)
{
	echo "Not able to open file <b>{$filename}</b>"; 
	exit;
}
else
{
	echo "Opened file <b>{$filename}</b>"; 	
	
    $contents = fread ($starfile,filesize ($filename));

    fclose ($starfile);
    ?>

    <font color="blue" face="arial" size="4">Complete File Contents</font>
    <hr>

    <?
    # echo $contents;
    ?>

    <br><br>
    <font color="blue" face="arial" size="4">Split File Contents</font>
    <hr>

    <?
    $delimiter = "\t";

    $records = explode("\r\n", $contents);
    $rcount = sizeof($records) - 1; // ignore final linefeed.

    echo "{$rcount} records.<br/>";

    for($i=0; $i<$rcount; $i++) {
        $record = trim($records[$i]);
        $splitcontents = explode($delimiter, $record);

        $counter = "";

        foreach ( $splitcontents as $color )
        {
            $counter = $counter+1;
            echo "<b>Split $counter: </b> $color<br>";
        }
    }
}
?>
</body>
</html>