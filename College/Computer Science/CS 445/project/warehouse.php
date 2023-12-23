<?php

set_time_limit(10*60);

include dirname(__FILE__) . '/common.php';

// get all tags and do counting
$result = db_list('SELECT Tags FROM media ', NULL, 'Tags');

$tags = array();
foreach($result as $i => $new_tags)
{
	$new_tags = explode(' ', $new_tags);
	foreach($new_tags as $j => $tag)
	{
		if(in_array($tag, $tags))
			continue;
			
		$tags[] = $tag;
		$tag_results = db_assoc('SELECT count(*) FROM media WHERE LEFT(Tags, ?)=? OR LOCATE(?, Tags) > 0', array(
			strlen($tag),
			$tag,
			$tag,
		));
		$idf = log((1 + count($result)) / $tag_results[0]['count(*)']);
		
		// check if tag is in remote datawarehouse
		switch_db(setting('wh_connect'));
		$tag_results = db_assoc('SELECT * FROM wh_tags WHERE Tag=?', array($tag));
		if(count($tag_results))
		{
			// update idf
			db_insert('UPDATE wh_tags SET Relevance=? WHERE id=?', array($idf, $tag_results[0]['id']));
		}
		else
		{
			// insert tag and idf
			db_insert('INSERT INTO wh_tags (Tag, Relevance) VALUES (?, ?)', array($tag, $idf));
		}
		
		switch_db(setting('db_connect'));
	}
}
