import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static List<Medico> medicos;
    private static List<Paciente> pacientes;
    private static InterfaceMedico interfaceMedico;
    private static InterfacePaciente interfacePaciente;
    private static InterfaceMedicoConsole interfaceMedicoConsole;
    private static InterfacePacienteConsole interfacePacienteConsole;

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

    public static void main(String[] args) {
        try {
            carregarDados();
            inicializarInterfaces();

            System.out.println("Bem-vindo ao Sistema de Gerenciamento de Médicos e Pacientes!");
            System.out.println("Este sistema oferece duas interfaces diferentes:");
            System.out.println("1. Interface personalizada com funcionalidade de salvar em CSV (opções 1 e 2)");
            System.out.println("2. Interface console padrão (opções 3 e 4)");
            System.out.println();

            exibirMenuPrincipal();
        } catch (FileNotFoundException e) {
            System.out.println("Erro ao carregar arquivos CSV: " + e.getMessage());
        }
    }

    private static void carregarDados() throws FileNotFoundException {
        // Carrega os dados dos arquivos CSV
        medicos = LeitorCSV.lerMedicos("medicos.csv");
        pacientes = LeitorCSV.lerPacientes("pacientes.csv");
        List<Consulta> consultas = LeitorCSV.lerConsultas("consultas.csv");

        // Associa consultas aos pacientes
        for (Consulta consulta : consultas) {
            for (Paciente paciente : pacientes) {
                if (paciente.getCpf().equals(consulta.cpfPaciente())) {
                    paciente.adicionarConsulta(consulta);
                }
            }
        }

        // Associa pacientes aos médicos
        for (Paciente paciente : pacientes) {
            for (Consulta consulta : paciente.getConsultas()) {
                for (Medico medico : medicos) {
                    if (medico.getCodigo() == consulta.codigoMedico()) {
                        medico.adicionarPaciente(paciente);
                    }
                }
            }
        }
    }

    private static void inicializarInterfaces() {
        interfaceMedico = new InterfaceMedico(medicos, pacientes);
        interfacePaciente = new InterfacePaciente(medicos, pacientes);
        interfaceMedicoConsole = new InterfaceMedicoConsole(interfaceMedico);
        interfacePacienteConsole = new InterfacePacienteConsole(interfacePaciente);
    }

    private static void exibirMenuPrincipal() {
        int opcao;
        do {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1. Menu Médico");
            System.out.println("2. Menu Paciente");
            System.out.println("3. Menu Médico (Interface Console)");
            System.out.println("4. Menu Paciente (Interface Console)");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer

            switch (opcao) {
                case 1 -> menuMedico();
                case 2 -> menuPaciente();
                case 3 -> interfaceMedicoConsole.iniciar();
                case 4 -> interfacePacienteConsole.iniciar();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private static void menuMedico() {
        int opcao;
        do {
            System.out.println("\n--- MENU MÉDICO ---");
            System.out.println("1. Listar pacientes de um médico");
            System.out.println("2. Listar consultas de um médico em um período");
            System.out.println("3. Listar pacientes inativos de um médico");
            System.out.println("4. Salvar resultados da última busca em CSV");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer

            try {
                switch (opcao) {
                    case 1 -> listarPacientesDoMedico();
                    case 2 -> listarConsultasDoMedicoNoPeriodo();
                    case 3 -> listarPacientesInativos();
                    case 4 -> salvarUltimaBuscaMedico();
                    case 0 -> System.out.println("Voltando...");
                    default -> System.out.println("Opção inválida!");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private static void menuPaciente() {
        int opcao;
        do {
            System.out.println("\n--- MENU PACIENTE ---");
            System.out.println("1. Listar médicos de um paciente");
            System.out.println("2. Listar consultas passadas com um médico");
            System.out.println("3. Listar consultas futuras");
            System.out.println("4. Salvar resultados da última busca em CSV");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer

            try {
                switch (opcao) {
                    case 1 -> listarMedicosDoPaciente();
                    case 2 -> listarConsultasPassadasComMedico();
                    case 3 -> listarConsultasFuturas();
                    case 4 -> salvarUltimaBuscaPaciente();
                    case 0 -> System.out.println("Voltando...");
                    default -> System.out.println("Opção inválida!");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    // Variáveis para armazenar os resultados das últimas buscas
    private static List<Paciente> ultimosPacientes;
    private static List<Medico> ultimosMedicos;
    private static List<Consulta> ultimasConsultas;
    private static String tipoUltimaBuscaMedico = "";
    private static String tipoUltimaBuscaPaciente = "";

    private static void listarPacientesDoMedico() {
        System.out.print("Digite o código do médico: ");
        int codigoMedico = scanner.nextInt();
        scanner.nextLine();

        ultimosPacientes = interfaceMedico.listarPacientesDoMedico(codigoMedico);
        tipoUltimaBuscaMedico = "pacientes_do_medico";

        if (ultimosPacientes.isEmpty()) {
            System.out.println("Nenhum paciente encontrado para este médico.");
            return;
        }

        System.out.println("\nPacientes encontrados:");
        ultimosPacientes.forEach(p -> System.out.println("Nome: " + p.getNome() + " - CPF: " + p.getCpf()));
    }

    private static void listarConsultasDoMedicoNoPeriodo() {
        try {
            System.out.print("Digite o código do médico: ");
            int codigoMedico = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Digite a data de início (dd/MM/yyyy): ");
            LocalDate dataInicio = LocalDate.parse(scanner.nextLine(), dateFormatter);

            System.out.print("Digite a data de fim (dd/MM/yyyy): ");
            LocalDate dataFim = LocalDate.parse(scanner.nextLine(), dateFormatter);

            if (dataFim.isBefore(dataInicio)) {
                System.out.println("Erro: A data final deve ser posterior à data inicial.");
                ultimasConsultas = List.of();
                return;
            }

            ultimasConsultas = interfaceMedico.listarConsultasDoMedicoNoPeriodo(codigoMedico, dataInicio, dataFim);
        } catch (DateTimeParseException e) {
            System.out.println("Erro: Formato de data inválido. Use o formato dd/MM/yyyy.");
            ultimasConsultas = List.of();
            return;
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            ultimasConsultas = List.of();
            return;
        }
        tipoUltimaBuscaMedico = "consultas_do_medico_periodo";

        if (ultimasConsultas.isEmpty()) {
            System.out.println("Nenhuma consulta encontrada para este médico no período especificado.");
            return;
        }

        System.out.println("\nConsultas encontradas:");
        ultimasConsultas.forEach(c -> System.out.println("Data: " + c.data().format(dateFormatter) +
                " - Hora: " + c.hora() +
                " - CPF do Paciente: " + c.cpfPaciente()));
    }

    private static void listarPacientesInativos() {
        System.out.print("Digite o código do médico: ");
        int codigoMedico = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Digite o número de meses de inatividade: ");
        int mesesInatividade = scanner.nextInt();
        scanner.nextLine();

        ultimosPacientes = interfaceMedico.listarPacientesInativos(codigoMedico, mesesInatividade);
        tipoUltimaBuscaMedico = "pacientes_inativos";

        if (ultimosPacientes.isEmpty()) {
            System.out.println("Nenhum paciente inativo encontrado para este médico.");
            return;
        }

        System.out.println("\nPacientes inativos encontrados:");
        ultimosPacientes.forEach(p -> System.out.println("Nome: " + p.getNome() + " - CPF: " + p.getCpf()));
    }

    private static void listarMedicosDoPaciente() {
        System.out.print("Digite o CPF do paciente: ");
        String cpfPaciente;
        try {
            cpfPaciente = padronizarCPF(scanner.nextLine());
            ultimosMedicos = interfacePaciente.listarMedicosDoPaciente(cpfPaciente);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
            ultimosMedicos = List.of();
        }
        tipoUltimaBuscaPaciente = "medicos_do_paciente";

        if (ultimosMedicos.isEmpty()) {
            System.out.println("Nenhum médico encontrado para este paciente.");
            return;
        }

        System.out.println("\nMédicos encontrados:");
        ultimosMedicos.forEach(m -> System.out.println("Nome: " + m.getNome() + " - Código: " + m.getCodigo()));
    }

    private static void listarConsultasPassadasComMedico() {
        System.out.print("Digite o CPF do paciente: ");
        String cpfPaciente;
        try {
            cpfPaciente = padronizarCPF(scanner.nextLine());

            System.out.print("Digite o código do médico: ");
            int codigoMedico = scanner.nextInt();
            scanner.nextLine();

            ultimasConsultas = interfacePaciente.listarConsultasPassadasComMedico(cpfPaciente, codigoMedico);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
            ultimasConsultas = List.of();
        }
        tipoUltimaBuscaPaciente = "consultas_passadas_com_medico";

        if (ultimasConsultas.isEmpty()) {
            System.out.println("Nenhuma consulta passada encontrada com este médico.");
            return;
        }

        System.out.println("\nConsultas passadas encontradas:");
        ultimasConsultas.forEach(c -> System.out.println("Data: " + c.data().format(dateFormatter) +
                " - Hora: " + c.hora()));
    }

    private static void listarConsultasFuturas() {
        System.out.print("Digite o CPF do paciente: ");
        String cpfPaciente;
        try {
            cpfPaciente = padronizarCPF(scanner.nextLine());
            ultimasConsultas = interfacePaciente.listarConsultasFuturas(cpfPaciente);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
            ultimasConsultas = List.of();
        }
        tipoUltimaBuscaPaciente = "consultas_futuras";

        if (ultimasConsultas.isEmpty()) {
            System.out.println("Nenhuma consulta futura agendada.");
            return;
        }

        System.out.println("\nConsultas futuras encontradas:");
        ultimasConsultas.forEach(c -> System.out.println("Data: " + c.data().format(dateFormatter) +
                " - Hora: " + c.hora() +
                " - Código do Médico: " + c.codigoMedico()));
    }

    private static void salvarUltimaBuscaMedico() {
        if (tipoUltimaBuscaMedico.isEmpty()) {
            System.out.println("Nenhuma busca realizada ainda.");
            return;
        }

        try {
            switch (tipoUltimaBuscaMedico) {
                case "pacientes_do_medico", "pacientes_inativos" -> salvarPacientesEmCSV(ultimosPacientes, tipoUltimaBuscaMedico + ".csv");
                case "consultas_do_medico_periodo" -> salvarConsultasEmCSV(ultimasConsultas, tipoUltimaBuscaMedico + ".csv");
                default -> System.out.println("Tipo de busca não suportado para salvamento.");
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar arquivo CSV: " + e.getMessage());
        }
    }

    private static void salvarUltimaBuscaPaciente() {
        if (tipoUltimaBuscaPaciente.isEmpty()) {
            System.out.println("Nenhuma busca realizada ainda.");
            return;
        }

        try {
            switch (tipoUltimaBuscaPaciente) {
                case "medicos_do_paciente" -> salvarMedicosEmCSV(ultimosMedicos, tipoUltimaBuscaPaciente + ".csv");
                case "consultas_passadas_com_medico", "consultas_futuras" -> salvarConsultasEmCSV(ultimasConsultas, tipoUltimaBuscaPaciente + ".csv");
                default -> System.out.println("Tipo de busca não suportado para salvamento.");
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar arquivo CSV: " + e.getMessage());
        }
    }

    // Método para obter o nome do arquivo CSV
    private static String obterNomeArquivo(String nomeArquivoPadrao) {
        System.out.print("Nome do arquivo (Enter para usar " + nomeArquivoPadrao + "): ");
        String nomeEscolhido = scanner.nextLine().trim();
        if (!nomeEscolhido.isEmpty()) {
            // Adiciona a extensão .csv se não estiver presente
            if (!nomeEscolhido.toLowerCase().endsWith(".csv")) {
                nomeEscolhido += ".csv";
            }
            return nomeEscolhido;
        }
        return nomeArquivoPadrao;
    }

    private static void salvarPacientesEmCSV(List<Paciente> pacientes, String nomeArquivo) throws IOException {
        // Permite ao usuário escolher o nome do arquivo
        nomeArquivo = obterNomeArquivo(nomeArquivo);

        File arquivo = new File(nomeArquivo);
        FileWriter writer = new FileWriter(arquivo);

        // Escreve o cabeçalho
        writer.write("Nome,CPF\n");

        // Escreve os dados dos pacientes
        for (Paciente paciente : pacientes) {
            writer.write(paciente.getNome() + "," + paciente.getCpf() + "\n");
        }

        writer.close();
        System.out.println("Dados salvos com sucesso no arquivo " + nomeArquivo);
    }

    private static void salvarMedicosEmCSV(List<Medico> medicos, String nomeArquivo) throws IOException {
        // Permite ao usuário escolher o nome do arquivo
        nomeArquivo = obterNomeArquivo(nomeArquivo);

        File arquivo = new File(nomeArquivo);
        FileWriter writer = new FileWriter(arquivo);

        // Escreve o cabeçalho
        writer.write("Nome,Código\n");

        // Escreve os dados dos médicos
        for (Medico medico : medicos) {
            writer.write(medico.getNome() + "," + medico.getCodigo() + "\n");
        }

        writer.close();
        System.out.println("Dados salvos com sucesso no arquivo " + nomeArquivo);
    }

    private static void salvarConsultasEmCSV(List<Consulta> consultas, String nomeArquivo) throws IOException {
        // Permite ao usuário escolher o nome do arquivo
        nomeArquivo = obterNomeArquivo(nomeArquivo);

        File arquivo = new File(nomeArquivo);
        FileWriter writer = new FileWriter(arquivo);

        // Escreve o cabeçalho
        writer.write("Data,Hora,Código do Médico,CPF do Paciente\n");

        // Escreve os dados das consultas
        for (Consulta consulta : consultas) {
            writer.write(consulta.data().format(dateFormatter) + "," +
                         consulta.hora() + "," +
                         consulta.codigoMedico() + "," +
                         consulta.cpfPaciente() + "\n");
        }

        writer.close();
        System.out.println("Dados salvos com sucesso no arquivo " + nomeArquivo);
    }
}
