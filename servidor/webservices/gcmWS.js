module.exports = function(app){  

  var gcm = require('node-gcm');
  var sender = new gcm.Sender('AIzaSyClBprsTLkbUoiFhHUtSLHpWt0GHc5vGF0');
  var Usuario = require('../models/usuario');
  var mongoose = require('mongoose');
   
  register = function(req, res) {
    Usuario.findOne({_id: req.params.idusuario}, function(error, usuario) {
      if ((usuario !== null) && (usuario !== undefined) && (usuario !== '')){
        if (req.body.idgcm !== undefined){
            usuario.idgcm=req.body.idgcm;
            usuario.save();
        }
        res.send("ok");  
      } else {
        res.send("ok");
      }
    });
  };
   
  unregister = function(req, res) {
    Usuario.findOne({_id: req.params.idusuario}, function(error, usuario) {
      if ((usuario !== null) && (usuario !== undefined) && (usuario !== '')){
        usuario.idgcm=null;
        usuario.save();
        res.send("ok");  
      } else {
        res.send("ok");
      }
    });
  };

  app.post('/gcm/register/:idusuario',   register);
  app.post('/gcm/unregister/:idusuario', unregister);

} 