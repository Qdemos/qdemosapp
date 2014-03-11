// Signal Class
var mongoose = require('mongoose'),  
    Schema = mongoose.Schema;  
  
var usuarioSchema = new Schema({ 
	nombre: String, 
	idfacebook: String,
	idgcm: String
});  
  
//Export the schema  
module.exports = mongoose.model('Usuario', usuarioSchema); 