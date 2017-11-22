var expireDate = new Date();
expireDate.setMonth(expireDate.getMonth() + 3);

var users = search.luceneSearch('TYPE:"cm:person"');

for(var i = 0 ; i < users.length ; i++){
    var user = users[i]
    user.properties["{http://cts-beta.homeoffice.gov.uk/model/user/1.0}passwordExpiryDate"] = expireDate;
    user.save();
}