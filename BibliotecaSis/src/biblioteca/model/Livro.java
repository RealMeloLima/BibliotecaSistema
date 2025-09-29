package biblioteca.model;

public class Livro {
    private String titulo;
    private String autor;
    private int ano;
    private int quantidade;

    public Livro(String titulo, String autor, int ano, int quantidade) {
        this.titulo = titulo;
        this.autor = autor;
        this.ano = ano;
        this.quantidade = quantidade;
    }

    public boolean decrementarEstoque() {
        if (this.quantidade > 0) {
            this.quantidade--;
            return true;
        }
        return false;
    }

    public void incrementarEstoque() {
        this.quantidade++;
    }

    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getAno() { return ano; }
    public int getQuantidade() { return quantidade; }

    @Override
    public String toString() {
        return "Livro{" +
                "titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", ano=" + ano +
                ", quantidade=" + quantidade +
                '}';
    }
}