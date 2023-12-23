<?php

$pspell_link = pspell_new("en");

// problem 1
$input = 'MX IRHIH YT RSX FIMRK EHSFI\'W TVSFPIQ XS FIKMR AMXL ERCAEC: E ZYPRIVEFMPMXC XLEX IREFPIH NEZEWGVMTX GSHI AMXLMR E WTIGMJMGEPPC GVEJXIH YVP XS VYR YRGLIGOIH, ERH PEYRGL ERC IBIGYXEFPI GSHI.';

$best_match = '';
$correct_words = 0;
$key = 0;
for($j = 1; $j <= 25; $j++)
{
	$output = '';
	for($i = 0; $i < strlen($input); $i++)
	{
		if(ord($input[$i]) >= 65 && ord($input[$i]) <= 90)
			if(ord($input[$i]) + $j > 90)
				$output .= chr(ord($input[$i]) + $j - 26);
			else
				$output .= chr(ord($input[$i]) + $j);
		else
			$output .= $input[$i];
	}
	// split in to words and check spelling
	$words = preg_split('/[^A-Z]/i', $output);
	$correct = 0;
	foreach($words as $word)
	{
		if(pspell_check($pspell_link, $word))
			$correct++;
	}
	// select the key with the most correct words
	if($correct > $correct_words)
	{
		$best_match = $output;
		$correct_words = $correct;
		$key = $j;
	}
}	

print $best_match . '<br />';
print 'Key: ' . $key . '<br />';

// problem 2

$input = 'F VN UHMHGPOOVNBGFUBPNT FIHNGZ RFT FAASPWHE LBOFJ FT F IMPDFM TUFNEFSE QPS OPDBMH EHWBGHT, LRBGR LBMM AFWH URH LFZ QPS LBEHTASHFE FEPAUBPN PQ URH AMFUQPSO FT F OHURPE QPS NHJU-IHNHSFUBPN GHMMVMFS EFUF USFNTQHS.';
$words = preg_split('/[^A-Z]/i', $input);
$translated = array(
	'U' => array('T'),
	'H' => array('E'),
	'M' => array('L'),
	'G' => array('C'),
	'P' => array('O'),
	'O' => array('M'),
	'V' => array('U'),
	'N' => array('N'),
	'B' => array('I'),
	'F' => array('A'),
	'B' => array('I'),
	'T' => array('S'),
);

$counter = 0;
$key_counter = 0;
foreach($words as $word)
{
	// get characters used in current word
	$chars = array_unique(preg_split('/|[^A-Z]/i', $word));
	array_shift($chars);
	$counter++;
	//generate(count($chars));
	if($counter > 2)
		break;
}
//print $chars . '<br />';

function generate($length = 3, $prefix = array())
{
	global $pspell_link, $word, $chars, $translated, $key_counter;
	// generate every posibility of letter replacements
	for($i = 0; $i < 26; $i++)
	{
		if(in_array(chr(65 + $i), $prefix))
			continue;
		
		$temp_chars = array_merge($prefix, array(chr(65 + $i)));
		if($length > 1)
			// create replacement keys for all the characters in the current word
			generate($length - 1, $temp_chars);
		else
		{
			$test = str_replace($chars, $temp_chars, $word);
			$key_counter++;
			// DEBUG: print out indication every 100 thousands keys
			if($key_counter % 100000 == 0)
			{
				print $key_counter . '<br />';
				flush();
			}
			// check to see if the key has created a valid word
			if(pspell_check($pspell_link, $test))
			{
				// add to translated
				foreach($chars as $j => $char)
				{
					if(!isset($translated[$char]) || !in_array($temp_chars[$j], $translated[$char]))
					$translated[$char][] = $temp_chars[$j];
				}
			}
		}
	}
}


// problem 3

include 'vigenere.php';

$input = 'Zwtkvwfsg vrl hkirrr kh tebr psimhme cnfkl vj zgf Kzgksnf fclkji tbqs rohmcnozv.';



//problem 5
$key = "1818";
$message = "cs499";

$rc4 = new Crypt_RC4;
$rc4->setKey($key);
echo "Original message: $message <br>\n";
$rc4->crypt($message);
echo "Encrypted message: $message <br>\n";
$rc4->decrypt($message);
echo "Decrypted message: $message <br>\n";

class Crypt_Rc4 {

    /**
    * Real programmers...
    * @var array
    */
    var $s= array();
    /**
    * Real programmers...
    * @var array
    */
    var $i= 0;
    /**
    * Real programmers...
    * @var array
    */
    var $j= 0;

    /**
    * Key holder
    * @var string
    */
    var $_key;

    /**
    * Constructor
    * Pass encryption key to key()
    *
    * @see    setKey() 
    * @param  string key    - Key which will be used for encryption
    * @return void
    * @access public
    */
    function Crypt_RC4($key = null) {
        if ($key != null) {
            $this->setKey($key);
        }
    }

    function setKey($key) {
        if (strlen($key) > 0)
            $this->_key = $key;
    }

    /**
    * Assign encryption key to class
    *
    * @param  string key	- Key which will be used for encryption
    * @return void
    * @access public    
    */
    function key(&$key) {
        $len= strlen($key);
        for ($this->i = 0; $this->i < 4; $this->i++) {
            $this->s[$this->i] = $this->i;
        }

        $this->j = 0;
        for ($this->i = 0; $this->i < 4; $this->i++) {
            $this->j = ($this->j + $this->s[$this->i] + ord($key[$this->i % $len])) % 4;
            $t = $this->s[$this->i];
            $this->s[$this->i] = $this->s[$this->j];
            $this->s[$this->j] = $t;
        }
        $this->i = $this->j = 0;
    }

    /**
    * Encrypt function
    *
    * @param  string paramstr 	- string that will encrypted
    * @return void
    * @access public    
    */
    function crypt(&$paramstr) {

        //Init key for every call, Bugfix 22316
        $this->key($this->_key);

        $len= strlen($paramstr);
        for ($c= 0; $c < $len; $c++) {
            $this->i = ($this->i + 1) % 4;
            $this->j = ($this->j + $this->s[$this->i]) % 4;
            $t = $this->s[$this->i];
            $this->s[$this->i] = $this->s[$this->j];
            $this->s[$this->j] = $t;

            $t = ($this->s[$this->i] + $this->s[$this->j]) % 4;

            $paramstr[$c] = chr(ord($paramstr[$c]) ^ $this->s[$t]);
        }
    }

    /**
    * Decrypt function
    *
    * @param  string paramstr 	- string that will decrypted
    * @return void
    * @access public    
    */
    function decrypt(&$paramstr) {
        //Decrypt is exactly the same as encrypting the string. Reuse (en)crypt code
        $this->crypt($paramstr);
    }


}	//end of RC4 class

