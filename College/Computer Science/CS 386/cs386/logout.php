<?php

// include required functions
require 'include/common.php';

// start MySQL
startMySQL();
$mysql =& $GLOBALS['mysql'];

// get the template variable
startTemplate();
$template =& $GLOBALS['template'];

// This is where we load the template files required for this page
$template->setFile('logout', SITE_TEMPLATE . 'edit/logout.html');

// check login information
$login = checkLogin($error, $user);

// I don't know why this is necessary, but you have to store the session variables again, or it forgets their values.
$_SESSION['Username'] = $user['Username'];
$_SESSION['Pass'] = $user['Pass'];

$template->setVar('MESSAGE', 'You have logged out successfully.');
if ($login == true)
{
	unset($_SESSION['Username']);
	unset($_SESSION['Pass']);
} else {
	//$template->setVar('MESSAGE', 'No user was logged in.');
}

// Store the name of the currently logged in user
$template->setVar('USER_NAME', $user['Username']);

// print out page
print($template->fParse('out', 'logout'));

?>
