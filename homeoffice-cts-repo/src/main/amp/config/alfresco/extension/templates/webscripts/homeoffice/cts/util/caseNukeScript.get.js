
var scriptLog = [];
function reportLog(log) {
	logger.log(log);
	scriptLog.push(log);
}

var production_check = people.getGroup("GROUP_THIS_IS_NOT_PRODUCTION");
// if group exists, delete them
if (production_check == null) {
	status.code = 403;
	status.message = "This cannot be executed on production. A group must be created called THIS_IS_NOT_PRODUCTION";
	status.redirect = true;
} else {

	var ctsNode = companyhome.childByNamePath("CTS");
	var children = ctsNode.children;
	for(var i = 0 ; i < children.length ; i++){
		if(children[i].name == 'Cases'){
			//delete the children only
			var subChildren = children[i].children;
			for(var j = 0 ; j < subChildren.length ; j++){
				subChildren[j].remove();
			}
		}
	}

	model.report = scriptLog;


}