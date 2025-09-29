package biblioteca.model;

public class Aluno extends Usuario {

    public Aluno(String matricula, String nome, String curso) {
        super(matricula, nome, curso);
    }

    @Override
    public int getPrazoDias() {
        return 7;
    }

    @Override
    public String toString() {
        return "Aluno: " + super.getNome() + " (Matrícula: " + super.getMatricula() + ")";
    }
}