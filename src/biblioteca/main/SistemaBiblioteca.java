package biblioteca.main;

import biblioteca.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;


public class SistemaBiblioteca {
    
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Livro> livros = new ArrayList<>();
    private List<Emprestimo> emprestimos = new ArrayList<>();


    public void cadastrarUsuario(Usuario usuario) {
        boolean existe = usuarios.stream().anyMatch(u -> u.getMatricula().equals(usuario.getMatricula()));
        if (existe) {
            System.out.println("ERRO: Usuário com matrícula " + usuario.getMatricula() + " já cadastrado.");
            return;
        }
        usuarios.add(usuario);
        System.out.println("SUCESSO: Usuário cadastrado: " + usuario.getNome() + " (" + usuario.getClass().getSimpleName() + ")");
    }

    public void cadastrarLivro(Livro livro) {
        boolean existe = livros.stream().anyMatch(l -> 
            l.getTitulo().equalsIgnoreCase(livro.getTitulo()) && 
            l.getAutor().equalsIgnoreCase(livro.getAutor()) &&
            l.getAno() == livro.getAno()
        );
        if (existe) {
            System.out.println("ERRO: Livro já cadastrado. Utilize uma função de atualização de estoque.");
            return;
        }
        livros.add(livro);
        System.out.println("SUCESSO: Livro cadastrado: '" + livro.getTitulo() + "' (Estoque inicial: " + livro.getQuantidade() + ")");
    }
    
    public void realizarEmprestimo(String matriculaUsuario, String tituloLivro) {
        Optional<Usuario> userOpt = usuarios.stream()
            .filter(u -> u.getMatricula().equals(matriculaUsuario))
            .findFirst();

        if (userOpt.isEmpty()) {
            System.out.println("ERRO: Usuário com matrícula " + matriculaUsuario + " não encontrado.");
            return;
        }
        Usuario usuario = userOpt.get();

        Optional<Livro> livroOpt = livros.stream()
            .filter(l -> l.getTitulo().equalsIgnoreCase(tituloLivro))
            .findFirst();

        if (livroOpt.isEmpty()) {
            System.out.println("ERRO: Livro com título '" + tituloLivro + "' não encontrado.");
            return;
        }
        Livro livro = livroOpt.get();

        if (livro.getQuantidade() == 0) {
            System.out.println("ERRO: Estoque esgotado para o livro '" + livro.getTitulo() + "'.");
            return;
        }

        if (livro.decrementarEstoque()) { // Atualiza estoque
            Emprestimo novoEmprestimo = new Emprestimo(usuario, livro);
            emprestimos.add(novoEmprestimo);
            
            System.out.println("\n--- NOVO EMPRÉSTIMO REALIZADO ---");
            System.out.println(novoEmprestimo);
            System.out.println("Estoque atual de '" + livro.getTitulo() + "': " + livro.getQuantidade());
        } else {
            System.out.println("ERRO: Falha ao decrementar estoque. Empréstimo não realizado.");
        }
    }

    public void registrarDevolucao(int idEmprestimo) {
        Optional<Emprestimo> emprestimoOpt = emprestimos.stream()
            .filter(e -> e.getId() == idEmprestimo)
            .findFirst();

        if (emprestimoOpt.isEmpty()) {
            System.out.println("ERRO: Empréstimo com ID " + idEmprestimo + " não encontrado.");
            return;
        }
        Emprestimo emprestimo = emprestimoOpt.get();

        if (!emprestimo.estaEmAndamento()) {
            System.out.println("AVISO: Empréstimo ID " + idEmprestimo + " já foi devolvido em " + emprestimo.getDataDevolucao() + ".");
            return;
        }

        emprestimo.getLivro().incrementarEstoque();
        emprestimo.registrarDevolucao();
        
        System.out.println("\n--- DEVOLUÇÃO REGISTRADA ---");
        System.out.println("Empréstimo ID " + idEmprestimo + " de '" + emprestimo.getLivro().getTitulo() + "' devolvido por " + emprestimo.getUsuario().getNome() + ".");
        System.out.println("Estoque atual de '" + emprestimo.getLivro().getTitulo() + "': " + emprestimo.getLivro().getQuantidade());
        
        if (emprestimo.getDataDevolucao().isAfter(emprestimo.getDataPrevista())) {
            System.out.println("!!! ATENÇÃO: Devolução com ATRASO! Data prevista: " + emprestimo.getDataPrevista() + ".");
        }
    }

    public void listarEmprestimosEmAndamento() {
        System.out.println("\n============================================");
        System.out.println("  LISTA DE EMPRÉSTIMOS EM ANDAMENTO  ");
        System.out.println("============================================\n");
        
        List<Emprestimo> emAndamento = emprestimos.stream()
            .filter(Emprestimo::estaEmAndamento)
            .toList();

        if (emAndamento.isEmpty()) {
            System.out.println("Nenhum empréstimo em andamento no momento.");
            return;
        }

        for (Emprestimo e : emAndamento) {
            System.out.println(e);
            System.out.println("---");
        }
    }

    public static void main(String[] args) {
        SistemaBiblioteca sistema = new SistemaBiblioteca();

        System.out.println("--- 1. CADASTRO DE USUÁRIOS (Herança/Polimorfismo) ---");
        
        Usuario aluno1 = new Aluno("12345", "João Silva", "ADS");
        sistema.cadastrarUsuario(aluno1);
        
        Usuario professor1 = new Professor("P987", "Maria Oliveira", "Engenharia");
        sistema.cadastrarUsuario(professor1);

        sistema.cadastrarUsuario(new Aluno("12345", "João Duplicado", "Medicina"));
        
        System.out.println("\n--- 2. CADASTRO DE LIVROS ---");
        
        Livro livro1 = new Livro("Java Profissional", "J. Gosling", 2020, 2); // 2 cópias
        sistema.cadastrarLivro(livro1);

        Livro livro2 = new Livro("Estruturas de Dados", "A. Boas", 2018, 1); // 1 cópia
        sistema.cadastrarLivro(livro2);

        System.out.println("\n--- 3. REALIZAÇÃO DE EMPRÉSTIMOS (Composição/Cálculo de Prazo) ---");
        
        sistema.realizarEmprestimo(aluno1.getMatricula(), livro1.getTitulo());
        
        sistema.realizarEmprestimo(professor1.getMatricula(), livro2.getTitulo());

        sistema.realizarEmprestimo(aluno1.getMatricula(), livro1.getTitulo());

        sistema.realizarEmprestimo(professor1.getMatricula(), livro2.getTitulo());

        System.out.println("\n--- 4. LISTAGEM DE EMPRÉSTIMOS EM ANDAMENTO ---");
        sistema.listarEmprestimosEmAndamento();
        
        System.out.println("\n--- 5. REGISTRO DE DEVOLUÇÃO (Atualização de Estoque) ---");
        
        sistema.registrarDevolucao(2);

        sistema.registrarDevolucao(1);

        System.out.println("\n--- 6. NOVA LISTAGEM (Menos itens em andamento) ---");
        sistema.listarEmprestimosEmAndamento();
        
        System.out.println("\n--- 7. NOVO EMPRÉSTIMO APÓS DEVOLUÇÃO ---");
        sistema.realizarEmprestimo(aluno1.getMatricula(), livro2.getTitulo());
    }
}