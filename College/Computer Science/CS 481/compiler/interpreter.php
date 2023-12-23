<?php

class VM
{
	var $stack = array();
	var $global = array();
	var $code = '';
	
	var $ip = 0;
	var $sp = 0;
	var $fp = 0;
	
	function VM($file, $is_file = true)
	{
		// read in file and seperate commands into array
		if($is_file)
		{
			$fp = fopen($file, 'r');
			$file = fread($fp, filesize($file));
			fclose($fp);
		}

		$this->code = preg_split('//', $file, -1);
		for($i = 0; $i < count($this->code); $i++)
		{
			$this->code[$i] = dechex(ord($this->code[$i]));
		}
		
		ob_start();
		
		// read the informational part of the file
		// make sure it is a small vm file
		//var_dump($this->code);
		$count = 0;
		if(next($this->code) == '53' && next($this->code) == '4d' && next($this->code) == '56' && next($this->code) == '4d')
		{
			$version = next($this->code) . next($this->code) . next($this->code) . next($this->code);
			$var_count = next($this->code) . next($this->code) . next($this->code) . next($this->code);
			$code_length = next($this->code) . next($this->code) . next($this->code) . next($this->code);
			
			while(($op_code = next($this->code)) !== false)
			{
				$this->ip = key($this->stack);
				switch(hexdec($op_code))
				{
					case 0x5A: // push
						next($this->code);
						array_push($this->stack, hexdec(next($this->code) . next($this->code) . next($this->code) . next($this->code)));
					break;
					case 0x5B: // pop
						next($this->code);
						$index = hexdec(next($this->code) . next($this->code) . next($this->code) . next($this->code));
						$this->global[$index] = array_pop($this->stack);
					break;
					case 0x4B: // gsave index
						next($this->code);
						$index = hexdec(next($this->code) . next($this->code) . next($this->code) . next($this->code));
						$this->global[$index] = array_pop($this->stack);
					break;
					case 0x3C: // call
						next($this->code);
						$index = hexdec(next($this->code) . next($this->code) . next($this->code) . next($this->code));
						array_push($this->stack, key($this->code));
						$this->fp = count($this->stack);
						reset($this->code);
						for($i = 0; $i < $index-1; $i++) next($this->code);
					break;
					case 0x3D: // return
						if(count($this->stack) == 0) return;
						$index = array_pop($this->stack);
						reset($this->code);
						for($i = 0; $i < $index; $i++) next($this->code);
					break;
					case 0x3B: // jumpif
						next($this->code);
						$condition = hexdec(next($this->code) . next($this->code) . next($this->code) . next($this->code));
						next($this->code);
						$address = hexdec(next($this->code) . next($this->code) . next($this->code) . next($this->code));
					break;
					case 0x3E: // jlt
						$rhs = array_pop($this->stack);
						$lhs = array_pop($this->stack);
						next($this->code);
						$index = hexdec(next($this->code) . next($this->code) . next($this->code) . next($this->code));
						if($lhs < $rhs)
						{
							reset($this->code);
							for($i = 0; $i < $index; $i++) next($this->code);
						}
					break;
					case 0x3F: // jgt
						$rhs = array_pop($this->stack);
						$lhs = array_pop($this->stack);
						next($this->code);
						$index = hexdec(next($this->code) . next($this->code) . next($this->code) . next($this->code));
						if($lhs > $rhs)
						{
							reset($this->code);
							for($i = 0; $i < $index; $i++) next($this->code);
						}
					break;
					case 0x4A: // gload
						next($this->code);
						$index = hexdec(next($this->code) . next($this->code) . next($this->code) . next($this->code));
						array_push($this->stack, $this->global[$index]);
					break;
					case 0x01: // print
						$value = array_pop($this->stack);
						print $value;
					break;
					case 0x02: // printc
						$value = array_pop($this->stack);
						print chr($value);
					break;
					case 0x7A: // add
						array_push($this->stack, array_pop($this->stack) + array_pop($this->stack));
					break;
					case 0x7B: // sub
						array_push($this->stack, array_pop($this->stack) - array_pop($this->stack));
					break;
					case 0x7C: // mult
						array_push($this->stack, array_pop($this->stack) * array_pop($this->stack));
					break;
					case 0x7D: // div
						array_push($this->stack, array_pop($this->stack) / array_pop($this->stack));
					break;
					case 0x7E: // pow
						array_push($this->stack, pow(array_pop($this->stack), array_pop($this->stack)));
					break;
					case 0x3A: // jump
						next($this->code);
						$index = hexdec(next($this->code) . next($this->code) . next($this->code) . next($this->code));
						reset($this->code);
						for($i = 0; $i < $index; $i++) next($this->code);
					break;
					case 0x00: // noop
						
					break;
					default:
					
				}
				
				$this->sp = count($this->stack);
				ob_flush();
			}
		}
		else
		{
			ob_end_flush();
			return;
		}
		
	}
}


?>
