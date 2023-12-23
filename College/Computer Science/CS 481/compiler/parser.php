<?php

require_once 'scanner.php';
require_once 'nodes.php';

class Parser
{

	var $file;
	var $scanner;
	var $tree;

	function Parser($file)
	{
		$this->scanner = new Scanner($file);
		//print_r($this->scanner->tokens);
	}


	function parse()
	{
		$ast = new AST();
		$this->tree = $ast->parseAST($this->scanner->tokens);
		
		return $this->tree;
	}

}


?>