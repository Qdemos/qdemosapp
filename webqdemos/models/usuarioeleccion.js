// Tracking Class
var mongoose = require('mongoose'),  
    Schema = mongoose.Schema;  
  
var usuarioeleccionSchema = new Schema({ 
	idqdada: String,
	idfacebook: String,
	fechas: [Date]
});  
  
//Export the schema  
module.exports = mongoose.model('UsuarioEleccion', usuarioeleccionSchema); 