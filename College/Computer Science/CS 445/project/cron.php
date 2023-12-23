<?php

header('Content-Type: text/plain');

include dirname(__FILE__) . '/common.php';

// loop through saved links and extract media
$week_old = mktime(date('H'), date('i'), date('s'), date('n'), date('j') - 7, date('Y'));

$link = db_assoc('SELECT * FROM media WHERE Added < ? ORDER BY Added ASC LIMIT 1', array(date('Y-m-d H:i:s', $week_old)));

//$link[0]['URL'] = 'http://www.youtube.com/watch?v=Nuqt2lUq4ZY&feature=rec-LGOUT-real_rev-rn-1r-23-HM';

while(count($link) > 0)
{
	// call media handlers
	if(robots_allowed($link[0]['URL']))
	{
		handle_url($link[0]['URL']);
	}

//exit;
	// update date on link to current time
	$id = db_query('UPDATE media SET Added=? WHERE id=?', array(date('Y-m-d H:i:s'), $link[0]['id']));

	// get new link to process
	$link = db_assoc('SELECT * FROM media WHERE Added < ? ORDER BY Added ASC LIMIT 1', array(date('Y-m-d H:i:s', $week_old)));
	
	// exit if running longer then 10 minutes
	$secs_total = array_sum(explode(' ', microtime())) - $GLOBALS['tm_start'];
	if($secs_total > 10*60)
		break;
}