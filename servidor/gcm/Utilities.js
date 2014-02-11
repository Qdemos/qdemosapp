module.exports.sendMessage = function(receivers, qdada, res) {
    var gcm = require('node-gcm')
    , sender = new gcm.Sender('AIzaSyClBprsTLkbUoiFhHUtSLHpWt0GHc5vGF0');
    var message;
    message = new gcm.Message;
    var msg = {};
    msg.idqdada = qdada._id;
    msg.descripcion = qdada.descripcion;
    msg.idcreador = qdada.creador;
    msg.invitados = qdada.invitados;
    msg.fechas = qdada.fechas;
    msg.latitud = qdada.geo[1];
    msg.longitud = qdada.geo[0];
    msg.direccion = qdada.direccion;
    msg.fechaganadora = qdada.fechaganadora;
    msg.limite = qdada.limite;
    msg.reinvitacion = qdada.reinvitacion;
    var jsonString = JSON.stringify(msg);
    message.addData('message', jsonString);
    sender.send(message, receivers, 4, function(error, result) {
      if (error === null){
          res.send("ok");
      } else{
          console.log("Error: "+result);
          res.send("err");
      }
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