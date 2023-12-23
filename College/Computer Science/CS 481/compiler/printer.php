<?php

require_once 'visitor.php';


class PrinterVisitor extends Visitor
{
	function visit($tree)
	{
		if(is_object($tree))
		{
			print get_class($tree) . '(';
			$f_name = 'visit' . get_class($tree);
			if(method_exists($this, $f_name))
				call_user_func(array($this, $f_name), $tree);
			else
				$this->visitNode($tree);
			$this->visit($tree->tree);
			print ')';
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
	
	function visitNode($tree)
	{
		
	}
	
	function visitIdentifier($tree)
	{
		print $tree->tree['Identifier'];
	}
	
	function visitLiteral($tree)
	{
		print current($tree->tree['Literal']);
	}
}


?>