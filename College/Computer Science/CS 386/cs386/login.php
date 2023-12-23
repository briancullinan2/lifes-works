<?php

// Created by Travis Hudson (November 18, 2007)

session_start();

// include required functions
require 'include/common.php';

// start MySQL
startMySQL();
$mysql =& $GLOBALS['mysql'];

// get the template variable
startTemplate();
$template =& $GLOBALS['template'];

// This is where we load the template files required for this page
$template->setFile('login', SITE_TEMPLATE . 'edit/login.html');

// code here

// check for a post
if(isset($_POST["username"]) && isset($_POST["password"]))
{
	$_SESSION['Username'] = $_POST["username"];
	$_SESSION['Pass'] = $_POST["password"];
	
	$login = checkLogin($error, $user);
	
	if($login == true)
	{
		header("Location: manage.php");
		
		exit();
	}
	else
	{
		$template->setVar('LOGIN_ERROR', $error);
	}
}
/*
$template->setVar('LOGIN_VERDICT', getLoginVerdict($mysql));

$template->setVar('DEBUG_INFO', 'erase this line');

// Returns 'return 0' if login information is incorrect.
// 'return 1' if correct.
// 'return 2' if some fields aren't filled in yet.
function getLoginVerdict($mysql) {

    // Put the posted password in a variable, or ""
    // if there isn't one.
    $password = "";
    if (isset($_POST["password"])) {
        $password = $_POST["password"];
    }
    
    // Store the username as well.
    $username = "";
    if (isset($_POST["username"])) {
        $username = $_POST["username"];
    }

    // If either of these is empty, then do nothing
    if ($username == "" || $password == "") {
        return "return 2;\n\n";
    } else {
    
       // Retrieve the necessary database information
       $query = "SELECT Username, Pass FROM users WHERE Username='$username' and Pass='$password'";

       $mysql->query($query);
       $rawResult = $mysql->result();
       if (sizeof($rawResult) == 0) {
          return "return 0;\n\n";
       } else {
          return "return 1;\n\n";
          
          // record their username and the fact that they are now logged in, within the session variable
          $_SESSION['Username'] = $username;
          $_SESSION['Pass'] = $password;
          
          $template->setVar('DEBUG_INFO', $username);
       }
    }

}
*/   	
// print out page
print($template->fParse('out', 'login'));

// Use print_r() to print out the value of a variable or array.
// dollar sign preceeds every variable
// & is a pointer
// the =& makes sure it only loads that object, instead of making a copy of it
// @ is suppress errors (put it next to any function call)
?>
