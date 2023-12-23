<?php


/**
 * sorting function for terms in a keyword search
 * @param a the first item to compare
 * @param b the second item to compare
 * @return 1 for comes after, -1 for comes before, 0 for equal
 */
function termSort($a, $b)
{
	if(($a[0] == '+' && $a[0] == $b[0]) || ($a[0] == '-' && $a[0] == $b[0]) || ($a[0] != '+' && $a[0] != '-' && $b[0] != '+' && $b[0] != '-'))
	{
		if(strlen($a) > strlen($b))
			return -1;
		elseif(strlen($a) < strlen($b))
			return 1;
		else
			return 0;
	} elseif($a[0] == '+') {
		return -1;
	} elseif($b[0] == '+') {
		return 1;
	} elseif($a[0] == '-') {
		return -1;
	} else {
		return 1;
	}
}


/**
 * Determines the type of search a user would like to perform based on the surrounding characters
 * @param search the search query for any column
 * @return 'normal' by default indicating the search string should be tokenized and read for '+' required tokens, '-' excluded tokens, and optional include tokens
 * 'literal' if the search string is surrounded by double quotes
 * 'equal' if the search string is surrounded by equal signs
 * 'regular' for regular expression if the search is surrounded by forward slashes
 */
function search_get_type($search)
{
	if(strlen($search) > 1 && $search[0] == '"' && $search[strlen($search)-1] == '"')
		return 'literal';
	elseif(strlen($search) > 1 && $search[0] == '=' && $search[strlen($search)-1] == '=')
		return 'equal';
	elseif(strlen($search) > 1 && $search[0] == '/' && $search[strlen($search)-1] == '/')
		return 'regular';
	else
		return 'normal';
}

/**
 * For a normal search, get each piece that may be preceeded with a '+' for require or a '-' for exclude
 * @param search the search string
 * @return an associative array containing:
 * 'length' of the query string
 * 'count' of all the pieces
 * 'required' all the pieces preceeded by a '+' in the query string
 * 'excluded' all the pieces to be excluded preceeded by a '-'
 * 'includes' all the pieces that should contain at least 1 include
 */
function search_get_pieces($search)
{
	// loop through search terms and construct query
	$pieces = explode(' ', $search);
	$pieces = array_unique($pieces);
	$empty = array_search('', $pieces, true);
	if($empty !== false) unset($pieces[$empty]);
	$pieces = array_values($pieces);
	
	// sort items by inclusive, exclusive, and string size
	// rearrange pieces, but keep track of index so we can sort them correctly
	uasort($pieces, 'termSort');
	$length = strlen(join(' ', $pieces));
	
	// these are the 3 types of terms we can have
	$required = array();
	$excluded = array();
	$includes = array();

	foreach($pieces as $j => $piece)
	{
		if($piece[0] == '+')
			$required[$j] = substr($piece, 1);
		elseif($piece[0] == '-')
			$excluded[$j] = substr($piece, 1);
		else
			$includes[$j] = $piece;
	}
	
	return array(
		'length' => $length,
		'count' => count($pieces),
		'required' => $required,
		'excluded' => $excluded,
		'includes' => $includes
	);
}

/**
 * Generate an SQL query from the pieces
 * @param pieces The pieces from search_get_pieces()
 * @return an associative array of properties of the SQL query, containing COLUMNS, ORDER, and WHERE
 */
function search_get_pieces_query($pieces)
{
	$columns = '';
	
	$required = '';
	$excluded = '';
	$includes = '';
	
	foreach($pieces['required'] as $j => $piece)
	{
		if($required != '') $required .= ' AND ';
		$required .= 'LOCATE("' . addslashes($piece) . '", {column}) > 0';
		$columns .= '(LOCATE("' . addslashes($piece) . '", {column}) > 0)+';
	}
	
	foreach($pieces['excluded'] as $j => $piece)
	{
		if($excluded != '') $excluded .= ' AND ';
		$excluded .= 'LOCATE("' . addslashes($piece) . '", {column}) = 0';
		$columns .= '(LOCATE("' . addslashes($piece) . '", {column}) = 0)+';
	}
	
	foreach($pieces['includes'] as $j => $piece)
	{
		if($includes != '') $includes .= ' OR ';
		$includes .= 'LOCATE("' . addslashes($piece) . '", {column}) > 0';
		$columns .= '(LOCATE("' . addslashes($piece) . '", {column}) > 0)+';
	}
	
	// add parenthesis to each part
	if($required != '') $required = '(' . $required . ')';
	if($excluded != '') $excluded = '(' . $excluded . ')';
	if($includes != '') $includes = '(' . $includes . ')';
	
	// remove trailing comma
	if($columns != '')
		$columns = '(' . substr($columns, 0, -1) . ')';
	
	$part = '';
	$part .= (($required != '')?(($part != '')?' AND ':'') . $required:'');
	$part .= (($excluded != '')?(($part != '')?' AND ':'') . $excluded:'');
	$part .= (($includes != '')?(($part != '')?' AND ':'') . $includes:'');
	
	return array(
		'columns' => $columns,
		'where' => $part,
	);
}

/**
 * Implementation of alter_query
 * Alter a database queries when the search variable is set in the request
 * @ingroup alter_query
 */
function filter_search($request)
{
	// some other variables we need
	$request['order_by'] = validate($request, 'order_by');
	
	// they can specify multiple columns to search for the same string
	$columns = array('Name', 'URL', 'Source', 'Tags');
		
	// array for each column
	$parts = array();
	
	$result = array();
	
	// loop through each column to compile search query
	foreach($columns as $i => $column)
	{
		if(isset($request['search_' . $column]) && $request['search_' . $column] != '')
			$search = $request['search_' . $column];
		elseif(isset($request['search']) && $request['search'] != '')
			$search = $request['search'];
		else
			continue;

		$type = search_get_type($search);
		
		// remove characters on either side of input
		if($type != 'normal')
			$search = substr($search, 1, -1);
		
		// incase an aliased path is being searched for replace it here too!
		if(setting('alias_enable'))
			$search = alias_replace($search);
		
		// tokenize input
		if($type == 'normal')
		{
			$pieces = search_get_pieces($search);
			
			if(!isset($_GET['nfpr']))
			{
				// do spell checking
				$pl = pspell_new("en");
				$corrections = array();
				foreach(array('includes', 'excluded', 'required') as $words)
				{
					if(isset($pieces[$words]))
					{
						foreach($pieces[$words] as $i => $word)
						{
							if(!pspell_check($pl, $word) && ($suggestions = pspell_suggest($pl, $word)) && 
								count($suggestions) > 0)
							{
								$corrections[$pieces[$words][$i]] = $suggestions[0];
								$pieces[$words][$i] = $suggestions[0];
							}
						}
					}
				}
				if(count($corrections) > 0)
					$result['spelling'] = $corrections + (isset($result['spelling'])?$result['spelling']:array());
			}
			
			$query = search_get_pieces_query($pieces);
			
			$replaced = str_replace(array('{column}', '{column_index}'), array($column, $i), $query);
			$parts[] = $replaced['where'];
			$result['columns'] = (isset($result['columns'])?($result['columns'] . '+'):'') . $replaced['columns'];
			
			$result['columns'] .= '*10-ABS(LENGTH(' . $column . ') - ' . $pieces['length'] . ')';
		}
		elseif($type == 'equal')
		{
			$parts[] = $column . ' = "' . addslashes($search) . '"';
		}
		elseif($type == 'regular')
		{
			$parts[] = $column . ' REGEXP "' . addslashes($search) . '"';
		}
		elseif($type == 'literal')
		{
			$parts[] = ' LOCATE("' . addslashes($search) . '", ' . $column . ')';
		}
	}
	
	// remove leading commas
	if(!empty($result['columns']))
		$result['columns'] .= ' AS Relevance';
	
	// set the new props
	if(count($parts) > 0)
	{
		$result['where'] = join((isset($request['search_operator'])?(' ' . $request['search_operator'] . ' '):' OR '), $parts);
		$result['order'] = 'Relevance DESC';
	}
		
	return $result;
}
