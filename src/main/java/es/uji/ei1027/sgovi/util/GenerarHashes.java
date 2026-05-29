
package es.uji.ei1027.sgovi.util;

import org.jasypt.util.password.BasicPasswordEncryptor;

public class GenerarHashes {
    public static void main(String[] args) {
        BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();
        String[] passwords = {"admin", "usuario1", "usuario2", "candidato1", "candidato2", "formador1"};
        for (String pwd : passwords) {
            System.out.println(pwd + " -> " + encryptor.encryptPassword(pwd));
        }
    }
}

