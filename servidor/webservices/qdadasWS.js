module.exports = function(app){  

    var Qdada = require('../models/qdada');
    var Usuario = require('../models/usuario');
    var UsuarioEleccion = require('../models/usuarioeleccion');
    var Utilities = require('../gcm/Utilities');
  
    // Función que se encarga de crear un nuevo usuario en el servidor, si no esta creado, a partir de la info que recibe mediante el servicio web de tipo rest
    nuevoUsuario = function (req, res){
        Usuario.findOne({idfacebook: req.body.idfacebook}, function(err, usuario) {  
            if ((usuario !== null) && (usuario !== undefined) && (usuario !== '')){
                 res.send(usuario._id);
            } else {
                 var usuarioNuevo = new Usuario({ 
                                                    nombre: req.body.nombre,
                                                    idgcm: req.body.idgcm,
                                                    idfacebook: req.body.idfacebook
                                                });
                 usuarioNuevo.save();
                 res.send(usuarioNuevo._id);
            }
        });
    }  

    // Función encargada de insertar o modificar (mediante la eliminacion y la insercion) la eleccion de un usuario a una Qdada a partir de los datos
    // recibidos por el web services de tipo rest
    nuevaEleccion = function (req, res){
        UsuarioEleccion.findOne({$and: [{idfacebook: req.body.idfacebook}, {idqdada: req.body.idqdada}]}, function(err, usuarioEleccion) {  
            if ((usuarioEleccion !== null) && (usuarioEleccion !== undefined) && (usuarioEleccion !== '')){
                 usuarioEleccion.remove();
            } 
            // Como las fechas la obtenemos parseadas en un JSON, la convertimos a un tipo Fecha para su almacenaje
            var fechas=[];
            req.body.fechas.forEach(function (f){
               fechas.push(Utilities.parseDate(f));
            });
            var eleccion = new UsuarioEleccion({ 
                                                    idqdada: req.body.idqdada,
                                                    idfacebook: req.body.idfacebook,
                                                    fechas: fechas
                                               });
            eleccion.save();
            res.send("ok");
        });
    }

    // Función encargada de enviar los mensajes a los invitados a través de las notificaciones push de Google Cloud Messaging
    var callbackInformar = function (registrationIds, qdada, res){
        Utilities.sendMessage(registrationIds, qdada, res);
    }

    // Función encargada de comprobar los usuarios invitados que son usuarios de la app y llamar a la funcion encargada de notificarles la Qdada
    function notificarInvitados (qdada, res, funcion){
        Usuario.find({_id: {$in: qdada.invitados}}, function (err, usuarios){
            var registrationIds=[];
            usuarios.forEach(function (u){
                registrationIds.push(u.idgcm);
            });
            funcion(registrationIds, qdada, res);
        });
    }

    // Función encargada de crear una nueva Qdada en base de datos a partir de la info que recibe mediante el web service de tipo rest
    nuevaQdada = function (req, res){
        try {
             // Como las fechas la obtenemos parseadas en un JSON, la convertimos a un tipo Fecha para su almacenaje
             var fechas=[];
             req.body.fechas.forEach(function (f){
                fechas.push(Utilities.parseDate(f));
             });

             var qdadaNueva = new Qdada({ 
                                            titulo: req.body.titulo,
                                            descripcion: req.body.descripcion,
                                            creador: req.body.creador,
                                            invitados: req.body.invitados,
                                            fechas: fechas,
                                            geo: [parseFloat(req.body.longitud), parseFloat(req.body.latitud)],
                                            direccion: req.body.direccion,
                                            fechaganadora: Utilities.parseDate(req.body.fecha),
                                            limite: Utilities.parseDate(req.body.fecha),
                                            reinvitacion: req.body.reinvitacion
                                        });
             qdadaNueva.save();
             // Notificar a los invitados
             notificarInvitados(qdadaNueva, res, callbackInformar);
             res.send(qdadaNueva._id);
        } catch (err){
            res.send("err");
        }
    }

    // Función encargada de devolver a los usuarios los datos que se van actualizando de la Qdada para que lo reflejen en local, mediante el
    // web services de tipo rest.
    datosQdada = function (req, res){
        try {
            Qdada.findOne({_id: req.params.idqdada}, function(err, qdada) {  
            if ((qdada !== null) && (qdada !== undefined) && (qdada !== '')){
                var msg = {};
                msg.idqdada = qdada._id;
                UsuarioEleccion.find({idqdada: qdada._id}, function(err, usuariosElecciones) {  
                    if ((usuariosElecciones !== null) && (usuariosElecciones !== undefined)){
                         msg.usuarioselecciones = usuariosElecciones;
                         var jsonString = JSON.stringify(msg);
                         res.send(jsonString);
                    } 
                });
            } else {
                 res.send("err_no_exist");
            }
        });
        } catch (err){
            res.send("err");
        }
    }

    // Servicios Web de tipo rest, tantos POST como GET
    app.post('/nuevoUsuario', nuevoUsuario);     // Crear Usuario
    app.post('/nuevaQdada', nuevaQdada);         // Crear Qdada
    app.post('/nuevaEleccion', nuevaEleccion);   // Modificar Eleccion Qdada por parte de una Usuario
    app.get('/datosQdada/:idqdada', datosQdada); // Devolver datos más relevantes de las actualizaciones de una Qdada
} 