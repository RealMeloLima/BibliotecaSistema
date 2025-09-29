package biblioteca.model;

public abstract class Usuario {
    private String matricula;
    private String nome;
    private String curso;

    public Usuario(String matricula, String nome, String curso) {
        this.matricula = matricula;
        this.nome = nome;
        this.curso = curso;
    }

    public abstract int getPrazoDias();
    public String getMatricula() { return matricula; }
    public String getNome() { return nome; }
    public String getCurso() { return curso; }

    @Override
    public String toString() {
        return "Usuario{" +
                "matricula='" + matricula + '\'' +
                ", nome='" + nome + '\'' +
                ", curso='" + curso + '\'' +
                '}';
    }
}