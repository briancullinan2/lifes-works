<?php


/**

for embedding content
use media id to determine source and embedd using that handler

*/


function scan_url($url, $depth = 3, $_current_depth = 0)
{
	$page = fetch($url, array(), array('header_callback' => 'handle_default_read'));
	
	if($page['status'] == 200)
	{
		
		// verify the page is valid
		if(substr($page['headers']['content-type'], 0, 5) == 'text/')
		{
			// update page title
			if(preg_match('/<title[^>]*?>(.*?)<\/title>/i', $page['content'], $matches) > 0)
				$result = db_query('UPDATE media SET Name=?, MIME=? WHERE URL=?', array($matches[1], $page['headers']['content-type'], $url));
			
			// get all the hyperlinks in a page
			$links = get_links($page['content'], $url);
			
			foreach($links as $i => $link)
			{
				// remove the fragment from the link
				if(strpos($link, '#') !== false)
					$link = substr($link, 0, strpos($link, '#'));
				
				// add to pages to scan database
				$pages = db_assoc('SELECT * FROM media WHERE URL=? AND Source=?', array($link, $url));
				if(count($pages) > 0)
					continue;
				
				$config = array(
					'URL' => $link,
					'Source' => $url,
				);
				
				$id = db_insert('INSERT INTO media ' . sql_insert($config), array_values($config));
				
				// scan url recursively
				if($_current_depth < $depth)
					scan_url($link, $depth, $_current_depth + 1);
			}
		}
		elseif(robots_allowed($url))
		{
			handle_url($url);
		}
	}
	
	// update url with time in pages
	db_query('UPDATE media SET Added=? WHERE URL=?', array(date('Y-m-d h:i:s'), $url));
}


function handle_url($url, $source = '', $popularity = 1.0)
{
	$sources = db_assoc('SELECT *,COUNT(*) FROM media WHERE URL=? GROUP BY URL LIMIT 1', array($url));
	if(!is_string($source))
	{
		if(isset($sources[0]['Source']))
			$source = $sources[0]['Source'];
			
		if(isset($sources[0]['Popularity']))
			$popularity = floatval($popularity + $sources[0]['COUNT(*)'] + $sources[0]['Popularity']);
	}
	
	call_user_func_array(get_handler($url), array($url, $source, $popularity));
}


function get_handler($url)
{
	// split up the url in to pieces, and call the appropriate handler
	$hostname = generic_validate_hostname(array('url' => $url), 'url');
	$pieces = preg_split('/:\/\/|\./i', $hostname);

	foreach($pieces as $i => $piece)
	{
		if(function_exists('handle_' . implode('_', $pieces)))
			return 'handle_' . implode('_', $pieces);
		else
			array_shift($pieces);
	}
	return 'handle_';
}


function handle_($url, $source, $popularity)
{
	// default handler checks for file type in the path
	$path = generic_validate_urlpath(array('url' => $url), 'url');

	$type = mime_type($path);

	if( $type == 'audio' )
	{
		$config = array(
			'URL' => $url,
			'MIME' => mime($path),
			'Source' => $source,
			'Added' => date('Y-m-d H:i:s'),
			'Popularity' => $popularity,
		);
		
		$pages = db_assoc('SELECT * FROM media WHERE URL=?', array($url));
		if(count($pages) > 0)
			$id = db_insert('UPDATE media SET ' . sql_update($config) . ' WHERE id=' . $pages[0]['id'], array_values($config));
		
		$id = db_insert('INSERT INTO media ' . sql_insert($config), array_values($config));
		
		return;
	}
	
	// get the page for further analyzation
	$page = fetch($url, array(), array('header_callback' => 'handle_default_read'));

	if($page['status'] == 200)
	{
		// check the header for a content type
		if(substr($page['headers']['content-type'], 0, 6) == 'audio/' || substr($page['headers']['content-type'], 0, 6) == 'video/' || $page['headers']['content-type'] == 'application/x-shockwave-flash')
		{
			$config = array(
				'URL' => $url,
				'MIME' => $page['headers']['content-type'],
				'Source' => $source,
				'Added' => date('Y-m-d H:i:s'),
				'Popularity' => $popularity,
			);
			
			$pages = db_assoc('SELECT * FROM media WHERE URL=?', array($url));
			if(count($pages) > 0)
				$id = db_insert('UPDATE media SET ' . sql_update($config) . ' WHERE id=' . $pages[0]['id'], array_values($config));
			
			$id = db_insert('INSERT INTO media ' . sql_insert($config), array_values($config));
			
			return;
		}
		elseif(substr($page['headers']['content-type'], 0, 5) == 'text/')
		{
			// update page title
			if(preg_match('/<title[^>]*?>(.*?)<\/title>/i', $page['content'], $matches) > 0)
				$result = db_query('UPDATE media SET Name=? WHERE URL=?', array($matches[1], $url));
			
			// check for embeds in the page
			$embeds = get_embeds($page['content']);
			
			if(count($embeds) > 0)
			{
				foreach($embeds as $config)
				{
					if(get_handler($config['URL']) == 'handle_')
					{
						$config['Source'] = $url;
						$config['Added'] = date('Y-m-d H:i:s');
						$config['Popularity'] = $popularity;
						
						$pages = db_assoc('SELECT * FROM media WHERE URL=?', array($config['URL']));
						if(count($pages) > 0)
							$id = db_insert('UPDATE media SET ' . sql_update($config) . ' WHERE id=' . $pages[0]['id'], array_values($config));
						
						$id = db_insert('INSERT INTO media ' . sql_insert($config), array_values($config));
					}
					else
						handle_url($config['URL'], $source);
				}
			}
		}
	}
}

function handle_default_read($ch, $input)
{
	// if it is a non text file then stop recieving
	if(preg_match('/content-type: ?(audio|video|application)/i', $input) != 0)
		curl_setopt($ch, CURLOPT_NOBODY, true);

	return strlen($input);
}

function handle_soundcloud_com($url, $source, $popularity)
{
	$config = array();
	
	// get query string containing video id
	$query = generic_validate_query_str(array('query' => $url), 'query');
	$path = generic_validate_urlpath(array('path' => $url), 'path');
	
	$request = extract_query($query);
	if(preg_match('/^\/?([^\/]+\/[^\/]+)(\/download)?\/?$/i', $path, $matches) && substr($matches[1], 0, 5) != 'tags/')
		$request['url'] = 'http://soundcloud.com/' . $matches[1];
		
	if(isset($request['url']))
	{
		$page = fetch($request['url']);
		if($page['status'] != 200)
			return;
		
		$title_count = preg_match('/<title[^>]*?>(.*?)<\/title>/i', $page['content'], $titles);
		$tags_count = preg_match_all('/<a href="\/tags\/(.*?)"/i', $page['content'], $tags);
		
		if(preg_match('/^log in/i', $titles[1]) > 0)
			return;
		
		$config['Name'] = $titles[1];
		$config['URL'] = 'http://player.soundcloud.com/player.swf?url=' . $request['url'];
		$config['MIME'] = 'application/x-shockwave-flash';
		$config['Source'] = $source;
		$config['Added'] = date('Y-m-d H:i:s');
		$config['Embed'] = '<embed src="' . $config['URL'] . '" type="application/x-shockwave-flash" allowscriptaccess="always" width="480" height="81"></embed>';
		$config['Tags'] = implode(' ', format_tags($tags[1]));
		$config['Popularity'] = $popularity;
		
		$media = db_assoc('SELECT * FROM media WHERE URL=?', array($config['URL']));
		if(count($media) > 0)
			$id = db_insert('UPDATE media SET ' . sql_update($config) . ' WHERE id=' . $media[0]['id'], array_values($config));
			
		// add to media database
		$id = db_insert('INSERT INTO media ' . sql_insert($config), array_values($config));
	}
	else
		handle_($url, $source);
}


// this allows for a shortcut so the page doesn't has to be loaded looking for the embedd and description
function handle_youtube_com($url, $source, $popularity)
{
	$config = array();
	
	// get query string containing video id
	$query = generic_validate_query_str(array('query' => $url), 'query');
	$path = generic_validate_urlpath(array('path' => $url), 'path');
	
	$request = extract_query($query);
	if(strpos($path, '/v/') !== false)
		$request['v'] = substr($path, strpos($path, '/v/') + 3);
	
	if(isset($request['v']))
	{
		$page = fetch('http://gdata.youtube.com/feeds/api/videos/' . $request['v']);
		if($page['status'] != 200)
			return;

		$title_count = preg_match('/<title[^>]*?>(.*?)<\/title>/i', $page['content'], $titles);
		$tags_count = preg_match_all('/<category.*?term=\'(.*?)\'/i', $page['content'], $tags);
		$rating_count = preg_match('/<gd:rating.*?average=\'(.*?)\'.*?max=\'(.*?)\'/i', $page['content'], $rating);
		$config['Name'] = $titles[1];
		$config['URL'] = 'http://www.youtube.com/v/' . $request['v'];
		$config['MIME'] = 'application/x-shockwave-flash';
		$config['Source'] = $source;
		$config['Added'] = date('Y-m-d H:i:s');
		$config['Embed'] = '<embed src="' . $config['URL'] . '" type="application/x-shockwave-flash" allowscriptaccess="always" allowfullscreen="true" width="480" height="385"></embed>';
		$config['Tags'] = implode(' ', format_tags($tags[1]));
		$config['Popularity'] = $popularity + (floatval($rating[2]) / floatval($rating[1]));
		
		$media = db_assoc('SELECT * FROM media WHERE URL=?', array($config['URL']));
		if(count($media) > 0)
			$id = db_insert('UPDATE media SET ' . sql_update($config) . ' WHERE id=' . $media[0]['id'], array_values($config));
			
		// add to media database
		$id = db_insert('INSERT INTO media ' . sql_insert($config), array_values($config));
	}
	else
		handle_($url, $source);
}



