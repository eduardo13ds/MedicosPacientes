import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LeitorCSV {

    public static List<Medico> lerMedicos(String caminhoArquivo) throws FileNotFoundException {
        List<Medico> medicos = new ArrayList<>();
        Scanner scanner = new Scanner(new File(caminhoArquivo));

        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        while (scanner.hasNextLine()) {
            String linha = scanner.nextLine();
            String[] campos = linha.split(",");

            Medico medico = new Medico(campos[0], Integer.parseInt(campos[1]));
            medicos.add(medico);
        }

        scanner.close();
        return medicos;
    }

    public static List<Paciente> lerPacientes(String caminhoArquivo) throws FileNotFoundException {
        List<Paciente> pacientes = new ArrayList<>();
        Scanner scanner = new Scanner(new File(caminhoArquivo));

        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        while (scanner.hasNextLine()) {
            String linha = scanner.nextLine();
            String[] campos = linha.split(",");

            String cpf = padronizarCPF(campos[1]);
            Paciente paciente = new Paciente(campos[0], cpf);
            pacientes.add(paciente);
        }

        scanner.close();
        return pacientes;
    }

    // Método para padronizar o CPF como uma string de 11 dígitos com apenas números
    private static String padronizarCPF(String cpf) {
        // Remove todos os caracteres não numéricos
        String cpfNumerico = cpf.replaceAll("\\D", "");

        // Verifica se o CPF tem 11 dígitos
        if (cpfNumerico.length() != 11) {
            throw new IllegalArgumentException("CPF deve conter 11 dígitos numéricos");
        }

        return cpfNumerico;
    }

    public static List<Consulta> lerConsultas(String caminhoArquivo) throws FileNotFoundException {
        List<Consulta> consultas = new ArrayList<>();
        Scanner scanner = new Scanner(new File(caminhoArquivo));

        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        while (scanner.hasNextLine()) {
            String linha = scanner.nextLine();
            String[] dados = linha.split(",");

            LocalDate data = LocalDate.parse(dados[0].trim());
            LocalTime hora = LocalTime.parse(dados[1].trim());
            int codigoMedico = Integer.parseInt(dados[2].trim());
            String cpfPaciente = padronizarCPF(dados[3].trim());

            Consulta consulta = new Consulta(data, hora, codigoMedico, cpfPaciente);
            consultas.add(consulta);
        }

        scanner.close();
        return consultas;
    }

}
