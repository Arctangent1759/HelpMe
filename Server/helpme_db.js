var constants = require('./constants.js').constants;
var ObjectID = require('mongodb').ObjectID;
function HelpMeDb(db,auth){
    this.collection = db.collection(constants.favors.collection); // pointless comment

    this._auth=auth;
    this._taskMapping={}

    /**
     * loc: location of user who sent help request
     * epicenter: location at which people will be notified of the help request
     **/ 
    this.update = function(sessionkey, title, desc, urgent, loc, epicenter, onComplete){

        collection = this.collection;
        this._auth.getUserData(sessionkey,function(data){
            newFavor = { 
                'username': data.username,
                'title': title,
                'desc': desc,
                'urgent': urgent,
                'loc': loc,
                'epicenter': epicenter,
                'active': true,
                'helpComing':false
            }
            collection.insert(newFavor, function(err,item){
                onComplete(err);
            });    
        })
    }

    this.get = function(sessionKey, loc, callbach){
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
        //console.log("min x: " + min_x + " max_x: " + max_x); 
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

    this.helpComing = function(sessionKey,id){
        this._auth.addActiveTask(sessionKey,id);
        if (!this._taskMapping[id]){
            this._taskMapping[id]=[];
        }
        this._taskMapping[id].push(sessionKey);

        objId = new ObjectID(id); 
        this.collection.update( {'_id': objId}, {$set: {'helpComing':true}}, function(err, item){
            if(err){
                console.log("Error updating helpComing field: " + err);
            }
        }); 
    }

    this.favorCompleted = function(id){
        for (var i = 0; i < this._taskMapping[id].length; i++){
            this._auth.removeActiveTask(this._taskMapping[id][i],id);
            this._auth.incrementKarma(this._taskMapping[id][i],100,function(){})//TODO: implement karma function
        }
        delete this._taskMapping[id];

        objId = new ObjectID(id); 
        this.collection.update( {'_id': objId}, {$set: {'active':false}}, function(err, item){
            if(err){
                console.log("Error updating active field: " + err);
            }
        }); 
    }
}

exports.HelpMeDb = HelpMeDb;
