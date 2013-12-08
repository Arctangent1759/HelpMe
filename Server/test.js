/*
var mongo = require('mongodb')
var mongoClient = new mongo.MongoClient(new mongo.Server("localhost", 27017))
mongoClient.open(function(err,mongoClient){
    var db = mongoClient.db("test1");
    var c = db.collection('testCollection');
    c.insert({"foo":"fladnag","bar":1234,"baz":{"a":true,"b":false}},{safe:true},function(){});
    mongoClient.close();
})
*/

var mongo = require('mongodb');
var Auth = require('./auth.js').Auth;
var mongoClient = new mongo.MongoClient(new mongo.Server("localhost", 27017))
mongoClient.open(function(err,mongoClient){
    var db = mongoClient.db("helpmedb")
    var a = new Auth(db)
    a.newUser("alex.p.chu@gmail.com","arctangent","1234asdf",function(result){
        a.authenticate("alex.p.chu@gmail.com","1234asdf",function(result){
            console.log(result);
            mongoClient.close();
        });
    });
})
