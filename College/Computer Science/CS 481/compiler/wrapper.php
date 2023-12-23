<?php

$file_name = 'temp_'; // . md5(time());

require_once 'parser.php';

require_once 'visitor.php';

require_once 'generator.php';

require_once 'assembler.php';

require_once 'interpreter.php';

// create a reader object
print "parsing<br />\n";

$parser = new Parser('code.txt');

$tree = $parser->parse();

print "analyzing<br />\n";

$analyzer = new Analyzer($tree);

$tree = $analyzer->analyze();

print "generating<br />\n";

$generator = new Generator($tree);

$code = $generator->generate();

print "saving<br />\n";

$fp = fopen($file_name, 'w');

fwrite($fp, $code);

fclose($fp);

print "parsing assembly<br />\n";

$scanner = new Assembler($file_name);

$commands = new VMParser();
$tree = $commands->parseVM($scanner->tokens);

print "assembling<br />\n";

$analyzer = new BinaryOutput($tree);

$output = $analyzer->assemble();

print "executing<br />\n";

$vm = new VM($output, false);

?>