var constants = require('./constants.js').constants;
var ObjectID = require('mongodb').ObjectID;
function HelpMeDb(db){
    this.collection = db.collection(constants.favors.collection); // pointless comment
    
    /**
     * loc: location of user who sent help request
     * epicenter: location at which people will be notified of the help request
    **/ 
    this.update = function(users, sessionkey, title, desc, urgent, loc, epicenter){
        newFavor = { 
                            'username': users[sessionkey],
                            'title': title,
                            'desc': desc,
                            'urgent': urgent,
                            'loc': loc,
                            'epicenter': epicenter,
                            'active': true,
                            'helpComing':false
        }
        this.collection.insert(newFavor, function(err,item){
            if(err){
                console.log("ka-boom");   
            }
        });    
    }
    
    this.get = function(sessionKey, loc, user_keys, callbach){
        this.collection.update( {'sessionKey':sessionKey}, 
                                {$set: {'loc.x': loc.x, 'loc.y': loc.y}}, function(err, item){
            if(err){
                console.log("d-d-d-d-d-d-d-d-d-DROP THE BASS WUBB WUBB WUBB WUBB");
            }
        });
        var max_x = loc.x + constants.app.degradius; // a portmanteau of 'degree' and 'radius'
        var min_x = loc.x - constants.app.degradius;
        var max_y = loc.y + constants.app.degradius;
        var min_y = loc.y - constants.app.degradius;
        console.log("min x: " + min_x + " max_x: " + max_x); 
        var nearby;
        // squarey-findey thing to make db go zoom-zoom
        this.collection.find({  'epicenter.x': {$gt: min_x, $lt: max_x}, 
                                'epicenter.y': {$gt: min_y, $lt: max_y},
                                'active': true  }, function(err,item){
            item.toArray(function(err, docs)
                {
                    callbach(docs); // Don't you want to take a leap of faith? Or become an old man, filled with regret, waiting to die alone!
                });
        });      
    }

    this.helpComing = function(id){
        objId = new ObjectID(id); 
        this.collection.update( {'_id': objId}, {$set: {'helpComing':true}}, function(err, item){
            if(err){
                console.log("Error updating helpComing field: " + err);
            }
        }); 
    }

    this.favorCompleted = function(id){
        objId = new ObjectID(id); 
        this.collection.update( {'_id': objId}, {$set: {'active':false}}, function(err, item){
            if(err){
                console.log("Error updating active field: " + err);
            }
        }); 
    }
}

exports.HelpMeDb = HelpMeDb;
