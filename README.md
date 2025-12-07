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
- **XML + ViewBinding**
- **Coroutines & Flow**

### ☁️ Back-end (Serverless)
- **Firebase**
- **Cloud Firestore**
- **Firebase Authentication**

### 🧰 Ferramentas
- Gradle (Kotlin DSL)
- Injeção manual: ViewModelFactory

---

## 📂 Estrutura de Diretórios
```text
App-Gestao-fazenda/
├── build.gradle.kts           # Configurações do projeto raiz
├── app/                       # Módulo principal da aplicação
│   ├── build.gradle.kts       # Dependências (Firebase, AndroidX, etc.)
│   ├── google-services.json   # Configuração do Firebase
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/farmmanagement/
│       │   │   ├── data/                  # Camada de Dados
│       │   │   │   ├── model/             # Classes de Dados (Animal, Producao, Usuario)
│       │   │   │   ├── repository/        # Repositórios (Abstração de dados)
│       │   │   │   └── source/            # Data Sources (Chamadas ao Firestore)
│       │   │   └── ui/                    # Camada de Interface
│       │   │       ├── activity/          # Telas (Activities)
│       │   │       ├── adapter/           # Adaptadores para RecyclerViews
│       │   │       ├── fragment/          # Fragmentos e Dialogs
│       │   │       └── viewmodel/         # Gerenciamento de Estado (MVVM)
│       │   └── res/
│       │       ├── layout/                # Arquivos XML de UI
│       │       ├── drawable/              # Ícones e Backgrounds customizados
│       │       ├── values/                # Strings, Cores e Temas
│       │       └── mipmap/                # Ícones do App
│       └── test/                          # Testes Unitários
```

---

#---

## 🌿 Estrutura de Branches

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
