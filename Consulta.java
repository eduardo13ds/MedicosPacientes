import java.time.LocalDate;
import java.time.LocalTime;

public record Consulta(LocalDate data, LocalTime hora, int codigoMedico, String cpfPaciente) {
}
