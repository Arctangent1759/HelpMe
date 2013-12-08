var url = require('url')
var querystring = require('querystring')

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
        res.writeHeader(200,{"Content-Type":"text/plain"});
        res.write("This page should not see the light of day.");
        res.end();
    },
    "/getRequests":function(req,res,auth,helpMeDb){
        var args = querystring.parse(url.parse(req.url).query);
        res.writeHeader(200,{"Content-Type":"text/plain"});
        res.write("This page should not see the light of day.");
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
