# Triscal — Sistema de Apontamento de Horas

Repositório do projeto de **modernização do sistema de apontamento de horas (timesheet)** da
Triscal. Reúne o sistema legado (para referência e migração), a análise das regras de negócio
e o prompt de geração do novo sistema.

## Objetivo

Substituir o sistema legado (JSP/Java + Oracle) por uma aplicação **moderna, responsiva
(mobile-first) e segura**, hospedada no **Microsoft Azure**, com **SSO via Microsoft Entra ID**,
preservando o histórico de dados e adicionando novos recursos (lançamento por dia, alertas e
visões gerenciais).

## Estrutura do repositório

| Caminho | Descrição |
|---------|-----------|
| `Sistema Legado/` | Código-fonte do sistema atual (JSP/Java JDK 11) e o DDL do banco Oracle (schema `TIMESHEET`), mantidos como referência para extração de regras e migração de dados. |
| `Analise_Sistema_Legado_e_Recomendacoes.docx` | Documento de análise: visão do legado, modelo de dados, regras de negócio extraídas e recomendações de tecnologia. |
| `PROMPT_GERACAO_NOVO_SISTEMA.md` | Prompt autocontido para geração do código do novo sistema (stack, arquitetura, modelo de dados, regras e telas). |

## Stack recomendada para o novo sistema

- **Backend:** ASP.NET Core 8 (C#), Web API REST, Clean Architecture + CQRS
- **Banco:** PostgreSQL (Azure Database for PostgreSQL) via Entity Framework Core 8 (Npgsql)
- **Frontend:** React 18 + TypeScript + Vite (PWA, mobile-first)
- **Autenticação:** Microsoft Entra ID (OpenID Connect / MSAL)
- **Infra:** Azure App Service / Container Apps, Key Vault, Application Insights

## Principais regras de negócio (resumo)

- Jornada padrão de **8 horas/dia**; excedente vira **hora extra** ou **BHE** (banco de horas).
- **Status do dia** (`ST_TIMESHEET_DIA`): `A` aberto, `F` fechado pelo funcionário, `P` aprovado,
  `R` rejeitado. Cada funcionário tem um aprovador; gestor não aprova o próprio log.
- **BHE:** PJ (`TP='E'`) só gera extra 50%; CLT (`'F'`) gera 100% em domingo/feriado, 50% nos demais.
- **Tarefas externas** (integração tipo OTRS): exibidas no log, porém somente leitura.
- **Quinzenas** de fechamento (legado: 11–25 e 26–10) e **calendário de feriados** (nacional/local).

Detalhes completos no documento de análise e no prompt de geração.

## Histórico (Git)

Projeto versionado com Git. Branch principal: `main`.

```bash
git status
git log --oneline
git push -u origin main   # primeiro push para o GitHub
```

> Observação: mantenha este repositório **fora de pastas sincronizadas pelo OneDrive**, pois a
> sincronização pode corromper os arquivos internos do Git (`.git/`).

---

_Documentação gerada a partir da análise do sistema legado — Triscal, 2026._
