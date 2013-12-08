var http = require('http');
var mongo = require('mongodb');
var async = require('async');
var constants = require('./constants.js').constants;
var Auth = require('./auth.js').Auth;
var HelpMeDb = require('./helpme_db.js').HelpMeDb;
var requestHandler = require('./requestHandler.js').requestHandler;

var globals = {}

async.series([
        function initDb(next){
            var mongoClient = new mongo.MongoClient(new mongo.Server(constants.app.hostname, constants.db.port));

            mongoClient.open(function(err,mongoClient){
                var db = mongoClient.db(constants.db.name);
                globals.auth = new Auth(db);
                globals.helpMeDb = new HelpMeDb(db);
                next();
            })
        },
        function startHTTP(next){
            var server = http.createServer(function(req,res){
                requestHandler(req,res,globals.auth,globals.helpMeDb)
            }).listen(constants.app.port,function(){
                console.log("HTTP Started...");
                console.log("Listening at http://"+ constants.app.hostname + ":" + constants.app.port);
                console.log("Press ctrl-c to stop.");
                next();
            })
        }
    ])

