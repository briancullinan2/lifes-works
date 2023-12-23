<?php


require_once 'reader.php';

class CompileError
{

	var $file;
	
	var $line;
	
	var $col;
	
	var $msg;
	
	function CompileError($file, $line, $col, $msg)
	{
		$this->file = $file;
		$this->line = $line;
		$this->col = $col;
		$this->msg = $msg;
	}
	
	function query()
	{
		return 'line ' . $this->line . ', col ' . $this->col;
	}
	
	function line_query()
	{
		// open the file and get the specified line
		$reader = new SourceReader($this->file);

		while($reader->line < $this->line)
		{
			$int = $reader->read();
			if($int == -1)
				break;
		}
		
		$line = '';
		
		while($reader->line == $this->line)
		{
			$int = $reader->read();
			if($int == -1)
				break;
			$line .= chr($int);
		}
		if(strpos($line, "\n") === false) $line .= "\n";
	
		return $line . sprintf('%' . ($this->col - 1) . 's', '') . '^';
	}

	function error_query()
	{
		trigger_error('At ' . $this->query() . ': ' . $this->msg . "\n\n" . $this->line_query() . "\n", E_USER_ERROR);
	}

}


?>