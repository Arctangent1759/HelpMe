var http = require('http');
var constants = require('./constants.js').constants;

var server = http.createServer(function(req,res){
  res.writeHeader(200,{'Content-Type':'text/plain'});
  res.write("Herro Wurldz! Random number is " + Math.random() + ".");
  res.end();
}).listen(constants.port,function(){
  console.log("HTTP started.");
  console.log("Listening on port " + constants.port);
})
