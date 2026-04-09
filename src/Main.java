/**
 * CLASSE MAIN
 *
 * Para que serve?
 * Esta e a classe que o usuario vai executar.
 * Ela contem o MENU principal do sistema.
 *
 * O que ela faz?
 * - Exibe as opcoes disponiveis
 * - Le o que o usuario digita
 * - Chama os metodos corretos do GerenciadorImpressao
 * - Controla o loop do programa (repete ate o usuario sair)
 *
 * Por que separar do GerenciadorImpressao?
 * Para que a logica de negocio (regras de impressao, cotas)
 * fique separada da interface (menu, entrada do usuario).
 */

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {

    // ===========================================
    // ATRIBUTOS STATIC (pertencem a classe, nao ao objeto)
    // ===========================================
    // Scanner: para ler o que o usuario digita no teclado
    private static Scanner scanner = new Scanner(System.in);

    // Gerenciador: o "cerebro" do sistema
    private static GerenciadorImpressao gerenciador = new GerenciadorImpressao();

    // ===========================================
    // METODO PRINCIPAL (executado quando o programa inicia)
    // ===========================================
    public static void main(String[] args) {
        int opcao;

        // Loop do menu: repete ate o usuario escolher 0 (sair)
        do {
            exibirMenu();                     // Mostra as opcoes
            opcao = lerInteiro("Escolha: ");  // Le a opcao escolhida

            // Executa a opcao escolhida
            switch (opcao) {
                case 1:
                    imprimir();
                    break;
                case 2:
                    gerenciador.exibirRelatorioCotas();
                    break;
                case 3:
                    listarUsuarios();
                    break;
                case 4:
                    consultarCota();
                    break;
                case 5:
                    relatorioPeriodo();
                    break;
                case 6:
                    cadastrarUsuario();
                    break;
                case 7:
                    excluirUsuario();
                    break;
                case 8:
                    gerenciador.resetarCotas();
                    break;
                case 9:
                    gerenciador.exibirHistorico();
                    break;
                case 10:
                    alternarModoImpressao();
                    break;
                case 0:
                    System.out.println("Saindo do sistema...");
                    break;
                default:
                    System.out.println("Opcao invalida! Digite um numero de 0 a 10.");
            }
        } while (opcao != 0);  // Repete enquanto nao digitar 0

        scanner.close();  // Fecha o scanner (liberar recursos)
    }

    // ===========================================
    // METODO PARA EXIBIR O MENU
    // ===========================================
    private static void exibirMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("     SISTEMA DE GERENCIAMENTO DE IMPRESSOES");
        System.out.println("=".repeat(50));
        System.out.println("1.  Imprimir");
        System.out.println("2.  Relatorio de Cotas");
        System.out.println("3.  Listar Usuarios");
        System.out.println("4.  Consultar Cota de Usuario");
        System.out.println("5.  Relatorio por Periodo");
        System.out.println("6.  Cadastrar Usuario");
        System.out.println("7.  Excluir Usuario");
        System.out.println("8.  Resetar Cotas Mensais");
        System.out.println("9.  Ver Historico Completo");

        // Mostra opcao diferente conforme o modo atual
        if (gerenciador.isImpressaoRealAtiva()) {
            System.out.println("10. DESATIVAR Impressao Real (modo simulacao)");
        } else {
            System.out.println("10. ATIVAR Impressao Real (imprime de verdade)");
        }

        System.out.println("0. Sair");
        System.out.println("=".repeat(50));

        // Mostra status atual do modo de impressao
        if (gerenciador.isImpressaoRealAtiva()) {
            System.out.println("MODO IMPRESSAO REAL ATIVO! (vai imprimir de verdade)");
        } else {
            System.out.println("Modo SIMULACAO ativo (nao gasta papel)");
        }
    }

    // ===========================================
    // METODOS PARA CADA OPCAO DO MENU
    // ===========================================

    /**
     * Opcao 1: Realizar uma impressao
     */
    private static void imprimir() {
        System.out.println("\nREALIZAR IMPRESSAO");
        System.out.println("-".repeat(40));

        System.out.print("Nome do documento: ");
        String documento = scanner.nextLine();

        System.out.print("Matricula do usuario: ");
        String matricula = scanner.nextLine();

        int paginas = lerInteiro("Numero de paginas: ");

        gerenciador.imprimir(documento, matricula, paginas);
    }

    /**
     * Opcao 3: Listar todos os usuarios
     */
    private static void listarUsuarios() {
        System.out.println("\nLISTA DE USUARIOS CADASTRADOS");
        System.out.println("=".repeat(60));

        for (Usuario u : gerenciador.getUsuarios()) {
            System.out.printf("%s - %s | Cota: %d/%d paginas%n",
                    u.getNome(), u.getMatricula(),
                    u.getImpressoesUsadas(), u.getCotaMensal());
        }
        System.out.println("=".repeat(60));
    }

    /**
     * Opcao 4: Consultar cota de um usuario especifico
     */
    private static void consultarCota() {
        System.out.print("\nDigite a matricula do usuario: ");
        String matricula = scanner.nextLine();
        gerenciador.consultarCota(matricula);
    }

    /**
     * Opcao 5: Relatorio de impressoes por periodo
     */
    private static void relatorioPeriodo() {
        System.out.println("\nRELATORIO POR PERIODO");
        System.out.println("-".repeat(40));

        System.out.print("Data inicial (DD/MM/AAAA): ");
        String inicioStr = scanner.nextLine();
        System.out.print("Data final (DD/MM/AAAA): ");
        String fimStr = scanner.nextLine();

        try {
            // Converte texto para data
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate inicio = LocalDate.parse(inicioStr, fmt);
            LocalDate fim = LocalDate.parse(fimStr, fmt);

            gerenciador.relatorioPeriodo(inicio, fim);

        } catch (Exception e) {
            System.err.println("Data invalida! Use o formato DD/MM/AAAA");
        }
    }

    /**
     * Opcao 6: Cadastrar novo usuario
     */
    private static void cadastrarUsuario() {
        System.out.println("\nCADASTRO DE NOVO USUARIO");
        System.out.println("-".repeat(40));

        System.out.print("Nome completo: ");
        String nome = scanner.nextLine();

        System.out.print("Matricula: ");
        String matricula = scanner.nextLine();

        int cota = lerInteiro("Cota mensal de paginas: ");

        gerenciador.cadastrarUsuario(nome, matricula, cota);
    }

    /**
     * Opcao 7: Excluir usuario
     */
    private static void excluirUsuario() {
        System.out.println("\nEXCLUIR USUARIO");
        System.out.println("-".repeat(40));

        System.out.print("Digite a matricula do usuario: ");
        String matricula = scanner.nextLine();

        // Busca o usuario para mostrar confirmacao
        Usuario u = gerenciador.getUsuario(matricula);
        if (u == null) {
            System.out.println("Usuario nao encontrado!");
            return;
        }

        System.out.println("\nUsuario encontrado:");
        System.out.println("   Nome: " + u.getNome());
        System.out.println("   Matricula: " + u.getMatricula());
        System.out.println("   Impressoes usadas: " + u.getImpressoesUsadas());
        System.out.println("   Cota restante: " + u.getImpressoesRestantes());

        System.out.print("\nTem certeza que deseja excluir este usuario? (S/N): ");
        String confirmacao = scanner.nextLine();

        if (confirmacao.equalsIgnoreCase("S")) {
            gerenciador.excluirUsuario(matricula);
        } else {
            System.out.println("Exclusao cancelada.");
        }
    }

    /**
     * Opcao 10: Alternar entre modo simulacao e impressao real
     */
    private static void alternarModoImpressao() {
        boolean atual = gerenciador.isImpressaoRealAtiva();
        gerenciador.ativarImpressaoReal(!atual);  // Inverte o valor
    }

    /**
     * Le um numero inteiro do teclado com tratamento de erro
     *
     * Por que este metodo e util?
     * Se o usuario digitar letras, o programa nao quebra!
     * Ele pede para digitar novamente ate digitar um numero.
     *
     * @param mensagem Texto a ser exibido antes de ler
     * @return Numero inteiro digitado pelo usuario
     */
    private static int lerInteiro(String mensagem) {
        System.out.print(mensagem);

        // Enquanto o usuario NAO digitar um numero
        while (!scanner.hasNextInt()) {
            System.out.print("Digite um numero valido: ");
            scanner.next();  // Descarta o que foi digitado (que nao e numero)
        }

        int valor = scanner.nextInt();
        scanner.nextLine();  // Limpa o buffer (importante!)
        return valor;
    }
}