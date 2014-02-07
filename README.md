Qdemos!
=========

<a href="http://qdemos.wordpress.com/">
  <p align="center">
    <img alt="Qdemos!" src="http://qdemos.files.wordpress.com/2014/01/screenshot_2014-01-08-17-24-58.png?w=310&h=550" />
  </p>
</a>

App Android participante en el [**VIII Concurso Universitario de Software Libre**](http://www.concursosoftwarelibre.org/1314/)

<a href="http://www.concursosoftwarelibre.org/1314/">
  <p align="center">
    <img alt="CUSL" src="http://www.concursosoftwarelibre.org/1314/themes/kanji/logo.png" />
  </p>
</a>

Con **Qdemos!** podremos crear y gestionar quedadas entre amigos, convirtiendo en historia las largas discusiones que se generan por Whatsapp o Facebook cuando queremos coordinarnos para quedar.

Nuestro **algoritmo de matching,** permitirá buscar **la mejor fecha posible** entre todas las propuestas, acorde a las preferencias de los usuarios.

¡Y todo esto sin dar datos extras, haciendo uso de la información de nuestros perfiles de las **redes sociales**!

¿Quieres quedar?... **Qdemos!**

## Cómo Participar ##

Si quieres participar en el proyecto... ¡sólo hace falta que tengas ganas!. Después configura todo el entorno como se explica en sucesivas secciones y empieza a hacer tus pull request de las funcionalidades o mejoras que cambiarias o añadirias. ¡Esperamos tu colaboración!

## Configurar el Entorno de Desarrollo ##

Para configurar el entorno de desarrollo se deben instalar y preparar las siguiente herramientas:

### 1. Android Studio ###

Lo primero que debeis hacer es bajar el IDE de Desarrollo, que en este caso es Android Studio. Lo instalais y preparais tal cual especifican en su página oficial: [**Instalar Android Studio**](http://developer.android.com/sdk/installing/studio.html)

Y se deberá configurar Android Studio descargando los siguientes paquetes añadidos, que aparecen en la imagen, a la instalación del SDK por defecto que viene preinstalado con el mismo (Que suele ser la última versión, para este proyecto se está usando todo relativo a la Rev. 19, si se usa otra revisión se debe modificar los ficheros típicos de configuración como build.gradle y demás para hacerlo notar).


<a href="https://github.com/Qdemos/qdemosapp/blob/master/imagenesREADME/instalacionQdemosSDKAndroid.png?raw=true">
  <p align="center">
    <img alt="ConfiguracionAS" src="https://github.com/Qdemos/qdemosapp/blob/master/imagenesREADME/instalacionQdemosSDKAndroid.png?raw=true" />
  </p>
</a>

Después procedemos a hacer un **git clone** del repositorio en github: [**Qdemos en Github**](https://github.com/Qdemos/qdemosapp) e importamos el proyecto en Android Studio.

El siguiente paso es modificar el fichero **build.gradle** que se aloja bajo el path: *qdemos/build.gradle* para cambiar los paths absolutos de dos de las librerias que usamos (mediante su empaquetado en .jar) por el path absoluto que las referencia en nuestra cuenta. Esto es debido a que no he podido lograr configurar el path relativo a librerias .jar en mi configuración de Android Studio, siempre me daba fallos gradle al intentar acceder a ellos mediante el path relativo. Una explicación más extensa podeis verla en la entrada del blog que escribí sobre ello: [**Problemas importando .jar en Android Studio mediante el path relativo**](http://qdemos.wordpress.com/2014/01/13/definiendo-los-modelos-de-bbdd/)

### 2. Mongo DB ###

Para la base de datos del servidor se usará un esquema usando MongoDB, una base de datos no relacional. Para su instalación se procederá a seguir los pasos indicados en su página web: [**Instalación MongoDB**](http://www.mongodb.org/downloads)

Para ejecutar MongoDB, o creamos un servicio en el arranque del sistema, o cada vez que queramos hacer correr la BBDD para hacer uso de ella tendremos que situarnos en un terminal en la carpeta *bin/* dentro del directorio donde hemos descomprimido MongoDB y ejecutar **mongod**

### 3. Node.js ###

Una vez instalado el entorno de la base de datos, hará falta instalar todo el framework encargado de la lógica del servidor. Para ello simplemente seguimos los pasos dados en la página oficial de Node.js que será la insfraestructura del servidor que usemos para definir todo el servicio de APIs para los Web Services dse tipo REST de Qdemos. [**Instalación Node.js**](http://www.nodejs.org/)

Habrá que configurar el path del sistema (si estamos en windows) para añadir el directorio *bin/* de la instalación de node.js para poder ejecutarlo desde la carpeta que queramos por consola.

Para hacer correr el servidor, simplemente debemos situarnos en la carpeta donde lo hallamos clonado y ejecutar el comando **node qdemos**

## Librerias externas ##

Para este proyecto se han usado librerias externas (de código abierto) tanto para la parte del cliente Android como para el servidor Node.js (módulos). A continuación se detallan cuales han sido, y su enlace para una posible reutilización en proyectos externos.

### Android ###

* [**DateTimePicker**](https://github.com/flavienlaurent/datetimepicker): Para el elección de fechas.
* [**Facebook SDK**](https://github.com/facebook/facebook-android-sdk): Permite Login y Recabar información con Facebook.
* [**ADA Framework**](https://github.com/mobandme/ADA-Framework): ORM de SQL Lite, para el manejo de base de datos.
* [**GSON**](https://code.google.com/p/google-gson/): Utilidades para el manejo del formato JSON.
* [**Crouton**](https://github.com/keyboardsurfer/Crouton): Alternativa mejorada de los típicos Toasts.

### Node.js ###

* [**Mongoose**](https://github.com/LearnBoost/mongoose): ORM de MongoDB, para el manejo de la base de datos.
* [**Express**](https://github.com/visionmedia/express): Para crear WebServices de tipo REST.
* [**Node-gcm**](https://github.com/ToothlessGear/node-gcm): Para comunicar nuestro servidor node con Google Cloud Messaging de manera fácil. 
