import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InterfaceMedico {
    private List<Medico> medicos;
    private List<Paciente> pacientes;

    public InterfaceMedico(List<Medico> medicos, List<Paciente> pacientes) {
        this.medicos = medicos;
        this.pacientes = pacientes;
    }

    public List<Paciente> listarPacientesDoMedico(int codigoMedico) {
        return medicos.stream()
                .filter(medico -> medico.getCodigo() == codigoMedico)
                .findFirst()
                .map(Medico::getPacientes)
                .orElse(new ArrayList<>());
    }

    public List<Consulta> listarConsultasDoMedicoNoPeriodo(int codigoMedico, LocalDate dataInicio, LocalDate dataFim) {
        return pacientes.stream()
                .flatMap(p -> p.getConsultas().stream())
                .filter(c -> c.codigoMedico() == codigoMedico)
                .filter(c -> c.data().isAfter(dataInicio) && c.data().isBefore(dataFim))
                .sorted(Comparator.comparing(Consulta::hora))
                .collect(Collectors.toList());
    }

    public List<Paciente> listarPacientesInativos(int codigoMedico, int mesesInatividade) {
        LocalDate hoje = LocalDate.now();
        return medicos.stream()
                .filter(medico -> medico.getCodigo() == codigoMedico)
                .findFirst()
                .map(medico -> medico.getPacientes().stream()
                        .filter(paciente -> paciente.getConsultas().stream()
                                .noneMatch(consulta -> consulta.data().isAfter(hoje.minusMonths(mesesInatividade))))
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }
}
