//App routes  
module.exports = function(app){  

    var utilities = require('../app/controllers/utilities');
    
    /* Home Page */
    app.get('/', utilities.index);

	
}