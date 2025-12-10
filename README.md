# 🐮 Farm Management (Gestão Rural)

## 📝 Descrição do Sistema
O **Farm Management** é um aplicativo Android criado para digitalizar e otimizar a administração de fazendas de gado leiteiro.  
Ele substitui anotações manuais e descentralizadas, permitindo que **Gestores** e **Funcionários** registrem informações essenciais em tempo real.

O sistema centraliza:

- Controle do rebanho  
- Produção de leite  
- Abastecimento de maquinário  
- Gestão de folgas  
- Relatórios financeiros automáticos  

---

## 🚀 Funcionalidades Principais

### 🔐 Autenticação por Perfil
- Login seguro com perfis de **Gestor** e **Funcionário**.

### 🐄 Gestão de Animais
- Cadastro de nascimentos.
- Listagem filtrada (mês ou número do brinco).
- Edição de dados.

### 🥛 Controle de Produção Leiteira
- Registro diário da litragem por tanque.
- Cálculo total automático.

### ⛽ Controle de Diesel
- Registro de abastecimentos.
- Horímetro.
- Identificação de motorista e maquinário.

### 🧑‍🌾 Gestão de RH – Folgas
**Funcionário**  
- Solicitação de folga e visualização via calendário.

**Gestor**  
- Aprovação/Reprovação de solicitações pendentes.

### 📊 Relatórios Financeiros
- Faturamento mensal consolidado.  
- Média diária automática.  
- Preço do leite configurável.

---

## 🛠 Tecnologias Utilizadas

### 📱 Mobile (Android Nativo)
- **Kotlin**
- **MVVM**
- **XML + ViewBinding**: XML para construção de layouts e ViewBinding para manipulação segura de Views, eliminando o risco de `NullPointerException` comum no antigo `findViewById`.
- **Coroutines & Flow**: Coroutines evitam o travamento da UI durante chamadas de rede, e Flow permite observar mudanças no banco de dados em tempo real.

### ☁️ Back-end (Serverless)
- **Firebase**
- **Cloud Firestore**
- **Firebase Authentication**

### 🧰 Ferramentas
- **Gradle (Kotlin DSL)**: Sistema de build moderno configurado com scripts Kotlin (.kts) para melhor legibilidade.
- **Injeção manual (ViewModelFactory)**: Padrão utilizado para injetar dependências (como Repositories) nas ViewModels, garantindo testabilidade e separação de responsabilidades.

---

## 📂 Estrutura de Diretórios

```text
App-Gestao-fazenda/
├── build.gradle.kts           # Configurações globais do projeto (plugins, versões do Kotlin)
├── app/                       # Módulo principal da aplicação Android
│   ├── build.gradle.kts       # Declaração de dependências (Firebase, AndroidX, Material Design)
│   ├── google-services.json   # Arquivo de configuração (credenciais) para conexão com o Firebase
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml # Manifesto: define permissões, Activities e metadados do app
│       │   ├── java/com/example/farmmanagement/
│       │   │   ├── data/                  # CAMADA DE DADOS (Data Layer)
│       │   │   │   ├── model/             # Classes de Dados: representam os objetos (ex: Animal, ProducaoLeite)
│       │   │   │   ├── repository/        # Repositórios: centralizam a lógica de acesso a dados e decidem a fonte
│       │   │   │   └── source/            # Data Sources: realizam as chamadas diretas à API do Firestore/Auth
│       │   │   └── ui/                    # CAMADA DE INTERFACE (UI Layer)
│       │   │       ├── activity/          # Activities: representam as telas principais (ex: LoginActivity, PrincipalGestorActivity)
│       │   │       ├── adapter/           # Adapters: controlam as listas (RecyclerView) para exibição de dados (ex: AnimalAdapter)
│       │   │       ├── fragment/          # Fragments/Dialogs: componentes modulares de UI (ex: Dialog para reprovar folga)
│       │   │       └── viewmodel/         # ViewModels: gerenciam o estado da tela e comunicam com o repositório
│       │   └── res/
│       │       ├── layout/                # Arquivos XML que definem a estrutura visual das telas
│       │       ├── drawable/              # Recursos gráficos: ícones, vetores e backgrounds customizados
│       │       ├── values/                # Recursos de valores: strings (textos), cores e temas do app
│       │       └── mipmap/                # Ícones de lançamento do aplicativo (ícone da grade de apps)
│       └── test/                          # Testes unitários para validar a lógica de negócios localmente
##

🌿 Estrutura de Branches

- **main** → versão estável do app integrada com Firebase  
- **feature/*** → desenvolvimento de novas funcionalidades  
  - Exemplo:  
    - `feature/controle-vacinas`  
    - `feature/graficos`

> 💡 Atenção: O arquivo `google-services.json` deve estar na pasta `/app`.

---

## 🚀 Instruções de Execução

### ✔️ Pré-requisitos
- Android Studio **Ladybug** ou superior  
- JDK 11+  
- Conta no Firebase (opcional para novo ambiente)

---

### 1️⃣ Clonar o Repositório

```bash
git clone https://github.com/ricardoissadesousa/app-gestao-fazenda.git
cd App-Gestao-fazenda
2️⃣ Configurar o Firebase
O projeto já possui um google-services.json configurado para:

go
Copiar código
package: com.example.farmmanagement
Apenas garanta que o dispositivo/emulador tenha internet.

3️⃣ Executar o Projeto
Abrir o Android Studio

Ir em Open e selecionar a pasta do projeto

Aguardar o Gradle Sync

Selecionar o emulador ou dispositivo físico

Clicar em Run (▶️)
```
---

## 👥 Contribuições da Equipe
<table>
  <tr>
    <td align="center">
      <a href="https://github.com/ricardoissadesousa">
        <img src="https://github.com/ricardoissadesousa.png" width="100px;" alt="Foto do ricardo"/><br>
        <sub>
          <p>Ricardo</p>
          <b>Função: Back-End, Banco de Dados, Front-End</b>
        </sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/Marllonlp">
        <img src="https://avatars.githubusercontent.com/u/143544599?v=4" width="100px;" alt="Foto do marlon"/><br>
        <sub>
          <p>Marlon</p>
          <b>Função: Back-End, Banco de Dados, Front-End</b>
        </sub>
      </a>
    </td>
    
  </tr>

