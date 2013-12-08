var gcm = require('node-gcm');
function Response(){
    this.gcmKey = 'AIzaSyA2s54h6S-taxs3Rm8wvm6fIW74wJOQqQ4'; // API Key
    
    this.write = function(msgData, regId){
        var message = new gcm.Message({
            collapseKey: 'Pending Help Requests',
            data: msgData
        });
        var sender = new gcm.Sender(this.gcmKey);
        var regIds = [regId];
        sender.send(message, regIds, 4, function(err, result){
            if(err){
                console.log("Error sending message to client");
            }
        }
    }
     
}

exports.Response = Response;
