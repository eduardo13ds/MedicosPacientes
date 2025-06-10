import java.time.format.DateTimeFormatter;

public class Util {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Método para padronizar o CPF como uma string de 11 dígitos com apenas números
    public static String padronizarCPF(String cpf) {
        // Remove todos os caracteres não numéricos
        String cpfNumerico = cpf.replaceAll("\\D", "");

        // Verifica se o CPF tem 11 dígitos
        if (cpfNumerico.length() != 11) {
            throw new IllegalArgumentException("CPF deve conter 11 dígitos numéricos");
        }

        return cpfNumerico;
    }
}