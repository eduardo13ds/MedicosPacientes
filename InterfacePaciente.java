import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InterfacePaciente {
    private final List<Medico> medicos;
    private final List<Paciente> pacientes;

    public InterfacePaciente(List<Medico> medicos, List<Paciente> pacientes) {
        this.medicos = medicos;
        this.pacientes = pacientes;
    }

    // 1. Listar todos os médicos de um paciente
    public List<Medico> listarMedicosDoPaciente(String cpfPaciente) {
        return pacientes.stream()
                .filter(p -> p.getCpf().equals(cpfPaciente))
                .findFirst()
                .map(paciente -> paciente.getConsultas().stream()
                        .map(consulta -> medicos.stream()
                                .filter(m -> m.getCodigo() == consulta.codigoMedico())
                                .findFirst()
                                .orElse(null))
                        .distinct()
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }

    // 2. Histórico de consultas passadas com um médico específico
    public List<Consulta> listarConsultasPassadasComMedico(String cpfPaciente, int codigoMedico) {
        LocalDate hoje = LocalDate.now();
        return pacientes.stream()
                .filter(p -> p.getCpf().equals(cpfPaciente))
                .findFirst()
                .map(paciente -> paciente.getConsultas().stream()
                        .filter(c -> c.codigoMedico() == codigoMedico && c.data().isBefore(hoje))
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }

    // 3. Consultas futuras agendadas
    public List<Consulta> listarConsultasFuturas(String cpfPaciente) {
        LocalDate hoje = LocalDate.now();
        return pacientes.stream()
                .filter(p -> p.getCpf().equals(cpfPaciente))
                .findFirst()
                .map(paciente -> paciente.getConsultas().stream()
                        .filter(c -> c.data().isAfter(hoje) ||
                                (c.data().isEqual(hoje) && c.hora().isAfter(LocalTime.now())))
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }
}