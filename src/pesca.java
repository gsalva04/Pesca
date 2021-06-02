import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class pesca {

    //Instanciamos objetos
    usuario usuario = new usuario();
    pez pez = new pez();

    //Muestra el menu al usuario
    public void menu() throws Exception {
        Scanner consola = new Scanner(System.in);
        boolean salir = false;
        while (!salir) {
            System.out.println("**************************************************");
            System.out.println("* Benvinguts a el programa de pesca *");
            System.out.println("* Menú principal *");
            System.out.println("**************************************************");
            System.out.println("* 1) Alta d'usuari *");
            System.out.println("* 2) Baixa d'usuari *");
            System.out.println("* 3) Pescar en una pesquera *");
            System.out.println("* 4) Estadístiques per usuari *");
            System.out.println("* 5) Estadístiques globals *");
            System.out.println("* s) Sortir del programa *");
            System.out.println("**************************************************");
            System.out.print("OPCIÓ? ");
            String opcion = consola.nextLine();
            switch (opcion) {
                case "1":
                    registrarUsuario(datosUsuario());
                    break;
                case "2":
                    eliminarUsuario(datosUsuario());
                    break;
                case "3":
                    usuario = datosUsuario();
                    if (!comprobarUsuario(usuario)) {
                        System.out.println("¡No tiene licencia para pescar!");
                    }else {
                        final File pesqueras = new File("src/archivos/pesqueras"); //Se establece la carpeta
                        System.out.println("");
                        System.out.println("* Pesqueras disponibles: ");
                        listarPesqueras(pesqueras);
                        System.out.println("");
                        pez = pescarPez(datosPesquera());
                        String captura = "#" + usuario.getNombre() + "#" + usuario.getApellido() + "#" + pez.getNombre() + "#" + pez.getPeso() + "#";
                        registrarCaptura(captura);
                    }
                    break;
                case "4":
                    usuario = datosUsuario();
                    generarEstadisticasUsuario(usuario);
                    break;
                case "5":
                    generarEstadisticasGenerales();
                    break;
                case "s":
                    salir = true;
                    break;
                default:
                    System.out.println("Opción no valida.");
            }
        }
    }

    //Método para pedir los datos del usuario al usuario
    public usuario datosUsuario(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("* Nombre: ");
        String nombre = scanner.nextLine();
        System.out.println("* Apellido: ");
        String apellido = scanner.nextLine();
        usuario usuario = new usuario(nombre, apellido);
        return usuario;
    }

    //Método para pedir donde quiere pescar el usuario
    public String datosPesquera(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("* Seleccione el nombre de una pesquera: (sin .txt)");
        String pesquera = scanner.nextLine();
        return pesquera;
    }

    //Método para crear usuarios
    public void registrarUsuario(usuario usuario) throws IOException {

        //Comprovamos sino existe el usuario
        if (comprobarUsuario(usuario)) {
            System.out.println("El usuario ya existe");
        }
        else {
            //Abrimos el fichero con el que queremos trabajar para escribir usuario. Append: añade nueva linea y no sobrescribe
            OutputStream escritor = new FileOutputStream("src/archivos/users.txt", true);
            //Escribe espacio en blanco para que no se concatenen los usuarios
            escritor.write(10);
            //Preparamos linea para escribir
            String linea = "#" + usuario.getNombre() + "#" + usuario.getApellido() + "#";
            //Bucle para escribir dentro del fichero
            for (int i = 0; i < linea.length(); i++) {
                escritor.write(linea.charAt(i));
            }
            escritor.close();
            System.out.println("El usuario ha sido creado.");
        }
    }

    //Método para comprobar si un usuario ya existe
    private boolean comprobarUsuario(usuario usuario) throws IOException {
        //Abrimos fichero para leerlo
        File archivoUsuarios = new File("src/archivos/users.txt");
        //Contador de lines. Empieza por la linea 1
        int contador = 3;

        String linea = leerlinea(archivoUsuarios, 3, contador);
        String comprobar = "#" + usuario.getNombre() + "#" + usuario.getApellido() + "#";
        //Recorre fichero buscando si existe el usuario, al leer una linea el contdor sumara los 3 hastags
        while(!linea.isEmpty()){
            contador = contador + 3;//Pasa a la siguiente linea en bucle
            if ( linea.equals(comprobar)){
                return true;
            }
            linea = leerlinea(archivoUsuarios, 3, contador); //Lee linea con el contador actualizado
        }
        return false;
    }

    //Método para leer lineas de un fichero caracter a caracter
    private String leerlinea (File archivo, int limitHashtags, int numeroHashtags) throws IOException {
        //Iniciamos el fichero que queremos leer
        InputStream inputStream = new FileInputStream(archivo);
        //Dato leera caracter a caracter
        int datos = inputStream.read();
        int contadorHashtags = 0;
        String linea = "";

        //Mientras datos sea difente a final de linea...
        while (datos != -1){

            char charLeido = (char) datos; //Parseamos el dato a char

            if (charLeido == '#'){
                contadorHashtags++;
            }

            linea =  linea + charLeido;
            datos = inputStream.read();//Continua leyendo caracter a caracter

            //si el contador de hashtags es igual al numero de hashtags en el que estamos devuelve la linea deseada
            if(contadorHashtags == numeroHashtags){

                //Cerramos inputstream y devolvemos la linea buscada
                inputStream.close();
                return linea;
            }
            //Hacemos salto de linea, cada tres hstgs la linea se vacia. Residuo es = 0 se cumple
            if (contadorHashtags % limitHashtags == 0){
                linea = "";
            }
        }
        
        inputStream.close();
        return linea;
    }

    //Método para eliminar usuarios
    public void eliminarUsuario(usuario usuario) throws IOException {

        if (!comprobarUsuario(usuario)) {
            System.out.println("¡No tiene licencia para pescar!");
        }else {
            //Instanciamos fichero temporal para la transferencia
            File temporal = new File("src/archivos/temp.txt");
            //Inicializamos el archivo para escribir
            OutputStream escritor = new FileOutputStream(temporal, true);

            //Instanciamos fichero de usuarios principal
            File archivoUsuarios = new File("src/archivos/users.txt");
            //Inicializamos el archivo para leer
            InputStream lector = new FileInputStream(archivoUsuarios);

            int contador = 3;
            String comprobar = "#" + usuario.getNombre() + "#" + usuario.getApellido() + "#";
            String linea = leerlinea(archivoUsuarios, 3, contador);

            //Recorre fichero buscando si existe el usuario, al leer una linea el contdor sumara los 3 hastags
            while (!linea.isEmpty()) {
                if (!linea.equals(comprobar)) { //Si el usuario no es igual al que queremos borrar, para que no copie el que queremos borrar en el nuevo txt
                    for (int i = 0; i < linea.length(); i++) {
                        escritor.write(linea.charAt(i));
                    }
                    escritor.write(10);
                }
                contador = contador + 3;
                linea = leerlinea(archivoUsuarios, 3, contador);//Pasa a la siguiente linea
            }
            System.out.println("El usuario ha sido eliminado.");
            archivoUsuarios.delete();
            //System.gc();

            //Mueve el archivo temporal al principal
            Files.move(Paths.get(temporal.toString()), Paths.get(archivoUsuarios.toString()));

            escritor.close();
            lector.close();
        }
    }

    //Método para contar lineas
    public int contarLineas(File fichero) throws Exception{
        //Inicializamos el fichero para leerlo
        InputStream lector = new FileInputStream(fichero);
        int contadorLinea = 0;
        int contadorHstg = 0;
        //Dato leera caracter a caracter
        int datos = lector.read();
        while(datos != -1){
            if (datos == '#') {
                contadorHstg++;
            }
            if (contadorHstg == 5) {//Limite # en pesqueras = 5
                contadorLinea++;
                contadorHstg = 0;
            }
            datos = lector.read();
        }
        return contadorLinea;
    }

    //Método para enseñar por pantalla los archivos txt que hay en la carpeta archivos
    public void listarPesqueras(final File carpeta) {

        //Listamos los ficheros de una carpeta. Metodo recursivo, es decir que se llama a si mismo
        for (final File pesquera : carpeta.listFiles()) {
            if (pesquera.isDirectory()) {
                listarPesqueras(pesquera);
            } else {
                if(!pesquera.getName().startsWith(".")) { //.DS_Store
                    System.out.println(pesquera.getName());
                }
            }
        }
    }

    public String obtenerAtributo(File fichero, int numLinea , int objetivoHtg) throws Exception{
        //Inicializamos el fichero para leerlo
        InputStream lector = new FileInputStream(fichero);
        int contadorLinea = 0;
        int contadorHstg = 0;
        String atributoPez = "";
        String linea = "";
        int datos = lector.read(); //Lee caracter a caracter
        while(datos != -1){
            linea = linea + (char)datos; //Parsea a char y lo sumamos a linea
            if (datos == '#') {
                contadorHstg++;
            }
            if (contadorHstg == objetivoHtg && contadorLinea == numLinea) {
                if (linea.startsWith("#")) {
                    linea = ""; //Limpia la linea
                }
                atributoPez = linea;
            }
            if (contadorHstg == 5) { //Limite # en pesqueras = 5
                contadorLinea++;
                contadorHstg = 0;
            }
            datos = lector.read(); //Pasa al siguiente carcter
        }
        lector.close();
        return atributoPez;
    }

    //Método para obtener porcentaje aleatorio
    public int seleccionarPezRandom(File pesquera, int linea, int objetivoHtg) throws Exception {
        double random = Math.random();
        for (int i = 0; i < contarLineas(pesquera) ; i++) {
            double probabilidad = Double.parseDouble(obtenerAtributo(pesquera, linea, objetivoHtg));

            if (probabilidad > random) {
                return linea;
            }
            linea++;
        }
        return -1;
    }

    //Metodo para pescar un pez en función de su probabilidad comparandolo
    public pez pescarPez(String nombre) throws Exception {

        //Instanciamos fichero
        File pesquera = new File("src/archivos/pesqueras/" + nombre + ".txt");

        //Referencia a cada atributo por hstgs
        int hstgNombrePez = 1;
        int hstgProbabilidad = 2;
        int hstgPesoMin = 3;
        int hstgPesoMax = 4;

        int lineaPez = seleccionarPezRandom(pesquera, 0, hstgProbabilidad);
        String nombrePez = obtenerAtributo(pesquera, lineaPez, hstgNombrePez);
        double pesoMin = Double.parseDouble(obtenerAtributo(pesquera, lineaPez, hstgPesoMin));
        double pesoMax = Double.parseDouble(obtenerAtributo(pesquera, lineaPez, hstgPesoMax));
        double pesoPez = (Math.random() * ((pesoMax - pesoMin))) + pesoMin; //Peso aleatorio entre peso max y peso min
        pesoPez = Math.floor(pesoPez * 100) / 100; //redondea el valor
        //Constructor de la clase pez
        pez pezCapturado = new pez(nombrePez, pesoPez);
        System.out.println("Usted ha pescado: ");
        System.out.println("- Pez: " + nombrePez + "\n- Peso: "+ pesoPez);

        return pezCapturado;
    }

    //Método para registrar las pescas en registres.txt
    public void registrarCaptura(String captura) throws IOException {

        //Instanciamos fichero
        File archivoRegistro = new File("src/archivos/registres.txt");
        //Abrimos el fichero con el que queremos trabajar. Append: añade nueva linea y no sobrescribe
        OutputStream registro = new FileOutputStream(archivoRegistro, true);
        //Escribe espacio en blanco para que no se concatenen los usuarios
        registro.write(10);
        //Bucle para escribir dentro del fichero
        for (int i = 0; i < captura.length(); i++) {
            registro.write(captura.charAt(i));
        }
        registro.close();

        System.out.println("La captura se ha registrado correctamente en registres.txt.");
    }

    //Método para generar las estadisiticas de un usuario en concreto
    public void generarEstadisticasUsuario(usuario usuario) throws Exception {
        if (!comprobarUsuario(usuario)) {
            System.out.println("¡No tiene licencia para pescar!");
        }else {
            File archivoRegistro = new File("src/archivos/registres.txt");

            InputStream registro = new FileInputStream(archivoRegistro);

            int hstgNombre = 1;
            int hstgApellido = 2;
            int hstgPez = 3;

            HashMap<String, Integer> frecuencia = new HashMap<String, Integer>();

            //Recorremos fichero registro.txt
            for (int i = 0; i < contarLineas(archivoRegistro); i++) {
                String nombre = obtenerAtributo(archivoRegistro, i, hstgNombre);
                String apellido = obtenerAtributo(archivoRegistro, i, hstgApellido);

                if (nombre.equals(usuario.getNombre()) && apellido.equals(usuario.getApellido())) { //Comprueba usuario
                    String pez = obtenerAtributo(archivoRegistro, i, hstgPez); //Coge nombre pez
                    frecuencia.put(pez, frecuencia.getOrDefault(pez, 0) + 1); //Añade al hashmap nombre pez, si existe suma 1 y sino lo crea y lo pone a 1
                }
            }

            //Recorre el hashmap
            for (Map.Entry<String, Integer> entry : frecuencia.entrySet()) {
                System.out.println("Pez: " + entry.getKey() + ",  Cantidad total: " + entry.getValue());
            }
            registro.close();
        }
    }

    //Método para generar las estadisiticas generales
    public void generarEstadisticasGenerales() throws Exception {
        File archivoRegistro = new File("src/archivos/registres.txt");

        InputStream registro = new FileInputStream(archivoRegistro);

        int hstgNombre = 1;
        int hstgApellido = 2;
        int hstgPez = 3;

        //Creamos hashmaps
        HashMap<String, HashMap> pescadores = new HashMap<String, HashMap>();
        HashMap<String, Integer> frecuencia = null;

        //Bucle para recorrer el archivo de registro
        for (int i = 0; i < contarLineas(archivoRegistro) ; i++) {
            String nombre = obtenerAtributo(archivoRegistro, i, hstgNombre);
            String apellido = obtenerAtributo(archivoRegistro, i, hstgApellido);
            String nombreCompleto = nombre + " " + apellido;

            if(!pescadores.containsKey(nombreCompleto)){ //Nuevo pescador (no existia en el hashmap)
                frecuencia = new HashMap<String, Integer>();
                pescadores.put(nombreCompleto,frecuencia); // Lo introducimos en el hashmap junto con el hashmap de peces
            }

            String pez = obtenerAtributo(archivoRegistro, i, hstgPez);
            // get de frecuencia asignada a la posicion de nombreCOmpleto y put en la posicion de pez
            pescadores.get(nombreCompleto).put(pez, frecuencia.getOrDefault(pez, 0) + 1);
        }

        //Bucle para recorrer los dos hashmaps, el primero de pescadores y el segundo de frencuencia
        for (Map.Entry<String, HashMap> pescador : pescadores.entrySet()) {
            frecuencia = pescador.getValue();
            System.out.println(pescador.getKey());
            for (Map.Entry<String, Integer> pez : frecuencia.entrySet()) {
                System.out.println("Pez: " + pez.getKey() + "Cantidad total: " + pez.getValue());
            }
        }
        registro.close();
    }

}
