package biblioteca.main;

import biblioteca.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SistemaBiblioteca {
    
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Livro> livros = new ArrayList<>();
    private List<Emprestimo> emprestimos = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    private Optional<Usuario> buscarUsuario(String matricula) {
        return usuarios.stream()
            .filter(u -> u.getMatricula().equals(matricula))
            .findFirst();
    }
    
    private Optional<Livro> buscarLivro(String titulo) {
        return livros.stream()
            .filter(l -> l.getTitulo().equalsIgnoreCase(titulo))
            .findFirst();
    }

    public void cadastrarUsuarioInterativo() {
        System.out.println("\n--- CADASTRO DE USUÁRIO ---");
        System.out.print("Tipo (A para Aluno, P para Professor): ");
        String tipo = scanner.nextLine().trim().toUpperCase();
        
        System.out.print("Matrícula: ");
        String matricula = scanner.nextLine().trim();

        if (buscarUsuario(matricula).isPresent()) {
            System.out.println("ERRO: Usuário com matrícula " + matricula + " já cadastrado.");
            return;
        }

        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Curso/Departamento: ");
        String curso = scanner.nextLine();
        
        Usuario novoUsuario = null;
        if (tipo.equals("A")) {
            novoUsuario = new Aluno(matricula, nome, curso);
        } else if (tipo.equals("P")) {
            novoUsuario = new Professor(matricula, nome, curso);
        } else {
            System.out.println("ERRO: Tipo de usuário inválido.");
            return;
        }
        
        usuarios.add(novoUsuario);
        System.out.println("SUCESSO: Usuário cadastrado: " + novoUsuario.getNome() + " (" + novoUsuario.getClass().getSimpleName() + ")");
    }

    public void cadastrarLivroInterativo() {
        System.out.println("\n--- CADASTRO DE LIVRO ---");
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        System.out.print("Autor: ");
        String autor = scanner.nextLine();
        
        if (livros.stream().anyMatch(l -> 
                l.getTitulo().equalsIgnoreCase(titulo) && l.getAutor().equalsIgnoreCase(autor))) {
            System.out.println("AVISO: Livro já existe. Use a opção de atualizar estoque se necessário.");
            return;
        }

        int ano = 0;
        int quantidade = 0;
        try {
            System.out.print("Ano: ");
            ano = Integer.parseInt(scanner.nextLine());
            System.out.print("Quantidade (Estoque): ");
            quantidade = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ERRO: Entrada numérica inválida.");
            return;
        }

        Livro livro = new Livro(titulo, autor, ano, quantidade);
        livros.add(livro);
        System.out.println("SUCESSO: Livro cadastrado: '" + livro.getTitulo() + "'.");
    }
    
    public void realizarEmprestimoInterativo() {
        System.out.println("\n--- REALIZAR EMPRÉSTIMO ---");
        System.out.print("Matrícula do Usuário: ");
        String matriculaUsuario = scanner.nextLine().trim();
        System.out.print("Título do Livro: ");
        String tituloLivro = scanner.nextLine().trim();
        
        Optional<Usuario> userOpt = buscarUsuario(matriculaUsuario);
        if (userOpt.isEmpty()) {
            System.out.println("ERRO: Usuário não encontrado.");
            return;
        }
        Usuario usuario = userOpt.get();

        Optional<Livro> livroOpt = buscarLivro(tituloLivro);
        if (livroOpt.isEmpty()) {
            System.out.println("ERRO: Livro não encontrado.");
            return;
        }
        Livro livro = livroOpt.get();

        if (livro.getQuantidade() == 0) {
            System.out.println("ERRO: Estoque esgotado para o livro '" + livro.getTitulo() + "'.");
            return;
        }

        if (livro.decrementarEstoque()) {
            Emprestimo novoEmprestimo = new Emprestimo(usuario, livro);
            emprestimos.add(novoEmprestimo);
            
            System.out.println("\nSUCESSO: Empréstimo realizado! (ID #" + novoEmprestimo.getId() + ")");
            System.out.println("  Prazo (dias): " + usuario.getPrazoDias());
            System.out.println("  Data Prevista: " + novoEmprestimo.getDataPrevista());
            System.out.println("  Estoque atual: " + livro.getQuantidade());
        }
    }

    public void registrarDevolucaoInterativa() {
        System.out.println("\n--- REGISTRAR DEVOLUÇÃO ---");
        System.out.print("ID do Empréstimo a ser devolvido: ");
        
        int idEmprestimo;
        try {
            idEmprestimo = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ERRO: ID inválido.");
            return;
        }

        Optional<Emprestimo> emprestimoOpt = emprestimos.stream()
            .filter(e -> e.getId() == idEmprestimo)
            .findFirst();

        if (emprestimoOpt.isEmpty()) {
            System.out.println("ERRO: Empréstimo com ID " + idEmprestimo + " não encontrado.");
            return;
        }
        Emprestimo emprestimo = emprestimoOpt.get();

        if (!emprestimo.estaEmAndamento()) {
            System.out.println("AVISO: Empréstimo ID " + idEmprestimo + " já foi devolvido.");
            return;
        }

        emprestimo.getLivro().incrementarEstoque();
        emprestimo.registrarDevolucao();
        
        System.out.println("\nSUCESSO: Devolução registrada.");
        System.out.println("  Empréstimo ID: " + emprestimo.getId());
        System.out.println("  Estoque atual: " + emprestimo.getLivro().getQuantidade());
        
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
            .collect(Collectors.toList());

        if (emAndamento.isEmpty()) {
            System.out.println("Nenhum empréstimo em andamento no momento.");
            return;
        }

        for (Emprestimo e : emAndamento) {
            System.out.println(e);
            System.out.println("---");
        }
    }

    public void exibirMenu() {
        String opcao = "";
        do {
            System.out.println("\n\n=============== MENU BIBLIOTECA ===============");
            System.out.println("1. Cadastrar Usuário");
            System.out.println("2. Cadastrar Livro");
            System.out.println("3. Realizar Empréstimo");
            System.out.println("4. Registrar Devolução");
            System.out.println("5. Listar Empréstimos em Andamento");
            System.out.println("0. Sair");
            System.out.println("===============================================");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextLine().trim();

            try {
                switch (opcao) {
                    case "1":
                        cadastrarUsuarioInterativo();
                        break;
                    case "2":
                        cadastrarLivroInterativo();
                        break;
                    case "3":
                        realizarEmprestimoInterativo();
                        break;
                    case "4":
                        registrarDevolucaoInterativa();
                        break;
                    case "5":
                        listarEmprestimosEmAndamento();
                        break;
                    case "0":
                        System.out.println("Saindo do sistema. Obrigado!");
                        scanner.close();
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (Exception e) {
                System.out.println("Ocorreu um erro inesperado: " + e.getMessage());
            }
        } while (!opcao.equals("0"));
    }

    public static void main(String[] args) {
        SistemaBiblioteca sistema = new SistemaBiblioteca();
        sistema.exibirMenu();
    }
}