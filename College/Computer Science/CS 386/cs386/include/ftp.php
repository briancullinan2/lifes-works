<?PHP

function startFTP()
{

	// create a mysql object
	if(isset($GLOBALS['ftp']))
	{
		return $GLOBALS['ftp'];
	}

	$ftp =& new ftp(FTP_SERVER);
	$ftp->login(FTP_USER, FTP_PASS);
	
	$GLOBALS['ftp'] = $ftp;

	return $ftp;
	
}

class ftp
{
	var $connect_id;
	
	function ftp($FTP_server)
	{
		$this->connect_id = ftp_connect($FTP_server);
		printError($this->connect_id, 'FTP Start');
	}
	
	function login($FTP_USER, $FTP_PASS)
	{
		ftp_login($this->connect_id, FTP_USER, FTP_PASS);
		printError($FTP_USER . ',' . $FTP_PASS, 'FTP Login');
	}
	
	function put($remote, $local)
	{
		ftp_put($this->connect_id, $remote, $local, FTP_BINARY);
		printError($local . ' ===> ' . $remote, 'FTP Put');
	}
	
	function delete($remote)
	{
		ftp_delete($this->connect_id, $remote);
		printError($remote, 'FTP Delete');
	}
	
	function close()
	{
		ftp_close($this->connect_id);
		printError('Connection Closed', 'FTP Close');
	}
	
}

?>
