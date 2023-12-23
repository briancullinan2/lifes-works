<?php

header('Content-Type: text/plain');

include dirname(__FILE__) . '/common.php';


/**

start from a page
read links
store media and descriptions
store other html links in the pages to be scanned
rank media based on response and hits for keywords in that media


*/

$url = 'http://www.reddit.com/r/dubstep/top/';
//$url = 'http://www.youtube.com/watch?v=vh53AJfmaLo';
//$url = 'http://theinternettoday.net/music/new-ep-from-skrillex-scary-monsters-nice-sprites/';

//$urls = db_assoc('SELECT * FROM media WHERE Scanned < ? AND LEFT(MIME, 5) = "text/" ORDER BY id ASC LIMIT 1', array(date('Y-m-d h:i:s', mktime(date("H"), date("i") - 1, date("s"), date("n"), date("j"), date("Y")))));
//if(count($urls) > 0)
//	$url = $urls[0]['URL'];

// check to make sure we have permission to access the page
$config = scan_url($url);
