/* ========================================================= */
/*                    Table Structure                        */
/* ========================================================= */

/* User */
DROP TABLE IF EXISTS users;
CREATE TABLE users (
Userid INT NOT NULL AUTO_INCREMENT,
PRIMARY KEY (Userid),
Firstname VARCHAR(30),
Lastname VARCHAR(30),
Pass VARCHAR(32),
Username VARCHAR(30),
Email VARCHAR(30),
Created DATETIME NOT NULL
);

/* Survey */
DROP TABLE IF EXISTS surveys;
CREATE TABLE surveys (
Surveyid INT NOT NULL AUTO_INCREMENT,
PRIMARY KEY (Surveyid),
Name VARCHAR(30),
Username VARCHAR(30),
Xmlstring TEXT
);

/* Feedback */
/* Survey_Name and Username are foreign keys form surveys */
DROP TABLE IF EXISTS feedback;
CREATE TABLE feedback (
Feedbackid INT NOT NULL AUTO_INCREMENT,
PRIMARY KEY (Feedbackid),
Kind VARCHAR(30),
Submitter_Name VARCHAR(30),
Email VARCHAR(30),
Content TEXT,
Survey_Name VARCHAR(30),
Username VARCHAR(30)
);

/* Results */
/* Name and Username are foreign keys from surveys;
   They help to define where these results came from. */
DROP TABLE IF EXISTS results;
CREATE TABLE results (
Resultid INT NOT NULL AUTO_INCREMENT,
PRIMARY KEY (Resultid),
Name VARCHAR(30),
Username VARCHAR(30),
Xmlstring TEXT
);

/* ========================================================= */
/*                    Initial Test Data                      */
/* ========================================================= */

/* Users */
INSERT INTO users (Firstname, Lastname, Pass, Username, Email, Created) VALUES ('Travis', 'Hudson', md5('password'), 'th58', 'th58@nau.edu', NOW());

/* Surveys */
INSERT INTO surveys (Name, Username, Xmlstring) VALUES ('Cake Survey', 'th58',
'<?xml version=\"1.0\" encoding=\"utf-8\"?>
<survey>

<checkbox>
<location>0, 0, 300, 200</location>
<question>Please select everything that you like:</question>
<response order=\"0\">Cake</response>
<response order=\"1\">Sunny Days</response>
<response order=\"2\">Flowers</response>
</checkbox>

<radio>
<location>0, 150, 300, 200</location>
<question>Is the cake a lie?</question>
<response order=\"0\">Yes</response>
<response order=\"1\">No</response>
<dropdown>false</dropdown>
</radio>

<text>
<question>Questions and comments, please:</question>
</text>
</survey>'
);

INSERT INTO surveys (Name, Username, Xmlstring) VALUES ('iBuy Survey', 'th58', 'xml content2');

INSERT INTO surveys (Name, Username, Xmlstring) VALUES ('Apple Survey', 'th58', 'xml content3');

/* Results */
INSERT INTO results (Name, Username, Xmlstring) VALUES ('Cake Survey', 'th58',
'<?xml version=\"1.0\" encoding=\"utf-8\"?>
<survey>
	<checkbox>
		Cake,Flowers
	</checkbox>
	<radio>
		Yes
	</radio>
	<text>
		This is a comment.
	</text>
</survey>'
);

INSERT INTO results (Name, Username, Xmlstring) VALUES ('Cake Survey', 'th58',
'<?xml version=\"1.0\" encoding=\"utf-8\"?>
<survey>
	<checkbox>
		Cake,Flowers
	</checkbox>
	<radio>
		Yes
	</radio>
	<text>
		This is a comment 2.
	</text>
</survey>'
);

INSERT INTO results (Name, Username, Xmlstring) VALUES ('Cake Survey', 'th58',
'<?xml version=\"1.0\" encoding=\"utf-8\"?>
<survey>
	<checkbox>
		Cake
	</checkbox>
	<radio>
		No
	</radio>
	<text>
		This is a comment 3.
	</text>
</survey>'
);

/* Feedback */
INSERT INTO feedback (Kind, Submitter_Name, Email, Content, Survey_Name, Username) VALUES ('Other', 'GLaDOS', 'happy@customer.com', 'I am so very pleased with your product. You guys rock!', 'Cake Survey', 'th58');

INSERT INTO feedback (Kind, Submitter_Name, Email, Content, Survey_Name, Username) VALUES ('Other', 'Chell', 'disappointed@customer.com', 'Wait, so the cake was really just a lie all this time? I have been lied to?!', 'Cake Survey', 'th58');

INSERT INTO feedback (Kind, Submitter_Name, Email, Content, Survey_Name, Username) VALUES ('Other', 'Sycophant', 'happy@customer.com', 'I love to use the iBuy product. It is fantastic. Keep up the good work!', 'iBuy Survey', 'th58');
