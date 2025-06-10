import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class InterfaceMedicoConsole {
    private final InterfaceMedico interfaceMedico;
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public InterfaceMedicoConsole(InterfaceMedico interfaceMedico) {
        this.interfaceMedico = interfaceMedico;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        int opcao;
        do {
            exibirMenu();
            opcao = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer

            try {
                switch (opcao) {
                    case 1 -> listarPacientes();
                    case 2 -> listarConsultasPeriodo();
                    case 3 -> listarPacientesInativos();
                    case 0 -> System.out.println("Saindo...");
                    default -> System.out.println("Opção inválida!");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void exibirMenu() {
        System.out.println("\n--- MENU MÉDICO ---");
        System.out.println("1. Listar todos os pacientes");
        System.out.println("2. Listar consultas em um período");
        System.out.println("3. Listar pacientes inativos");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private void listarPacientes() {
        System.out.print("Digite o código do médico: ");
        int codigoMedico = scanner.nextInt();
        List<Paciente> pacientes = interfaceMedico.listarPacientesDoMedico(codigoMedico);

        if (pacientes.isEmpty()) {
            System.out.println("Nenhum paciente encontrado.");
            return;
        }

        System.out.println("\nPacientes encontrados:");
        pacientes.forEach(p -> System.out.println("Nome: " + p.getNome() + " - CPF: " + p.getCpf()));
    }

    private void listarConsultasPeriodo() {
        System.out.print("Digite o código do médico: ");
        int codigoMedico = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Digite a data inicial (dd/MM/yyyy): ");
        LocalDate dataInicio = LocalDate.parse(scanner.nextLine(), dateFormatter);

        System.out.print("Digite a data final (dd/MM/yyyy): ");
        LocalDate dataFim = LocalDate.parse(scanner.nextLine(), dateFormatter);

        List<Consulta> consultas = interfaceMedico.listarConsultasDoMedicoNoPeriodo(codigoMedico, dataInicio, dataFim);

        if (consultas.isEmpty()) {
            System.out.println("Nenhuma consulta encontrada no período.");
            return;
        }

        System.out.println("\nConsultas encontradas:");
        consultas.forEach(c -> System.out.println("Data: " + c.data().format(dateFormatter) +
                " - Hora: " + c.hora() +
                " - CPF Paciente: " + c.cpfPaciente()));
    }

    private void listarPacientesInativos() {
        System.out.print("Digite o código do médico: ");
        int codigoMedico = scanner.nextInt();

        System.out.print("Digite o número de meses de inatividade: ");
        int mesesInatividade = scanner.nextInt();

        List<Paciente> pacientes = interfaceMedico.listarPacientesInativos(codigoMedico, mesesInatividade);

        if (pacientes.isEmpty()) {
            System.out.println("Nenhum paciente inativo encontrado.");
            return;
        }

        System.out.println("\nPacientes inativos encontrados:");
        pacientes.forEach(p -> System.out.println("Nome: " + p.getNome() + " - CPF: " + p.getCpf()));
    }
}