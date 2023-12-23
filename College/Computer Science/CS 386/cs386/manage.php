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

if($login == true)
{
	
	if(isset($_POST['survey_id']))
	{
		// do what post says to do
		if(isset($_POST['delete']))
		{
			$mysql->deleteSurvey('', '', $_POST['survey_id']);
			
			// forward back to self to null out post
			header('Location: ' . $_SERVER['REQUEST_URI']);
		}
		elseif(isset($_POST['edit']))
		{
			// go to edit page
			header('Location: edit.php?id=' . $_POST['survey_id']);
		}
		elseif(isset($_POST['results']))
		{
			// go to edit page
			header('Location: results.php?id=' . $_POST['survey_id']);
		}
		
		exit();
	}
	
}
else
{
	header("Location: login.php");
	
	exit();
}


// This is where we load the template files required for this page
$template->setFile('manage', SITE_TEMPLATE . 'edit/manage.html');

// Store the name of the currently logged in user
$template->setVar('USER_NAME', $user['Username']);

// Store the list of surveys owned by this user
$surveys = $mysql->getSurveys('', $user['Username']);

$survey_string = '';
foreach($surveys as $index => $survey)
{
		$survey_string .= '<option value="' . $survey['Surveyid'] . '">' . $survey['Name'] . ' (' . $survey['Surveyid'] . ')</option>';
}
$template->setVar('LIST', $survey_string);

$err = "";
$template->setVar('ERROR', $err);

// print out page
print($template->fParse('out', 'manage'));

?>
