<?php


require_once 'nodes.php';

require_once 'error.php';

class Visitor
{
	var $tree;
	
	function Visitor($tree)
	{
		$this->tree = $tree;
	}
	
	function visit($tree)
	{
		if(is_object($tree))
		{
			$f_name = 'visit' . get_class($tree);
			$this->visitAlways($tree);
			if(method_exists($this, $f_name))
				call_user_func(array($this, $f_name), $tree);
			else
				$this->visitNode($tree);
				
			$this->visit($tree->tree);
			if(method_exists($this, 'leave' . get_class($tree)))
				call_user_func(array($this, 'leave' . get_class($tree)), $tree);
			else
				$this->leaveNode($tree);
			$this->leaveAlways($tree);
		}
		else
		{
			if(is_array($tree))
			{
				foreach($tree as $key => $object)
				{
					$this->visit($object);
				}
			}
		}
	}
	
	function visitAlways($tree)
	{
	}
	
	function leaveAlways($tree)
	{
	}
	
	function visitNode($tree)
	{
	}
	
	function leaveNode($tree)
	{
	}
}

class Analyzer extends Visitor
{
	var $typetable = array();
	var $functable = array();
	var $vartable = array();
	var $reverse_vartable = array();

	function analyze()
	{
		$collector = new Collector($this->tree);
		$collector->analyze();
		
		$this->typetable = $collector->typetable;
		$this->functable = $collector->functable;
		$this->vartable = $collector->vartable;
		$this->reverse_vartable = $collector->reverse_vartable;
		
		$this->visit($this->tree);
		//var_dump($this->typetable);
		//var_dump($this->vartable);
		//var_dump($this->functable);
		//print_r($this->tree);
		return $this->tree;
	}
	
	var $levelcounter = 0;
	function visitCommandList($tree)
	{
		$this->levelcounter++;
	}
	
	function leaveCommandList($tree)
	{
		$this->levelcounter--;
	}
	
	function getVar($level, $name)
	{
		$output = array();
		foreach($this->vartable as $index => $varinfo)
		{
			if($varinfo->name == $name)
			{
				if(count($output) == 0 || ($varinfo->level > $output->level && $varinfo->level <= $level))
					$output = $this->vartable[$index];
			}
		}
		return $output;
	}
	
	function getFunc($level, $name, $params = -1)
	{
		$output = array();
		foreach($this->functable as $index => $varinfo)
		{
			if($varinfo->name == $name && ($params == -1 || $varinfo->parameters == $params))
			{
				if(count($output) == 0 || ($varinfo->level > $output->level && $varinfo->level <= $level))
					$output = $this->functable[$index];
			}
		}
		return $output;
	}
	
	function leaveIfStatement($tree)
	{
		if($tree->tree['Expression']->type->index != 0)
		{
			$error = new CompileError($tree->token->file, $tree->token->line, $tree->token->col, 'Expression must be type Boolean (' . $tree->token->type . '): ' . $tree->token->content);
			$error->error_query();
		}
	}
	
	function visitIdentifier($tree)
	{
		$var = $this->getVar($this->levelcounter, $tree->tree['Identifier']);
		if(!is_object($var))
		{
			$var = $this->getFunc($this->levelcounter, $tree->tree['Identifier']);
			if(!is_object($var))
			{
				$error = new CompileError($tree->token->file, $tree->token->line, $tree->token->col, 'Var does not exist (' . $tree->token->type . '): ' . $tree->token->content);
				$error->error_query();
			}
			else
			{
				$tree->info = $var;
			}
		}
		else
		{
			$tree->info = $var;
		}
	}
	
	function visitFunctionCall($tree)
	{
		$func = $this->getFunc($this->levelcounter, $tree->tree['FunctionName']);
		if(!is_object($func))
		{
			$error = new CompileError($tree->token->file, $tree->token->line, $tree->token->col, 'Undefined function (' . $tree->token->type . '): ' . $tree->token->content);
			$error->error_query();
		}
		else
		{
			$tree->info = $func;
		}
	}
	
	function leaveForLoop($tree)
	{
		$varinfo = &$this->getVar($this->levelcounter, $tree->tree['Start']->tree['Assignment']['VariableName']->tree['Identifier']);
		$varinfo->type = $tree->tree['Start']->tree['Assignment']['Expression']->type;
	}
	
	function leaveKeyword($tree)
	{
		// assign initial type
		if($tree->tree['Keyword'] == 'v' || $tree->tree['Keyword'] == 'l')
		{
			$varinfo = &$this->getVar($this->levelcounter, $tree->tree['Declaration']->tree['Assignment']['VariableName']->tree['Identifier']);
			$varinfo->type = $tree->tree['Declaration']->tree['Assignment']['Expression']->type;
		}
	}
	
	function leaveExpression($tree)
	{
		//print_r($tree->tree);
		if(!isset($tree->tree['Operator']))
		{
			if(isset($tree->tree['Literal']->tree['BinaryLiteral'])) $tree->type = &$this->typetable[0];
			if(isset($tree->tree['Literal']->tree['IntLiteral'])) $tree->type = &$this->typetable[1];
			if(isset($tree->tree['Literal']->tree['RealLiteral'])) $tree->type = &$this->typetable[2];
			if(isset($tree->tree['Literal']->tree['HexLiteral'])) $tree->type = &$this->typetable[3];
			if(isset($tree->tree['Literal']->tree['StringLiteral'])) $tree->type = &$this->typetable[4];
		}
		elseif(!isset($tree->tree['VariableName']))
		{
			$type1 = 0;
			if(isset($tree->tree['Literal']->tree['BinaryLiteral'])) $type1 = 0;
			if(isset($tree->tree['Literal']->tree['IntLiteral'])) $type1 = 1;
			if(isset($tree->tree['Literal']->tree['RealLiteral'])) $type1 = 2;
			if(isset($tree->tree['Literal']->tree['HexLiteral'])) $type1 = 3;
			if(isset($tree->tree['Literal']->tree['StringLiteral'])) $type1 = 4;
			$type2 = 0;
			if(isset($tree->tree['Expression']->tree['Literal']->tree['BinaryLiteral'])) $type2 = 0;
			if(isset($tree->tree['Expression']->tree['Literal']->tree['IntLiteral'])) $type2 = 1;
			if(isset($tree->tree['Expression']->tree['Literal']->tree['RealLiteral'])) $type2 = 2;
			if(isset($tree->tree['Expression']->tree['Literal']->tree['HexLiteral'])) $type2 = 3;
			if(isset($tree->tree['Expression']->tree['Literal']->tree['StringLiteral'])) $type2 = 4;
			
			if(preg_match('/^(\<|\>|==|\<=|\>=|\|\||&&|\!=)$/', $tree->tree['Operator']) != 0) $tree->type = &$this->typetable[0];
			elseif($type1 > $type2) $tree->type = &$this->typetable[$type1];
			else $tree->type = &$this->typetable[$type2];
		}
		else
		{
			$type1 = $tree->tree['VariableName']->type->index;
			$type2 = 0;
			if(isset($tree->tree['Expression']->tree['Literal']->tree['BinaryLiteral'])) $type2 = 0;
			if(isset($tree->tree['Expression']->tree['Literal']->tree['IntLiteral'])) $type2 = 1;
			if(isset($tree->tree['Expression']->tree['Literal']->tree['RealLiteral'])) $type2 = 2;
			if(isset($tree->tree['Expression']->tree['Literal']->tree['HexLiteral'])) $type2 = 3;
			if(isset($tree->tree['Expression']->tree['Literal']->tree['StringLiteral'])) $type2 = 4;
			
			if(preg_match('/^(\<|\>|==|\<=|\>=|\|\||&&|\!=)$/', $tree->tree['Operator']) != 0) $tree->type = &$this->typetable[0];
			elseif($type1 > $type2) $tree->type = &$this->typetable[$type1];
			else $tree->type = &$this->typetable[$type2];
		}
	}
}

class Collector extends Analyzer
{
	function analyze()
	{
		$this->typetable = array(
			new TypeInfo(0, 'BinaryLiteral', 1),
			new TypeInfo(1, 'IntLiteral', 8),
			new TypeInfo(2, 'RealLiteral', 16),
			new TypeInfo(3, 'HexLiteral', 8),
			new TypeInfo(4, 'StringLiteral', 8)
		);
		$this->functable[] = new FunctionInfo(0, 0, 'p', 1, array());
		
		$this->visit($this->tree);
	}
	
	function visitParamDef($tree)
	{
		foreach($tree->tree['Params'] as $i => $identifier)
		{
			$this->vartable[] = new VarInfo(count($this->vartable), $tree->token, $this->levelcounter, $identifier->tree['Identifier']);
			$newvar = &$this->vartable[count($this->vartable)-1];
			$this->reverse_vartable[$newvar->name . $newvar->level] = $newvar;
		}
	}
	
	function visitFunctionDef($tree)
	{
		$this->functable[] = new FunctionInfo(count($this->functable), $this->levelcounter, $tree->tree['F-Name']->tree['Identifier'], count($tree->tree['ParamDef']->tree['Params']), $tree->tree['CommandList']);
	}
	
	function visitForLoop($tree)
	{
		// automatically transform this assignment into a declaration
		$this->vartable[] = new VarInfo(count($this->vartable), $tree->token, $this->levelcounter, $tree->tree['Start']->tree['Assignment']['VariableName']->tree['Identifier']);
		$newvar = &$this->vartable[count($this->vartable)-1];
		$this->reverse_vartable[$newvar->name . $newvar->level] = $newvar;
	}
	
	function visitKeyword($tree)
	{
		if($tree->tree['Keyword'] == 'v' || $tree->tree['Keyword'] == 'l')
		{
			if($tree->tree['Keyword'] == 'v')
			{
				$this->vartable[] = new VarInfo(count($this->vartable), $tree->token, 0, $tree->tree['Declaration']->tree['Assignment']['VariableName']->tree['Identifier']);
			}
			else
			{
				$this->vartable[] = new VarInfo(count($this->vartable), $tree->token, $this->levelcounter, $tree->tree['Declaration']->tree['Assignment']['VariableName']->tree['Identifier']);
			}
			$newvar = &$this->vartable[count($this->vartable)-1];
			$this->reverse_vartable[$newvar->name . $newvar->level] = $newvar;
		}
	}
	
	function leaveIfStatement($tree)
	{
	}
	function visitFunctionCall($tree)
	{
	}
	function leaveForLoop()
	{
	}
	function leaveKeyword()
	{
	}
	function leaveExpression()
	{
	}
	function visitIdentifier()
	{
	}
}

class TypeInfo
{
	var $name = '';
	var $index = 0;
	var $size = 8;
	
	function TypeInfo($index, $name, $size)
	{
		$this->index = $index;
		$this->name = $name;
		$this->size = $size;
	}
}

class VarInfo
{
	var $level = 0;
	var $token = array();
	var $index = 0;
	var $name = '';
	
	function VarInfo($index, $token, $level, $name)
	{
		$this->level = $level;
		$this->token = $token;
		$this->index = $index;
		$this->name = $name;
	}
}

class FunctionInfo
{
	var $level = 0;
	var $name = '';
	var $index = 0; // signature
	var $parameters = 0;
	var $body = array();
	
	function FunctionInfo($index, $level, $name, $parameters, $body)
	{
		$this->index = $index;
		$this->level = $level;
		$this->name = $name;
		$this->parameters = $parameters;
		$this->body = $body;
	}
}

?>