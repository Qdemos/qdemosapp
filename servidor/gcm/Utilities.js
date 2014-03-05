module.exports.sendMessage = function(receivers, qdada, res) {
    var gcm = require('node-gcm')
    , sender = new gcm.Sender('AIzaSyClBprsTLkbUoiFhHUtSLHpWt0GHc5vGF0');
    var message;
    var Usuario = require('../models/usuario');
    message = new gcm.Message;
    var msg = {};
    var users = [];
    qdada.invitados.forEach(function (inv){
      users.push(inv);
    });
    users.push(qdada.creador);
    Usuario.find({idfacebook: {$in: users}}, function(err, usuarios) {  
        nombreusuarios = [];
        if ((usuarios !== null) && (usuarios !== undefined) && (usuarios !== '')){
            usuarios.forEach(function (u){
                nombreusuarios.push(u.nombre);
            });
        } 
        console.log("Nombres: "+nombreusuarios);
        msg.nombreinvitados = nombreusuarios;
        msg.idqdada = qdada._id;
        msg.titulo = qdada.titulo;
        msg.descripcion = qdada.descripcion;
        msg.idcreador = qdada.creador;
        msg.invitados = qdada.invitados;
        msg.fechas = qdada.fechas;
        msg.latitud = qdada.geo[1];
        msg.longitud = qdada.geo[0];
        msg.direccion = qdada.direccion;
        msg.fechaganadora = qdada.fechaganadora;
        msg.limite = qdada.limite;
        msg.reinvitacion = qdada.reinvitacion ? qdada.reinvitacion != undefined : false;
        var jsonString = JSON.stringify(msg);
        message.addData('message', jsonString);
        sender.send(message, receivers, 4, function(error, result) {
          if (error === null){
              if (res != null)
                res.send("ok");
          } else{
              if (res != null)
                res.send("err");
          }
        });
    });

}

module.exports.parseDate = function(fecha){
   try {
      var parts = fecha.split(" ");
      var horas = parts[1].split(":");
      var dias = parts[0].split("-");
      return new Date(Date.UTC(dias[2], dias[1]-1, dias[0], horas[0], horas[1]));
   } catch (err){
      return new Date();
   }
}

module.exports.getFechaGanadora = function(fechas){
  var comparador=[];
  var repeticiones=[];
  var mayor=-1;
  var indice=0;
  fechas.forEach(function (fecha){
    if (comparador.indexOf(fecha) !== -1){
      var index = comparador.indexOf(fecha);
      var repetido = (repeticiones[index]+1);
      if (repetido > mayor){
        mayor = repetido;
        indice = index;
      }
      repeticiones[index] = repetido;
    } else {
      comparador.push(fecha);
      repeticiones.push(0);
    }
  })
  try {
    return comparador[indice];
  } catch (err){
    return null;
  }
}