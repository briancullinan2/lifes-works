<?php

// connection constants
define('DB_SERVER',                'flagstaff.cse.nau.edu');
define('DB_USER',                   'travisty');
define('DB_PASS',                   'changeme');
define('DB_NAME',                 'travistyDB');
define('FTP_SERVER',               'localhost');
define('FTP_USER',                  'jambonie');
define('FTP_PASS',          'iKstFKTcxoM8jf33');

// old info that has been replaced
// define('DB_USER',             'jambonie_main');
// define('DB_PASS',                 'iUj498Jjs');
// define('DB_NAME',             'jambonie_main');

// site constants these are loaded by the template
define('SITE_LOCALROOT',                       '/var/www/cs386/');
define('SITE_HTMLPATH',             'http://209.250.30.30/cs386');
define('SITE_HTMLROOT',                                '/cs386/');
define('SITE_TEMPLATE',            					'templates/');

// link constants, these are also loaded by the template
define('LINK_',			SITE_HTMLROOT . '/');

// status constants
define('STATUS_UNACTIVATED',		0);
define('STATUS_ACTIVATED',			1);
define('STATUS_PASSRESET',			2);

// comment-out-able
ini_set('error_reporting', E_ALL);
ini_set('include_path', '.:' . SITE_LOCALROOT . ':' . SITE_LOCALROOT . 'include/');

// compression options
ini_set('zlib.output_compression', 1);
ini_set('zlib.output_compression_level', 5);

// set some session preferences
ini_set('session.use_only_cookies', 1);

?>
