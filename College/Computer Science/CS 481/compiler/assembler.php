<?php

require_once 'reader.php';
require_once 'token.php';
require_once 'error.php';
require_once 'visitor.php';


class BinaryOutput extends Visitor
{
	var $named = array();
	var $output = '';
	var $named_counter = 12345678;
	var $unknowns = array();
	
	function assemble()
	{
		$this->visit($this->tree);
		
		$this->output = '534d564d' . str_pad(dechex(1), 8, 0, STR_PAD_LEFT) . str_pad(dechex(count($this->named)), 8, 0, STR_PAD_LEFT) . str_pad(dechex(strlen($this->output)), 8, 0, STR_PAD_LEFT) . $this->output;
		
		$output = '';
		for($i = 0; $i < strlen($this->output) / 2; $i++)
		{
			$output .= chr(hexdec(substr($this->output, $i*2, 2)));
		}
		
		return $output;
	}
	
	function visitAIdentifier($tree)
	{
		if(!isset($this->named[$tree->token->content]))
		{
			$this->named[$tree->token->content] = $this->named_counter;
			$this->named_counter++;
		}
		$this->unknowns[$this->named[$tree->token->content]][] = strlen($this->output);
		$i = dechex($this->named[$tree->token->content]);
		$this->output .= '01' . str_pad($i, 8, 0, STR_PAD_LEFT);
	}
	
	function visitALabel($tree)
	{
		$tree->token->content = substr($tree->token->content, 0, strlen($tree->token->content) - 1);
		$previous_index = $this->named[$tree->token->content];
		$this->named[$tree->token->content] = strlen($this->output) / 2 + 16; // add the header to the positions
		// fix all the previous references to the label
		if(isset($this->unknowns[$previous_index]))
		{
			$new_address = dechex($this->named[$tree->token->content]);
			foreach($this->unknowns[$previous_index] as $i => $address)
			{
				$this->output = substr_replace($this->output, '01' . str_pad($new_address, 8, 0, STR_PAD_LEFT), $address, 10);
			}
		}
	}
	
	function visitAKeyword($tree)
	{
		switch($tree->token->content)
		{
			case '32load': $this->output .= '0A'; break;
			case '64load': $this->output .= '1A'; break;
			case 'save': $this->output .= '2A'; break;
			case 'load': $this->output .= '2B'; break;
			case 'move': $this->output .= '2C'; break;
			case 'jump': $this->output .= '3A'; break;
			case 'jumpif': $this->output .= '3B'; break;
			case 'call': $this->output .= '3C'; break;
			case 'return': $this->output .= '3D'; break;
			case 'jlt': $this->output .= '3E'; break;
			case 'jgt': $this->output .= '3F'; break;
			case 'gload': $this->output .= '4A'; break;
			case 'gsave': $this->output .= '4B'; break;
			case 'push': $this->output .= '5A'; break;
			case 'pop': $this->output .= '5B'; break;
			case 'peek': $this->output .= '5C'; break;
			case 'add': $this->output .= '7A'; break;
			case 'sub': $this->output .= '7B'; break;
			case 'xor': $this->output .= '6A'; break;
			case 'or': $this->output .= '6B'; break;
			case 'and': $this->output .= '6C'; break;
			case 'not': $this->output .= '6D'; break;
			case 'mul': $this->output .= '7C'; break;
			case 'div': $this->output .= '7D'; break;
			case 'pow': $this->output .= '7E'; break;
			case 'noop': $this->output .= '00'; break;
			case 'print': $this->output .= '01'; break;
			case 'printc': $this->output .= '02'; break;
		}
		
	}
	
	function visitALiteral($tree)
	{
		if(isset($tree->tree['BinaryLiteral'])) $this->output .= '00' . str_pad(dechex($tree->tree['BinaryLiteral']), 8, 0, STR_PAD_LEFT);
		if(isset($tree->tree['IntLiteral'])) $this->output .= '01' . str_pad(dechex($tree->tree['IntLiteral']), 8, 0, STR_PAD_LEFT);
		if(isset($tree->tree['RealLiteral'])) $this->output .= '02' . str_pad(dechex($tree->tree['RealLiteral']), 16, 0, STR_PAD_LEFT);
		if(isset($tree->tree['HexLiteral'])) $this->output .= '03' . str_pad($tree->tree['HexLiteral'], 8, 0, STR_PAD_LEFT);
		if(isset($tree->tree['StringLiteral'])) $this->output .= '04' . str_pad(dechex(strlen($tree->tree['StringLiteral'])), 8, 0, STR_PAD_LEFT) . $tree->tree['StringLiteral'];
	}
}

class Assembler
{
	var $file;
	var $tokens = array();
	
	function Assembler($file)
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
			// instruction
			elseif(preg_match('/^(32load|64load|save|load|move|jump|jumpif|call|return|jlt|jgt|gload|gsave|push|pop|peek|add|sub|xor|or|and|not|mul|div|pow|noop|print|printc)$/', $buffer . chr($int)) != 0)
			{
				$type = T_KEYWORD;
				$buffer .= chr($reader->read());
			}
			// match named stuff
			elseif(preg_match('/^[a-zA-Z][a-zA-Z0-9]*$/', $buffer . chr($int)) != 0)
			{
				// always assume it is an identifier until proven otherwise
				$type = T_IDENTIFIER;
				$buffer .= chr($reader->read());
			}
			// match labels
			elseif(preg_match('/^[a-zA-Z][a-zA-Z0-9]*:$/', $buffer . chr($int)) != 0)
			{
				// always assume it is an identifier until proven otherwise
				$type = T_LABEL;
				$buffer .= chr($reader->read());
			}
			// check if it is some kind of white space delimiter
			elseif(preg_match('/^( *|\(|\)|\[|\]|,)$/', $buffer . chr($int)) != 0)
			{
				$type = T_WHITESP;
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

class VMParser
{
	var $token = array();
	var $tree = array();
	
	function accept(&$list)
	{
		//var_dump(current($list)->type);
		$this->token = current($list);
		next($list);
	}
	
	function parseVM(&$list)
	{
		reset($list);
		
		$program = new ACommandList();
		$this->tree['VM'] = $program->parseCommandList($list);
		
		return $this;
	}
	
	function make_error(&$list)
	{
		// create error
		$error = new CompileError(current($list)->file, current($list)->line, current($list)->col, 'Invalid Token (' . current($list)->type . '): ' . current($list)->content);
		$error->error_query();
	}
}

class ALabel extends VMParser
{
	
	function parseLabel(&$list)
	{
		if(current($list)->type == T_LABEL)
		{
			$this->tree['Label'] = current($list)->content;
			$this->accept($list);
		}
		else
		{
			$this->make_error(&$list);
		}
		
		return $this;
	}
}

class AIdentifier extends VMParser
{
	
	function parseIdentifier(&$list)
	{
		if(current($list)->type == T_IDENTIFIER)
		{
			$this->tree['Identifier'] = current($list)->content;
			$this->accept($list);
		}
		else
		{
			$this->make_error(&$list);
		}
		
		return $this;
	}
}

class AExpression extends VMParser
{
	function parseExpression(&$list)
	{
		if(current($list)->type == T_LITERAL)
		{
			$literal = new ALiteral();
			$this->tree['Value'] = $literal->parseLiteral($list);
		}
		elseif(current($list)->type == T_IDENTIFIER)
		{
			$identifier = new AIdentifier();
			$this->tree['Value'] = $identifier->parseIdentifier($list);
		}
		else
		{
			$this->make_error($list);
		}
		
		return $this;
	}
}


class ALiteral extends VMParser
{
	
	function parseLiteral(&$list)
	{
		if(current($list)->type == T_LITERAL)
		{
			// first check front end for type declaration
			switch(current($list)->content[0])
			{
				case 'i':
					$this->parseIntLiteral($list, true);
				break;
				case 'r':
					$this->parseRealLiteral($list, true);
				break;
				case 'h':
					$this->parseHexLiteral($list);
				break;
				case 'b':
					$this->parseBinaryLiteral($list);
				break;
				default:
					if(is_numeric(current($list)->content) && preg_match('/^[0-9]*$/', current($list)->content) != 0)
					{
						$this->parseIntLiteral($list);
					}
					elseif(is_numeric(current($list)->content))
					{
						$this->parseRealLiteral($list);
					}
					else
					{
						$this->parseStringLiteral($list);
					}
			}
			$this->accept($list);
		}
		else
		{
			$this->make_error(&$list);
		}
		
		return $this;
	}

	function parseIntLiteral(&$list, $haspreceder = false)
	{
		$this->tree['IntLiteral'] = ($haspreceder)?substr(current($list)->content, 1):current($list)->content;
	}
	
	function parseRealLiteral(&$list, $haspreceder = false)
	{
		$this->tree['RealLiteral'] = ($haspreceder)?substr(current($list)->content, 1):current($list)->content;
	}
	
	function parseHexLiteral(&$list)
	{
		$this->tree['HexLiteral'] = substr(current($list)->content, 1);
	}
	
	function parseBinaryLiteral(&$list)
	{
		$this->tree['BinaryLiteral'] = substr(current($list)->content, 1);
	}
	
	function parseStringLiteral(&$list)
	{
		$this->tree['StringLiteral'] = current($list)->content;
	}
}


class AKeyword extends VMParser
{

	function parseKeyword(&$list)
	{
		switch(current($list)->content)
		{
			case '32load':
			case 'save':
			case 'load':
			case 'move':
			case 'call':
			case 'gload':
			case 'gsave':
			case 'push':
			case 'pop':
			case 'peek':
			case 'not':
			case 'jump':
			case 'jlt':
			case 'jgt':
				$this->tree['Keyword'] = current($list)->content;
				$this->accept($list);
				$expression = new AExpression();
				$this->tree['Arg1'] = $expression->parseExpression($list);
			break;
			case '64load':
			case 'jumpif':
				$this->tree['Keyword'] = current($list)->content;
				$this->accept($list);
				$expression = new AExpression();
				$this->tree['Arg1'] = $expression->parseExpression($list);
				$expression = new AExpression();
				$this->tree['Arg2'] = $expression->parseExpression($list);
			break;
			case 'add':
			case 'sub':
			case 'xor':
			case 'or':
			case 'and':
			case 'mul':
			case 'div':
			case 'pow':
			case 'return':
			case 'noop':
			case 'print':
			case 'printc':
				$this->tree['Keyword'] = current($list)->content;
				$this->accept($list);
			break;
			default:
				$this->make_error(&$list);
		}

		return $this;
	}
}

class ACommand extends VMParser
{
	function parseCommand(&$list)
	{
		switch(current($list)->type)
		{
			case T_KEYWORD:
				$keyword = new AKeyword();
				$this->tree['Command'] = $keyword->parseKeyword($list);
				if(current($list)->type == T_EOL) $this->accept($list);
			break;
			case T_LABEL:
				$label = new ALabel();
				$this->tree['Label'] = $label->parseLabel($list);
				if(current($list)->type == T_EOL) $this->accept($list);
			break;
			default:
				$this->make_error(&$list);
		}
		
		return $this;
	}
}

class ACommandList extends VMParser
{
	function parseCommandList(&$list)
	{
		if(current($list)->type == T_EOL) $this->accept($list);

		do
		{
			// parse a command
			$continue = true;
			$command = new ACommand();
			$this->tree['CommandList'][] = $command->parseCommand($list);
		}while($continue && current($list) !== false);
		
		return $this;
	}
}


?>
