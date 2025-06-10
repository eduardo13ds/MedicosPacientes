import java.util.ArrayList;
import java.util.List;

public class Medico {
    private final String nome;
    private final int codigo;
    private final List<Paciente> pacientes;

    public Medico(String nome, int codigo) {
        this.nome = nome;
        this.codigo = codigo;
        this.pacientes = new ArrayList<>();
    }

    public void adicionarPaciente(Paciente paciente) {
        this.pacientes.add(paciente);
    }

    public String getNome() {
        return nome;
    }

    public int getCodigo() {
        return codigo;
    }

    public List<Paciente> getPacientes() {
        return pacientes;
    }
}
