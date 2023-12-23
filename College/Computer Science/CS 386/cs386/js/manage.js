// Returns the value of the selected item in the list of surveys.
function getSelected() {
	var selIndex = document.getElementById('surveySelect').selectedIndex;
	var selValue = document.getElementById('surveySelect')[selIndex].value;
	return selValue;
}

function submitForm() {
	alert('form submitted.');
}

function reloadPage() {
	document.getElementById('selValue').value = getSelected();
	document.getElementById('mainForm').submit();
}

function createSurvey() {
	// TODO: Link to create survey page
	alert('This is where the create survey part comes into play.');
}

function deleteSurvey() {
	/*var yn = confirm('Are you sure you want to delete this survey?');
	if (yn) {
		alert('Deleting: ' + selIndex);
	}*/

	document.getElementById('action').value = 'delete';
	reloadPage();
}

function viewResults() {
	alert('Viewing results: ');
}
