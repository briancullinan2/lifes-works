<?php

class AST
{
	var $token = array();
	var $info = NULL;
	var $tree = array();
	
	function accept(&$list)
	{
		//var_dump(current($list)->type);
		$this->token = current($list);
		next($list);
	}
	
	function parseAST(&$list)
	{
		reset($list);
		
		$program = new Program();
		$this->tree['AST'] = $program->parseProgram($list);
		
		return $this;
	}
	
	function make_error(&$list)
	{
		// create error
		$error = new CompileError(current($list)->file, current($list)->line, current($list)->col, 'Invalid Token (' . current($list)->type . '): ' . current($list)->content);
		$error->error_query();
	}
}

class Literal extends AST
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

class Identifier extends AST
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


class Expression extends AST
{
	function parseExpression(&$list)
	{
		if(current($list)->type == T_LITERAL)
		{
			$literal = new Literal();
			$this->tree['Literal'] = $literal->parseLiteral($list);
			if(current($list)->type == T_OPERATOR || current($list)->type == T_LOGICAL)
			{
				$this->tree['Operator'] = current($list)->content;
				$this->accept($list);
				$expression = new Expression();
				$this->tree['Expression'] = $expression->parseExpression($list);
			}
		}
		elseif(current($list)->type == T_IDENTIFIER)
		{
			$identifier = new Identifier();
			$this->tree['VariableName'] = $identifier->parseIdentifier($list);
			if(current($list)->type == T_OPERATOR || current($list)->type == T_LOGICAL)
			{
				$this->tree['Operator'] = current($list)->content;
				$this->accept($list);
				$expression = new Expression();
				$this->tree['Expression'] = $expression->parseExpression($list);
			}
			else
			{
				//prev($list);
			}
		}
		elseif(current($list)->type == T_OPERATOR || current($list)->type == T_LOGICAL)
		{
			$this->tree['Operator'] = current($list)->content;
			$this->accept($list);
			$expression = new Expression();
			$this->tree['Expression'] = $expression->parseExpression($list);
		}
		else
		{
			$expression = new Expression();
			$this->tree['Expression1'] = $expression->parseExpression($list);
			if(current($list)->type == T_OPERATOR || current($list)->type == T_LOGICAL)
			{
				$this->tree['Operator'] = current($list)->content;
				$this->accept($list);
			}
			else
			{
				$this->make_error($list);
			}
			$expression = new Expression();
			$this->tree['Expression2'] = $expression->parseExpression($list);
		}
		
		return $this;
	}
}

class Assignment extends Command
{
	
	function parseAssignment(&$list)
	{
		$identifier = new Identifier();
		$this->tree['Assignment']['VariableName'] = $identifier->parseIdentifier($list);
		if(current($list)->type == T_OPERATOR && current($list)->content == '=')
		{
			$this->tree['Assignment']['Operator'] = current($list)->content;
			$this->accept($list);
		}
		else
		{
			$this->make_error($list);
		}
		$expression = new Expression();
		$this->tree['Assignment']['Expression'] = $expression->parseExpression($list);
		
		return $this;
	}
}

class ParamCall extends AST
{
	function parseParamCall(&$list)
	{
		do
		{
			$continue = false;
			$expression = new Expression();
			$this->tree['Params'][] = $expression->parseExpression($list);
			if(current($list)->type == T_DELIM)
			{
				$this->accept($list);
				$continue = true;
			}
		}while($continue);

		return $this;
	}
}

class FunctionCall extends Command
{
	function parseFunctionCall(&$list)
	{
		if(current($list)->type == T_IDENTIFIER)
		{
			$this->tree['FunctionName'] = current($list)->content;
			$this->accept($list);
			$paramcall = new ParamCall();
			$this->tree['ParamCall'] = $paramcall->parseParamCall($list);
		}
		
		return $this;
	}
	
}

class WhileLoop extends Command
{
	function parseWhileLoop(&$list)
	{
		if(current($list)->type == T_PREC) $this->accept($list);
		else $this->make_error($list);
		
		$expression = new Expression();
		$this->tree['Expression'] = $expression->parseExpression($list);
		
		if(current($list)->type == T_PREC) $this->accept($list);
		else $this->make_error($list);
		
		if(current($list)->type == T_EOL) $this->accept($list);
		
		$commandlist = new CommandList();
		$this->tree['CommandList'] = $commandlist->parseCommandList($list);
		
		return $this;
	}
}

class ForLoop extends Command
{
	function parseForLoop(&$list)
	{
		if(current($list)->type == T_PREC) $this->accept($list);
		else $this->make_error($list);
		
		$assignment = new Assignment();
		$this->tree['Start'] = $assignment->parseAssignment($list);
		
		if(current($list)->type == T_DELIM) $this->accept($list);
		else $this->make_error($list);
		
		$expression = new Expression();
		$this->tree['Expression'] = $expression->parseExpression($list);
		
		if(current($list)->type == T_DELIM) $this->accept($list);
		else $this->make_error($list);
		
		$assignment2 = new Assignment();
		$this->tree['Counter'] = $assignment2->parseAssignment($list);
		
		if(current($list)->type == T_PREC) $this->accept($list);
		else $this->make_error($list);
		
		if(current($list)->type == T_EOL) $this->accept($list);
		
		$commandlist = new CommandList();
		$this->tree['CommandList'] = $commandlist->parseCommandList($list);
		
		return $this;
	}
}

class ElseStatement extends Command
{
	function parseElseStatement(&$list)
	{		
		if(current($list)->type == T_EOL) $this->accept($list);
		
		$commandlist = new CommandList();
		$this->tree['CommandList'] = $commandlist->parseCommandList($list);
		
		return $this;
	}
}

class IfStatement extends Command
{
	function parseIfStatement(&$list)
	{
		if(current($list)->type == T_PREC) $this->accept($list);
		else $this->make_error($list);
		
		$expression = new Expression();
		$this->tree['Expression'] = $expression->parseExpression($list);
		
		if(current($list)->type == T_PREC) $this->accept($list);
		else $this->make_error($list);
		
		if(current($list)->type == T_EOL) $this->accept($list);
		
		$commandlist = new CommandList();
		$this->tree['CommandList'] = $commandlist->parseCommandList($list);
		
		return $this;
	}
}

class ParamDef extends AST
{
	function parseParamDef(&$list)
	{
		do
		{
			$continue = false;
			$identifier = new Identifier();
			$this->tree['Params'][] = $identifier->parseIdentifier($list);
			if(current($list)->type == T_DELIM)
			{
				$this->accept($list);
				$continue = true;
			}
		}while($continue);

		return $this;

	}
}

class FunctionDef extends Command
{
	function parseFunctionDef(&$list)
	{
		$identifier = new Identifier();
		$this->tree['F-Name'] = $identifier->parseIdentifier($list);
	
		if(current($list)->type == T_PREC) $this->accept($list);
		else $this->make_error($list);
		
		$paramdef = new ParamDef();
		$this->tree['ParamDef'] = $paramdef->parseParamDef($list);
		
		if(current($list)->type == T_PREC) $this->accept($list);
		else $this->make_error($list);
		
		if(current($list)->type == T_EOL) $this->accept($list);
		
		$commandlist = new CommandList();
		$this->tree['CommandList'] = $commandlist->parseCommandList($list);
		
		return $this;
	}
}

class Keyword extends AST
{

	function parseKeyword(&$list)
	{
		switch(current($list)->content)
		{
			case 'v':
			case 'c':
			case 'l':
				$this->tree['Keyword'] = current($list)->content;
				$this->accept($list);
				$assignment = new Assignment();
				$this->tree['Declaration'] = $assignment->parseAssignment($list);
			break;
			case 'p':
			case 'pc':
				$this->tree['Keyword'] = current($list)->content;
				$this->accept($list);
				$expression = new Expression();
				$this->tree['Print'] = $expression->parseExpression($list);
			break;
			case 'fo':
				$this->tree['Keyword'] = current($list)->content;
				$this->accept($list);
				$forloop = new ForLoop();
				$this->tree['ForLoop'] = $forloop->parseForLoop($list);
			break;
			case 'w':
				$this->tree['Keyword'] = current($list)->content;
				$this->accept($list);
				$whileloop = new WhileLoop();
				$this->tree['WhileLoop'] = $whileloop->parseWhileLoop($list);
			break;
			case 'elif':
			case 'if':
				$this->tree['Keyword'] = current($list)->content;
				$this->accept($list);
				$ifstatement = new IfStatement();
				$this->tree['IfStatement'] = $ifstatement->parseIfStatement($list);
			break;
			case 'el':
				$this->tree['Keyword'] = current($list)->content;
				$this->accept($list);
				$elsestatement = new ElseStatement();
				$this->tree['ElseStatement'] = $elsestatement->parseElseStatement($list);
			break;
			case 'f':
				$this->tree['Keyword'] = current($list)->content;
				$this->accept($list);
				$functiondef = new FunctionDef();
				$this->tree['FunctionDef'] = $functiondef->parseFunctionDef($list);
			break;
			default:
				$this->make_error(&$list);
		}

		return $this;
	}
}

class Command extends AST
{
	function parseCommand(&$list)
	{
		switch(current($list)->type)
		{
			case T_KEYWORD:
				$keyword = new Keyword();
				$this->tree['Command'] = $keyword->parseKeyword($list);
				if(current($list)->type == T_EOL) $this->accept($list);
			break;
			case T_IDENTIFIER:
				next($list);
				if(current($list)->type == T_OPERATOR && current($list)->content == '=')
				{
					prev($list);
					$assignment = new Assignment();
					$this->tree['Command'] = $assignment->parseAssignment($list);
				}
				else
				{
					prev($list);
					$functioncall = new FunctionCall();
					$this->tree['FunctionCall'] = $functioncall->parseFunctionCall($list);
				}
				if(current($list)->type == T_EOL) $this->accept($list);
			break;
			default:
				$this->make_error(&$list);
		}
		
		return $this;
	}
}

class CommandList extends Program
{
	function parseCommandList(&$list)
	{
		if(current($list)->type == T_STATL && current($list)->content == '[') $this->accept($list);
		else $this->make_error($list);
		
		if(current($list)->type == T_EOL) $this->accept($list);

		do
		{
			// block structure niftyness
			if(current($list)->type == T_STATL && current($list)->content == '[')
			{
				$commandlist = new CommandList();
				$this->tree[] = $commandlist->parseCommandList($list);
				continue;
			}
			// parse a command
			$continue = true;
			$command = new Command();
			$this->tree['CommandList'][] = $command->parseCommand($list);
			if(current($list)->type == T_STATL && current($list)->content == ']')
			{
				$this->accept($list);
				if(current($list)->type == T_EOL) $this->accept($list);
				$continue = false;
			}
		}while($continue && current($list) !== false);
		
		return $this;
	}
}

class Program extends AST
{
	function parseProgram(&$list)
	{
		$commandlist = new CommandList();
		$this->tree['Program'] = $commandlist->parseCommandList($list);
		return $this;
	}
	
}




?>