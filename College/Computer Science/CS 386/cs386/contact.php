<?php

// Created by Travis Hudson (November 19, 2007)

// include required functions
require 'include/common.php';

// start MySQL
startMySQL();
$mysql =& $GLOBALS['mysql'];

// get the template variable
startTemplate();
$template =& $GLOBALS['template'];

if (!isset($_POST['category'])) {

// This is where we load the template files required for this page
$template->setFile('contact', SITE_TEMPLATE . 'edit/contact.html');

// code here
$template->setVar('SURVEY_ID', $_GET['id']);
} else {
	
	$template->setFile('contact', SITE_TEMPLATE . 'edit/contact2.html');
	
// code here

// retrieve survey info using ID
$result = $mysql->query("SELECT * FROM surveys
	WHERE surveyid='" . $_POST['survey_id'] . "'") or die(mysql_error());
$survey = mysql_fetch_array( $result );

// sanitizing the input before query
foreach($_POST as $key => $value){
	$_POST[$key] = mysql_real_escape_string($value);
}

// insert new entry into feedback table
$mysql->query("INSERT INTO feedback
	(Kind, Submitter_Name, Email, Content, Survey_Name, Username)
	VALUES('$_POST[category]', '$_POST[name]',
	'$_POST[emailadd]', '$_POST[comments]',
	 '$survey[Name]', '$survey[Username]')")
or die(mysql_error());

// display successful result for user
$template->setVar('CATEGORY', stripslashes($_POST['category']));
$template->setVar('NAME', stripslashes($_POST['name']));
$template->setVar('EMAIL', stripslashes($_POST['emailadd']));
$template->setVar('COMMENTS', stripslashes($_POST['comments']));
}

// print out page
print($template->fParse('out', 'contact'));
?>
