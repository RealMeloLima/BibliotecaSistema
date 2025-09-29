package biblioteca.model;

public class Professor extends Usuario {

    public Professor(String matricula, String nome, String curso) {
        super(matricula, nome, curso);
    }

    @Override
    public int getPrazoDias() {
        return 14;
    }

    @Override
    public String toString() {
        return "Professor: " + super.getNome() + " (Matrícula: " + super.getMatricula() + ")";
    }
}