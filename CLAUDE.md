# CLAUDE.md — Projeto Triscal: Apontamento de Horas

Contexto e instruções padrão para qualquer sessão de IA neste repositório. Leia antes de
gerar, revisar ou documentar código.

## Objetivo do projeto

Substituir o sistema legado de apontamento de horas (timesheet) da Triscal — JSP/Java sobre
Oracle (schema `TIMESHEET`) — por um sistema **moderno, responsivo (mobile-first), seguro e
sem dependência de fornecedor (open-source)**, hospedado no Azure, com SSO via Microsoft
Entra ID. O histórico de dados deve ser migrado preservando a semântica do legado.

## Estrutura do repositório

| Caminho | Conteúdo |
|---------|----------|
| `Sistema Legado/` | Código JSP/Java + DDL Oracle do sistema atual (referência e migração). Não alterar. |
| `PROMPT_GERACAO_NOVO_SISTEMA.md` | Especificação autocontida para geração do novo sistema (stack, arquitetura, regras, telas, entregáveis). Fonte da verdade para gerar código. |
| `Analise_Sistema_Legado_e_Recomendacoes.docx` | Análise do legado, validação de arquitetura e recomendações de tecnologia. |
| `.claude/skills/triscal-apontamento-horas/` | Skill com as regras de negócio do apontamento (BHE, status, quinzena, etc.). |
| `README.md` | Visão geral do projeto. |

## Regras de negócio

As regras de negócio estão na skill **`triscal-apontamento-horas`** (em `.claude/skills/`).
**Sempre siga essa skill** ao implementar/revisar qualquer funcionalidade do timesheet
(jornada de 8h, BHE, status do dia, aprovação, feriados, quinzena, % de andamento,
fechamento quinzenal). Em conflito entre o legado e a skill, a **skill prevalece**.

Pontos críticos a nunca violar:
- BHE: PJ (`TP='E'`) só gera extra 50%; CLT (`'F'`) gera 100% em domingo/feriado, senão 50%;
  apenas quem tem `IN_POSSUI_BANCO_HORAS='S'`. Lógica na camada de aplicação, com testes.
- Status do dia `A → F → (P | R)`; gestor nunca aprova o próprio log.
- Tarefas externas (`IN_TAREFA_EXTERNA='S'`) são somente leitura no log.
- Limite de 8h/dia (excedente = extra ou BHE) e máximo de 24h/dia.
- Mínimo de 0,5h (30 min) por lançamento de tarefa, em múltiplos de 0,5h.
- Entra ID = só autenticação; papéis/hierarquia são 100% internos (perfil, funcionario_perfil, func_cd_func_aprovador).

## Stack-alvo (open-source, sem lock-in)

- **Backend:** Java 21 + Spring Boot 3 (camadas pragmáticas: controller → service → repository).
- **Banco:** PostgreSQL 16 + Flyway (JPA/Hibernate).
- **Frontend:** React 18 + TypeScript + Vite (PWA, mobile-first).
- **Identidade:** Keycloak federando o Entra ID via OIDC, **apenas para autenticação**. Permissionamento e hierarquia (papéis, aprovador, gestor de projeto) são controlados **na aplicação**, em tabelas internas — o token não carrega papéis.
- **Infra (simplificada p/ ~100 usuários):** contêiner Docker em Azure App Service/Container Apps (instância única, sem Kubernetes); jobs com Spring Scheduler in-process (sem broker); Actuator + logs (Prometheus/Grafana opcionais). Relatórios: OpenPDF/JasperReports + Apache POI.
- **CI/CD:** GitHub Actions.

O Azure é apenas hospedagem e o Entra ID o IdP corporativo; a aplicação não deve depender de
bibliotecas/serviços proprietários.

## Padrões de engenharia

- Segurança OWASP ASVS/Top 10; autorização **por papel e por dado**; queries parametrizadas;
  validação server-side; segredos em cofre (Vault/Secrets); LGPD para dados pessoais.
- **Sempre** acompanhar código novo de: classes de teste (unit + integração, cobertura ≥ 80%
  no núcleo de negócio), atualização da documentação técnica e do manual do usuário.
- Idioma padrão **pt-BR**. Commits descritivos.

## Convenções de Git

- Branch principal: `main`. Manter o repositório **fora do OneDrive** (sincronização corrompe
  o `.git/`). Remoto: `origin` (GitHub).
