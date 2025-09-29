# Biblioteca em Java

Este projeto implementa um sistema básico de gerenciamento de biblioteca utilizando os conceitos de Orientação a Objetos em Java, como Herança, Polimorfismo e Composição.

## Entidades Principais

| Entidade | Atributos |
| :--- | :--- |
| **USUARIO** (Base Abstrata) | `matricula`, `nome`, `curso` |
| **LIVRO** | `titulo`, `autor`, `ano`, `quantidade` (em estoque) |
| **EMPRESTIMO** | `id`, `usuario` (objeto), `livro` (objeto), `dataEmprestimo`, `dataPrevista`, `dataDevolucao` |

## Funcionalidades

1.  **Cadastro de Usuários e Livros.**
2.  **Realizar Empréstimo:** Calcula a `dataPrevista` com base no prazo do tipo de usuário (Polimorfismo).
3.  **Atualização de Estoque:** Decrementa ao emprestar e incrementa ao devolver.
4.  **Listagem:** Exibe todos os empréstimos "Em Andamento" (sem `dataDevolucao`).
5.  **Validações:** Não permite empréstimo se o estoque for `0`.
