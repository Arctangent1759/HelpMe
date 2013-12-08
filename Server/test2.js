/*
var mongo = require('mongodb');

var db = new mongo.Db("helpmedb",new mongo.Server("localhost", 27017))
db.open(function(err,mongoClient){
    users = db.collection('authentication');
    users.findOne({"email":"alex.p.chu@gmail.com"},function(err,item){
        console.log(err);
        console.log(item);
    })
    mongoClient.close();
})
*/

var mongo = require('mongodb');
var Auth = require('./auth.js').Auth;
var mongoClient = new mongo.MongoClient(new mongo.Server("localhost", 27017))
mongoClient.open(function(err,mongoClient){
    var db = mongoClient.db("helpmedb")
    var users = db.collection("authentication");
    users.findOne({'email':'alex.p.chu@gmail.com'},{},function(err,item){
        console.log(err)
        console.log(item)
        mongoClient.close()
    })
})
