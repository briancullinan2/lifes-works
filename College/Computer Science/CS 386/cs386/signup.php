<?php

// include required functions
require 'include/common.php';

// start MySQL
startMySQL();
$mysql =& $GLOBALS['mysql'];

// do what post says to do
if (isset($_POST['action']) && ($_POST['action'] != 'nothing')) {
	$action = $_POST['action'];
	$selValue = $_POST['selValue'];
	
	if ($action == 'delete') {
		$mysql->deleteSurvey('', '', $selValue);
	}
	
	unset($_POST['action']);
	unset($_POST['selValue']);
}

// get the template variable
startTemplate();
$template =& $GLOBALS['template'];

// This is where we load the template files required for this page
$template->setFile('signup', SITE_TEMPLATE . 'edit/signup.html');

// check login information
$login = checkLogin($error, $user);

// I don't know why this is necessary, but you have to store the session variables again, or it forgets their values.
if ($login == true)
{
	
}

// Store the name of the currently logged in user
$template->setVar('USER_NAME', $user['Username']);

$err = "";
$template->setVar('ERROR', $err);

// print out page
print($template->fParse('out', 'signup'));

?>
