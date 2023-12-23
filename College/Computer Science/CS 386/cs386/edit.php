<?php

// include required functions
require 'include/common.php';

// start MySQL
startMySQL();
$mysql =& $GLOBALS['mysql'];

// get the template variable
startTemplate();
$template =& $GLOBALS['template'];

// check login information
$login = checkLogin($error, $user);

// current survey being edited
$survey = array();

if(isset($_POST['manage']))
{
	header('Location: manage.php');
	exit();
}
// make sure user is logged in to edit surveys
if($login == true)
{
	// perform actions based on post
	if(isset($_POST['xml']))
	{
		// save post to session and refresh page
		$_SESSION['xml'] = $_POST['xml'];
		$_SESSION['survey_name'] = $_POST['name'];
		
		// make sure user is logged in and save if post
		if(isset($_POST['save']))
		{
			// save the changes to the database based on get variable
			$mysql->setSurvey($_POST['name'], $user['Username'], '<survey>' . urldecode($_POST['xml']) . '</survey>');
		}
		elseif(isset($_POST['view']))
		{
			header('Location: take.php');
		}
		else
		{
			header('Location: ' . $_SERVER['REQUEST_URI']);
		}
		
		exit();
	}

	// edit current survey specified in get variable
	if(isset($_GET['id']))
	{
		// get survey from database based on get variable
		$survey = $mysql->getSurvey('', '', $_GET['id']);
		$template->setVar('SURVEY_NAME', $survey['Name']);
		// parse the tree
		$tree = new Tree();
		$tree->setInputString($survey['Xmlstring']);
		
		$survey = $tree->parse();
		//print_r($survey);
	}
}
else
{
	$template->setVar('ERROR', 'You must log in to save your survey.');
}

// if the session survey is set then edit that one
// this is a safety feature for refreshing or accidental tab closing
if(isset($_SESSION['xml']))
{
	if(isset($_SESSION['survey_name']))
	{
		$template->setVar('SURVEY_NAME', $_SESSION['survey_name']);
	}
	
	$survey_xml = '<survey>' . urldecode($_SESSION['xml']) . '</survey>';
	
	$tree = new Tree();
	$tree->setInputString($survey_xml);
	//$tree->setInputFile('templates/surveyTest.xml');
	
	$survey = $tree->parse();
	
	//print_r($survey);
}

// parse the array and generate the inputs with the specified data type
parseArray($survey, 'edit');

// load template files needed for this page
$template->setFile('edit_main', SITE_TEMPLATE . 'edit/main.html');

// set the inputs to the right variable for placing in Main
$template->setVar('INPUTS', $template->getVar('SURVEY_LIST'));

// call general function for creating toolbox by clearing fields
prepareToolbox();

// print out page
print($template->fParse('out', 'edit_main'));

?>