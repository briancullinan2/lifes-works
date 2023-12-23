<?php



$to = 'kefei.wang@nau.edu';

$subject = htmlspecialchars($_POST['subject']);
$from = $_POST['from'];
if (preg_match('/[a-z0-9!#$%&\'*+\/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&\'*+\/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?/i', $from) == 0)
{
	print 'Invalid \'from\' e-mail address';
	unset($from);
}

$body = htmlspecialchars($_POST['body']);

if(isset($from) && isset($body) && isset($subject))
{
	mail($to, $subject, $body, 'From: ' . $from);
	print 'Comment submitted';
}
