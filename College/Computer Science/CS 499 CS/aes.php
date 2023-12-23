<?php

$pspell_link = pspell_new("en");

set_time_limit(0);

chdir('/var/www/');

$candidates = array();

$count = 0;
$best_match = '';
$correct_words = 0;
for($i = 0; $i < 100; $i++)
{
	for($j = 0; $j < 256; $j++)
	{
		for($k = 0; $k < 256; $k++)
		{
			unset($output);
			$result = exec('./decrypt ' . sprintf('%02s', dechex($i)) . sprintf('%02s', dechex($j)) . sprintf('%02s', dechex($k)) . ' hw.crypt 2>&1', $output);
			$key = array_shift($output);
			$words =  array_values(array_unique(preg_split('/[^A-Z]/i', implode("\n", $output))));
			
			if(preg_match_all('/[a-z]/i', implode("\n", $output), $matches) > strlen(implode("\n", $output)) * .90)
			{
				print 'KEY: ' . sprintf('%02s', dechex($i)) . sprintf('%02s', dechex($j)) . "\n";
				print $output . "\n";
				flush();
			}
	
			$count++;
			if($count % 16777 == 0)
			{
				print ($count / (256*256*256)) * 100 . "\n";
				flush();
				sleep(1);
			}
		}
	}
}

print $best_match . '<br />';
