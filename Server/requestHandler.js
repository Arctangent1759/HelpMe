var url = require('url')
var querystring = require('querystring')
var R = require('./response.js').Response
r = new R();

handle={
    "/":function(req,res,auth,helpMeDb){
        res.writeHeader(200,{"Content-Type":"text/plain"});
        res.write("This page should not see the light of day.");
        res.end();
    },
    "/createAccount":function(req,res,auth,helpMeDb){
        //email, username, password, reg_id
        var args = querystring.parse(url.parse(req.url).query);
        auth.newUser(args.email, args.username, args.password, args.reg_id, function(result){
            res.writeHeader(200,{"Content-Type":"text/plain"});
            res.write(""+result.error);
            res.end();
        })
    },
    "/login":function(req,res,auth,helpMeDb){
        var args = querystring.parse(url.parse(req.url).query);
        auth.authenticate(args.credential,args.password,function(result){
            res.writeHeader(200,{"Content-Type":"text/plain"});
            if (result.error){
                res.write(result.error)
            }else{
                res.write(result.sessionKey)
            }
            res.end();

        })
    },
    "/getHelp":function(req,res,auth,helpMeDb){
        var args = querystring.parse(url.parse(req.url).query);
        helpMeDb.update(args.sessionKey, args.title, args.desc, args.urgent=="true", JSON.parse(args.loc), JSON.parse(args.epicenter), function(err){
            res.writeHeader(200,{"Content-Type":"text/plain"});
            if (err){
                res.write("An error occured while posting your help request.")
            }else{
                res.write("Help request posted successfully.")
                 
            }
            res.end();
        })
    },
    "/getRequests":function(req,res,auth,helpMeDb){
        var args = querystring.parse(url.parse(req.url).query);
        helpMeDb.get(args.sessionKey, JSON.parse(args.loc), function(docs){
            res.writeHeader(200,{"Content-Type":"text/plain"});
            res.write(JSON.stringify(docs));
            res.end();
        })
    },
    "/helpComing":function(req,res,auth,helpMeDb){
        var args = querystring.parse(url.parse(req.url).query);
        helpMeDb.helpComing(args.sessionKey,args.id);
        res.writeHeader(200,{"Content-Type":"text/plain"});
        res.write("Help is on the way!");
        res.end();

    },
    "/favorCompleted":function(req,res,auth,helpMeDb){
        var args = querystring.parse(url.parse(req.url).query);
        helpMeDb.favorCompleted(args.id);
        res.writeHeader(200,{"Content-Type":"text/plain"});
        res.write("Favor completed.");
        res.end();

    },

}

function requestHandler(req,res,auth,helpMeDb){
    var pathname = url.parse(req.url).pathname;

    if (!handle[pathname]){

        res.writeHeader(404,{'content-type':'text/plain'});
        res.write("404: A grilled-cheese-relalted error has occurred. Please standby and/or wait for the singularity.");
        res.end()
    }else{
        handle[pathname](req,res,auth,helpMeDb)
    }
}
exports.requestHandler=requestHandler
