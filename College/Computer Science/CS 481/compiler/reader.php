<?php



class SourceReader
{
	var $file;
	
	var $fp;
	
	var $fpos = 0;
	
	var $buffer;
	
	const buffer_length = 2048;
	
	var $col = 1;
	
	var $line = 1;
	
	function SourceReader($file)
	{
		// store file name/path
		$this->file = $file;
		
		// open the file for reading
		$this->fp = fopen($this->file, 'r');
		
		// read a lot of information from the file
		if(!isset($this->buffer))
			$this->buffer = fread($this->fp, SourceReader::buffer_length);
	}
	
	function read()
	{
		if(!isset($this->fp))
			return -1;
	
		if(!feof($this->fp) || strlen($this->buffer) > 0)
		{
		
			// only read 1 character
			$this->buffer .= fread($this->fp, 1);
				
			// return the first character on the buffer
			$return = $this->buffer[0];
			$this->buffer = substr($this->buffer, 1);
			
			// special cases for modifying other things
			switch($return)
			{
				case chr(10):
					$this->line++;
					$this->col = 1;
				break;
				case chr(13):
					$this->col = 1;
				break;
				case chr(9):
					$return = ' ';
					$this->buffer = '       ' . $this->buffer;
				break;
				default:
					$this->col++;
			}
				
			return ord($return);
		}
		else
		{
			fclose($this->fp);
			
			unset($this->fp);
			
			return -1;
		}
	}
	
	function peek($offset)
	{
		if(feof($this->fp) && strlen($this->buffer) == 0)
		{
			fclose($this->fp);
			
			unset($this->fp);
		}
	
		if(!isset($this->fp))
			return -1;
			
		// return the character at the specified position without modifying the buffer
		return ord($this->buffer[$offset - 1]);
	}
	
	function pos()
	{
		return array('line' => $this->line, 'col' => $this->col);
	}
	
	
	
}



?>