// Qdada Class
var mongoose = require('mongoose'),  
    Schema = mongoose.Schema;  

var Usuario = require('../models/usuario');
  
var qdadaSchema = new Schema({  
    titulo: String,
    descripcion: String,
    creador: String, //IdFacebook
    invitados: [String], //IdFacebook
    fechas:  [Date],
    geo: {type: [Number], index: '2d'},
    direccion: String,
    fechaganadora: Date,
    limite: Date,
    reinvitacion: Boolean
});  

//Export the schema  
module.exports = mongoose.model('Qdada', qdadaSchema); 