//App routes  
module.exports = function(app){  

    require('../webservices/qdadasWS')(app); 
    require('../webservices/gcmWS')(app); 
    
    var utilities = require('../app/controllers/utilities');
    /* Home Page */
    app.get('/', utilities.index);

	
}