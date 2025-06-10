import java.util.List;
import java.util.Scanner;

public class InterfacePacienteConsole {
    private final InterfacePaciente interfacePaciente;
    private final Scanner scanner;

    public InterfacePacienteConsole(InterfacePaciente interfacePaciente) {
        this.interfacePaciente = interfacePaciente;
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
                    case 1 -> listarMedicosDoPaciente();
                    case 2 -> listarConsultasPassadasComMedico();
                    case 3 -> listarConsultasFuturas();
                    case 0 -> System.out.println("Saindo...");
                    default -> System.out.println("Opção inválida!");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void exibirMenu() {
        System.out.println("\n--- MENU PACIENTE ---");
        System.out.println("1. Listar todos os médicos de um paciente");
        System.out.println("2. Histórico de consultas passadas com um médico específico");
        System.out.println("3. Consultas futuras agendadas");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private void listarMedicosDoPaciente() {
        System.out.print("Digite o CPF do paciente: ");
        String cpfPaciente = Util.padronizarCPF(scanner.nextLine());
        List<Medico> medicos = interfacePaciente.listarMedicosDoPaciente(cpfPaciente);

        if (medicos.isEmpty()) {
            System.out.println("Nenhum médico encontrado para este paciente.");
            return;
        }

        System.out.println("\nMédicos encontrados:");
        medicos.forEach(m -> {
            System.out.println("Nome: " + m.getNome() + " - Código: " + m.getCodigo());
        });
    }

    private void listarConsultasPassadasComMedico() {
        System.out.print("Digite o CPF do paciente: ");
        String cpfPaciente = Util.padronizarCPF(scanner.nextLine());

        System.out.print("Digite o código do médico: ");
        int codigoMedico = scanner.nextInt();
        scanner.nextLine();

        List<Consulta> consultas = interfacePaciente.listarConsultasPassadasComMedico(cpfPaciente, codigoMedico);

        if (consultas.isEmpty()) {
            System.out.println("Nenhuma consulta passada encontrada com este médico.");
            return;
        }

        System.out.println("\nConsultas passadas encontradas:");
        consultas.forEach(c -> {
            System.out.println("Data: " + c.data().format(Util.DATE_FORMATTER) +
                    " - Hora: " + c.hora());
        });
    }

    private void listarConsultasFuturas() {
        System.out.print("Digite o CPF do paciente: ");
        String cpfPaciente = Util.padronizarCPF(scanner.nextLine());

        List<Consulta> consultas = interfacePaciente.listarConsultasFuturas(cpfPaciente);

        if (consultas.isEmpty()) {
            System.out.println("Nenhuma consulta futura agendada.");
            return;
        }

        System.out.println("\nConsultas futuras encontradas:");
        consultas.forEach(c -> {
            System.out.println("Data: " + c.data().format(Util.DATE_FORMATTER) +
                    " - Hora: " + c.hora() +
                    " - Código do Médico: " + c.codigoMedico());
        });
    }
}
