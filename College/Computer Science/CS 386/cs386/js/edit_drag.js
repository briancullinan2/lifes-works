// JavaScript Document

// set the actions for dragable objects
function setDragAction(obj)
{
	setListener(obj);
	obj.listener["focus"][obj.listener["focus"].length] = dragFocus;
	obj.listener["mousedown"][obj.listener["mousedown"].length] = dragMouseDown;
	obj.listener["mouseup"][obj.listener["mouseup"].length] = dragMouseUp;
	obj.listener["mousemove"][obj.listener["mousemove"].length] = dragMouseMove;
	obj.listener["blur"][obj.listener["blur"].length] = dragBlur;
}

// when the object recieves focus
function dragFocus(ev, obj)
{
	// get the event
	ev = ev || window.event;
	// get the event target
	var target = obj || ev.target || ev.srcElement;
	// set the currentObj
	currentObj = target;
	// set the selected class on currentObj
	removeClass(currentObj, unselectedClass);
	addClass(currentObj, selectedClass);
	// switch the current object with a placeholder and make the current object position:absolute
	setPlaceholder();
	// check if currentObj has a preset binding
	var bind = null;
	if(currentObj.getAttribute("bind"))
	{
		// get the binding object
		bind = document.getElementById(currentObj.getAttribute("bind"));
		// make sure the bind object exists
		if(typeof(bind) == "undefined")
		{
			// default to parent number
			bind = currentObj.parentNode;
		}
	}
	// default to the parent object as a binding area
	else
	{
		// get the binding object
		bind = currentObj.parentNode;
	}
	// get the bounds of the binding object
	var bindBounds = getBounds(bind);
	// get the absolute location of the binding object
	var bindPos = findPos(bind);
	// set the current binding area
	currentBind = {
		x:bindPos.x,
		y:bindPos.y,
		width:bindBounds.width,
		height:bindBounds.height,
		right:bindPos.x + bindBounds.width,
		bottom:bindPos.y + bindBounds.height
	}
}

// when the object loses focus
function dragBlur(ev)
{
	// switch the placeholder and the currentObj
	unsetPlaceholder();
	// remove the selected class on currentObj
	removeClass(currentObj, selectedClass);
	// add the unselected class
	addClass(currentObj, unselectedClass);
	// make sure this movement does not affect other relative objects
	
}

// the mousedown function for dragging
function dragMouseDown(ev, obj)
{
	// get the event
	ev = ev || window.event;
	// get the event target
	var target = obj || ev.target || ev.srcElement;
	// fire the blur for the currentObj
	if(currentObj != null && currentObj != target)
	{
		currentObj.blur(ev);
	}
	// fire the focus event for this object
	target.focus(ev)
	// set the position of the resize box just for the border effect
	setResize();
	// get the mouse coordinates
	var mousePos = mouseCoords(ev);
	// get the current object position after placeholder replacement
	var pos = findPos(currentObj);
	// set the mouseOffset variable to the mouse down position
	mouseDownOffset = {
		x:mousePos.x - pos.x,
		y:mousePos.y - pos.y
	};
	// set the dragging class on currentObj
	addClass(currentObj, draggingClass);
	// turn off browser text selection
	document.onselectstart = function () { return false; }
	document.onmousedown = function () { return false; }
	// set some global document actions
	document.onmouseup = function (ev) { currentObj.onmouseup(ev); }
	document.onmousemove = function (ev) { currentObj.onmousemove(ev); }
	// set the draggable variable to start dragging
	isDragging = true;
}

// the mouseup function for dragging
function dragMouseUp(ev)
{
	if(isDragging == true)
	{
		// set the draggable variable to stop dragging
		isDragging = false;
		// turn on browser text selection
		document.onselectstart = function () { return true; }
		document.onmousedown = function () { return true; }
		// unset some global document actions
		document.onmouseup = function () { return true; }
		document.onmousemove = function () { return true; }
		// remove the dragging class on currentObj
		removeClass(currentObj, draggingClass);
		// only do this if there is no resizeable class
		if(hasClass(currentObj, sizableClass) == false)
		{
			// hide the resize box to enable editing
			unsetResize();
		}
	}
}

// the mousemove function for draggin
function dragMouseMove(ev, obj)
{
	if(isDragging == true)
	{
		// get the mouse location
		var mousePos = mouseCoords(ev);
		// adjust for mousedownOffset
		mousePos.x -= mouseDownOffset.x;
		mousePos.y -= mouseDownOffset.y;
		// get the currentObj bounds to use below
		var bounds = getBounds(currentObj);
		// make sure location is within the binding object
		// first check left side
		if(mousePos.x <= currentBind.x)
		{
			mousePos.x = currentBind.x;
		}
		// check right
		if(mousePos.x + bounds.width >= currentBind.right)
		{
			mousePos.x = currentBind.right - bounds.width;
		}
		// check top
		if(mousePos.y <= currentBind.y)
		{
			mousePos.y = currentBind.y;
		}
		// check bottom
		if(mousePos.y + bounds.height >= currentBind.bottom)
		{
			mousePos.y = currentBind.bottom - bounds.height;
		}
		// set the currentObj location
		setBounds(currentObj, mousePos);
		// move the resize box at the same time
		setResize();
		// only do this if there is a resizeable class
		if(hasClass(currentObj, sizableClass) == true)
		{
			// set the position of all the resize Nodes
			setResizeNodes();
		}
	}
}

// switch the current object with a placeholder
function setPlaceholder()
{
	if(placeHolderSet == false)
	{
		// get the currentObj position before changing anything
		var pos = findPos(currentObj);
		// add the placeholder to the currentObj parent
		currentObj.parentNode.insertBefore(placeHolder, currentObj);
		// make the placeholder display
		placeHolder.style.display = "block";
		// match all the boundary position with the currentObj
		var bounds = getBounds(currentObj);
		// only recalculate if the currentObj is already position absolute
		if(currentObj.style.position == "absolute")
		{
			// get the placeHolder position
			var placePos = findPos(placeHolder);
			// get the placeholder relative bounds
			var placeBounds = getBounds(placeHolder);
			// recalculate the location for the placeholder
			bounds.x = placeBounds.x + (bounds.x - placePos.x);
			bounds.y = placeBounds.y + (bounds.y - placePos.y);
		}
		// set the placeholder location
		setBounds(placeHolder, bounds);
		// make it match everything
		placeHolder.className = currentObj.className;
		// make it appear blank
		placeHolder.style.visibility = "hidden";
		// make the placeholder position relative so it can match the currentObj location
		placeHolder.style.position = "relative";
		// make the currentObj position:absolute
		currentObj.style.position = "absolute";
		// line up the currentObj to where it just was
		setBounds(currentObj, pos);
		// toggle placeHolderSet
		placeHolderSet = true;
	}
}

// switch back the currentObj with the placeholder
function unsetPlaceholder()
{
	if(placeHolderSet == true)
	{
		// get the placeholder position before changing anything
		var placePos = findPos(placeHolder);
		// get the placeholder relative bounds
		var placeBounds = getBounds(placeHolder);
		// reset the objects relativity
		currentObj.style.position = "relative";
		// don't display the placeholder
		placeHolder.style.display = "none";
		// get the new bounds of the currentObj
		var bounds = getBounds(currentObj);
		// recalculate the location for the currentObj
		bounds.x = placeBounds.x + (bounds.x - placePos.x);
		bounds.y = placeBounds.y + (bounds.y - placePos.y);
		// set the new bounds on the currentObj
		setBounds(currentObj, bounds);
		// toggle placeHolderSet
		placeHolderSet = false;
	}
}

// set the resize box to the current objects position
function setResize()
{
	// make sure resize is visible
	resize.style.display = "block";
	// make sure resize is position absolute
	resize.style.position = "absolute";
	// get the aboslute position of the current object
	var pos = findPos(currentObj);
	// move the resize box to the current object bounds
	var bounds = getBounds(currentObj);
	// make a few adjustments for the size of the resize box
	bounds.x = pos.x - (resize.offsetWidth - resize.clientWidth) / 2;
	bounds.y = pos.y - (resize.offsetHeight - resize.clientHeight) / 2;
	bounds.width += (resize.offsetWidth - resize.clientWidth);
	bounds.height += (resize.offsetHeight - resize.clientHeight);
	// set the position of the resize box
	setBounds(resize, bounds);
}

// hide and unset the resize box
function unsetResize()
{
	// make sure resize is hidden
	resize.style.display = "none";
	// move it to the blank position
	var bounds = {
		x:0,
		y:0,
		height:0,
		width:0
	}
	// set the position of the resize box
	setBounds(resize, bounds);
}
