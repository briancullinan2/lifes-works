<?php

$GLOBALS['tm_start'] = array_sum(explode(' ', microtime()));


include dirname(__FILE__) . '/curl.php';
include dirname(__FILE__) . '/includes/database.inc';
include dirname(__FILE__) . '/request.inc';
include dirname(__FILE__) . '/handlers.php';

/**
 * @name Error Levels
 * Error codes so we know which errors to print to the user and which to print to debug
 */
//@{
/** @enum E_DEBUG the DEBUG level error used for displaying errors in the debug template block */
define('E_DEBUG',					2);
define('E_VERBOSE',					4);
/** @enum E_USER USER level errors are printed to the user by the templates */
define('E_USER',					8);
/** @enum E_WARN the WARN level error prints a different color in the error block, this is
 * used by parts of the site that cause problems that may not be intentional */
define('E_WARN',					16);
/** @enum E_FATAL the FATAL errors are ones that cause the script to end at an unexpected point */
define('E_FATAL',					32);
/** @enum E_NOTE the NOTE error level is used for displaying positive information to users such as
 * "account has been created" */
define('E_NOTE',					64);
//@}

setup_mime();
setup_database();

function get_handlers()
{
	return array(
		'media' => array(
			'database' => array(
				'Name' => 'TEXT',
				'URL' => 'TEXT',
				'MIME' => 'TEXT',
				'Source' => 'TEXT',
				'Embed' => 'TEXT',
				'Added' => 'DATETIME',
				'Tags' => 'TEXT',
				'Popularity' => 'DOUBLE',
			)
		),
	);
}

function module_implements()
{
	return false;
}

/**
 * Function for checking in libraries are installed, specifically PEAR which likes to use /local/share/php5/
 * @param filename the library filename from the scope of the expected include_path
 * @return the full, real path of the library, or false if it is not found in any include path
 */
function include_path($filename)
{
	// Check for absolute path
	if (realpath($filename) == $filename) {
		return $filename;
	}
	
	// Otherwise, treat as relative path
	$paths = explode(PATH_SEPARATOR, get_include_path());
	$paths[] = dirname(__FILE__);
	foreach ($paths as $path)
	{
		if (substr($path, -1) == DIRECTORY_SEPARATOR)
			$fullpath = $path . $filename;
		else
			$fullpath = $path . DIRECTORY_SEPARATOR . $filename;
		
		if (file_exists($fullpath))
		{
			return $fullpath;
		}
	}
	
	return false;
}

/**
 * Implementation of setting, basic wrapper for checks
 * @ingroup setting
 * @return possible settings file paths in order of preference to the system, false if file doesn't exist anywhere
 */
function setting_settings_file()
{
	return dirname(__FILE__) . '/settings.php';
}

/**
 * Helper function
 */
function preload_settings()
{
	// get system settings from a few different locations
	if(file_exists(setting_settings_file()))
	{
		if(($GLOBALS['settings'] = parse_ini_file(setting_settings_file(), true)))
		{
			// awesome settings are loaded properly
			foreach($GLOBALS['settings'] as $name => $value)
			{
				if(is_array($value))
				{
					foreach($value as $subsetting => $subvalue)
					{
						$GLOBALS['settings'][$name][$subsetting] = urldecode($subvalue);
					}
				}
				else
					$GLOBALS['settings'][$name] = urldecode($value);
			}
		}
	}
	
	// this stores a list of all the settings from the modules
	if(!isset($GLOBALS['all_settings']))
		$GLOBALS['all_settings'] = array();
		
	// the include must have failed
	if(!isset($GLOBALS['settings']) || empty($GLOBALS['settings']))
	{
		$GLOBALS['settings'] = array();
		
		// try and forward them to the install page
		if(!isset($_REQUEST['module'])) $_REQUEST['module'] = 'admin_install';
	}
}


/**
 * Get a setting
 * @ingroup setting
 * @param name The setting name to get
 */
function setting($name)
{
	$args = func_get_args();
	array_shift($args);
	
	// if by chance settings isn't set up
	if(!isset($GLOBALS['settings']))
		preload_settings();
		
	array_unshift($args, $GLOBALS['settings']);
	
	// if the setting is not found, try to get the default
	if(function_exists('setting_' . $name))
		$GLOBALS['settings'][$name] = call_user_func_array('setting_' . $name, $args);
	elseif(isset($GLOBALS['setting_' . $name]) && is_callable($GLOBALS['setting_' . $name]))
		$GLOBALS['settings'][$name] = call_user_func_array($GLOBALS['setting_' . $name], $args);
	
	// if the setting is loaded already use that
	if(isset($GLOBALS['settings'][$name]))
		return $GLOBALS['settings'][$name];
		
	// if the setting isn't found in the configuration
	raise_error('Setting \'' . $name . '\' not found!', E_VERBOSE);
}

/**
 * Implementation of setting
 * @ingroup setting
 * @return false by default, set to true to record all notices
 */
function setting_verbose($settings)
{
	$verbose = generic_validate_boolean_false($settings, 'verbose');

	if(isset($settings['verbose']) && ($settings['verbose'] === "2" || $settings['verbose'] === 2))
		return 2;
	else
		return $verbose;
}

/**
 * Implementation of setting
 * @ingroup setting
 * @return 16MB by default
 */
function setting_buffer_size($settings)
{
	if(isset($settings['buffer_size']['value']) && isset($settings['buffer_size']['multiplier']) && 
		is_numeric($settings['buffer_size']['value']) && is_numeric($settings['buffer_size']['multiplier'])
	)
		$settings['buffer_size'] = $settings['buffer_size']['value'] * $settings['buffer_size']['multiplier'];
	
	if(isset($settings['buffer_size']) && is_numeric($settings['buffer_size']) && $settings['buffer_size'] > 0)
		return $settings['buffer_size'];
	else
		return 8*8*1024;
}



function raise_error($str, $code)
{
	$error = new StdClass;
	$error->code = $code;
	$error->message = $str;
	$error->backtrace = debug_backtrace();
	
	if(error_reporting() != 0 || setting('verbose') === 2)
		error_callback($error);
}

function format_tags($tags)
{
	foreach($tags as $i => $tag)
		$tags[$i] = strtolower($tag);
	return array_unique($tags);
}


function get_pages($start, $limit, $total_count)
{
	$pages = array();
	
	// return here if all the results fit on one page
	if($total_count <= $limit)
		return $pages;
	
	$current_page = round($start / $limit);
	$last_page = floor($total_count / $limit);
	
	// if it is not on the first page, add a first link
	$pages[] = array('First', 'start=0');

	// if it is not on the first page, add previon link
	if($current_page > 1)
		$pages[] = array('Prev', 'start=' . ($current_page-1)*$limit);
	
	// start the links 5 or less away from current page
	if($last_page < 10)
	{
		$start = 0;
		$stop = $last_page;
	}
	else
	{
		if($current_page - 5 < 0)
			$start = 0;
		else
			if($current_page + 5 > $last_page)
				$start = $last_page - 10;
			else
				$start = $current_page - 5;
		
		$stop = $start + 10;
	}
		
	// add numeric pages in between
	for($i = $start; $i <= $stop; $i++)
	{
		if($i == $current_page)
			$pages[] = array(($i+1), 'start=' . $i * $limit, false);
		else
			$pages[] = array(($i+1), 'start=' . $i * $limit);
	}
	
	// if it before the last page, add a next link
	if($current_page < $last_page - 1)
		$pages[] = array('Next', 'start=' . ($current_page+1)*$limit);

	// if it is not on the last page, add a last link
	$pages[] = array('Last', 'start=' . $last_page * $limit);
		
	// add convenience stuff to array since this is an API after all
	foreach($pages as $key => $page) { $pages[$key]['name'] = $page[0]; $pages[$key]['href'] = $page[1]; }
	
	return $pages;
}

/**
 * The callback function for the PEAR error handler to use
 * @param error the pear error object to add to the error stack
 */
function error_callback($error, $callback = NULL)
{
	if((php_sapi_name() == 'cli'))
	{
		print $error->message . "\n";
		flush();
	}
	
	if($error->code & E_USER)
		$GLOBALS['user_errors'][] = $error->message;
	if($error->code & E_WARN)
		$GLOBALS['warn_errors'][] = $error->message;
	if($error->code & E_NOTE)
		$GLOBALS['note_errors'][] = $error->message;
	
	// debug information
	if($error->code & E_DEBUG || 
		(
			(
				$error->code & E_VERBOSE || 
				$error->code & E_USER
			) && setting('verbose') === 2)
		)
	{
		if(count($GLOBALS['debug_errors']) < 200)
		{
			if(isset($GLOBALS['tm_start']))
				$error->time = array_sum(explode(' ', microtime())) - $GLOBALS['tm_start'];

			// add special error handling based on the origin of the error
			foreach($error->backtrace as $i => $stack)
			{
				if($stack['function'] == 'raise_error')
					break;
			}
			$i++;
			if(isset($error->backtrace[$i]['file']))
			{
				if(dirname($error->backtrace[$i]['file']) == 'modules' && basename($error->backtrace[$i]['file']) == 'template.php')
				{
					for($i = $i; $i < count($error->backtrace); $i++)
					{
						if(dirname($error->backtrace[$i]['file']) != 'modules' || basename($error->backtrace[$i]['file']) != 'template.php')
							break;
					}
				}
			
				$error->message .= ' in ' . $error->backtrace[$i]['file'] . ' on line ' . $error->backtrace[$i]['line'];
			}
			
			if($error->code & E_VERBOSE)
				$error->message = 'VERBOSE: ' . $error->message;
			
			// only show verbose errors if it is really verbose!
			if($error->code & E_DEBUG || setting('verbose'))
			{
				if(isset($GLOBALS['debug_errors'][md5($error->message)]))
					$error->count = isset($GLOBALS['debug_errors'][md5($error->message)]->count)?$GLOBALS['debug_errors'][md5($error->message)]->count++:2;
				//$GLOBALS['debug_errors'][] = $error;
				$GLOBALS['debug_errors'][md5($error->message)] = $error;
			}
		}
	}
}


/** 
 * parse mime types from a mime.types file, 
 * This functionality sucks less then the PEAR mime type library
 * @ingroup setup
 */
function setup_mime()
{
	// this will load the mime-types from a linux dist mime.types file stored in includes
	// this will organize the types for easy lookup
	if(file_exists(include_path('mime.types')))
	{
		$handle = fopen(include_path('mime.types'), 'r');
		$mime_text = fread($handle, filesize(include_path('mime.types')));
		fclose($handle);
		
		$mimes = explode("\n", $mime_text);
		
		$ext_to_mime = array();
		foreach($mimes as $index => $mime)
		{
			$mime = preg_replace('/#.*?$/', '', $mime);
			if($mime != '')
			{
				// mime to ext
				$file_types = preg_split('/[\s,]+/', $mime);
				$mime_type = $file_types[0];
				// general type
				$tmp_type = explode('/', $mime_type);	
				$type = $tmp_type[0];
				// unset mime part to get all its filetypes
				unset($file_types[0]);
				
				// ext to mime
				foreach($file_types as $index => $ext)
				{
					$ext_to_mime[strtolower($ext)] = $mime_type;
				}
			}
		}
		
		// set global variables
		$GLOBALS['ext_to_mime'] = $ext_to_mime;
	}
}


function ext($file)
{
	if(strpos(basename($file), '.') !== false)
		return strtolower(substr(basename($file), strrpos(basename($file), '.') + 1));
	else
		return '';
}

/**
 * get mime type based on file extension
 * @param ext the extension or filename to get the mime type of
 * @return a mime type based on the UNIX mime.types file
 */
function mime($ext)
{
	if(strpos($ext, '.') !== false)
	{
		$ext = ext($ext);
	}
	
	$ext = strtolower($ext);
	
	if(isset($GLOBALS['ext_to_mime'][$ext]))
	{
		return $GLOBALS['ext_to_mime'][$ext];
	}
	else
	{
		return '';
	}
}

function mime_type($ext)
{
	$mime = mime($ext);
	
	$type = explode('/', $mime);
	
	return $type[0];
}

function extract_query($query)
{
	$request = array();
	
	// process the query part
	//   this is done here because fragment takes precedence over path
	//   this allows for calling an output function modified request input
	$arr = explode('&', $query);
	if(count($arr) == 1 && $arr[0] == '')
		$arr = array();
	
	// loop through all the query string and generate our new request array
	foreach($arr as $i => $value)
	{
		// split each part of the query string into name value pairs
		$x = explode('=', $value, 2);
		
		// set each part of the query string in our new request array
		$request[$x[0]] = urldecode(isset($x[1])?$x[1]:'');
	}
	
	return $request;
}

/**
 * stolen from PEAR, 
 * DSN parser for use internally
 * @return an associative array of parsed DSN information
 */
function parseDSN($dsn)
{
	$parsed = array();
	if (is_array($dsn)) {
		$dsn = array_merge($parsed, $dsn);
		if (!$dsn['dbsyntax']) {
			$dsn['dbsyntax'] = $dsn['phptype'];
		}
		return $dsn;
	}

	// Find phptype and dbsyntax
	if (($pos = strpos($dsn, '://')) !== false) {
		$str = substr($dsn, 0, $pos);
		$dsn = substr($dsn, $pos + 3);
	} else {
		$str = $dsn;
		$dsn = null;
	}

	// Get phptype and dbsyntax
	// $str => phptype(dbsyntax)
	if (preg_match('|^(.+?)\((.*?)\)$|', $str, $arr)) {
		$parsed['phptype']  = $arr[1];
		$parsed['dbsyntax'] = !$arr[2] ? $arr[1] : $arr[2];
	} else {
		$parsed['phptype']  = $str;
		$parsed['dbsyntax'] = $str;
	}

	if (!count($dsn)) {
		return $parsed;
	}

	// Get (if found): username and password
	// $dsn => username:password@protocol+hostspec/database
	if (($at = strrpos($dsn,'@')) !== false) {
		$str = substr($dsn, 0, $at);
		$dsn = substr($dsn, $at + 1);
		if (($pos = strpos($str, ':')) !== false) {
			$parsed['username'] = rawurldecode(substr($str, 0, $pos));
			$parsed['password'] = rawurldecode(substr($str, $pos + 1));
		} else {
			$parsed['username'] = rawurldecode($str);
		}
	}

	// Find protocol and hostspec

	// $dsn => proto(proto_opts)/database
	if (preg_match('|^([^(]+)\((.*?)\)/?(.*?)$|', $dsn, $match)) {
		$proto       = $match[1];
		$proto_opts  = $match[2] ? $match[2] : false;
		$dsn         = $match[3];

	// $dsn => protocol+hostspec/database (old format)
	} else {
		if (strpos($dsn, '+') !== false) {
			list($proto, $dsn) = explode('+', $dsn, 2);
		}
		if (   strpos($dsn, '//') === 0
			&& strpos($dsn, '/', 2) !== false
			&& $parsed['phptype'] == 'oci8'
		) {
			//oracle's "Easy Connect" syntax:
			//"username/password@[//]host[:port][/service_name]"
			//e.g. "scott/tiger@//mymachine:1521/oracle"
			$proto_opts = $dsn;
			$dsn = substr($proto_opts, strrpos($proto_opts, '/') + 1);
		} elseif (strpos($dsn, '/') !== false) {
			list($proto_opts, $dsn) = explode('/', $dsn, 2);
		} else {
			$proto_opts = $dsn;
			$dsn = null;
		}
	}

	// process the different protocol options
	$parsed['protocol'] = (!empty($proto)) ? $proto : 'tcp';
	$proto_opts = rawurldecode($proto_opts);
	if (strpos($proto_opts, ':') !== false) {
		list($proto_opts, $parsed['port']) = explode(':', $proto_opts);
	}
	if ($parsed['protocol'] == 'tcp') {
		$parsed['hostspec'] = $proto_opts;
	} elseif ($parsed['protocol'] == 'unix') {
		$parsed['socket'] = $proto_opts;
	}

	// Get dabase if any
	// $dsn => database
	if ($dsn) {
		// /database
		if (($pos = strpos($dsn, '?')) === false) {
			$parsed['database'] = $dsn;
		// /database?param1=value1&param2=value2
		} else {
			$parsed['database'] = substr($dsn, 0, $pos);
			$dsn = substr($dsn, $pos + 1);
			if (strpos($dsn, '&') !== false) {
				$opts = explode('&', $dsn);
			} else { // database?param1=value1
				$opts = array($dsn);
			}
			foreach ($opts as $opt) {
				list($key, $value) = explode('=', $opt);
				if (!isset($parsed[$key])) {
					// don't allow params overwrite
					$parsed[$key] = rawurldecode($value);
				}
			}
		}
	}

	return $parsed;
}
