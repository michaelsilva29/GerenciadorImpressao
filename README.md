# 🖨️ Sistema de Gerenciamento de Impressões

## 📌 Sobre o Projeto

Este sistema foi desenvolvido para **solucionar um problema real** no laboratório onde trabalho. O objetivo é controlar e gerenciar as impressões realizadas pelos usuários, evitando desperdício de papel e tinta, além de permitir um controle justo do uso dos recursos.

### Problema Identificado
No laboratório, não havia um controle efetivo sobre quantas páginas cada usuário imprimia, resultando em:
- Desperdício excessivo de papel e tinta
- Dificuldade em identificar quem mais utilizava os recursos
- Impossibilidade de estabelecer limites justos por usuário

### Solução Implementada
O sistema permite:
- ✅ Cadastro de usuários com cotas mensais personalizadas
- ✅ Controle de páginas impressas por usuário
- ✅ Bloqueio automático quando a cota é excedida
- ✅ Histórico completo de todas as impressões
- ✅ Relatórios por período para análise de uso
- ✅ Persistência dos dados em banco de dados SQLite

## 🚀 Funcionalidades

| Funcionalidade | Descrição |
|----------------|-----------|
| 👤 Cadastro de Usuários | Adiciona novos usuários com nome, matrícula e cota mensal |
| 📋 Listar Usuários | Exibe todos os usuários cadastrados com suas cotas |
| 🔍 Consultar Cota | Mostra a situação atual da cota de um usuário específico |
| 🖨️ Simular Impressão | Realiza a impressão e desconta da cota do usuário |
| 📊 Relatório de Cotas | Exibe todas as cotas em formato de tabela |
| 📈 Relatório por Período | Mostra quantas páginas cada usuário imprimiu em um período |
| 📜 Histórico Completo | Lista todas as tentativas de impressão (sucessos e falhas) |
| 🔄 Resetar Cotas | Zera as cotas de todos os usuários (início do mês) |
| 🗑️ Excluir Usuário | Remove um usuário do sistema |

## 🛠️ Tecnologias Utilizadas

- **Java 17** - Linguagem de programação
- **SQLite** - Banco de dados embutido
- **JDBC** - Conexão com banco de dados
- **Git** - Controle de versão

## 📋 Pré-requisitos

Para executar o sistema, você precisa ter instalado:

- **Java 17** ou superior
  - Download: [https://www.java.com/download/](https://www.java.com/download/)
- **Bibliotecas** (incluídas na pasta do projeto)
  - `sqlite-jdbc-3.44.1.0.jar` - Driver do SQLite
  - `slf4j-simple-2.0.9.jar` - Logging
    
**📁 Estrutura do Projeto**

GerenciadorImpressao/
├── src/
│   ├── Database.java           # Conexão e operações com banco de dados
│   ├── GerenciadorImpressao.java # Lógica principal do sistema
│   ├── Main.java               # Menu e interface com usuário
│   ├── RegistroImpressao.java  # Modelo de registro de impressões
│   └── Usuario.java            # Modelo de usuário
├── .gitignore                  # Arquivos ignorados pelo Git
├── README.md                   # Documentação do projeto
├── sqlite-jdbc-3.44.1.0.jar    # Driver do SQLite
└── slf4j-simple-2.0.9.jar      # Biblioteca de logging

**💾 Persistência dos Dados**

Os dados são salvos automaticamente no arquivo impressoes.db (banco SQLite), que é criado na primeira execução. Este arquivo contém:
Tabela usuarios - Dados cadastrais e cotas
Tabela historico - Registro de todas as impressões

**👥 Autores**
Desenvolvido por Michael Silva - Projeto para controle de impressões no laboratório

**📅 Status do Projeto**
✅ Concluído - Sistema em produção no laboratório
