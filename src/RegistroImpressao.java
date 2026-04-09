import java.time.LocalDateTime; // Para trabalhar com data e hora
import java.time.format.DateTimeFormatter; // Para formatar data como texto

public class RegistroImpressao {

    private String usuario; // Quem imprimiu
    private String arquivo; // Nome do arquivo/documento
    private int paginas; // Quantas paginas foram impressas
    private LocalDateTime dataHora; // Data e hora exata da impressao
    private String status; // O que aconteceu (SUCESSO, ERRO, etc.)

    // LocalDateTime.now() pega a data e hora atual do computador
    public RegistroImpressao(String usuario, String arquivo, int paginas, String status){
        this.usuario = usuario; // Guarda quem imprimiu
        this.arquivo = arquivo; // Guarda o nome do arquivo
        this.paginas = paginas; // Guarda a quantidade de paginas
        this.dataHora = LocalDateTime.now(); // Guarda a data/hora AGORA
        this.status = status; // Guarda o status
    }

    // ===========================================
    // GETTERS - Metodos para LER os valores
    // ===========================================

    public String getUsuario(){
        return usuario;
    }

    public String getArquivo(){
        return arquivo;
    }

    public int getPaginas(){
        return paginas;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getStatus(){
        return status;
    }

    // ===========================================
    // SETTERS - Metodos para ALTERAR valores
    // ===========================================

    public void setUsuario(String usuario){
        this.usuario = usuario;
    }

    public void setArquivo(String arquivo){
        this.arquivo = arquivo;
    }

    public void setPaginas(int paginas){
        this.paginas = paginas;
    }

    public void setDataHora(LocalDateTime dataHora){
        this.dataHora = dataHora;
    }

    public void setStatus(String status){
        this.status = status;
    }

    // ===========================================
    // METODO PARA FORMATAR A DATA
    // ===========================================
    // Retorna a data como texto no formato brasileiro
    // Exemplo: "15/03/2024 14:30:25"
    public String getDataFormatada(){
        // dd = dia (2 digitos)
        // MM = mes (2 digitos)
        // yyyy = ano (4 digitos)
        // HH = hora (24h)
        // mm = minuto
        // ss = segundo
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dataHora.format(fmt);  // Aplica o formato a data
    }

    // ===========================================
    // METODO toString - Representacao textual
    // ===========================================

    @Override
    public String toString(){
        return String.format("%s | %s | %s | %d paginas | %s",
                getDataFormatada(), usuario, arquivo, paginas, status);
    }
}