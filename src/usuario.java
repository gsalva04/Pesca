import java.io.*;

public class usuario {

    //Atributos
    String nombre;
    String apellido;

    //Contructores de la clase usuario
    public usuario() {
    }
    public usuario(String nombre,String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
    }

    //Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }
}
