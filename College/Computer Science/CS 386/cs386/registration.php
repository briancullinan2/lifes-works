<?php

// Created by Noah Hilt

session_start();

// include required functions
require 'include/common.php';

// start MySQL
startMySQL();
$mysql =& $GLOBALS['mysql'];

// get the template variable
startTemplate();
$template =& $GLOBALS['template'];

// This is where we load the template files required for this page
$template->setFile('registration', SITE_TEMPLATE . 'edit/registration.html');

// code here

// check for a post
if(isset($_POST["username"]))
{
	// check for missing values and correct password/re-password combination
	
	
	// add user to database
}

print($template->fParse('out', 'registration'));
?>
