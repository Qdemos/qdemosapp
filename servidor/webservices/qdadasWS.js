module.exports = function(app){  

    var Qdada = require('../models/qdada');
    var Usuario = require('../models/usuario');
    var UsuarioEleccion = require('../models/usuarioeleccion');
    var Utilities = require('../gcm/Utilities');
  
    // Función que se encarga de crear un nuevo usuario en el servidor, si no esta creado, a partir de la info que recibe mediante el servicio web de tipo rest
    nuevoUsuario = function (req, res){
        Usuario.findOne({idfacebook: req.body.idfacebook}, function(err, usuario) {  
            if ((usuario !== null) && (usuario !== undefined) && (usuario !== '')){
                 if ((usuario.idgcm === null) || (usuario.idgcm === undefined) || (usuario.idgcm === '') || (usuario.idgcm === '1')){
                    if ((req.body.idgcm !== null) && (req.body.idgcm!== undefined) && (req.body.idgcm !== '')){
                        usuario.idgcm=req.body.idgcm;
                        usuario.save();  
                    } 
                 }
                 res.send("ok");
            } else {
                 var usuarioNuevo = new Usuario({ 
                                                    nombre: req.body.nombre,
                                                    idgcm: req.body.idgcm,
                                                    idfacebook: req.body.idfacebook
                                                });
                 usuarioNuevo.save();
                 res.send("ok");
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
            var listaFechas = JSON.parse(req.body.fechas);
            listaFechas.forEach(function (f){
               fechas.push(Utilities.parseDate(f));
            });
            if (fechas.length === 0) { // Si sus elecciones han sido todas que NO
                res.send("ok");
            } else {
                var eleccion = new UsuarioEleccion({ 
                                                        idqdada: req.body.idqdada,
                                                        idfacebook: req.body.idfacebook,
                                                        fechas: fechas
                                                   });
                eleccion.save();
                UsuarioEleccion.findOne({idqdada: req.body.idqdada}, function(err, usuariosElecciones) {  
                    if ((usuariosElecciones !== null) && (usuariosElecciones !== undefined) && (usuariosElecciones !== '')){ 
                        Qdada.findOne({_id: req.body.idqdada}, function(err, qdada) {  
                            if ((qdada!== null) && (qdada !== undefined) && (qdada !== '')){
                                 qdada.fechaganadora = Utilities.getFechaGanadora(usuariosElecciones.fechas);
                                 qdada.save();
                                 res.send("ok");
                            } else {
                                 res.send("err");
                            }
                        });
                    } else {
                        res.send("err");
                    }
                });
            }
        });
    }

    // Función encargada de enviar los mensajes a los invitados a través de las notificaciones push de Google Cloud Messaging
    var callbackInformar = function (registrationIds, qdada, res){
        Utilities.sendMessage(registrationIds, qdada, res);
    }

    // Función encargada de comprobar los usuarios invitados que son usuarios de la app y llamar a la funcion encargada de notificarles la Qdada
    function notificarInvitados (qdada, res, funcion){
        Usuario.find({idfacebook: {$in: qdada.invitados}}, function (err, usuarios){
            if ((usuarios !== null) && (usuarios !== undefined) && (usuarios !== '')){
                var registrationIds=[];
                usuarios.forEach(function (u){
                    registrationIds.push(u.idgcm);
                });
                funcion(registrationIds, qdada, res);
            }
        });
    }

    // Función encargada de crear una nueva Qdada en base de datos a partir de la info que recibe mediante el web service de tipo rest
    nuevaQdada = function (req, res){
        try {
             // Como las fechas la obtenemos parseadas en un JSON, la convertimos a un tipo Fecha para su almacenaje
             var fechas=[];
             var listaFechas = JSON.parse(req.body.fechas);
             listaFechas.forEach(function (f){
                fechas.push(Utilities.parseDate(f));
             });
             var invitadosJSON = JSON.parse(req.body.invitados);
             var invitadosNombreJSON = JSON.parse(req.body.invitadosnombre);
             Usuario.find({idfacebook: {$in: invitadosJSON}}, function(err, usuarios) {
                if ((usuarios !== null) && (usuarios !== undefined) && (usuarios !== '')){
                    var i = -1;
                    var ids = [];
                    usuarios.forEach (function(us){
                        ids.push(us.idfacebook);
                    });
                    invitadosJSON.forEach(function(u){
                        i++;
                        if (ids.indexOf(u) !== -1){
                            var index = ids.indexOf(u);
                            usuarios[index].nombre=invitadosNombreJSON[i];
                            usuarios[index].save();
                        } else {
                            var usuarioNuevo = new Usuario({ 
                                                nombre: invitadosNombreJSON[i],
                                                idgcm: null,
                                                idfacebook: u
                                            });
                            usuarioNuevo.save();
                        }
                    });
                } else {
                    var i = -1;
                    invitadosJSON.forEach(function(u){
                        i++;
                        var usuarioNuevo = new Usuario({ 
                                                nombre: invitadosNombreJSON[i],
                                                idgcm: null,
                                                idfacebook: u
                                            });
                        usuarioNuevo.save();
                    });
                }
             });


             var qdadaNueva = new Qdada({ 
                                            titulo: req.body.titulo,
                                            descripcion: req.body.descripcion,
                                            creador: req.body.creador,
                                            invitados: invitadosJSON,
                                            fechas: fechas,
                                            geo: [req.body.longitud, req.body.latitud],
                                            direccion: req.body.direccion,
                                            fechaganadora: Utilities.parseDate(req.body.fecha),
                                            limite: Utilities.parseDate(req.body.fecha),
                                            reinvitacion: req.body.reinvitacion
                                        });
             qdadaNueva.save();
             // Creamos las elecciones del creador del usuario en la qdada, que seran todas las fechas propuestas
             var eleccion = new UsuarioEleccion({ 
                                                idqdada: qdadaNueva._id,
                                                idfacebook: req.body.creador,
                                                fechas: fechas
                                           });
             eleccion.save();
             // Notificar a los invitados
             notificarInvitados(qdadaNueva, res, callbackInformar);
             res.send(qdadaNueva._id);
        } catch (err){
            console.log(err);
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

    // Función encargada de devolver a los usuarios los datos que se van actualizando de un conjunto de Qdadas para que lo reflejen en local, mediante el
    // web services de tipo rest.
    datosQdadas = function (req, res){
        try {
            var idsJSON = JSON.parse(req.body.ids);
            var listRes = [];
            var msg;
            var idsReady = [];
            var ues = [];
            UsuarioEleccion.find({idqdada: {$in: idsJSON}}, function(err, usuariosElecciones) {  
                if ((usuariosElecciones !== null) && (usuariosElecciones !== undefined)){
                    usuariosElecciones.forEach(function (ue){
                        if (idsReady.indexOf(ue.idqdada) === -1){
                            msg = {};
                            msg.idqdada = ue.idqdada;
                            usuariosElecciones.forEach(function (uej){
                                if (ue.idqdada === uej.idqdada){
                                    ues.push(uej);
                                }
                            });
                            msg.usuarioselecciones = JSON.stringify(ues);
                            var jsonString = JSON.stringify(msg);
                            listRes.push(jsonString);
                            idsReady.push(ue.idqdada);
                        }
                    });
                    res.send(JSON.stringify(listRes));
                } else {
                    res.send("err_no_exist");
                }   
            });
        } catch (err){
            console.log("Error: "+err);
            res.send("err");
        }
    }

    // Servicios Web de tipo rest, tantos POST como GET
    app.post('/nuevoUsuario', nuevoUsuario);     // Crear Usuario
    app.post('/nuevaQdada', nuevaQdada);         // Crear Qdada
    app.post('/nuevaEleccion', nuevaEleccion);   // Modificar Eleccion Qdada por parte de una Usuario
    app.get('/datosQdada/:idqdada', datosQdada); // Devolver datos más relevantes de las actualizaciones de una Qdada
    app.put('/datosQdadas', datosQdadas); // Devolver datos más relevantes de las actualizaciones de un conjunto de Qdadas
} 