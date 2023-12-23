<?php

// the different types
define('T_KEYWORD', 0);
define('T_OPERATOR', 1);
define('T_IDENTIFIER', 2);
define('T_LITERAL', 3);
define('T_DELIM', 4);
define('T_COMMENT', 5);
define('T_EOL', 6);
define('T_PREC', 7);
define('T_STATL', 8);
define('T_WHITESP', 9);
define('T_LOGICAL', 10);
define('T_LABEL', 11);

class Token
{
	var $file;
	var $line;
	var $col;
	var $type;
	var $content;
	
	function Token($file, $line, $col, $type, $content)
	{
		$this->file = $file;
		$this->line = $line;
		$this->col = $col;
		$this->type = $type;
		$this->content = $content;
		
	}
	
}



?>