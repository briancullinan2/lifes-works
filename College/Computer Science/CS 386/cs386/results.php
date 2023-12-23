<?php

// include required functions
require 'include/common.php';

// start MySQL
startMySQL();
$mysql =& $GLOBALS['mysql'];

$survey_id = '';
// the array that contains all the results added up
// and survey data
$all_results = array();
if (isset($_GET['id'])) {
	$survey_id = $_GET['id'];

	$survey = $mysql->getSurvey('', '', $survey_id);
	$tree = new Tree();
	$tree->setInputString($survey['Xmlstring']);
	
	$all_results = $tree->parse();

	// get the actual results
	$results = $mysql->getResults($survey_id);
	foreach($results as $id => $result)
	{
		// parse the tree
		$tree = new Tree();
		$tree->setInputString($result['Xmlstring']);
		$resultArray = $tree->parse();
	
		// iterate recursivly through tree turning the data field into an array of counted results
		// add up all the results
		countResults($resultArray, $all_results);
	}
	
}

// count results recursively
function countResults($tree, &$adding_to)
{
	foreach($adding_to as $index => $result)
	{
		// assign data field
		if(isset($tree[$index]['DATA']) && $tree[$index]['DATA'] != '' && isset($adding_to[$index]['DATA']))
		{
			if(is_array($adding_to[$index]['DATA']))
			{
				if(isset($adding_to[$index]['DATA'][$tree[$index]['DATA']]))
				{
					// add to existing array of data
					$adding_to[$index]['DATA'][$tree[$index]['DATA']]++;
				}
				//create array key
				else
				{
					$adding_to[$index]['DATA'][$tree[$index]['DATA']] = 1;
				}
			}
			// create an array
			else
			{
				$adding_to[$index]['DATA'] = array($tree[$index]['DATA'] => 1);
			}
		}
		
		// iterate through children
		if(isset($tree[$index]['CHILDREN']))
		{
			countResults($tree[$index]['CHILDREN'], $adding_to[$index]['CHILDREN']);
		}
	}
}

//recursively print out results matching the original survey

// get the template variable
startTemplate();
$template =& $GLOBALS['template'];

// load template files needed for this page
$template->setFile('results_main', SITE_TEMPLATE . 'results/main.html');

// parse the array and generate the inputs with the specified data type
parseArray($all_results, 'results');

// set the inputs to the right variable for placing in Main
$template->setVar('INPUTS', $template->getVar('SURVEY_LIST'));

// print out page
print($template->fParse('out', 'results_main'));

?>
