<?php

function checkLogin(&$error, &$user, $nochache = false)
{
	if(isset($GLOBALS['userinfo']) && $nochache == false)
	{
		// check if we want to log out
		if(isset($_SESSION['Username']) && isset($_SESSION['Pass']) && $_SESSION['Username'] != '' && $_SESSION['Pass'] != '')
		{
			$user = $GLOBALS['userinfo'];
			
			return true;
		}
		else
		{
			$error = '';

			unset($GLOBALS['userinfo']);
			
			return false;
		}
	}
	
	// first make sure the variables to compare are set
	if(isset($_SESSION['Username']) && isset($_SESSION['Pass']))
	{
		if($_SESSION['Username'] != '' && $_SESSION['Pass'] != '')
		{
			
			require_once 'include/common.php';
		
			startMySQL();
			$mysql =& $GLOBALS['mysql'];
			
			$user = $mysql->getUser(strtolower($_SESSION['Username']));
			
			// this will be empty if there were no users with that name
			if(isset($user))
			{
				// match up the passwords
				if(md5($_SESSION['Pass']) == $user['Pass'])
				{
					// check to see if they need to activate their account
					//$status = split(':', $user['Status']);
					
					//if($status[0] == STATUS_ACTIVATED)
					//{
						$GLOBALS['userinfo'] = $user;
						return true;
					//}
					/*
					elseif($status[0] == STATUS_UNACTIVATED)
					{
						$error = 'You must confirm your e-mail address and activate your account through the e-mail send to the address provided.';
						unset($user);
						unset($_SESSION['Pass']);
						return false;
					}
					elseif($status[0] == STATUS_PASSRESET)
					{
						$error = 'Your password has been reset.  You must follow the instructions in the e-mail to setup a new password.';
						unset($user);
						unset($_SESSION['Pass']);
						return false;
					}
					*/
				}
				else
				{
					$error = 'Invalid Password.';
					unset($user);
					unset($_SESSION['Pass']);
					return false;
				}
			}
			else
			{
				$error = 'Username does not exist.';
				unset($_SESSION['Username']);
				unset($_SESSION['Pass']);
				return false;
			}
		
		}
		else
		{
			$error = 'Username and/or Password not specified.';
			return false;
		}
	}
	else
	{
			$error = '';
			return false;
	}
	
}

?>