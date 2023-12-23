<?php

require_once 'reader.php';
require_once 'token.php';
require_once 'error.php';

class Scanner
{
	var $file;
	var $tokens = array();
	
	function Scanner($file)
	{
		$this->file = $file;
		
		$reader = new SourceReader($this->file);

		$buffer = '';
		$start = 1; # starting column
		while(true)
		{
			$int = $reader->peek(1);
			
			// these are not regular expressions for reading in tokens, they are just used to verify token content
			if($int == -1)
			{
				break;
			}
			// check if it is a literal
			elseif(preg_match('/^(i[0-9]+|r[0-9]+\.?[0-9]*|h[ABCDEF0-9]+|b[01]+|[0-9]*\.?[0-9]*|\"[^\"]*\"|\'[^\']*\')$/', $buffer . chr($int)) != 0)
			{
				$type = T_LITERAL;
				$buffer .= chr($reader->read());
			}
			elseif(preg_match('/^[a-zA-Z][a-zA-Z0-9]*$/', $buffer . chr($int)) != 0)
			{
				// always assume it is an identifier until proven otherwise
				$type = T_IDENTIFIER;
				
				// check if it is a keyword
				if(preg_match('/^(v|c|l|p|pc|f|fo|w|re|if|el)$/', $buffer . chr($int)) != 0)
				{
					$type = T_KEYWORD;
				}
				$buffer .= chr($reader->read());
			}
			// check if it is an operator
			elseif(preg_match('/^(\+|-|\*|\/|\^|=)$/', $buffer . chr($int)) != 0)
			{
				$type = T_OPERATOR;
				$buffer .= chr($reader->read());
			}
			// check for boolean and logical operators
			elseif(preg_match('/^(\<|\>|==|\<=|\>=|\|\||&&|\!=)$/', $buffer . chr($int)) != 0)
			{
				$type = T_LOGICAL;
				$buffer .= chr($reader->read());
			}
			// check if it is some kind of white space delimiter
			elseif(preg_match('/^( *|\(|\)|\[|\]|,)$/', $buffer . chr($int)) != 0)
			{
				$type = T_WHITESP;
				// check for precidence tokens
				if($buffer . chr($int) == '(' || $buffer . chr($int) == ')')
				{
					$type = T_PREC;
				}
				// check for a statement list token
				elseif($buffer . chr($int) == '[' || $buffer . chr($int) == ']')
				{
					$type = T_STATL;
				}
				// check for delimiter
				elseif($buffer . chr($int) == ',')
				{
					$type = T_DELIM;
				}
				
				$buffer .= chr($reader->read());
			}
			elseif(preg_match('/^(\s*|\w*|\r*|\n*)$/', $buffer . chr($int)) != 0)
			{
				$type = T_EOL;
				$buffer .= chr($reader->read());
			}
			// check for comments
			elseif(preg_match('/^#.*[\r|\n]?$/', $buffer . chr($int)) != 0)
			{
				$type = T_COMMENT;
				$buffer .= chr($reader->read());
			}
			// clear out buffer then set it to new character
			else
			{
				//var_dump($buffer);
				if($buffer != '')
				{
					if($type != T_COMMENT && $type != T_WHITESP)
					{
						// create token
						$this->tokens[] = new Token($this->file, $reader->line, $start, $type, $buffer);
					}
					unset($type);
					$buffer = '';
					$start = $reader->col;
				}
				// must be an invalid character
				else
				{
					// create error
					$error = new CompileError($this->file, $reader->line, $reader->col, 'Invalid Character: ' . chr($int));
					$error->error_query();
				}


			}

		}
	}
}

?>