/**
 * CLASSE GERENCIADORIMPRESSAO
 *
 * Para que serve?
 * Esta e a classe mais importante do sistema - o "CEREBRO" do programa.
 *
 * O que ela faz?
 * - Gerencia a lista de usuarios
 * - Controla as impressoes (verifica cota, desconta paginas)
 * - Mantem o historico
 * - Coordena as operacoes entre o usuario e o banco de dados
 *
 * Por que separar esta logica?
 * Para que a classe Main (menu) fique so com a interface,
 * e toda a regra de negocio fique aqui.
 */

// Importacoes necessarias
import java.time.LocalDate;      // Para trabalhar com datas (sem hora)
import java.time.format.DateTimeFormatter;  // Para formatar data
import java.util.*;              // Para Map, List, ArrayList, HashMap

public class GerenciadorImpressao {

    // ===========================================
    // ATRIBUTOS
    // ===========================================
    // Database: objeto que gerencia a conexao com o banco
    private Database db;

    // Map: "dicionario" que guarda usuarios usando a matricula como chave
    // Exemplo: "2024001" -> Objeto Usuario do Joao
    private Map<String, Usuario> usuarios;

    // List: lista ordenada de todos os registros de impressao
    private List<RegistroImpressao> historico;

    // Flag para controlar se imprime de verdade ou so simula
    private boolean impressaoRealAtiva = false;  // Comeca desativada

    // ===========================================
    // CONSTRUTOR
    // ===========================================
    // Quando criamos um GerenciadorImpressao, ele automaticamente:
    // 1. Cria o objeto Database (conecta ao banco)
    // 2. Inicializa as estruturas de dados vazias
    // 3. Carrega os dados salvos anteriormente
    public GerenciadorImpressao() {
        this.db = new Database();                    // Conecta ao banco
        this.usuarios = new HashMap<>();             // Cria Map vazio
        this.historico = new ArrayList<>();          // Cria List vazia
        carregarDados();                             // Carrega dados do banco
    }

    // ===========================================
    // METODO PARA CARREGAR DADOS DO BANCO
    // ===========================================
    /**
     * Carrega todos os dados do banco de dados para a memoria
     * Isso torna o sistema mais rapido porque nao precisa acessar o banco toda hora
     */
    private void carregarDados() {
        this.usuarios = db.carregarUsuarios();    // Carrega todos os usuarios
        this.historico = db.carregarHistorico();  // Carrega todo o historico
        System.out.println("Dados carregados: " + usuarios.size() + " usuarios");
    }

    // ===========================================
    // METODOS PARA CONTROLAR O MODO DE IMPRESSAO
    // ===========================================
    /**
     * Ativa ou desativa a impressao real
     * @param ativa true = imprime de verdade, false = so simula
     */
    public void ativarImpressaoReal(boolean ativa) {
        this.impressaoRealAtiva = ativa;
        if (ativa) {
            System.out.println("IMPRESSAO REAL ATIVADA!");
            System.out.println("Certifique-se de ter uma impressora instalada!");
        } else {
            System.out.println("Modo SIMULACAO ativado (nao gasta papel)");
        }
    }

    /**
     * Verifica se a impressao real esta ativa
     * @return true se ativa, false se simulacao
     */
    public boolean isImpressaoRealAtiva() {
        return impressaoRealAtiva;
    }

    // ===========================================
    // METODOS PARA LISTAR E BUSCAR USUARIOS
    // ===========================================
    /**
     * Retorna a lista de todos os usuarios cadastrados
     * @return Lista de Usuario
     */
    public List<Usuario> getUsuarios() {
        // Retorna uma COPIA para nao permitir modificacao direta do Map original
        return new ArrayList<>(usuarios.values());
    }

    /**
     * Busca um usuario pela matricula
     * @param matricula Matricula do usuario
     * @return Objeto Usuario ou null se nao encontrado
     */
    public Usuario getUsuario(String matricula) {
        return usuarios.get(matricula);
    }

    // ===========================================
    // CRUD - CREATE (Cadastrar)
    // ===========================================
    /**
     * Cadastra um novo usuario no sistema
     *
     * Fluxo:
     * 1. Verifica se ja existe usuario com esta matricula
     * 2. Se nao existe, cria o objeto Usuario
     * 3. Adiciona ao Map (memoria)
     * 4. Salva no banco de dados
     *
     * @param nome Nome completo do usuario
     * @param matricula Matricula unica (nao pode repetir)
     * @param cotaMensal Quantidade maxima de paginas por mes
     * @return true se cadastrou, false se ja existia
     */
    public boolean cadastrarUsuario(String nome, String matricula, int cotaMensal) {
        // Verifica se ja existe
        if (db.usuarioExiste(matricula)) {
            System.out.println("Usuario ja cadastrado!");
            return false;
        }

        // Cria o novo usuario
        Usuario novo = new Usuario(nome, matricula, cotaMensal);

        // Adiciona ao Map (memoria)
        usuarios.put(matricula, novo);

        // Salva no banco de dados
        db.salvarUsuario(novo);

        System.out.println("Usuario " + nome + " cadastrado!");
        return true;
    }

    // ===========================================
    // CRUD - DELETE (Excluir)
    // ===========================================
    /**
     * Exclui um usuario do sistema
     *
     * @param matricula Matricula do usuario a ser excluido
     * @return true se excluiu, false se nao encontrou
     */
    public boolean excluirUsuario(String matricula) {
        // Verifica se o usuario existe
        if (!usuarios.containsKey(matricula)) {
            System.out.println("Usuario nao encontrado!");
            return false;
        }

        // Pega o usuario para mostrar o nome na mensagem
        Usuario u = usuarios.get(matricula);

        // Remove do Map (memoria)
        usuarios.remove(matricula);

        // Remove do banco de dados
        db.excluirUsuario(matricula);

        System.out.println("Usuario " + u.getNome() + " excluido!");
        return true;
    }

    // ===========================================
    // FUNCAO PRINCIPAL - IMPRIMIR
    // ===========================================
    /**
     * Metodo principal de impressao
     *
     * Fluxo completo:
     * 1. Verifica se usuario existe
     * 2. Verifica se tem cota disponivel
     * 3. Verifica se tem paginas suficientes
     * 4. Imprime (real ou simulacao)
     * 5. Se deu certo, desconta da cota
     * 6. Registra no historico
     *
     * @param caminhoArquivo Nome/caminho do documento
     * @param matricula Matricula do usuario
     * @param paginas Quantidade de paginas
     * @return true se imprimiu, false se houve erro
     */
    public boolean imprimir(String caminhoArquivo, String matricula, int paginas) {
        // PASSO 1: Buscar o usuario
        Usuario usuario = usuarios.get(matricula);

        // PASSO 2: Validar se usuario existe
        if (usuario == null) {
            System.out.println("Usuario nao encontrado!");
            return false;
        }

        // PASSO 3: Validar se tem cota disponivel (pelo menos 1 pagina)
        if (!usuario.temCotaDisponivel()) {
            System.out.println(usuario.getNome() + " - COTA ESGOTADA!");
            return false;
        }

        // PASSO 4: Validar se tem paginas suficientes para esta impressao
        if (!usuario.temCotaParaPaginas(paginas)) {
            System.out.printf("%s - Cota insuficiente! Precisa %d, restam %d%n",
                    usuario.getNome(), paginas, usuario.getImpressoesRestantes());
            return false;
        }

        // PASSO 5: REALIZAR A IMPRESSAO (real ou simulada)
        boolean impressaoOk = false;

        if (impressaoRealAtiva) {
            // ===== IMPRESSAO REAL =====
            impressaoOk = imprimirReal(caminhoArquivo);
        } else {
            // ===== SIMULACAO =====
            System.out.printf("[SIMULACAO] Imprimindo '%s' para %s (%d paginas)...%n",
                    caminhoArquivo, usuario.getNome(), paginas);

            try {
                Thread.sleep(1000);  // Pausa de 1 segundo para simular
                impressaoOk = true;
            } catch (InterruptedException e) {
                impressaoOk = false;
                System.err.println("Erro na simulacao: " + e.getMessage());
            }
        }

        // PASSO 6: Se impressao deu certo, desconta da cota e registra
        if (impressaoOk) {
            // Atualiza a memoria
            usuario.adicionarMultiplasImpressoes(paginas);

            // Atualiza o banco de dados
            db.atualizarImpressoes(matricula, paginas);

            // Cria e salva o registro no historico
            RegistroImpressao registro = new RegistroImpressao(
                    usuario.getNome(), caminhoArquivo, paginas, "SUCESSO");
            historico.add(registro);
            db.salvarHistorico(registro);

            System.out.printf("Impressao concluida! Restam: %d paginas%n",
                    usuario.getImpressoesRestantes());

            // Avisa se a cota acabou
            if (!usuario.temCotaDisponivel()) {
                System.out.println("ATENCAO: COTA ESGOTADA!");
            }

            return true;
        } else {
            // Se deu erro, registra falha no historico
            RegistroImpressao registro = new RegistroImpressao(
                    usuario.getNome(), caminhoArquivo, paginas, "ERRO_IMPRESSAO");
            historico.add(registro);
            db.salvarHistorico(registro);
            return false;
        }
    }

    /**
     * IMPRESSAO REAL - Envia o documento para a impressora fisica
     *
     * Este metodo e chamado automaticamente quando impressaoRealAtiva = true
     *
     * Fluxo:
     * 1. Verifica se o arquivo existe
     * 2. Verifica se pode ler o arquivo
     * 3. Pega a impressora padrao do Windows
     * 4. Envia o documento para impressao
     *
     * @param caminhoArquivo Caminho completo do arquivo (ex: C:/documentos/teste.pdf)
     * @return true se enviou para impressao, false se erro
     */
    private boolean imprimirReal(String caminhoArquivo) {
        try {
            // Verifica se o arquivo existe
            java.io.File arquivo = new java.io.File(caminhoArquivo);
            if (!arquivo.exists()) {
                System.out.println("Arquivo nao encontrado: " + caminhoArquivo);
                return false;
            }

            // Verifica se o arquivo pode ser lido
            if (!arquivo.canRead()) {
                System.out.println("Nao e possivel ler o arquivo!");
                return false;
            }

            // Pega a impressora padrao do sistema operacional
            javax.print.PrintService impressora =
                    javax.print.PrintServiceLookup.lookupDefaultPrintService();

            if (impressora == null) {
                System.out.println("Nenhuma impressora encontrada!");
                return false;
            }

            System.out.println("Enviando para impressora: " + impressora.getName());

            // Prepara o documento para impressao
            java.io.FileInputStream fluxo = new java.io.FileInputStream(caminhoArquivo);
            javax.print.DocFlavor formato = javax.print.DocFlavor.INPUT_STREAM.AUTOSENSE;
            javax.print.Doc documento = new javax.print.SimpleDoc(fluxo, formato, null);

            // Envia para a impressora
            javax.print.DocPrintJob job = impressora.createPrintJob();
            job.print(documento, null);

            System.out.println("Documento enviado para impressao!");
            return true;

        } catch (Exception e) {
            System.out.println("Erro na impressao real: " + e.getMessage());
            return false;
        }
    }

    // ===========================================
    // RELATORIOS E CONSULTAS
    // ===========================================

    /**
     * Exibe um relatorio formatado de todas as cotas
     * Mostra: nome, matricula, paginas usadas, paginas restantes
     */
    public void exibirRelatorioCotas() {
        System.out.println("\nRELATORIO DE COTAS");
        System.out.println("=".repeat(50));
        System.out.printf("%-20s %-10s %10s %10s%n", "NOME", "MATRICULA", "USADAS", "RESTANTES");
        System.out.println("-".repeat(50));

        for (Usuario u : usuarios.values()) {
            System.out.printf("%-20s %-10s %10d %10d%n",
                    u.getNome(), u.getMatricula(),
                    u.getImpressoesUsadas(), u.getImpressoesRestantes());
        }
        System.out.println("=".repeat(50));
    }

    /**
     * Consulta e exibe a cota de um usuario especifico
     * Inclui barra de progresso visual
     *
     * @param matricula Matricula do usuario
     */
    public void consultarCota(String matricula) {
        Usuario u = usuarios.get(matricula);

        if (u == null) {
            System.out.println("Usuario nao encontrado!");
            return;
        }

        System.out.println("\nSTATUS DE COTA");
        System.out.println("=".repeat(40));
        System.out.printf("Usuario: %s%n", u.getNome());
        System.out.printf("Matricula: %s%n", u.getMatricula());
        System.out.printf("Cota total: %d paginas%n", u.getCotaMensal());
        System.out.printf("Utilizadas: %d paginas%n", u.getImpressoesUsadas());
        System.out.printf("Restantes: %d paginas%n", u.getImpressoesRestantes());

        // Calcula o percentual usado
        int percentual = (u.getImpressoesUsadas() * 100) / u.getCotaMensal();

        // Barra de progresso visual (20 caracteres)
        System.out.print("Progresso: [");
        for (int i = 0; i < 20; i++) {
            if (i < percentual / 5) {
                System.out.print("#");  // Caractere preenchido
            } else {
                System.out.print(".");  // Caractere vazio
            }
        }
        System.out.printf("] %d%%%n", percentual);

        // Alerta se a cota acabou
        if (!u.temCotaDisponivel()) {
            System.out.println("COTA ESGOTADA!");
        } else if (u.getImpressoesRestantes() < 10) {
            System.out.println("Atencao: Cota esta no final!");
        }
        System.out.println("=".repeat(40));
    }

    /**
     * Gera e exibe relatorio de impressoes por periodo de datas
     *
     * @param inicio Data inicial (ex: 2024-03-01)
     * @param fim Data final (ex: 2024-03-31)
     */
    public void relatorioPeriodo(LocalDate inicio, LocalDate fim) {
        // Converte LocalDate para String no formato que o banco entende
        String inicioStr = inicio.toString();  // "2024-03-01"
        String fimStr = fim.toString();        // "2024-03-31"

        // Busca o relatorio no banco
        Map<String, Long> relatorio = db.getRelatorioPorPeriodo(inicioStr, fimStr);

        System.out.printf("\nRELATORIO DE IMPRESSOES (%s a %s)%n", inicio, fim);
        System.out.println("=".repeat(50));

        if (relatorio.isEmpty()) {
            System.out.println("Nenhuma impressao neste periodo.");
        } else {
            System.out.printf("%-25s %10s%n", "USUARIO", "PAGINAS");
            System.out.println("-".repeat(40));

            long total = 0;
            for (Map.Entry<String, Long> entry : relatorio.entrySet()) {
                System.out.printf("%-25s %10d%n", entry.getKey(), entry.getValue());
                total += entry.getValue();
            }
            System.out.println("-".repeat(40));
            System.out.printf("%-25s %10d%n", "TOTAL GERAL", total);
        }
        System.out.println("=".repeat(50));
    }

    /**
     * Exibe todo o historico de impressoes
     * Mostra: data, usuario, arquivo, paginas, status
     */
    public void exibirHistorico() {
        System.out.println("\nHISTORICO DE IMPRESSOES");
        System.out.println("=".repeat(80));

        if (historico.isEmpty()) {
            System.out.println("Nenhuma impressao realizada ainda.");
        } else {
            for (RegistroImpressao r : historico) {
                System.out.println(r);
            }
        }
        System.out.println("=".repeat(80));
    }

    /**
     * Reseta as cotas de TODOS os usuarios
     * Util no inicio de cada mes
     */
    public void resetarCotas() {
        System.out.println("\nResetando cotas de todos os usuarios...");

        // Reseta na memoria
        for (Usuario u : usuarios.values()) {
            u.resetarCota();
        }

        // Reseta no banco de dados
        db.resetarCotas();

        System.out.println("Cotas resetadas com sucesso!");
    }
}