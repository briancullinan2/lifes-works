<?php
// Tree
// This is a handler for the XML parser that comes in the PEAR library.
// The part of the library we need has been localized, because it's not on the server.

class Tree extends XML_Parser
{

	var $tree = array(); // the final output variable for the stack
	
	var $stack = array(); // the stack where the XML elements are parsed into and a tree is created out of it
	
	var $data = ''; // Just a string for the data that's inbetween XML elements (eg "this stuff" in <data>this stuff</data>)

  // constructor
  function myParser()
  {
        parent::XML_Parser(); // that's how you call a super class. This is a static call.
	}

   /**
    * handle start element
    *
    * @access   private
    * @param    resource    xml parser resource
    * @param    string      name of the element
    * @param    array       attributes
    */
    function startHandler($xp, $name, $attribs)
    {
		$attribs['ELEMENT'] = $name;
		$attribs['CHILDREN'] = array();
		
		array_push($this->stack, $attribs);
		
		//print_r($this->stack);
    }

   /**
    * handle start element
    *
    * @access   private
    * @param    resource    xml parser resource
    * @param    string      name of the element
    * @param    array       attributes
    */
    function endHandler($xp, $name)
    {
		$element = array_pop($this->stack);

		$element['DATA'] = $this->data;
		$this->data = '';
		if(count($element['CHILDREN']) == 0)
		{
			unset($element['CHILDREN']);
		}
		
		if(count($this->stack) == 0)
		{
			array_push($this->tree, $element);
		}
		else
		{
			$parent = array_pop($this->stack);
			
			array_push($parent['CHILDREN'], $element);
			
			array_push($this->stack, $parent);
		}
		
    }
	
	/**
   * Handler function for reading in data.
   */
	function dataHandler($xp, $data)
	{
		$this->data = $data; // use the arrow to refer to an object inside of this class
	}
	
	
  /**
   * The common function that is overriden from the parent class. It calls the parent parse function, but returns tree instead of a true or false value. The other one did failure or success as a result.
   */
	function parse()
	{
		parent::parse();
		
		return $this->tree;
	}
	
}


?>
