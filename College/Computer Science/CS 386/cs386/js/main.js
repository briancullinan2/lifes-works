var mainXML = null;
var main = null;

function editInit()
{
	mainXML = document.getElementById("Main_XML");
	main = document.getElementById("Main");
	
	fireOnCreate(main);
}

function addClone(obj, parent)
{
	parent = parent || main;
	
	if(typeof(obj) == "string")
	{
		obj = document.getElementById(obj);
	}
	// clone entire object
	var clone = obj.cloneNode(true);
	parent.appendChild(clone);
	
	// go through and call oncreate refering to new cloned object
	fireOnCreate(main);
	
	// on create should create all references in xml
	
	// input functionality will use xml referneces
	
}

function fireOnCreate(obj)
{
	if(obj && obj.getAttribute && typeof(obj.getAttribute("oncreate")) != "undefined" && obj.getAttribute("oncreate") != null)
	{
		obj.create = new Function("obj", obj.getAttribute("oncreate"));
		
		// clear the on create
		obj.setAttribute("oncreate", "");
		
		obj.create(obj);

		// clear oncreate from obj
		obj.oncreate = null;
		
	}
	for(var i = 0; i < obj.childNodes.length; i++)
	{
		fireOnCreate(obj.childNodes[i]);
	}
}

function createSurveyElement(name, xml_parent, obj)
{
	var newEl = document.createElement(name);
	xml_parent = xml_parent || mainXML;
	xml_parent.appendChild(newEl);
	
	if(typeof(obj) != "undefined")
	{
		obj.xml = newEl;
	}
	
	return newEl;
}

function setSurveyElementValue(el, val)
{
	// remove all the children
	for(var i = el.childNodes.length - 1; i >= 0; i--)
	{
		el.removeChild(el.childNodes[i]);
	}
	
	// create new text object
	var newval = document.createTextNode(val)
	
	// add it to value
	el.appendChild(newval);
}

//--------general
// semi-abstract methods
function addLocationListner(obj, input)
{
	setListener(obj);
	obj.listener['blur'][obj.listener['blur'].length] = new Function("var bounds=getBounds(document.getElementById('" + obj.id + "')); setSurveyElementValue(document.getElementById('" + input.id + "').xml, bounds.x+','+bounds.y+','+bounds.width+','+bounds.height);");
}
