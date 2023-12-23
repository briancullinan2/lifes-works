<?PHP

function startMySQL()
{

	// create a mysql object
	if(isset($GLOBALS['mysql']))
	{
		return $GLOBALS['mysql'];
	}
	

	$mysql =& new sql(DB_SERVER, DB_USER, DB_PASS, DB_NAME);
	
	$GLOBALS['mysql'] = $mysql;

	return $mysql;
	
}

// not my class, pretty self explanitory.
class sql
{
	var $db_connect_id;
	var $query_result;
	var $rowset = array();
	var $num_queries = 0;


//=============================================
//  sql($SQL_server, $SQL_Username, $SQL_password, $SQL_database)
//=============================================
//  When the sql_db object is created it does a
//    few things
//  Variables for logging into the database are
//    passed through
//  Also switch to needed table if it is defined
//=============================================
	function sql($SQL_server, $SQL_username, $SQL_password, $SQL_db_name = "")
	{
		$this->db_connect_id = mysql_connect($SQL_server, $SQL_username, $SQL_password) or printError(mysql_error() . 'MySQL Login Error');

		printError($this->db_connect_id, 'MySQL Start');
		if ($SQL_db_name != "")
		{
			mysql_select_db($SQL_db_name, $this->db_connect_id) or printError(mysql_error(), 'MySQL Select DB Error');
		}
	}

    // loops through all rows that it returns and puts it into a nicely formated array.
    // do query() and then result(), and that will return all the results from the last query.
	function result()
	{
		// make rowset empty array
		$this->rowset[$this->query_result] = array();
		
		// check for error
		$mysql_error = mysql_error();
		
		// loop while there are still rows to read
		// also if error is blank
		if($mysql_error == '')
		{
			while($row = mysql_fetch_assoc($this->query_result))
			{
				$this->rowset[$this->query_result][] = $row;
			}
		}
		
		// return rows
		return $this->rowset[$this->query_result];
	}

//=============================================
//  query($query = "")
//=============================================
//  Just a handler for SQL queries specific to the objects connect id
//=============================================
	function query($query = "")
	{
		
		printError($query . '<br />', 'MySQL Query');
		
		// Remove any pre-existing queries
		unset($this->query_result);
		if($query != "")
		{
			$this->num_queries++;

			$this->query_result = mysql_query($query, $this->db_connect_id) or printError(mysql_error(), 'MySQL Query Error');
		}
		if($this->query_result)
		{
			unset($this->rowset[$this->query_result]);
			$this->rowset[$this->query_result] = array();
			
			return $this->query_result;
		}
		else
		{
			return false;
		}
	}

	
	function close()
	{
		printError($this->db_connect_id, 'MySQL Close');
		mysql_close($this->db_connect_id);
	}
	
	//===========================================================
	//-----------------------------------------------------------
	// Begin new stuff that's custom, and just for this project.
	//-----------------------------------------------------------
	//===========================================================
	
	function getAllUsers($properties)
	{
		// set up where clause for each argument that is set
		$where_clause = '';
	
		// loop through each property
		foreach($properties as $key => $value)
		{
			// if the value is not blank
			if($value != '')
			{
				// add the $key = $value to where_clause
				$where_clause .= (($where_clause == '')?'':'AND ') . 
					$key . ' = "' . $value . '" ';
			}
		}
		
		// combine the query together, including its WHERE clause
		$query = 'SELECT * FROM users WHERE ' . $where_clause;
		
		// run the query
		$this->query($query);
		
		// return the results of the query, in the form of a 2D array
		return $this->result();
	}
	
	//-----------------------------------------------------------
	// Gets the users who match these criteria.
	//-----------------------------------------------------------	
	function getUsers($username = '', $password = '', $first_name = '', $last_name = '', $email = '')
	{
		
		// set properties array
		$properties = array();
		
		// set the properties based on column names
		$properties['Username'] = $username;
		$properties['Pass'] = $password;
		$properties['Firstname'] = $first_name;
		$properties['Lastname'] = $last_name;
		$properties['Email'] = $email;
		
		// return function call
		return $this->getAllUsers($properties);
	}
	
	//-----------------------------------------------------------
	// Returns a single user, in the form of an array of attributes.
	// Returns nothing if no result could be found.
	//-----------------------------------------------------------
	function getUser($username = '', $password = '', $first_name = '', $last_name = '', $email = '')
	{		
		$users = $this->getUsers($username, $password, $first_name, $last_name, $email);
		if(isset($users[0]))
		{
			return $users[0];
		}
		else
		{
			return;
		}
	}
	
	//-----------------------------------------------------------
	// Adds a user to the database, or edits an existing one.
	// A user name must be specified.
	//-----------------------------------------------------------	
	function setUser($username, $password = '', $first_name = '', $last_name = '', $email = '') {
		
		// create a new user if one does not already exist
		$existingUser = getUser($username);
		if (isset($existingUser)) { // user must be edited
			$query = "UPDATE users SET Pass='" . $password . "', Firstname='" . $first_name . "', Lastname='" . $last_name . ", Email='" . $email . "'  WHERE Username='" . $username . "'";
			$this->query($query);
		} else { // user must be created
			$query = "INSERT INTO users (Firstname, Lastname, Pass, Username, Email, Created) VALUES ('" . $first_name . "', '" . $last_name . "', '" . $password . "', '" . $username . "', '" . $email . "', NOW())";
			$this->query($query);
		}
	}

	//-------------------------------------------------------
	// Returns all the survey results for a given survey id.
	//-------------------------------------------------------
	function getAllResults($properties)
	{
		// set up where clause for each argument that is set
		$where_clause = '';
	
		// loop through each property
		foreach($properties as $key => $value)
		{
			// if the value is not blank
			if($value != '')
			{
				// add the $key = $value to where_clause
				$where_clause .= (($where_clause == '')?'':'AND ') . 
					$key . ' = "' . $value . '" ';
			}
		}
		
		// combine the query together, including its WHERE clause
		$query = 'SELECT * FROM results WHERE ' . $where_clause;
		
		// run the query
		$this->query($query);
		
		// return the results of the query, in the form of a 2D array
		return $this->result();
	}

	// get results
	function getResults($survey_id = '')
	{
		
		// set properties array
		$properties = array();
		
		// set the properties based on column names
		$properties['Surveyid'] = $survey_id;
		
		// return function call
		return $this->getAllResults($properties);
	}
		
	//-----------------------------------------------------------
	// Returns surveys that mactch the criteria, in a 2D array.
	//-----------------------------------------------------------
	function getAllSurveys($properties)
	{
		// set up where clause for each argument that is set
		$where_clause = '';
	
		// loop through each property
		foreach($properties as $key => $value)
		{
			// if the value is not blank
			if($value != '')
			{
				// add the $key = $value to where_clause
				$where_clause .= (($where_clause == '')?'':'AND ') . 
					$key . ' = "' . $value . '" ';
			}
		}
		
		// combine the query together, including its WHERE clause
		$query = 'SELECT * FROM surveys WHERE ' . $where_clause;
		
		// run the query
		$this->query($query);
		
		// return the results of the query, in the form of a 2D array
		return $this->result();
	}

		
	//----------------------------------------------------------
	// Gets the surveys that match these criteria.
	//----------------------------------------------------------
	function getSurveys($name = '', $username = '')
	{
		
		// set properties array
		$properties = array();
		
		// set the properties based on column names
		$properties['Name'] = $name;
		$properties['Username'] = $username;
		
		// return function call
		return $this->getAllSurveys($properties);
	}
	
	//-----------------------------------------------------------
	// Returns a single survey, as an array of attributes.
	// Returns nothing if no result could be found.
	//-----------------------------------------------------------
	function getSurvey($name = '', $username = '', $id = '')
	{		
		// set properties array
		$properties = array();
		
		// set the properties based on column names
		$properties['Name'] = $name;
		$properties['Username'] = $username;
		$properties['Surveyid'] = $id;
		// get the surveys
		$surveys = $this->getAllSurveys($properties);
		if(isset($surveys[0]))
		{
			return $surveys[0];
		}
		else
		{
			return;
		}
	}

	//-----------------------------------------------------------
	// Adds a survey to the database, or edits an existing one.
	// Only the xmlstring can be edited.
	//-----------------------------------------------------------	
	function setSurvey($name, $username, $xmlstring) {
		
		// create a new entry if this does not already exist
		// ($name + $username) is a candidate key
		// both of these must be set.
		//$existingSurvey = getSurvey($name, $username);
		if (isset($existingSurvey)) { // survey must be edited
			$query = "UPDATE surveys SET Xmlstring='" . $xmlstring . "' WHERE Name='" . $name . "' and Username='" . $username . "'";
			$this->query($query);
		} else { // survey must be created
			$query = "INSERT INTO surveys (Name, Username, Xmlstring) VALUES ('" . $name . "', '" . $username . "', '" . $xmlstring . "')";
			$this->query($query);
		}
	}
	
	// sets a result
	function setResult($survey_id, $xmlstring)
	{
			$query = "INSERT INTO results (Surveyid, Xmlstring) VALUES ('" . $survey_id . "', '" . $xmlstring . "')";
			$this->query($query);
	}
	
	function deleteSurvey($name = '', $username = '', $id = '') {
		// choose to delete by id or by name + username
		if ($id == '') { // delete by name + username
			$query = "DELETE FROM surveys WHERE Username='" . $username . "' and Name='" . $name . "'";
			$this->query($query);
		} else { // delete by id
			$query = "DELETE FROM surveys WHERE Surveyid='" . $id . "'";
			$this->query($query);
		}
	}
	
// end class
}
?>
