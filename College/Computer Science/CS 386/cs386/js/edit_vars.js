// JavaScript Document


// the selected classname
var selectedClass = "selected";
// the unselected classname
var unselectedClass = "";
// draggable class
var draggableClass = "dragable";
// the dragging classname
var draggingClass = "dragging";
// sizable class
var sizableClass = "sizable";
// the sizing classname
var sizingClass = "resizing";

// resizer naming scheme
var resizerOrder = ["n", "e", "s", "w"];
// resizer prefix
var resizerPrefix = "r_";

// a list of all the element types that can be dragable, editable, or sizeable
var elTypes = Array("INPUT", "DIV");
// the resize box for easy reference
var resize = null;
// the array of resizer nodes
var resizers = Array();
// the selected resizer
var currentResizer = null;
// all the bindings for easy reference
var bindings = Array();
// the invisible placeholder that is inserted while the selected object is made position:absolute
var placeHolder = null;
// if the placeholder has been set or unset
var placeHolderSet = false;
// the current abstract id number for any function to set a unique id on an element
var currentId = 0;
// -------- Current variables
// all these variables are set dynamically when objects are manipulated
// the current bounds for the selected object
var currentBind = {
	x:0,
	y:0,
	width:0,
	height:0,
	right:0,
	bottom:0
};
// the selected object
var currentObj = null;
// the variable to indicate if the object should currently be dragging
var isDragging = false;
// the mouse down position to offset the object drag location
var mouseDownOffset = {
	x:0,
	y:0
};


// get the classes in an object className
function hasClass(obj, className)
{
	// get the current classes
	var classes = Array();
	if(typeof(obj.className) != "undefined")
	{
		classes = obj.className.split(" ");
	}
	
	// loop through and find the specified class
	for(var i = 0; i < classes.length; i++)
	{
		if(classes[i] == className)
		{
			// class was found
			return true;
		}
	}
	// class not found
	return false;
}

// removes the specified classname from the object
function removeClass(obj, className)
{
	// get the current classes
	var classes = Array();
	if(typeof(obj.className) != "undefined")
	{
		classes = obj.className.split(" ");
	}

	// loop through and find the specified class
	for(var i = 0; i < classes.length; i++)
	{
		if(classes[i] == className)
		{
			// set the class to nothing
			classes[i] = "";
		}
	}
	
	// join the classes back together and set
	obj.className = classes.join(" ");
	
// end removeClass
}

// add a class name to the object
function addClass(obj, className)
{
	// get the current classes
	var classes = Array();
	if(typeof(obj.className) != "undefined")
	{
		classes = obj.className.split(" ");
	}

	// loop through and find the specified class
	for(var i = 0; i < classes.length; i++)
	{
		if(classes[i] == className)
		{
			// class is already in array no need for change
			return;
		}
	}
	
	// add the class to the array
	classes[classes.length] = className;
	// join the classes back together and set
	obj.className = classes.join(" ");
	
// end addClass
}

// set up the listener values
// call this function from all setActions functions
function setListener(obj)
{
	// check to make sure listener isn't already defined
	if(typeof(obj.listener) == "undefined")
	{
		obj.listener = Array();
		obj.listener["focus"] = Array();
		obj.listener["mousedown"] = Array();
		obj.listener["mouseup"] = Array();
		obj.listener["mousemove"] = Array();
		obj.listener["blur"] = Array();
	}
}

// a function for calling multiple functions
function fireEvent(eventType, ev, obj)
{
	// check if listener type is defined
	if(obj.listener[eventType])
	{
		// loop through all listener actions
		//document.title = obj.listener[eventType].length + "\n" + obj.listener[eventType];
		for(var i = 0; i < obj.listener[eventType].length; i++)
		{
			// call listner action
			obj.listener[eventType][i](ev, obj);
		}
	}
}

// iterate through elements and assign properties
function elLoop(els)
{
	// loop through each element and check for designer classes
	for(var i = 0; i < els.length; i++)
	{
		var hasAny = false;
		// the object can be dragged add the action
		if(hasClass(els[i], draggableClass) == true)
		{
			setDragAction(els[i]);
			hasAny = true;
		}
		// the object can be resized add the size actions
		if(hasClass(els[i], sizableClass) == true)
		{
			setSizeAction(els[i]);
			hasAny = true;
		}
		if(hasAny == true)
		{
			// set up the fire event functions
			els[i].focus = function (ev) { if(typeof(fireEvent) != "undefined") { fireEvent("focus", ev, this); } };
			els[i].onmousedown = function (ev) { if(typeof(fireEvent) != "undefined") { fireEvent("mousedown", ev, this); } };
			els[i].onmouseup = function (ev) { if(typeof(fireEvent) != "undefined") { fireEvent("mouseup", ev, this); } };
			els[i].onmousemove = function (ev) { if(typeof(fireEvent) != "undefined") { fireEvent("mousemove", ev, this); } };
			els[i].blur = function (ev) { if(typeof(fireEvent) != "undefined") { fireEvent("blur", ev, this); } };
			
			// set a few more properties to make the system work
			els[i].style.margin = "0px";
		}
	}
}

// initiate all the dynamic objects
function init()
{
	// get all objects that might be editable, dragable, or sizeable
	for(var i = 0; i < elTypes.length; i++)
	{
		var els = document.getElementsByTagName(elTypes[i]);
		elLoop(els);
	}
	
	// add the actions accordingly
	
	// get all the bindings of all the elements
	
	// create an array of resize nodes
	resizers["nw"] = document.getElementById("r_nw");
	resizers["w"] = document.getElementById("r_w");
	resizers["sw"] = document.getElementById("r_sw");
	resizers["n"] = document.getElementById("r_n");
	resizers["s"] = document.getElementById("r_s");
	resizers["ne"] = document.getElementById("r_ne");
	resizers["e"] = document.getElementById("r_e");
	resizers["se"] = document.getElementById("r_se");
	// loop through each node and set some properties
	for(var i in resizers)
	{
		// make sure the node is hidden
		resizers[i].style.display = "none";
		// set the actions for a resizer
		resizers[i].onmousedown = resizerMouseDown;
		resizers[i].onmousemove = resizerMove;
		resizers[i].onmouseup = resizerMouseUp;
	}
	
	// set other objects the script uses
	resize = document.getElementById("resize");
	// set up the resize functions
	resize.focus = function (ev) {
		if(currentObj != null) { currentObj.focus(ev); }
	}
	resize.blur = function (ev) {
		if(currentObj != null) { currentObj.blur(ev); }
	}
	resize.onmousedown = function (ev) {
		if(currentObj != null) { currentObj.onmousedown(ev); }
	}
	resize.onmouseup = function (ev) {
		if(currentObj != null) { currentObj.onmouseup(ev); }
	}
	resize.onmousemove = function (ev) {
		if(currentObj != null) { currentObj.onmousemove(ev); }
	}
	
	placeHolder = document.getElementById("placeholder");
	
}


// get the next id in the series for referencing created objects
function nextId()
{
	// increment on return
	return ("id_" + currentId++);
// end nextId
}

// ---------- Position Functions: ---------- //

// get an objects boundaries
function getBounds(obj)
{
	var myheight = 0;
	var mywidth = 0;
	var myy = 0;
	var myx = 0;
	
	if(typeof(obj.style.height) == "undefined" || obj.style.height == "" || obj.style.height == 0)
	{
		if(obj.clientHeight == 0)
		{
			myheight = obj.offsetHeight;
		}
		else
		{
			myheight = obj.clientHeight;
		}
	}
	else
	{
		myheight = parseInt(obj.style.height.replace("px", ""));
	}
	if(typeof(obj.style.width) == "undefined" || obj.style.width == "" || obj.style.width == 0)
	{
		if(obj.clientWidth == 0)
		{
			mywidth = obj.offsetWidth;
		}
		else
		{
			mywidth = obj.clientWidth;
		}
	}
	else
	{
		mywidth = parseInt(obj.style.width.replace("px", ""));
	}
	if(obj.style.position == "relative" || obj.style.position == "absolute")
	{
		myx = parseInt(obj.style.left.replace("px", ""));
		myy = parseInt(obj.style.top.replace("px", ""));
	}
	else
	{
		//pos = findPos(obj);
		//myy = pos.y;
		//myx = pos.x;
		myy = 0;
		myx = 0;
	}
	
	return {
		x:myx,
		y:myy,
		height:myheight,
		width:mywidth
	}
	
}

function setBounds(obj, bounds)
{
	if(typeof(bounds.height) != "undefined")
	{
		obj.style.height = bounds.height + "px";
	}
	if(typeof(bounds.width) != "undefined")
	{
		obj.style.width = bounds.width + "px";
	}
	if(typeof(bounds.y) != "undefined")
	{
		obj.style.top = bounds.y + "px";
	}
	if(typeof(bounds.x) != "undefined")
	{
		obj.style.left = bounds.x + "px";
	}
}

// not written by me

// find the position of an object
function findPos(obj)
{
	var curleft = curtop = 0;
	if (obj.offsetParent) {
		curleft = obj.offsetLeft
		curtop = obj.offsetTop
		while (obj = obj.offsetParent) {
			curleft += obj.offsetLeft
			curtop += obj.offsetTop
		}
	}
	return {x:curleft, y:curtop};
}

// get the mouse cooridinates cross browser
function mouseCoords(ev){
	ev = ev || window.event;
	
	var posX = 0;
	var posY = 0;
	if(ev.pageX || ev.pageY){
		posX = ev.pageX;
		posY = ev.pageY;
	}
	else
	{
		posX = ev.clientX + document.body.scrollLeft - document.body.clientLeft;
		posY = ev.clientY + document.body.scrollTop  - document.body.clientTop;
	}
	return {x:posX, y:posY};
}

