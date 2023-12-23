<?php
    /*
    Created by Travis Hudson (November 8, 2007)
    */
    
    // perhaps a logged in admin who goes here will see an edit view

    require 'include/common.php';
    
    startMySQL();
    $mysql =& $GLOBALS['mysql'];
	
    $mysql->sql(DB_SERVER, DB_USER, DB_PASS, DB_NAME);
    
	
    // ------------------------------------------------------
    // Global Functions
    // ------------------------------------------------------
    
    // Returns an array full of the values in a specified column.
    // This is used on the results of an SQL query.
    // Example array2d:
    // firstname1    lastname1
    // firstname2    lastname2
    // firstname3    lastname3
    // Example call:
    // getCols($array2d, "First_Name");
    // Returns:
    // [firstname1, firstname2, firstname3]
    // Travis Hudson 2007-11-08
    function getCol($array2d, $colName) {
        $result[0] = "";
        for ($j = 0; $j < sizeof($array2d); $j += 1) {
            $result[$j] = $array2d[$j][$colName];
        }
    
        return $result;
    }
?>
<html>
    <head>
        <title>Take Survey</title>
        <link rel="stylesheet" href="css/take.css" type="text/css" />
        <script type="text/javascript">
        
        // Says thank you by going to a page with a nice message on it.
        function sayThankYou() {
            window.location = "thankyou.html";
        }
        
        // Handles the event when the user clicks on the submit button.
        function onSubmit() {
            document.main.submit();
            sayThankYou();
        }
        
        </script>
    </head>
    <body>
      <div id="page">
      
        <div id="header">
          <div id="headerimg">
            <!-- <br /><h1>Page Title</h1> -->
          </div>
        </div>

      <div id="content" class="widecolumn">
      
    <?php
    // use sql via cmd line and copy and paste xml in there to put a survey in the database

    $survey_id = 1; // id of survey to take. arbitrarily chosen.
    
    // get the xml describing what the survey is all about
    
    $query = "SELECT xml_string FROM survey;";
    $mysql->query($query);
    $rawResult = $mysql->result();
    
    // the second entry actually contains HTML,
    // so we don't have to fuss with it to display it
    $results = getCol($rawResult, "xml_string");
    $xml = $results[0];
    
    // now $xml contains the xml description for the survey that is to be taken
    echo $xml;

    ?>
     <input type="button" onClick="onSubmit()" value="Submit" \>
      </div>
      <div id="footer">
      	<img style="width: 592px;" src="graphics/minipage_bottom.png" />
      </div>
    </div>
    </body>
</html>
