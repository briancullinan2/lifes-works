<?php

// include required functions
require 'include/common.php';

// start MySQL
startMySQL();
$mysql =& $GLOBALS['mysql'];

// get the template variable
startTemplate();
$template =& $GLOBALS['template'];

// only save survey if there is an id otherwise it is just viewing it
if(isset($_POST['xml']) && isset($_GET['id']) && isset($_POST['submit']))
{
	// submit results
	$mysql->setResult($_GET['id'], '<survey>' . urldecode($_POST['xml']) . '</survey>');
	
	header('Location: ' . $_SERVER['REQUEST_URI']);
	
	exit();
}

// current survey being taken
$survey = array();

// get survey from database or post
if(isset($_GET['id']))
{
	$survey = $mysql->getSurvey('','',$_GET['id']);

	$tree = new Tree();
	$tree->setInputString($survey['Xmlstring']);
	
	$survey = $tree->parse();
	
}
// if id isn't set we must be doing a view so just get survey from session
elseif(isset($_SESSION['xml']))
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

// load template files needed for this page
$template->setFile('take_main', SITE_TEMPLATE . 'take/main.html');

// parse the array and generate the inputs with the specified data type
parseArray($survey, 'take');

// set the inputs to the right variable for placing in Main
$template->setVar('INPUTS', $template->getVar('SURVEY_LIST'));

// call general function for creating toolbox by clearing fields
prepareToolbox();

// print out page
print($template->fParse('out', 'take_main'));

?>
