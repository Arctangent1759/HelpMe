var gcm = require('node-gcm');
function Response(){
    this.gcmKey = 'AIzaSyCsXUPS7fPyGt5TD-hxTCVXjU8UvHyqmcU'; // API Key
    
    this.write = function(msgData, regId){
        var message = new gcm.Message({
            //collapseKey: 'Pending Help Requests',
            data: msgData
        });
        var sender = new gcm.Sender(this.gcmKey);
        var regIds = [regId];
        sender.send(message, regIds, 4, function(err, result){
            if(err){
                console.log("Error sending message to client");
            }
            console.log(err);
            console.log(result);
        });
    }
     
}

exports.Response = Response;
