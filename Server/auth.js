var mongo = require('mongodb');
var constants = require('./constants.js').constants;
var crypto = require('crypto');

function Auth(db){
    //Public
    
    this.getRegId(uname, onComplete){
        this._users.findOne({'username':uname}, function(err, item){
            onComplete(item.reg_id);
            console.log("processed registration id");
        });
    }
    this.getUserData=function(sessionKey,onComplete){
        if (!(sessionKey in this._sessions)){
            onComplete({'error':"SessionKey Invalid!"})
                return
        }
        var sessions = this._sessions;
        this._users.findOne({"email":sessions[sessionKey]},function(err,item){
            onComplete({
                'error':false,
                'email':item.email,
                'username':item.username,
                'karma':item.karma,
                'reg_id':item.reg_id,
            });
        });
    }
    this.incrementKarma=function(sessionKey,amt,onComplete){
        if (!(sessionKey in this._sessions)){
            onComplete({'error':"SessionKey Invalid!"});
            return;
        }
        var sessions = this._sessions;
        var users = this._users;
        this._users.findOne({"email":sessions[sessionKey]},function(err,item){
            users.update({"email":sessions[sessionKey]},{"$set":{"karma":item.karma+amt}},function(err,item){
                onComplete({'error':false});
            })
        })
    }
    this.addActiveTask=function(sessionKey,id){
        if (!this._activeTasks[sessionKey]){
            this._activeTasks[sessionKey]=[]
        }
        this._activeTasks[sessionKey].push({'timestamp':new Date(), 'taskId':id})
    }
    this.removeActiveTask=function(sessionKey,id){
        for (var i = 0; i < this._activeTasks[sessionKey].length; i++){
            if (this._activeTasks[sessionKey][i].taskId==id){
                return this._activeTasks[sessionKey].splice(i);
            }
        }
    }
    this.newUser=function(email, username, password, reg_id, onComplete){
        if (onComplete==undefined){
            onComplete=function(){}
        }

        //Validation
        if (!emailValidate(email)){
            onComplete({'error':'Invalid email'});
            return;
        }
        if (!usernameValidate(username)){
            onComplete({'error':'Invalid username'});
            return;
        }

        var users = this._users;
        //Compute salted hash
        var salt = this._salt();
        var saltedHash = this._hash(password,salt);

        users.findOne({'email':email},function(err,item){
            if (item != null){
                //Email is not unique
                onComplete({'error':'Email is not unique'})
            }else{
                users.findOne({'username':username},function(err,item){
                    if (item != null){
                        //Username is not unique
                        onComplete({'error':'Username is not unique'})
                    }else{
                        //Everything okay. Create user.
                        //Insert entry into database
                        users.insert({
                            'email':email,
                            'username':username,
                            'password':saltedHash,
                            'salt':salt,
                            'karma':0,
                            'reg_id':reg_id,
                        },{safe:true},function(){
                            onComplete({'error':false});
                        })
                    }
                })
            }
        })
    }

    this.authenticate=function(credential,password,onComplete){
        if (onComplete==undefined){
            onComplete=function(){}
        }

        var query = {}
        var credType = emailValidate(credential)?'email':'username' 
            query[credType]=credential;

        var hash = this._hash;
        var makeSessionKey = this._makeSessionKey;
        var sessions = this._sessions;
        var userToSession = this._userToSession;
        var activeTasks = this._activeTasks;

        this._users.findOne(query,function(err,item){
            if (!item){
                onComplete({"error":"Invalid "+credType,"sessionKey":false})
            }else{
                if (hash(password,item.salt)==item.password){
                    //Successful Login. Return session key.
                    var key = makeSessionKey(item.email,item.username,sessions,userToSession,activeTasks);
                    onComplete({"error":false,"sessionKey":key});
                }else{
                    onComplete({"error":'Incorrect password',"sessionKey":false});
                }
            }
        });
    }

    this.top_karma=function(lim, callback){
        this._users.find({}, {"karma":1, "username":1}, {limit: lim, sort: [['karma',1]]}, function(err, result){
            result.toArray(function(err, docs){
                callback(docs);
            });
        });
    }

    //Private

    //Create user collection
    this._users = db.collection(constants.auth.collection);

    //Two way search
    this._sessions={}
    this._userToSession={}

    //Extending Data
    this._activeTasks={}

    this._hash=function(s,salt){
        var inStr=s+salt;
        var sha = crypto.createHash('sha1');
        sha.update(inStr);
        return sha.digest('hex')
    }
    this._salt=function(){
        //Just add pepper...
        return (new Date()).getTime()
    }
    this._makeSessionKey=function(email, username,sessions,userToSession,activeTasks){
        if (userToSession[email]!=null){
            delete sessions[userToSession[email]];
            delete activeTasks[userToSession[email]];
            delete userToSession[email];
        }
        var key;
        do{
            key = ((new Date()).getTime()+Math.floor(Math.random()*579258932)).toString(16)
        }while (sessions[key]!=null);
        sessions[key]=email;
        activeTasks[key]=[];
        userToSession[email]=key;
        return key
    }

}

function emailValidate(email) {return /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(email)} 
function usernameValidate(username) {return !emailValidate(username)} 

exports.Auth=Auth;
