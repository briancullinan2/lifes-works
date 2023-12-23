// JavaScript Document

// set up the actions for resizable objects
function setSizeAction(obj)
{
	setListener(obj);
	obj.listener["focus"][obj.listener["focus"].length] = sizeFocus;
	obj.listener["mousedown"][obj.listener["mousedown"].length] = sizeMouseDown;
	obj.listener["mouseup"][obj.listener["mouseup"].length] = sizeMouseUp;
	obj.listener["mousemove"][obj.listener["mousemove"].length] = sizeMouseMove;
	obj.listener["blur"][obj.listener["blur"].length] = sizeBlur;
}

// when the object recieves focus
function sizeFocus(ev, obj)
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
function sizeBlur(ev)
{
	// switch the placeholder and the currentObj
	unsetPlaceholder();
	// hide all the resizers
	unsetResizeNodes();
	// remove the selected class on currentObj
	removeClass(currentObj, selectedClass);
	// add the unselected class
	addClass(currentObj, unselectedClass);
	// make sure this movement does not affect other relative objects
	
}

// the mousedown function for sizing
function sizeMouseDown(ev, obj)
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
	// set the position of all the resize Nodes
	setResizeNodes();
}


// the mouseup function for dragging
function sizeMouseUp(ev)
{

}

// the mousemove function for draggin
function sizeMouseMove(ev, obj)
{

}

// set the position of all the resize nodes
function setResizeNodes() 
{
	// loop through each node and set some properties
	for(var i in resizers)
	{
		// make sure the node is visiable
		resizers[i].style.display = "block";
		// get the aboslute position of the current object
		var pos = findPos(currentObj);
		// get the size of currentObj for centering the resize node
		var bounds = getBounds(currentObj);
		// make the default position the center of the object
		myx = pos.x + (bounds.width / 2) - (resizers[i].offsetWidth / 2);
		myy = pos.y + (bounds.height / 2) - (resizers[i].offsetHeight / 2);
		// change the resize position based on what letters it has in it's id
		var myid = resizers[i].id.substring(resizerPrefix.length, resizers[i].id.length);
		// if the id contains a north (n)
		if(myid.indexOf("n") != -1)
		{
			// move the resizer to the top
			myy = pos.y - resizers[i].offsetHeight;
		}
		// if the id contains a south (s)
		if(myid.indexOf("s") != -1)
		{
			// move the resizer to the bottom
			myy = pos.y + bounds.height;
		}
		// if the id contains a east (e)
		if(myid.indexOf("e") != -1)
		{
			// move the resizer to the right
			myx = pos.x + bounds.width;
		}
		// if the id contains a west (w)
		if(myid.indexOf("w") != -1)
		{
			// move the resizer to the left
			myx = pos.x - resizers[i].offsetWidth;
		}
		// set the location of the resizer
		setBounds(resizers[i], {x:myx, y:myy});
	}
}

// unset the resize nodes from the object
function unsetResizeNodes()
{
	// loop through each node and set some properties
	for(var i in resizers)
	{
		// make sure the node is hidden
		resizers[i].style.display = "none";
	}
}

// the mouse down for a resizeNode
function resizerMouseDown(ev)
{
	// get the event
	ev = ev || window.event;
	// get the event target
	currentResizer = ev.target || ev.srcElement;
	// set the sizing class on currentObj
	addClass(currentObj, sizingClass);
	// turn off browser text selection
	document.onselectstart = function () { return false; }
	document.onmousedown = function () { return false; }
	// set some global document actions
	document.onmouseup = function (ev) { currentResizer.onmouseup(ev); }
	document.onmousemove = function (ev) { currentResizer.onmousemove(ev); }
}

// when the resizer is mouse up
function resizerMouseUp()
{
	// clear the current resizer to stop resizing
	currentResizer = null;
	// turn on browser text selection
	document.onselectstart = function () { return true; }
	document.onmousedown = function () { return true; }
	// unset some global document actions
	document.onmouseup = function () { return true; }
	document.onmousemove = function () { return true; }
	// remove the sizing class on currentObj
	removeClass(currentObj, sizingClass);
}

// when the resizer is moved
function resizerMove(ev)
{
	// if the current resizer is set
	if(currentResizer != null)
	{
		// get the mouse position to move the resizer
		var mousePos = mouseCoords(ev);
		// center the resizer on the mouse
		mousePos.x = mousePos.x - (currentResizer.offsetWidth / 2);
		mousePos.y = mousePos.y - (currentResizer.offsetHeight / 2);
		// make sure resizer is inside bounds
		checkBounds(mousePos);
		// switch nodes that cross over
		var result = switchNodes(mousePos);
		mousePos = result.mousePos;
		// only move the related resizers
		moveRelated(mousePos);
		// get the new location of the two oposite nodes
		var nw = findPos(resizers["nw"]);
		var se = findPos(resizers["se"]);
		// use these two resizers to get the new control size
		var bounds = {
			x:nw.x + resizers["nw"].offsetWidth,
			y:nw.y + resizers["nw"].offsetHeight,
			width:se.x - (nw.x + resizers["nw"].offsetWidth),
			height:se.y - (nw.y + resizers["nw"].offsetHeight)
		}
		// set the bounds of the control and resizer
		setBounds(currentObj, bounds)
		// re-center the resize nodes that need it
		setResizeNodes();
		// move the resize box at the same time
		setResize();
		// check if newid is different
		if(result.newNode != currentResizer)
		{
			// change the current resizer from result above
			currentResizer = result.newNode;
		}
	}
}

// check if resizer is inside bounds
function checkBounds(mousePos)
{
	// make sure resizer is inside bounds
	// first check left side
	if(mousePos.x <= currentBind.x - currentResizer.offsetWidth)
	{
		mousePos.x = currentBind.x - currentResizer.offsetWidth;
	}
	// check right
	if(mousePos.x >= currentBind.right)
	{
		mousePos.x = currentBind.right;
	}
	// check top
	if(mousePos.y <= currentBind.y - currentResizer.offsetHeight)
	{
		mousePos.y = currentBind.y - currentResizer.offsetHeight;
	}
	// check bottom
	if(mousePos.y >= currentBind.bottom)
	{
		mousePos.y = currentBind.bottom;
	}
	// return adjusted mousePos
	return mousePos;
}

// move the related resizers to match the mousePos
// might be able to avoid looping
function moveRelated(mousePos)
{
	// get the id of the current resizer
	var myid = currentResizer.id.substring(resizerPrefix.length, currentResizer.id.length);
	// always make sure the two opposite nodes we use stay in the right position
	// change the position of related resizers including the current one
	var i = myid.substring(0, 1);
	var j = myid.substring(1, 2);
	// if j is blank then it is moving a horizontal node
	if(j == "") { j = i };
	// if the resizer is vertical
	if(myid.indexOf("n") != -1 || myid.indexOf("s") != -1)
	{
		// move all the resizers on the north
		setBounds(resizers[i + "w"], {y:mousePos.y});
		setBounds(resizers[i	  ], {y:mousePos.y});
		setBounds(resizers[i + "e"], {y:mousePos.y});
	}
	// if the resizer is horizontal
	if(myid.indexOf("e") != -1 || myid.indexOf("w") != -1)
	{
		// move all the resizers on the north
		setBounds(resizers["n" + j], {x:mousePos.x});
		setBounds(resizers[		 j], {x:mousePos.x});
		setBounds(resizers["s" + j], {x:mousePos.x});
	}
}

// switch nodes that cross over
function switchNodes(mousePos)
{
	// get the id of the current resizer
	var myid = currentResizer.id.substring(resizerPrefix.length, currentResizer.id.length);
	// get the location of the two oposite nodes
	var nw = findPos(resizers["nw"]);
	var se = findPos(resizers["se"]);
	// the new id to replace with
	var newid = myid;
	// check for switches when one node is moved past another node
	if(myid.indexOf("n") != -1 && mousePos.y >= se.y - currentResizer.offsetHeight)
	{
		newid = newid.replace("n", "s")
		mousePos.y = se.y - currentResizer.offsetHeight;
	}
	if(myid.indexOf("s") != -1 && mousePos.y <= nw.y + resizers["nw"].offsetHeight)
	{
		newid = newid.replace("s", "n")
		mousePos.y = nw.y + resizers["nw"].offsetHeight;
	}
	if(myid.indexOf("w") != -1 && mousePos.x >= se.x - currentResizer.offsetWidth)
	{
		newid = newid.replace("w", "e")
		mousePos.x = se.x - currentResizer.offsetWidth;
	}
	if(myid.indexOf("e") != -1 && mousePos.x <= nw.x + resizers["nw"].offsetWidth)
	{
		newid = newid.replace("e", "w")
		mousePos.x = nw.x + resizers["nw"].offsetWidth;
	}
	return {mousePos:mousePos, newNode:resizers[newid]};
}








