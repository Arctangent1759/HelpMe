var gcm = require('node-gcm');
function Response(){
    this.gcmKey = 'AIzaSyCsXUPS7fPyGt5TD-hxTCVXjU8UvHyqmcU'; // API Key
    
    /**
     * msgData - JSON object to be sent to GCM
     * regIds - list of one or more GCM regids
    **/
    this.write = function(msgData, regIds){
        var message = new gcm.Message({
            //collapseKey: 'Pending Help Requests',
            data: msgData
        });
        var sender = new gcm.Sender(this.gcmKey);
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
