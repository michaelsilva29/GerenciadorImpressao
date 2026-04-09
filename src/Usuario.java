public class Usuario {

    private String nome; // Guarda o nome do usuario
    private String matricula; // Guarda a matricula
    private int cotaMensal; // Guarda quantas paginas pode imprimir por mes
    private int impressoesUsadas; // Guarda quantas paginas ja usou este mes

    // ===========================================
    // CONSTRUTOR
    // ===========================================
    // O construtor e chamado quando criamos um novo Usuario
    // Exemplo: Usuario u = new Usuario("Joao", "2024001", 50);

    public Usuario(String nome, String matricula, int cotaMensal) {
        this.nome = nome;               // Guarda o nome recebido
        this.matricula = matricula;     // Guarda a matricula recebida
        this.cotaMensal = cotaMensal;   // Guarda a cota recebida
        this.impressoesUsadas = 0;      // Comeca com zero paginas usadas
    }

    // ===========================================
    // GETTERS - Metodos para LER os valores
    // ===========================================

    public String getNome() {
        return nome;    // Devolve o nome guardado
    }

    public String getMatricula() {
        return matricula;   // Devolve a matricula guardada
    }

    public int getCotaMensal() {
        return cotaMensal;  // Devolve a cota total
    }

    public int getImpressoesUsadas() {
        return impressoesUsadas;    // Devolve quantas paginas ja usou
    }

    // ===========================================
    // SETTERS - Metodos para ALTERAR valores
    // ===========================================

    // Altera o nome guardado
    public void setNome(String nome) {
        this.nome = nome;
    }

    // Altera a matricula guardada
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    // Altera a cota mensal
    public void setCotaMensal(int cotaMensal) {
        this.cotaMensal = cotaMensal;
    }

    // Altera o numero de paginas usadas
    // CORRIGIDO: o nome do parametro estava errado
    public void setImpressoesUsadas(int impressoesUsadas) {
        this.impressoesUsadas = impressoesUsadas;
    }

    // ===========================================
    // METODOS DE CALCULO
    // ===========================================

    // Calcula quantas paginas ainda podem ser impressas
    // Formula: Total - Usadas = Restantes
    // Exemplo: 50 - 5 = 45 paginas restantes
    public int getImpressoesRestantes() {
        return cotaMensal - impressoesUsadas;
    }

    // Adiciona UMA pagina as paginas usadas
    public void adicionarImpressao() {
        this.impressoesUsadas++;
    }

    // Adiciona MULTIPLAS paginas de uma vez
    public void adicionarMultiplasImpressoes(int paginas) {
        this.impressoesUsadas += paginas;
    }

    // Verifica se ainda tem cota disponivel (pelo menos 1 pagina)
    // Retorna 'true' se pode imprimir, 'false' se nao pode
    public boolean temCotaDisponivel() {
        return impressoesUsadas < cotaMensal;
    }

    // Verifica se tem paginas suficientes para uma impressao especifica
    // Exemplo: se ja usou 45 e quer imprimir 10, total seria 55 > 50 -> false
    public boolean temCotaParaPaginas(int paginas) {
        return (impressoesUsadas + paginas) <= cotaMensal;
    }

    // Zera o contador de paginas usadas (chamado no inicio de cada mes)
    public void resetarCota() {
        this.impressoesUsadas = 0;
    }

    // ===========================================
    // METODO toString - Representacao textual
    // ===========================================

    @Override
    public String toString() {
        return String.format("Nome: %s | Matricula: %s | Cota: %d/%d",
                nome, matricula, impressoesUsadas, cotaMensal);
    }
}
