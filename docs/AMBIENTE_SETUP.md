# Setup do ambiente de build — Triscal Apontamento de Horas

> Os bloqueios de npm/apt observados durante o desenvolvimento eram do **sandbox** usado para
> gerar o código. **Na sua máquina não há esse bloqueio** — basta ter as ferramentas (ou usar
> Docker, que dispensa instalá-las).

## Opção A — Docker (recomendada, sem instalar toolchain)
Pré-requisito único: **Docker Desktop** (Windows/macOS) ou Docker Engine (Linux).

```bash
# 1) Infra de apoio (PostgreSQL + Keycloak)
cd infra && docker compose up -d

# 2) Backend — build e testes dentro de um contêiner (JDK 21 + Maven já inclusos na imagem)
cd ../backend
docker run --rm -v "${PWD}:/app" -w /app maven:3.9-eclipse-temurin-21 mvn clean verify

# 3) Frontend — type-check e build dentro de um contêiner (Node 20)
cd ../frontend
docker run --rm -v "${PWD}:/app" -w /app node:20 sh -lc "npm install && npm run build"
```
No Windows (PowerShell), troque `${PWD}` por `${PWD}` (PowerShell) ou `%cd%` (cmd).
Para empacotar as imagens da aplicação: `docker build -t triscal-api ./backend` e
`docker build -t triscal-web ./frontend`.

## Opção B — Instalar as ferramentas na máquina (Windows)
JDK 21, Maven e Node. Via **winget** (PowerShell como admin):
```powershell
winget install EclipseAdoptium.Temurin.21.JDK
winget install Apache.Maven
winget install OpenJS.NodeJS.LTS
```
Alternativas: **Chocolatey** (`choco install temurin21 maven nodejs-lts`) ou os instaladores
oficiais (Adoptium/Temurin, maven.apache.org, nodejs.org). Depois, **reabra o terminal** e
configure `JAVA_HOME` apontando para o JDK 21 (o instalador winget normalmente já ajusta o PATH).

Verifique:
```powershell
java -version   # deve mostrar 21
mvn -v          # deve usar o JDK 21
node -v         # 20+
```

Rodar:
```powershell
cd infra; docker compose up -d
cd ..\backend; mvn clean verify
cd ..\frontend; npm install; npm run build
```

## Sobre o "npm bloqueado"
Não há nada a desbloquear na sua máquina — o npm acessa o registro público normalmente.
O bloqueio só ocorre no ambiente sandbox de geração de código (política de rede do Cowork),
por isso o build foi verificado de forma estática aqui e deve ser executado no seu ambiente.

## Linux (alternativa à winget)
```bash
sudo apt-get update && sudo apt-get install -y maven   # pode trazer JDK; garanta o 21:
# JDK 21 via Adoptium: https://adoptium.net  (ou: sdkman -> sdk install java 21-tem)
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash - && sudo apt-get install -y nodejs
```
