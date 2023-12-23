// JavaScript Document

// the array of resizer nodes
var resizers = Array();

// the resize box that controls element positions
var resize = null;

// the size of the grid
var GRID = 8;

// the size of resizer nodes
var RESIZER = 6;

var currentId = 0;

// body init function for setting up inputs
function init()
{
	// load all inputs with input class
	els = document.getElementsByTagName("INPUT");
	
	// loop through inputs
	for(i = 0; i < els.length; i++)
	{
		// make sure they are in the input class
		if(getClasses(els[i]).indexOf("input") != -1)
		{
			// set all the properties for the input
			setActions(els[i]);
			setAttributes(els[i]);
			// make readonly
			setReadonly(els[i], true);
		}
	}
	
	// load all divs with input class
	els = document.getElementsByTagName("DIV");
	
	// loop through all the divs
	for(i = 0; i < els.length; i++)
	{
		// make sure they are in the input class
		if(getClasses(els[i]).indexOf("input") != -1)
		{
			// set the properties
			setActions(els[i]);
			setAttributes(els[i]);
			
			// take everything out of the control and add it to a sub control div
			controls = document.createElement("div");
			controls.className = "controls";
			children = els[i].childNodes;
			// loop through all the children and add it to new div
			if (children && children.length > 0) {
				for (var x = 0; x < children.length; x=0) {
					child = children[x];
					els[i].removeChild(child);
					controls.appendChild(child);
				}
			}
			
			// add controls div to input div
			els[i].appendChild(controls);
			
			// get an id for cover to reference it anywhere
			cover_id = nextId();
			// create a cover to allow for easy selection and disabling inner contents
			cover = document.createElement("div");
			cover.id = cover_id;
			cover.style.top = -controls.clientHeight + "px";
			cover.className = "cover";
			// set action for new cover div
			cover.onmousedown = function(ev) {
				setActions(this.parentNode);
				this.parentNode.onclick(ev, this.parentNode);
				return false
			}
			// add cover to input div
			els[i].appendChild(cover);
			
			// add the id attribute for cover so cover can be found later
			els[i].setAttribute("cover_id", cover_id);
			
			// make it all readonly
			setReadonly(els[i], true);
			
		}
	}
	
	// create an array of resizers
	resizers[0] = document.getElementById("r_nw");
	resizers[1] = document.getElementById("r_w");
	resizers[2] = document.getElementById("r_sw");
	resizers[3] = document.getElementById("r_n");
	resizers[4] = document.getElementById("r_s");
	resizers[5] = document.getElementById("r_ne");
	resizers[6] = document.getElementById("r_e");
	resizers[7] = document.getElementById("r_se");
	
	// set the actions for every resizer
	for(i = 0; i < resizers.length; i++)
	{
		resizer = resizers[i];
		resizer.onmousedown = resizerMouseDown
	}
	
	// set the resize variable
	resize = document.getElementById("resize");
	
	// set the actions for the resize box
	resize.onmousedown = function(ev) {
		controlMouseDown(ev);
		dontBlur = true;
		return false;
	}
	// set the double click action to allow editing of controls inside input div
	resize.ondblclick = function() {
		setReadonly(currentControl, false);
		hideControls();
		currentControl.focus();
	}
	
	// set up the clear focus to always remove focus from controls
	document.getElementById("clearFocus").onmousedown = function() {
		// get all divs and possible siblings
		divs = document.getElementsByTagName("DIV");
		
		// loop through divs
		for(i = 0; i < divs.length; i++)
		{
			// match the div bind objects to the current binding
			if(divs[i].getAttribute("bind") && divs[i].getAttribute("bind") == "clearFocus")
			{
				// set the control to readonly
				setReadonly(divs[i], true);
			}
		}
		hideControls();
	}
	
	// use the document mouse up function to unset things
	document.onmouseup = function(ev) {
		resizerMouseUp(ev);
		controlMouseUp(ev);
	}
	
	// use the document mouse move function so the elements will always move to mouse position
	document.onmousemove = function(ev) {
		resizerMove(ev);
		controlMove(ev);
	}
	
// end init
}

// get the next id in the series for referencing created objects
function nextId()
{
	// increment on return
	return ("id_" + currentId++);
// end nextId
}

// removes the specified classname from the object
function removeClass(obj, className)
{
	if(obj != null)
	{
		// replace the class name with nothing to clear it
		obj.className = obj.className.replace(className, "");
		// replace extra spaces with 1 space
		obj.className = obj.className.replace("  ", " ");
	}
// end removeClass
}

// add a class name to the object
function addClass(obj, className)
{
	if(obj != null)
	{
		// make sure it isn't already in the classname
		removeClass(obj, className);
		// add the class with a space
		obj.className += " " + className;
		// replace many spaces with 1 space
		obj.className = obj.className.replace("  ", " ");
	}
// end addClass
}

// set up an input's actions
function setActions(obj)
{
	// add the onclick event to the object
	obj.onclick = function(ev, target) {
		// get the event
		ev = ev || window.event;
		// get the event target
		if(!target)
		{
			target = ev.target || ev.srcElement;
		}
		// only perform actions if the target is readonly
		//   this prevents the click event being exectuted every time the control is clicked
		//   this allows sub controls to be edited without recalling these functions
		if(target.getAttribute("readonly") == "readonly" || target.getAttribute("readonly") == true || target.readOnly == true)
		{
			// set readonly on all the old siblings
			setReadonlySiblings(this);
			
			// set current control
			currentControl = this;
			
			// get the current siblings
			getCurrentSiblings(this);
			
			// set readonly on same level siblings
			setReadonlySiblings(this);
			
			// remove the unselected class and add the selected class
			removeClass(this, "unselected");
			addClass(this, "selected");
			// place the resizers around this object
			placeResize(this);
			// get the current info about this object and set it to the local variables
			setCurrentInfo(this);
			// reinstate the no select policy on the document
		}
	//end onclick
	}
	
// end setActions
}

// sets up default and needed attributes about an object
function setAttributes(obj)
{
	
	// set up the minsize attributes
	if(obj.getAttribute("minsize"))
	{
		// read and split up the attribute should be x,y coordinates
		mymin = obj.getAttribute("minsize").split(/[,|x]/i);
		// if they are missing a coordinate make it the same as the first coordinate
		if(mymin.length < 2)
		{
			mymin[1] = mymin[0];
		}
		
		// round the given coordinates to match the grid
		mymin[0] = Math.round(mymin[0] / GRID) * GRID;
		mymin[1] = Math.round(mymin[1] / GRID) * GRID;
	}
	// if it is not set
	else
	{
		// set the minsize to the current size
		//   this means the smalles a control can get is the default size of the element
		mymin = Array();
		mymin[0] = Math.round(obj.clientWidth / GRID) * GRID;
		mymin[1] = Math.round(obj.clientHeight / GRID) * GRID;
	}
	
	// set the current height to the default minimum
	obj.style.width = mymin[0] + "px";
	obj.style.height = mymin[1] + "px";
	// set the minsize again for objects without minsize already specified
	obj.setAttribute("minsize", mymin[0] + "," + mymin[1]);
	
	// do the same stuff for maxsize
	if(obj.getAttribute("maxsize"))
	{
		// split up the coordinates
		mymax = obj.getAttribute("maxsize").split(/[,|x]/i);
		if(mymax.length < 2)
		{
			mymax[1] = mymax[0];
		}
		
		// round the coordinates to the grid
		mymax[0] = Math.round(mymax[0] / GRID) * GRID;
		mymax[1] = Math.round(mymax[1] / GRID) * GRID;
	}
	else
	{
		// the default is 0,0 which means no limit
		mymax = Array();
		mymax[0] = 0;
		mymax[1] = 0;
	}
	// if the max size is smaller then the min size,
	//   then make the max size equal to the min size
	//   this locks the control into not being able to be resized
	if(mymax[0] < mymin[0] && mymax[0] != 0)
	{
		mymax[0] = mymin[0];
	}
	// do it for height
	if(mymax[1] < mymin[1] && mymax[1] != 0)
	{
		mymax[1] = mymin[1];
	}
	
	// reset the maxsize for controls without ones already assigned
	obj.setAttribute("maxsize", mymax[0] + "," + mymax[1]);
	
	// get the start offset for moving around objects in reletive positions
	myoffset = findPos(obj);
	obj.setAttribute("startOffsetX", myoffset[0]);
	obj.setAttribute("startOffsetY", myoffset[1]);
	
	// add the unselected and readonly classes
	addClass(obj, "unselected");
	addClass(obj, "readonly");
	
//end setAttributes
}


// Contents:
// General:
//   vars
//   setCurrentInfo
//   setReadonlySiblings
//   setReadonly
//   getClasses
// Control Movement:
//   controlMove
//   controlMouseDown
//   controlMouseUp
// Control Placement:
//   placeControl
// Resizer Movement:
//   resizerMove
//   resizerMouseDown
//   resizerMouseUp
// Resizer Placement:
//   placeResize
//   hideControls
//   placeResizers
//   placeResizeBox
// Position Functions
//   findPos
//   mouseCoords
//   getPosition
//   getMouseOffset

// the currently selected control
var currentControl = null;
// the cover object inside of currentControl
var currentCover = null;
// the current control's minsize
var currentMin = {width:0, height:0};
// the current control's maxsize
var currentMax = {width:0, height:0};
// where the current control is bound to
var currentBind = {x:0, y:0, right:0, bottom:0};
// the current offsetY
var currentOffsetY = 0;
// the current offsetX
var currentOffsetX = 0;
// the current control's siblings
var currentSiblings = Array();
// the current resizer being moved
var currentResizer = null;

// if the control is being dragged
var dragging = false;

// if the 
//var dontBlur = false;

// the coordinates to offset control dragging to where the mouse down initially occured
var startCoords = null;

// timeout for moving resize and resizers after the moving has paused
var moveTimeout = null;


// set all the current variables when an object is selected
function setCurrentInfo(obj)
{
	
	// get the currentcontrol's cover object to resize at the same time as control
	currentCover = document.getElementById(currentControl.getAttribute("cover_id"));
	
	// extract the minsize attributes
	if(currentControl.getAttribute("minsize"))
	{
		// split up minsize
		mymin = currentControl.getAttribute("minsize").split(/[,|x]/i);
		// set the variable
		currentMin = {width:mymin[0], height:mymin[1]};
	}
	
	// extract the maxsize attributes
	if(currentControl.getAttribute("maxsize"))
	{
		// split up max size
		mymax = currentControl.getAttribute("maxsize").split(/[,|x]/i);
		// set the variable
		currentMax = {width:mymax[0], height:mymax[1]};
	}

	// get the binded area
	bind = currentControl.getAttribute("bind");
	
	if(bind)
	{
		// get the object that this control is bound to
		bindObj = document.getElementById(bind);
		
		// if it exists
		if(bindObj)
		{
			// get the position of the bind object
			pos = findPos(bindObj);
			// set up the variable
			currentBind = {
				// don't round because the grid is always 0 at the start points of the binding object
				x:pos[0], 
				y:pos[1], 
				// round the right and bottom
				right:Math.round((pos[0] + bindObj.clientWidth) / GRID) * GRID + GRID, 
				bottom:Math.round((pos[1] + bindObj.clientHeight) / GRID) * GRID + GRID
			};
		}
		
		// set up the currentOffsetY
		offsetY = 0;
		// loop through all siblings
		for(i = 0; i < currentSiblings.length; i++)
		{
			// if the current sibling is equal to this one
			if(currentSiblings[i] == currentControl)
			{
				// then stop adding to offsetY
				break;
			}
			else
			{
				// all siblings below current control add their Y position
				offsetY += currentSiblings[i].clientHeight;
			}
		}

		// set current variable
		currentOffsetY = offsetY;
	}
	// if there is no bind object
	else
	{
		// there is no bindings
		currentBind = {x:0, y:0, right:0, bottom:0};
		
		// no offset if there are no siblings
		currentOffsetY = 0;
	}
	
	// add startoffset to currentOffsetY
	if(currentControl.getAttribute("startOffsetY"))
	{
		currentOffsetY += (currentControl.getAttribute("startOffsetY") - currentBind.y);
	}
	if(currentControl.getAttribute("startOffsetX"))
	{
		currentOffsetX = (currentControl.getAttribute("startOffsetX") - currentBind.x);
	}
	else
	{
		currentOffsetX = 0;
	}

// end setCurrentInfo
}

// get the current binded siblings
function getCurrentSiblings(obj)
{
	// get the binded area
	bind = obj.getAttribute("bind");

	// get all divs and possible siblings
	divs = document.getElementsByTagName("DIV");
	
	// erase the current array
	currentSiblings = Array();
	// loop through divs
	for(i = 0; i < divs.length; i++)
	{
		// match the div bind objects to the current binding
		if(divs[i].getAttribute("bind") && divs[i].getAttribute("bind") == bind)
		{
			// add to the array
			currentSiblings[currentSiblings.length] = divs[i];
		}
	}
}

// set readonly on all the siblings
function setReadonlySiblings(obj)
{
	for(i = 0; i < currentSiblings.length; i++)
	{
		if(obj.getAttribute("bind") != currentControl.id)
		{
			//removeClass(currentSiblings[i], "unselected");
			//removeClass(currentSiblings[i], "selected");
			//addClass(currentSiblings[i], "unselected");
			//addClass(currentSiblings[i], "readonly");
			setReadonly(currentSiblings[i], true);
		}
		else
		{
			
		}
	}
	
}

// set the controls items to readonly
function setReadonly(obj, readonly)
{
	// if the object is not null
	if(obj != null)
	{
		// if read only is true
		if(readonly == true)
		{
			if(getClasses(obj).indexOf("input") != -1)
			{
				removeClass(obj, "unselected");
				removeClass(obj, "selected");
				addClass(obj, "unselected");
				addClass(obj, "readonly");
			}
			obj.setAttribute("readonly", "true");
		}
		// if not readonly
		else
		{
			if(getClasses(obj).indexOf("input") != -1)
			{
				removeClass(obj, "unselected");
				removeClass(obj, "selected");
				removeClass(obj, "readonly");
			}
			obj.setAttribute("readonly", "false");
		}
		// set the readonly
		obj.readOnly = readonly;
		// make it disabled
		if(obj.nodeName != "DIV")
		{
			obj.disabled = readonly;
		}
		
		// loop through children
		if (obj.childNodes && obj.childNodes.length > 0) {
			for (var x = 0; x < obj.childNodes.length; x++) {
				// if it is not text, and is not an input object
				if(obj.childNodes[x].nodeName != "#text" && obj.childNodes[x].nodeName != "#comment" && ((readonly == false && getClasses(obj.childNodes[x]).indexOf("input") == -1) || readonly == true))
				{
					// call readonly recursively
					setReadonly(obj.childNodes[x], readonly);
				}
			}
		}
	}
	
// end setReadonly
}

// get the classes from an object returns an array
function getClasses(obj)
{
	if(obj.className)
	{
		return obj.className.split(" ");
	}
	else
	{
		return Array();
	}
}


// ---------- Control Movement: ---------- //
// a control is being moved via the resize object
function controlMove(ev)
{
	// get the event
	ev = ev || window.event;
	// get event target
	var target = ev.target || ev.srcElement;
	
	// if the current control is not empty and it is being dragged
	if(currentControl != null && dragging == true)
	{
		// get the mouse coordinates
		var mousePos = mouseCoords(ev);
		
		// round the mouse positions to match the grid
		mousePos.y = Math.round((mousePos.y - startCoords[1]) / GRID) * GRID;
		mousePos.x = Math.round((mousePos.x - startCoords[0]) / GRID) * GRID;
		
		// on X (horizontal movement)
		// if the mouse position is outside the bounded area
		if(mousePos.x <= currentBind.x)
		{
			// set the position to the bounded area
			resize.style.left = currentBind.x - 1 + "px";
		}
		else
		{
			// if the mouse is outside the bounded area
			if(mousePos.x + resize.clientWidth >= currentBind.right)
			{
				// set the resize box to the bounded area
				resize.style.left = currentBind.right - currentControl.clientWidth - (GRID-(currentBind.x % GRID)) - 1 + "px";
			}
			else
			{
				// set the resize box to the mouse position
				resize.style.left = mousePos.x - (GRID-(currentBind.x % GRID)) - 1 + "px";
			}
		}
		
		// on Y (vertical movement)
		// if the mouse position is outside the bounded area
		if(mousePos.y <= currentBind.y)
		{
			// set the position to the bounded area
			resize.style.top = currentBind.y - 1 + "px";
		}
		else
		{
			// if the mouse is outside the bounded area
			if(mousePos.y + resize.clientHeight >= currentBind.bottom)
			{
				// set the position to the bounded area
				resize.style.top = currentBind.bottom - currentControl.clientHeight - (GRID-(currentBind.y % GRID)) - 1 + "px";
			}
			else
			{
				// set the resize box to the mouse position
				resize.style.top = mousePos.y - (GRID-(currentBind.y % GRID)) - 1 + "px";
			}
		}
		
		// reset the timer
		//if(moveTimeout)
		//{
			//clearTimeout(moveTimeout);
		//}
		
		// set a time for movement
		/*moveTimeout = setTimeout(function () {
		*/
			// clear previous timer
			//clearTimeout(moveTimeout);
			// place resize nodes
			placeResizers();
			// place control
			placeControl();
			
		/*}, 100);*/
	}
	
// end controlMove
}
	
// set up dragging
function controlMouseDown(ev)
{
	// get action event
	ev = ev || window.event;
	// get coordinates
	var mousePos = mouseCoords(ev);
	// get current position of control
	var pos = findPos(currentControl);
	
	// if the control is set
	if(currentControl != null)
	{
		// set the start coordinates
		startCoords = [mousePos.x - pos[0], mousePos.y - pos[1]];
	}
	
	// set draging to true
	dragging = true;
	
	// cancel select and other possible events based on this on
	return false;
	
// end controlMouseDown
}

// turn off dragging on mouseup
function controlMouseUp(ev)
{
	// get event
	ev = ev || window.event;
	// stop dragging
	dragging = false;
	
// end controlMouseUp
}


// ---------- Control Placement: ---------- //
// place the control based on the resize box position
function placeControl()
{
	// if the control is set
	if(currentControl != null)
	{
		// get the resize box location
		resizePos = findPos(resize);
		
		// place the current control
		currentControl.style.width = resize.clientWidth + "px";
		currentControl.style.height = resize.clientHeight + "px";
		// adjust for bind position and offsetY
		currentControl.style.left = resizePos[0] - currentOffsetX - currentBind.x + 1 + "px";
		currentControl.style.top = resizePos[1] - currentOffsetY - currentBind.y + 1 + "px";
		
		// if there is a cover then move it so it covers up everything in the control
		currentCover.style.top = -currentControl.clientHeight + "px";
	}
	
// end placeControl
}


// ---------- Resizer Movement: ---------- //
// moves a resize node
function resizerMove(ev)
{
	// get action event
	ev = ev || window.event;
	
	// if the current resizer is set
	if(currentResizer != null)
	{
		// get the mouse position to move the resizer
		var mousePos = mouseCoords(ev);
		
		// format the coordinates to the grid
		mousePos.y = Math.round((mousePos.y - (RESIZER / 2)) / GRID) * GRID;
		mousePos.x = Math.round((mousePos.x - (RESIZER / 2)) / GRID) * GRID;
		
		// if the resizer id contains the specified character
		//   move the resizer and limit it based on that side
		if(currentResizer.id.indexOf("n") != -1)
		{
			// calculate the resizer top location
			//   adjust for the grid offset and resizer size
			myTop = mousePos.y - (GRID-(currentBind.y % GRID)) - RESIZER + GRID - 1;
			bottom = findPos(resizers[4])[1];
			
			// if the resizer is past the opposite sides resizer then switch the current with the opposite
			if(bottom < (myTop + RESIZER))
			{
				// replace the currentResizer with the opposite by switching the characters in the id
				currentResizer = document.getElementById(currentResizer.id.replace("n", "s"));
			}
			else
			{
				// if the resizer is less than that minsize
				if(bottom - (myTop + RESIZER) <= currentMin.height)
				{
					// set the resizer to the minsize
					currentResizer.style.top = bottom - currentMin.height - RESIZER - 1 + "px";
				}
				else
				{
					// if the resizer is larger then the maxsize and is still inside the bind area
					if(bottom - (myTop + RESIZER) >= currentMax.height && currentMax.height != 0)
					{
						// set the resizer to the min size
						currentResizer.style.top = bottom - currentMax.height - RESIZER - 1 + "px";
					}
					else
					{
						// if the resizer goes outside the bind area
						if(myTop <= currentBind.y)
						{
							// if the resizer is still smaller then the max size
							if(bottom - (myTop + RESIZER) >= currentMax.height && currentMax.height != 0)
							{
								// set the resizer to the min size
								currentResizer.style.top = bottom - currentMax.height - RESIZER - 1 + "px";
							}
							else
							{
								// set the resizer within the bind area
								currentResizer.style.top = currentBind.y - RESIZER - 1 + "px";
							}
						}
						else
						{
							// move the resizer to the mouse position
							currentResizer.style.top = myTop + "px";
						}
					}
				}
			}
		}
		if(currentResizer.id.indexOf("s") != -1)
		{
			myTop = findPos(resize)[1] + 1;
			bottom = mousePos.y - (GRID-(currentBind.y % GRID));
			
			if(bottom < (myTop + RESIZER))
			{
				currentResizer = document.getElementById(currentResizer.id.replace("s", "n"));
			}
			else
			{
				if(bottom - myTop <= currentMin.height)
				{
					currentResizer.style.top = myTop - -currentMin.height + "px";
				}
				else
				{
					if(bottom - myTop >= currentMax.height && currentMax.height != 0)
					{
						currentResizer.style.top = myTop - -currentMax.height + "px";
					}
					else
					{
						if(bottom >= currentBind.bottom)
						{
							if(bottom - myTop >= currentMax.height && currentMax.height != 0)
							{
								currentResizer.style.top = myTop - -currentMax.height + "px";
							}
							else
							{
								currentResizer.style.top = currentBind.bottom - (GRID-(currentBind.y % GRID)) + "px";
							}
						}
						else
						{
							currentResizer.style.top = bottom + "px";
						}
					}
				}
			}
		}
		if(currentResizer.id.indexOf("w") != -1)
		{
			left = mousePos.x - (GRID-(currentBind.x % GRID)) - RESIZER + GRID - 1;
			right = findPos(resizers[6])[0];
		
			if(right < (left + RESIZER))
			{
				currentResizer = document.getElementById(currentResizer.id.replace("w", "e"));
			}
			else
			{
				if(right - (left + RESIZER) <= currentMin.width)
				{
					currentResizer.style.left = right - currentMin.width - RESIZER - 1 + "px";
				}
				else
				{
					if(right - (left + RESIZER) >= currentMax.width && currentMax.width != 0)
					{
						currentResizer.style.left = right - currentMax.width - RESIZER - 1 + "px";
					}
					else
					{
						if(left <= currentBind.x)
						{
							if(right - (left + RESIZER) >= currentMax.width && currentMax.width != 0)
							{
								currentResizer.style.left = right - currentMax.width - RESIZER - 1 + "px";
							}
							else
							{
								currentResizer.style.left = currentBind.x - RESIZER - 1 + "px";
							}
						}
						else
						{
							currentResizer.style.left = left + "px";
						}
					}
				}
			}
		}
		if(currentResizer.id.indexOf("e") != -1)
		{
			left = findPos(resize)[0] + 1;
			right = mousePos.x - (GRID-(currentBind.x % GRID));
			
			if(right < (left + RESIZER))
			{
				currentResizer = document.getElementById(currentResizer.id.replace("e", "w"));
			}
			else
			{
				if(right - left <= currentMin.width)
				{
					currentResizer.style.left = left - -currentMin.width + "px";
				}
				else
				{
					if(right - left >= currentMax.width && currentMax.width != 0)
					{
						currentResizer.style.left = left - -currentMax.width + "px";
					}
					else
					{
						if(right >= currentBind.right)
						{
							if(right - left >= currentMax.width && currentMax.width != 0)
							{
								currentResizer.style.left = left - -currentMax.width + "px";
							}
							else
							{
								currentResizer.style.left = currentBind.right - (GRID-(currentBind.x % GRID)) + "px";
							}
						}
						else
						{
							currentResizer.style.left = right + "px";
						}
					}
				}
			}
		}
		
		// place the resize box to new resizer position
		placeResizeBox();
		
	}
	
// end resizerMove
}

// resizer mouse down
function resizerMouseDown(ev)
{
	// set the target resizer to the currentResizer
	ev = ev || window.event;
	currentResizer = ev.target || ev.srcElement;
	
	// return false to prevent selection and other events
	return false;
// end resizerMouseDown
}

// resizer mouse up
function resizerMouseUp(ev)
{
	// clear the currentResizer
	currentResizer = null;
	
// end resizerMouseUp
}


// ---------- Resizer Placement: ---------- //
// place the resize box based on control location
function placeResize(obj)
{
	// make the resizer visible
	resize.style.display = "block";
	document.getElementById("resizers").style.display = "block";
	
	// get object position
	pos = findPos(obj);
	
	// place resize box
	resize.style.top = pos[1] - 1 + "px";
	resize.style.left = pos[0] - 1 + "px";
	resize.style.height = obj.clientHeight + "px";
	resize.style.width = obj.clientWidth + "px";
	
	// place resizers around box
	placeResizers();

// end placeResize
}

function hideControls()
{
	// hide the resize controls
	resize.style.display = "none";
	document.getElementById("resizers").style.display = "none";
// end hideControls
}

// place the resizer nodes based on resize position
function placeResizers()
{
	// get position of resize
	pos = findPos(resize);
	// set up bounds and adjustments
	bounds = {
		left:pos[0] - RESIZER, 
		top:pos[1] - RESIZER, 
		right:pos[0] + resize.clientWidth + 1, 
		bottom:pos[1] + resize.clientHeight + 1,
		center:pos[0] + (resize.clientWidth / 2) - (RESIZER / 2), 
		middle:pos[1] + (resize.clientHeight / 2) - (RESIZER / 2)
	};
	// place corresponding nodes
	resizers[0].style.left = bounds.left + "px";
	resizers[0].style.top = bounds.top + "px";
	
	resizers[1].style.left = bounds.left + "px";
	resizers[1].style.top = bounds.middle + "px";
	
	resizers[2].style.left = bounds.left + "px";
	resizers[2].style.top = bounds.bottom + "px";
	
	resizers[3].style.left = bounds.center + "px";
	resizers[3].style.top = bounds.top + "px";
	
	resizers[4].style.left = bounds.center + "px";
	resizers[4].style.top = bounds.bottom + "px";
	
	resizers[5].style.left = bounds.right + "px";
	resizers[5].style.top = bounds.top + "px";
	
	resizers[6].style.left = bounds.right + "px";
	resizers[6].style.top = bounds.middle + "px";
	
	resizers[7].style.left = bounds.right + "px";
	resizers[7].style.top = bounds.bottom + "px";
	
//end placeResizers
}

// place the resize box around the moved resizer
function placeResizeBox()
{
	// get position of the current resizer
	pos = findPos(currentResizer);
	// get position of the resize box
	rpos = findPos(resize);
	
	// the change in Y position
	changeY = 0;
	// vertical changes
	if(currentResizer.id.indexOf("n") != -1)
	{
		// set the new height of the resize box
		newHeight = resize.clientHeight + (rpos[1] - (pos[1] + RESIZER));
		// get the change in height of the resize box
		changeY = (newHeight - resize.clientHeight);
		// set the resize box size
		resize.style.top = pos[1] + RESIZER + "px";
		resize.style.height = newHeight + "px";
	}
	if(currentResizer.id.indexOf("s") != -1)
	{
		// set the new height of the resize box
		newHeight = pos[1] - rpos[1];
		// get the change in height of the resize box
		changeY = (newHeight - resize.clientHeight - 1);
		// set the resize box size
		resize.style.height = newHeight - 1 + "px";
	}
	
	// reset the offsets for all the controls after the one being changed
	if(changeY != 0)
	{
		// this is true when the iterations are past the current control
		startAdd = false;
		// loop through all siblings
		for(i = 0; i < currentSiblings.length; i++)
		{
			// if should add to the control
			if(startAdd == true)
			{
				// get the position of the current sibling
				tmppos = findPos(currentSiblings[i]);
				// get the current siblings offset
				tmpoffset = currentSiblings[i].getAttribute("startOffsetY");
				
				// subtract change in height from top
				currentSiblings[i].style.top = tmppos[1] - tmpoffset - changeY - currentBind.y + "px";
				// set the current offset again
				currentSiblings[i].setAttribute("startOffsetY", tmpoffset - -changeY);
			}
			// set start if past current control in list
			if(currentSiblings[i] == currentControl)
			{
				startAdd = true;
			}
			// if before current control skip processing
		}
	}
	
	// horizonal changes
	if(currentResizer.id.indexOf("w") != -1)
	{
		// set new position of resize
		resize.style.left = pos[0] + RESIZER + "px";
		resize.style.width = resize.clientWidth + (rpos[0] - (pos[0] + RESIZER)) + "px";
	}
	if(currentResizer.id.indexOf("e") != -1)
	{
		// set new width
		resize.style.width = pos[0] - rpos[0] - 1 + "px";
	}
	
	// reset timeout
	//if(moveTimeout)
	//{
		//clearTimeout(moveTimeout);
	//}
	
	// timeout before moving control
	/*moveTimeout = setTimeout(function () {
		*/
		// clear previous timer
		//clearTimeout(moveTimeout);
		// place resize nodes
		placeResizers();
		// place control
		placeControl();
		
	/*}, 100);*/
	
// end placeResizeBox
}

	
// ---------- Position Functions: ---------- //
// not written by me
// find the position of an object
function findPos(obj) {
	var curleft = curtop = 0;
	if (obj.offsetParent) {
		curleft = obj.offsetLeft
		curtop = obj.offsetTop
		while (obj = obj.offsetParent) {
			curleft += obj.offsetLeft
			curtop += obj.offsetTop
		}
	}
	return [curleft, curtop];
}

// get the mouse cooridinates cross browser
function mouseCoords(ev){
	ev = ev || window.event;
	
	posX = 0;
	posY = 0;
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

// get the position of an objects offset
function getPosition(e){
	e = e || window.event;
	var left = 0;
	var top  = 0;

	while (e.offsetParent){
		left += e.offsetLeft;
		top  += e.offsetTop;
		e     = e.offsetParent;
	}

	left += e.offsetLeft;
	top  += e.offsetTop;

	return {x:left, y:top};
}

// get the mouse coordinate offsets for scrolls
function getMouseOffset(target, ev){
	ev = ev || window.event;

	var docPos    = getPosition(target);
	var mousePos  = mouseCoords(ev);
	return {x:mousePos.x - docPos.x, y:mousePos.y - docPos.y};
}















