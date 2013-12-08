var constants = require('./constants.js').constants;
function HelpMeDb(db){
    this.collection = db.collection(constants.favors.collection); // pointless comment
    this.update = function(newFavor){
        this.collection.insert(newFavor);    
    }
    this.get = function(sessionKey, loc, user_keys){
        this.collection.update( {'sessionKey':sessionKey}, 
                                {$set: {'loc.x': loc.x, 'loc.y': loc.y}}
        );
        var max_x = loc.x + constants.app.degradius;
        var min_x = loc.x - constants.app.degradius;
        var max_y = loc.y + constants.app.degradius;
        var min_y = loc.y - constants.app.degradius;
        return constants.find(  {'epicenter.x': {$gt: min_x, $lt: max_x},
                                 'epicenter.y': {$gt: min_y, $lt: max_y} } // squarey-findey thing to make mongo go zoom-zoom
        ).toArray();    
    }
        
}

exports.HelpMeDb = HelpMeDb;
