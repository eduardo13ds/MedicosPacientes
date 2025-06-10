import java.util.ArrayList;
import java.util.List;

public class Paciente {
    private final String nome;
    private final String cpf;
    private final List<Consulta> consultas;

    public Paciente(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
        this.consultas = new ArrayList<>();
    }

    public void adicionarConsulta(Consulta consulta) {
        this.consultas.add(consulta);
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public List<Consulta> getConsultas() {
        return consultas;
    }
}
