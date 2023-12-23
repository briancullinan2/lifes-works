<?php

session_start();

require_once 'settings.php';

require_once 'ftp.php';

require_once 'mysql.php';

require_once 'HTML_Template.php';

require_once 'Simple.php';

require_once 'tree.php';

require_once 'errors.php';


function startTemplate()
{

	if(isset($GLOBALS['template']))
	{
		return $GLOBALS['template'];
	}

	// create our template object
	$template =& new HTML_Template(SITE_LOCALROOT, 'remove');
	
	// load critical template files
	$template->setFile('edit', SITE_TEMPLATE . 'edit/survey.html');
	$template->setFile('edit_ORIG', SITE_TEMPLATE . 'edit/survey.html');
	$template->setFile('xml', SITE_TEMPLATE . 'xml/survey.xml');
	$template->setFile('xml_ORIG', SITE_TEMPLATE . 'xml/survey.xml');
	$template->setFile('take', SITE_TEMPLATE . 'take/survey.html');
	$template->setFile('take_ORIG', SITE_TEMPLATE . 'take/survey.html');
	$template->setFile('results', SITE_TEMPLATE . 'results/survey.html');
	$template->setFile('results_ORIG', SITE_TEMPLATE . 'results/survey.html');

	$GLOBALS['template'] = $template;
	
	return $template;
	
}

// create blank controls to use in the toolbox
function prepareToolbox()
{
	startTemplate();
	$template =& $GLOBALS['template'];
	
	$template->setFile('toolbox', SITE_TEMPLATE . 'edit/toolbox.html');
	
	/*
	$template->setBlock('input', 'CHECKBOX');
	$template->setBlock('input', 'RADIO');
	
	// clear previously used variables
	$remove = array('LOCATION_DATA', 'QUESTION_DATA', 'RESPONSE_DATA');
	foreach($remove as $index => $value)
	{
		$template->setVar($value, '');
	}
	$template->setVar('SURVEY_INDEX', '$i');
	$template->setVar('CHECKBOX_INDEX', '$j');
	$template->setVar('RADIO_INDEX', '$j');
	
	// create checkbox
	$template->setVar('EXTRA', 'onclick="add(this)" id="checkbox_input"');
	$template->setVar('EXTRA2', 'onclick="add2(this)" id="checkbox_response"');
	$template->fParse('INPUT_TOOLS', 'CHECKBOX', true);
	
	// create radio
	$template->setVar('EXTRA', 'onclick="add(this)" id="radio_input"');
	$template->setVar('EXTRA2', 'onclick="add2(this)" id="radio_response"');
	*/
	$template->fParse('INPUT_TOOLS', 'toolbox');
	
}

//
function printError($error, $title)
{
	//print_r($title);
	//print '<br />';
	//print_r($error);
	//print '<br />';
}

//this is the main array parser function
function parseArray($tree, $parent)
{
	startTemplate();
	$template =& $GLOBALS['template'];
	
	// set all the original blocks in this element
	$template->setBlocks($parent . '_ORIG', '', '_ORIG');
	
	// types of children for clearing the template later
	$types_of_children = array();
	
	// loop through elements
	foreach($tree as $i => $element)
	{
		// if the element type is set
		if(isset($element['ELEMENT']))
		{
			// load element's block and change the name to list
			$template->setBlock($parent, $element['ELEMENT'], $element['ELEMENT'] . '_LIST');
			// use original template for new each element
			$template->setVar($element['ELEMENT'], $template->getVar($element['ELEMENT'] . '_ORIG'));
			
			// add this element list to the types of children
			$types_of_children = array_unique(array_merge($types_of_children, array($element['ELEMENT'])));
			
			// some extra fields to be set
			$template->setVar($parent . '_INDEX', $i);
			
			// get children
			$children = array();
			if(isset($element['CHILDREN']))
			{
				$children = parseArray($element['CHILDREN'], $element['ELEMENT']);
			}
			
			// loop through properties and set in template
			foreach($element as $key => $prop)
			{
				if($key == 'DATA')
				{
					if(is_array($prop))
					{
						$array_str = '';
						// loop through array printing out key - value
						foreach($prop as $key => $value)
						{
							$array_str .= $key . ' - ' . $value . '<br />' . "\n";
						}
						
						$template->setVar($element['ELEMENT'] . '_DATA', $array_str);
					}
					else
					{
						$template->setVar($element['ELEMENT'] . '_DATA', $prop);
					}
				}
				elseif($key != 'CHILDREN')
				{
					$template->setVar($key, $prop);
				}
				
			}
			
			// remove blank fields and elements
			// and set any case specific fields
			$remove = array();
			$clear = array();
			$list = $element['ELEMENT'] . '_LIST';
			switch($element['ELEMENT'])
			{
				case 'SURVEY':
					$remove = array('CHECKBOX', 'RADIO');
				break;
				case 'CHECKBOX':
				case 'RADIO':
					$remove = array('RESPONSE');
					$clear = array('QUESTION', 'LOCATION');
					$template->setVar('EXTRA', 'overClass="OverDragBox" dragClass="DragDragBox"');
					$list = 'INPUT_LIST';
				break;
			}
			
			// loop through all optional elements and make them blank
			foreach($clear as $index => $value)
			{
				// if there is nothing in the list from the children
				//  then clear it so it doesn't insert the last
				//  iterations values into this template
				if($template->getVar($value . '_LIST') == '')
				{
					$template->setVar($value . '_DATA', '');
				}
			}
			foreach($remove as $index => $value)
			{
				if($template->getVar($value . '_LIST') == '')
				{
					$template->setBlock($element['ELEMENT'], $value, $value . '_LIST');
					$template->setVar($value . '_LIST', '');
				}
			}

			// parse the element
			$template->fparse($list, $element['ELEMENT'], true);
			
			// print stuff for debugging
			//print "START------------\n";
			//print_r(htmlentities($template->getVar($element['ELEMENT'] . '_LIST')));
			//print "------------END\n";
			
			// now that it has already been parsed
			// clear previous types of children from template
			// so the next iteration of this function can start over
			foreach($children as $index => $type)
			{
				$template->setVar($type . '_LIST', '');
			}
			// reset the element to the original template
			// so the next iteration can start fresh
			
		}

	}
	
	// return types of children to parent element can clear the variable afterwards
	return $types_of_children;

}

?>