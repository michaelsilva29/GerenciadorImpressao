/**
 * CLASSE DATABASE
 *
 * Para que serve?
 * Esta classe e responsavel por TODA comunicacao com o banco de dados SQLite.
 *
 * O que ela faz?
 * 1. Conecta ao banco de dados
 * 2. Cria as tabelas se nao existirem
 * 3. Salva, consulta, atualiza e deleta dados
 *
 */

// Importacoes necessarias
import java.sql.*;              // Tudo para trabalhar com banco de dados
import java.time.LocalDateTime; // Para trabalhar com data/hora
import java.util.*;             // Para Map, List, HashMap, ArrayList

public class Database {

    // URL do SQLite: "jdbc:sqlite:" + nome do arquivo .db
    // Se o arquivo nao existir, o SQLite cria automaticamente
    private static final String URL = "jdbc:sqlite:impressoes.db";

    // Conexao com o banco - sera usada em todos os metodos
    private Connection conexao;

    // 1. Conecta ao banco
    // 2. Cria as tabelas (se nao existirem)
    public Database() {
        conectar();      // Chama o metodo que conecta
        criarTabelas();  // Chama o metodo que cria as tabelas
    }

    // ===========================================
    // METODO PARA CONECTAR AO BANCO
    // ===========================================
    private void conectar() {
        try {
            // Remove Class.forName - deixa o DriverManager encontrar automaticamente
            conexao = DriverManager.getConnection(URL);
            System.out.println("[OK] Banco de dados conectado!");

        } catch (SQLException e) {
            System.err.println("[ERRO] Ao conectar: " + e.getMessage());
            System.err.println("Verifique se os arquivos .jar estao na pasta");
        }
    }
    // ===========================================
    // METODO PARA CRIAR AS TABELAS
    // ===========================================
    private void criarTabelas() {
        // SQL para criar tabela de USUARIOS
        // IF NOT EXISTS = so cria se nao existir (evita erro)
        // TEXT = tipo texto (para palavras)
        // INTEGER = tipo numero inteiro
        // PRIMARY KEY = chave primaria (identificador unico, nao pode repetir)
        // DEFAULT 0 = se nao informar, comeca com 0
        String sqlUsuarios = """
            CREATE TABLE IF NOT EXISTS usuarios (
                matricula TEXT PRIMARY KEY,
                nome TEXT NOT NULL,
                cota_mensal INTEGER NOT NULL,
                impressoes_usadas INTEGER DEFAULT 0
            )
            """;

        // SQL para criar tabela de HISTORICO
        // AUTOINCREMENT = o ID e gerado automaticamente (1, 2, 3...)
        String sqlHistorico = """
            CREATE TABLE IF NOT EXISTS historico (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario TEXT NOT NULL,
                arquivo TEXT NOT NULL,
                paginas INTEGER NOT NULL,
                data_hora TEXT NOT NULL,
                status TEXT NOT NULL
            )
            """;

        // try-with-resources: fecha automaticamente os recursos ao final
        try (PreparedStatement pstmtUsuarios = conexao.prepareStatement(sqlUsuarios);
             PreparedStatement pstmtHistorico = conexao.prepareStatement(sqlHistorico)) {

            // execute() executa o comando SQL (para CREATE, INSERT, UPDATE, DELETE)
            pstmtUsuarios.execute();    // Cria tabela de usuarios
            pstmtHistorico.execute();   // Cria tabela de historico

            System.out.println("[OK] Tabelas criadas/verificadas!");

        } catch (SQLException e) {
            System.err.println("[ERRO] Ao criar tabelas: " + e.getMessage());
        }
    }

    // ===========================================
    // METODOS PARA USUARIOS (CRUD)
    // ===========================================

    /**
     * SALVAR USUARIO (CREATE)
     *
     * O que faz: Insere um novo usuario no banco ou substitui se ja existir
     * INSERT OR REPLACE = se a matricula ja existe, substitui; se nao, insere
     *
     * @param usuario Objeto Usuario com todos os dados preenchidos
     */
    public void salvarUsuario(Usuario usuario) {
        // SQL com 4 placeholders (?) que serao preenchidos depois
        String sql = "INSERT OR REPLACE INTO usuarios (matricula, nome, cota_mensal, impressoes_usadas) VALUES (?, ?, ?, ?)";

        // try-with-resources: o PreparedStatement e fechado automaticamente
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            // Preenche os placeholders (?) com os valores do usuario
            // O numero indica a posicao do placeholder (1 = primeiro ?)
            pstmt.setString(1, usuario.getMatricula());  // 1? = matricula
            pstmt.setString(2, usuario.getNome());       // 2? = nome
            pstmt.setInt(3, usuario.getCotaMensal());    // 3? = cota_mensal
            pstmt.setInt(4, usuario.getImpressoesUsadas()); // 4? = impressoes_usadas

            // executeUpdate() executa INSERT, UPDATE, DELETE
            // Retorna o numero de linhas afetadas
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[ERRO] Ao salvar usuario: " + e.getMessage());
        }
    }

    /**
     * CARREGAR USUARIOS (READ - todos)
     *
     * O que faz: Busca todos os usuarios do banco e retorna como um Map
     * Map: estrutura que associa uma chave (matricula) a um valor (usuario)
     *
     * @return Map onde a chave e a matricula e o valor e o objeto Usuario
     */
    public Map<String, Usuario> carregarUsuarios() {
        Map<String, Usuario> usuarios = new HashMap<>();
        String sql = "SELECT * FROM usuarios ORDER BY nome";

        // try-with-resources: PreparedStatement e ResultSet sao fechados automaticamente
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // ResultSet e como uma tabela de resultados
            // rs.next() avanca para a proxima linha e retorna false quando acaba
            while (rs.next()) {
                // Pega os valores de cada coluna pelo nome da coluna
                String matricula = rs.getString("matricula");
                String nome = rs.getString("nome");
                int cota = rs.getInt("cota_mensal");
                int usadas = rs.getInt("impressoes_usadas");

                // Cria o objeto Usuario
                Usuario u = new Usuario(nome, matricula, cota);

                // Adiciona as paginas ja usadas (para o contador ficar correto)
                for (int i = 0; i < usadas; i++) {
                    u.adicionarImpressao();
                }

                // Guarda no Map (matricula e a chave para encontrar depois)
                usuarios.put(matricula, u);
            }

        } catch (SQLException e) {
            System.err.println("[ERRO] Ao carregar usuarios: " + e.getMessage());
        }

        return usuarios;
    }

    /**
     * VERIFICAR SE USUARIO EXISTE (READ - especifico)
     *
     * O que faz: Verifica se existe um usuario com a matricula informada
     *
     * @param matricula Matricula a ser verificada
     * @return true se existe, false se nao existe
     */
    public boolean usuarioExiste(String matricula) {
        String sql = "SELECT 1 FROM usuarios WHERE matricula = ?";

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, matricula);
            ResultSet rs = pstmt.executeQuery();

            // Se encontrou alguma linha (rs.next() retorna true), existe
            return rs.next();

        } catch (SQLException e) {
            System.err.println("[ERRO] Ao verificar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * EXCLUIR USUARIO (DELETE)
     *
     * O que faz: Remove um usuario do banco de dados pela matricula
     *
     * @param matricula Matricula do usuario a ser removido
     */
    public void excluirUsuario(String matricula) {
        String sql = "DELETE FROM usuarios WHERE matricula = ?";

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, matricula);
            int linhasAfetadas = pstmt.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("[OK] Usuario removido do banco!");
            } else {
                System.out.println("[AVISO] Nenhum usuario encontrado com esta matricula");
            }

        } catch (SQLException e) {
            System.err.println("[ERRO] Ao excluir usuario: " + e.getMessage());
        }
    }

    /**
     * ATUALIZAR IMPRESSOES USADAS (UPDATE)
     *
     * O que faz: Soma as paginas impressas ao contador do usuario
     * Exemplo: se tinha 5 usadas e imprimiu 3, vai ficar 8
     *
     * @param matricula Matricula do usuario
     * @param paginas Quantidade de paginas a adicionar
     */
    public void atualizarImpressoes(String matricula, int paginas) {
        String sql = "UPDATE usuarios SET impressoes_usadas = impressoes_usadas + ? WHERE matricula = ?";

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, paginas);      // 1? = paginas a adicionar
            pstmt.setString(2, matricula); // 2? = matricula
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[ERRO] Ao atualizar impressoes: " + e.getMessage());
        }
    }

    /**
     * RESETAR COTAS (UPDATE em massa)
     *
     * O que faz: Zera o contador de impressoes de TODOS os usuarios
     * Usado no inicio de cada mes
     */
    public void resetarCotas() {
        String sql = "UPDATE usuarios SET impressoes_usadas = 0";

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            int linhasAfetadas = pstmt.executeUpdate();
            System.out.println("[OK] Cotas resetadas para " + linhasAfetadas + " usuarios!");

        } catch (SQLException e) {
            System.err.println("[ERRO] Ao resetar cotas: " + e.getMessage());
        }
    }

    // ===========================================
    // METODOS PARA HISTORICO
    // ===========================================

    /**
     * SALVAR HISTORICO (CREATE)
     *
     * O que faz: Registra uma tentativa de impressao no historico
     * Cada registro e uma linha na tabela historico
     *
     * @param registro Objeto RegistroImpressao com os dados da tentativa
     */
    public void salvarHistorico(RegistroImpressao registro) {
        String sql = "INSERT INTO historico (usuario, arquivo, paginas, data_hora, status) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, registro.getUsuario());
            pstmt.setString(2, registro.getArquivo());
            pstmt.setInt(3, registro.getPaginas());
            pstmt.setString(4, registro.getDataHora().toString()); // Converte data para texto
            pstmt.setString(5, registro.getStatus());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[ERRO] Ao salvar historico: " + e.getMessage());
        }
    }

    /**
     * CARREGAR HISTORICO (READ)
     *
     * O que faz: Busca todas as tentativas de impressao do banco
     * ORDER BY data_hora DESC = do mais recente para o mais antigo
     *
     * @return Lista com todos os registros de impressao
     */
    public List<RegistroImpressao> carregarHistorico() {
        List<RegistroImpressao> historico = new ArrayList<>();
        String sql = "SELECT * FROM historico ORDER BY data_hora DESC";

        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Pega os dados de cada coluna
                String usuario = rs.getString("usuario");
                String arquivo = rs.getString("arquivo");
                int paginas = rs.getInt("paginas");
                String status = rs.getString("status");
                String dataStr = rs.getString("data_hora");

                // Converte texto para LocalDateTime
                LocalDateTime data = LocalDateTime.parse(dataStr);

                // Cria o registro e ajusta a data (porque o construtor usaria a data atual)
                RegistroImpressao r = new RegistroImpressao(usuario, arquivo, paginas, status);
                r.setDataHora(data);  // Substitui pela data que veio do banco
                historico.add(r);
            }

        } catch (SQLException e) {
            System.err.println("[ERRO] Ao carregar historico: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERRO] Ao processar dados do historico: " + e.getMessage());
        }

        return historico;
    }

    /**
     * RELATORIO POR PERIODO (READ com filtro)
     *
     * O que faz: Calcula quantas paginas cada usuario imprimiu em um periodo
     *
     * @param inicio Data inicial no formato "yyyy-MM-dd" (ex: "2024-03-01")
     * @param fim Data final no formato "yyyy-MM-dd" (ex: "2024-03-31")
     * @return Map onde a chave e o nome do usuario e o valor e o total de paginas
     */
    public Map<String, Long> getRelatorioPorPeriodo(String inicio, String fim) {
        Map<String, Long> relatorio = new LinkedHashMap<>(); // LinkedHashMap mantem ordem
        String sql = """
            SELECT usuario, SUM(paginas) as total 
            FROM historico 
            WHERE status = 'SUCESSO' 
            AND date(data_hora) BETWEEN ? AND ?
            GROUP BY usuario
            ORDER BY total DESC
            """;

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, inicio);
            pstmt.setString(2, fim);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String usuario = rs.getString("usuario");
                long total = rs.getLong("total");
                relatorio.put(usuario, total);
            }

        } catch (SQLException e) {
            System.err.println("[ERRO] No relatorio: " + e.getMessage());
        }

        return relatorio;
    }

    /**
     * FECHAR CONEXAO
     *
     * O que faz: Fecha a conexao com o banco de dados
     * IMPORTANTE: Deve ser chamado quando o programa terminar
     */
    public void fechar() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
                System.out.println("[OK] Conexao com banco fechada!");
            }
        } catch (SQLException e) {
            System.err.println("[ERRO] Ao fechar conexao: " + e.getMessage());
        }
    }
}