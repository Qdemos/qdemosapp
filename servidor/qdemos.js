//Load app dependencies  
var express = require('express'),  
  mongoose = require('mongoose'), 
  path = require('path'),  
  http = require('http'),
  util = require('util'),
  request = require('request');

var app = express();  

//Configure: bodyParser to parse JSON data  
//           methodOverride to implement custom HTTP methods  
//           router to crete custom routes  
app.configure(function(){  
  app.set('views', __dirname + '/views');
  app.set('view engine', 'jade');
  app.use(express.favicon());
  app.use(express.logger('dev'));
  app.use(express.bodyParser());
  app.use(express.methodOverride());
  app.use(app.router);
  app.use(express.static(path.join(__dirname, 'public')));
});  
  
app.configure('development', function(){  
  app.use(express.errorHandler());  
});  
  
//Sample routes are in a separate module, just for keep the code clean  
routes = require('./routes/router')(app);  
  
//Connect to the MongoDB test database  
mongoose.connect('mongodb://localhost/qdemos_db');  
  
//Start the server  
http.createServer(app).listen(0108); 

console.log("Lanzado en puerto 0108..");

/*var registrationIds=[];
registrationIds.push("APA91bFQpVxpEIBFIjJD3_4MmiBSwh5SqyR3V4Qvtc3F0Ri_2Ys2ZDHSpqfxwbuTssyv4w5JNuQ8UY62HvsJkbsF9xzOPDNfKAdY5wZV3qS-ZclDItNFgaYxfHGikAm9ChmVtahNJNYbOEvZ7m_mg4RCx5re_GTmPw");
var Utilities = require('./gcm/Utilities');
var Qdada = require('./models/qdada');
Qdada.findOne({_id: "530b8026afdc864822ca2494"}, function(err, qdada) {  
    if ((qdada !== null) && (qdada !== undefined) && (qdada !== '')){
        Utilities.sendMessage(registrationIds, qdada, null);
    }
});*/
