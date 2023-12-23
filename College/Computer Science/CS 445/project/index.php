<?php

include dirname(__FILE__) . '/common.php';

/**
 * Implementation of #setup_validate()
 * @return Zero by default, any number greater then zero is valid
 */
function validate_start($request)
{
	return generic_validate_numeric_zero($request, 'start');
}

/**
 * Implementation of #setup_validate()
 * @return 15 by default, accepts any positive number
 */
function validate_limit($request)
{
	return generic_validate_numeric_default($request, 'limit', 15);
}

if(isset($_GET['search']))
{
	include dirname(__FILE__) . '/search.php';
	$search = filter_search($_GET);

	// do select
	if(isset($_GET['updates_only']))
	{
		$results = db_assoc('SELECT *' . (isset($search['columns'])?(',' . $search['columns']):'') . 
			' FROM wh_media WHERE Added > ? ' . 
			(isset($search['where'])?(' AND (' . $search['where'] . ')'):'') . 
			' ORDER BY Added DESC'
		, array(date('Y-m-d H:i:s', intval($_GET['updates_only']))));
	}
	else
	{
		$results = db_assoc('SELECT *' . (isset($search['columns'])?(',' . $search['columns']):'') . 
			' FROM wh_media ' . 
			(isset($search['where'])?('WHERE (' . $search['where'] . ')'):'') . 
			' ORDER BY ' . (isset($search['where'])?$search['order']:'Added DESC') . ' LIMIT ' . validate_start($_REQUEST) . ',10');
		
		// get count from all of query except for limit and order
		$results_count = db_assoc('SELECT count(*) FROM wh_media ' . 
			(isset($search['where'])?('WHERE (' . $search['where'] . ')'):''));
		$count = $results_count[0]['count(*)'];
	}

	// get tags
	$tags = '';
	foreach($results as $i => $media)
	{
		$tags .= ' ' . $media['Tags'];
	}
	$tags = format_tags(explode(' ', $tags));
	foreach($tags as $tag)
	{
		$relevance = db_assoc('SELECT * FROM wh_tags WHERE Tag=? ORDER BY Relevance', array($tag));
		if(count($relevance))
		{
			$output_tags[$tag] = $relevance[0]['Relevance'];
		}
	}

	// sort tags
	arsort($output_tags, SORT_NUMERIC);
	
	// print out results
	if(!isset($_GET['updates_only']))
	{
		theme_header($count, $output_tags, isset($search['spelling'])?$search['spelling']:array());
	
		theme_results($results);
		
		theme_footer($count);
	}
	else
		theme_results($results);
}
elseif(isset($_GET['embed']))
{
	$results = db_assoc('SELECT * FROM wh_media WHERE Hash=?', array($_GET['embed']));
	
	if(count($results) > 0)
		theme_embed($results[0]);
	else
		theme_error('Media not found!');
}
elseif(isset($_GET['tags']))
{
	header('Content-Type: text/plain');	
	
	// get current word
	if(isset($_GET['pos']))
	{
		$current = substr($_GET['tags'], 0, $_GET['pos']);
		$current = substr($current, strrpos($current, ' '));
		if(substr($current, 0, 1) == ' ')
			$current = substr($current, 1);
	}

	$tag_results = db_assoc('SELECT * FROM tags WHERE LEFT(Tags, ?)=? OR LOCATE(?, Tags) > 0', array(
		strlen($current),
		$current,
		$current,
	));

	$output_tags = array();
	foreach($tag_results as $tags)
	{
		$tags = explode(' ', $tags['Tags']);
		foreach($tags as $i => $tag)
		{
			if(strtolower(substr($tag, 0, strlen($current))) == strtolower($current))
				$output_tags[] = strtolower($tag);
				
			if(count($output_tags) > 10)
				break;
		}
	}
	
	$output_tags = array_unique($output_tags);
	
	print '["' . implode('", "', $output_tags) . '"]';
}
else
{
	theme_header();
	
	theme_footer();
}



function theme_embed($result)
{
	if($result['Embed'] != '')
	{
		print $result['Embed'];
	}
	elseif($result['MIME'] == 'application/x-shockwave-flash')
	{
		?><embed src="<?php print $result['URL']; ?>" type="application/x-shockwave-flash" allowscriptaccess="always"></embed><?php
	}
	else
	{
		theme_error('Not Implemented!');
	}
}

function theme_error($error)
{
	?><span class="error"><?php print $error; ?></span><?php
}

function theme_header($count = 0, $tags = array(), $alteration = array())
{
	
	?>
	<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
	<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>Monolithic Search</title>
	<link rel="stylesheet" href="search.css" type="text/css"/>
	<link rel="stylesheet" href="theme/jquery.css" type="text/css"/>
	<script language="javascript" type="text/javascript" src="jquery.js"></script>
	<script language="javascript" type="text/javascript" src="jquery-ui.js"></script>
	<script language="javascript" type="text/javascript" src="jquery-caret.js"></script>
	<script language="javascript">
	
		var selected = null;
		var selected_prev = null;
	
		$(document).ready(function()
		{
			set_events('body');
			
			$('#search').autocomplete({
				source: [],
				search: function(event, ui) {
					$.get('<?php print generic_validate_urlpath(array('page' => $_SERVER['REQUEST_URI']), 'page'); ?>?tags=' + $('#search').val() + '&pos=' + $('#search').caret().start, function(data){
						$('#search').autocomplete('option', 'source', data);
					}, 'json');
				}
			}).blur(function() {
				if($(this).val() == '')
				{
					$(this).css('color', '#999');
					$(this).val('Type in a genre, artist, or any music related term');
					$(this).attr('help', true);
				}
			}).focus(function() {
				if($(this).attr('help') == 'true')
				{
					$(this).val('');
					$(this).css('color', '#000');
					$(this).attr('help', false);
				}
			}).keydown(function() {
				if($(this).attr('help'))
				{
					$(this).css('color', '#000');
				}
			}).blur();
			

		});
		
		
		function set_events(selector)
		{
			$(selector + ' .result').hover(
				function()
				{
					$(this).addClass("highlight");
				},
				function()
				{
					$(this).removeClass("highlight");
				}
			);
			
			$(selector + ' .result .link').click(function() {
				var id = $(this).attr('id').substring(5);
				$.get('<?php print generic_validate_urlpath(array('page' => $_SERVER['REQUEST_URI']), 'page'); ?>?embed=' + id, function(data){
					$('#result_' + id + ' .embed').html(data);
				});
				return false;
			});
			
			$(selector + ' .result').mousedown(function() {
				if(selected)
				{
					if(selected.attr('id') == $(this).attr('id'))
						return;
					$('.embed', selected).html("");
					if(selected_prev)
					{
						selected.insertAfter(selected_prev);
					}
					else
					{
						selected.insertAfter($('.updates'));
					}
				}
				$('.result').removeClass("selected");
				$('.result').removeClass('highlight');
				$(this).addClass("selected");
				selected = $(this);
				selected_prev = $(this).prev();
				$(this).insertBefore($('#updates'));
				$('.link', this).click();
			});
		}

		<?php
			
		// print out updater
		$url = generic_validate_urlpath(array('page' => $_SERVER['REQUEST_URI']), 'page') . '?' . (isset($_GET['search'])?('search=' . $_GET['search'] . '&'):'') . 'updates_only=';
		?>
		var d = new Date();
		var time_diff = Math.round(d.getTime() / 1000) - <?php print time(); ?>;
		var current_time = (Math.round(d.getTime() / 1000) - time_diff);
		
		var updater = function() {
			$.get('<?php print $url; ?>' + current_time, function(data){
				if(data != "")
				{
					d = new Date();
					current_time = (Math.round(d.getTime() / 1000) - time_diff);
					$('#updates').hide();
					$('#updates').html(data);
					$('#updates').show('blind', {}, 1000, function() {
						set_events('#updates');
						$('#updates .result').insertAfter('#updates');
					});
				}
			}, 'text');
			timer = setTimeout(updater, 5000);
		}
		
		<?php
		if(isset($_GET['search']))
		{
			?>
			var timer = setTimeout(updater, 5000);
			<?php
		}
		?>
		
	</script>
	</head>
	
	<body>
	<div id="body">
	<center>
	<a class="banner" href="http://www.monolithicsearch.com/"><img id="logo" src="logo.png" alt="Monolithic Search" /></a>
	<div id="search_form" style="margin-top:50px;"><form action="" method="get"><input type="text" name="search" id="search" size="80" value="<?php print isset($_GET['search'])?htmlspecialchars($_GET['search'], ENT_QUOTES):''; ?>" /><input type="submit" id="button" value="Search" /></form></div>
	<div style="text-align:left; margin-left:40px;">
	<?php	
	if($count)
	{
		$time = $secs_total = array_sum(explode(' ', microtime())) - $GLOBALS['tm_start'];
		?><span id="count">About <?php print number_format($count); ?> results (<?php print round($time, 2); ?> seconds)</span><br /><br /><?php
	}
	elseif(isset($_GET['search']))
	{
		?><br /><br />Your search - <strong><?php print htmlspecialchars($_GET['search'], ENT_QUOTES); ?></strong> - did not match any documents.<br /><br />
		Suggestions:<br />
		<ul>
			<li>Make sure all words are spelled correctly.</li>
			<li>Try different keywords.</li>
			<li>Try more general keywords.</li>
		</ul>
		<?php
	}
	
	if(count($alteration) && isset($_GET['search']))
	{
		$keys = array();
		$replace = array();
		$incorrect = array();
		foreach($alteration as $i => $word)
		{
			$keys[] = htmlspecialchars($i, ENT_QUOTES);
			$replace[] = '<strong><i>' . htmlspecialchars($word, ENT_QUOTES) . '</i></strong>';
			$incorrect[] = '<strong><i>' . htmlspecialchars($i, ENT_QUOTES) . '</i></strong>';
		}
		?>Showing results for <a href="http://www.monolithicsearch.com/?search=<?php print htmlspecialchars(str_replace(array_keys($alteration), array_values($alteration), $_GET['search']), ENT_QUOTES) ;?>"><?php print str_replace($keys, $replace, htmlspecialchars($_GET['search'], ENT_QUOTES)); ?></a>. Search instead for <a href="http://www.monolithicsearch.com/?nfpr=1&search=<?php print htmlspecialchars($_GET['search'], ENT_QUOTES) ;?>"><?php print str_replace($keys, $incorrect, htmlspecialchars($_GET['search'], ENT_QUOTES));?></a><br /><?php
	}
	?>
	</div>
	<?php
	if(count($tags))
	{
		?>
		<div id="leftnav"><strong>Related Tags:</strong>
		<ul>
		<?php
		
		$count = 0;
		foreach($tags as $tag => $tag_count)
		{
			?><li><a href="<?php print generic_validate_urlpath(array('page' => $_SERVER['REQUEST_URI']), 'page'); ?>?search=<?php print htmlspecialchars(urlencode($tag)); ?>"><?php print htmlspecialchars($tag); ?></a></li><?php
			$count++;
			if($count == 10)
				break;
		}
		
		?></ul></div><?php
	}
	
	if($count > 0)
	{
		?>
		<div id="results">
			<div id="updates"></div>
			<?php
	}
}

function theme_results($results)
{
	foreach($results as $i => $result)
	{
		?><div id="result_<?php print $result['Hash']; ?>" class="result">
			<a id="link_<?php print $result['Hash']; ?>" class="link" href="<?php print $result['URL']; ?>"><?php print ($result['Name'] != '')?$result['Name']:$result['URL']; ?></a><br />
			<div class="embed"></div>
			<span class="tags">Tags: <?php print $result['Tags']; ?></span><br />
			<span class="source">Source: <a target="_blanks" href="<?php print $result['Source']; ?>"><?php print $result['Source']; ?></a></span>
		</div><?php
	}
	
}

function theme_footer($count = 0)
{
	if($count > 0)
	{
		?></div><?php
	}
	
	$pages = get_pages(validate_start($_REQUEST), validate_limit($_REQUEST), $count);
	
	foreach($pages as $i => $page_link)
	{
		list($name, $href) = $page_link;
		if(isset($page_link[2]) && $page_link[2] == false)
		{
			?>
			<div class="page<?php print (!is_numeric($name) || strlen($name) > 2)?'W':''; ?>">
				<strong><?php print $name; ?></strong>
			</div>
			<?php
		}
		else
		{
			$url = generic_validate_urlpath(array('page' => $_SERVER['REQUEST_URI']), 'page') . '?' . (isset($_GET['search'])?('search=' . $_GET['search'] . '&'):'') . $href;
			
			?>
			<div class="page<?php print (!is_numeric($name) || strlen($name) > 2)?'W':''; ?>">
				<a class="pageLink" href="<?php print htmlspecialchars($url); ?>"><?php print $name; ?></a>
			</div>
			<?php
		}
	}
	
	?>
	</center>
	</div>
	</body>
</html>
	
	<?php
}
