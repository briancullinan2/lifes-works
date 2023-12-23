function signUp() {
	alert('Signing up');
}

// the database has max lengths for some values
var EMAIL_MAX_LENGTH = 30;
var PASSWORD_MAX_LENGTH = 20;

// where the user is taken upoon successful login
var SUCCESS_URL = "http://flagstaff.cse.nau.edu:16080/~xtravishudson/project/Mailbox/Construction/Website/manage.php";

// Submits the form.
function submitForm() {
    document.mainForm.submit();
}
