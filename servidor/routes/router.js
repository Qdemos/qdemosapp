//App routes  
module.exports = function(app){  

    require('../webservices/qdadasWS')(app); 
    require('../webservices/gcmWS')(app); 
}  