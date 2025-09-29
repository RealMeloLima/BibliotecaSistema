package biblioteca.model;

import java.time.LocalDate;

public class Emprestimo {
    private static int contadorId = 1;

    private int id;
    private Usuario usuario;
    private Livro livro;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevista;
    private LocalDate dataDevolucao;

    public Emprestimo(Usuario usuario, Livro livro) {
        this.id = contadorId++;
        this.usuario = usuario;
        this.livro = livro;
        this.dataEmprestimo = LocalDate.now();
        this.dataPrevista = dataEmprestimo.plusDays(usuario.getPrazoDias());
        this.dataDevolucao = null;
    }

    public void registrarDevolucao() {
        this.dataDevolucao = LocalDate.now();
    }

    public int getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Livro getLivro() { return livro; }
    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public LocalDate getDataPrevista() { return dataPrevista; }
    public LocalDate getDataDevolucao() { return dataDevolucao; }
    
    public boolean estaEmAndamento() {
        return this.dataDevolucao == null;
    }

    @Override
    public String toString() {
        String status = estaEmAndamento() ? "Em Andamento" : "Devolvido";
        String detalhesDevolucao = estaEmAndamento() ? "" : ", Data Devolução: " + dataDevolucao;

        return String.format(
            "Emprestimo #%d: Status: %s\n" +
            "  - %s\n" +
            "  - Livro: '%s' (Autor: %s, Ano: %d)\n" +
            "  - Data Empréstimo: %s, Data Prevista: %s%s",
            id, status,
            usuario.toString(),
            livro.getTitulo(), livro.getAutor(), livro.getAno(),
            dataEmprestimo, dataPrevista, detalhesDevolucao
        );
    }
}