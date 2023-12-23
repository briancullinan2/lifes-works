<?php

// include some needed classes
require_once 'parser.php';

require_once 'printer.php';

// create a reader object
$parser = new Parser('code.txt');

$tree = $parser->parse();

//print_r($tree);

//$printer = new PrinterVisitor($tree);

//$printer->visit($tree);

$analyzer = new Analyzer($tree);

$analyzer->analyze();

?>