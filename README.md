# VersoLivros 📚✨

Plataforma Android moderna para **versionamento de livros**, leitura beta e feedback da comunidade antes do lançamento oficial.

---

## 📥 Downloads (Versão para Testes)

Você pode baixar o arquivo `.apk` pronto para instalar no seu celular Android de duas formas:

1. **Pela aba de Releases do GitHub**:
   - Vá na seção [Releases](https://github.com) deste repositório.
   - Faça o download do arquivo `app-debug.apk` ou `VersoLivros-Debug-APK`.
2. **Pela aba Actions (Artifacts)**:
   - Acesse a aba **Actions** no topo deste repositório, clique na execução mais recente e baixe o artefato gerado `VersoLivros-Debug-APK`.

---

## 🚀 Principais Funcionalidades

- **Upload de Capítulos**: Importação de arquivos `.txt` e `.md` diretamente do aparelho, preenchendo o texto e calculando palavras automaticamente.
- **Versionamento & Changelog**: Crie versões do seu livro (ex.: `v1.0-alpha`, `v1.2-beta`, `v2.0-rc`) com notas detalhadas das mudanças e duplicação contínua de capítulos.
- **Múltiplas Categorias & Busca**: Selecione múltiplas categorias para cada obra, crie tags personalizadas e use a barra de pesquisa rápida por nome, autor ou gênero.
- **Comentários Diretos no Capítulo**: Leitores beta podem comentar o capítulo geral ou selecionar parágrafos específicos para comentários inline (*Sugestão*, *Gramática*, *Elogio*, *Ritmo*).
- **Leitor com Conforto Visual**:
  - Modos de leitura: **Claro**, **Sépia**, **Noturno (Dark)** e **OLED**.
  - Customização de fontes (*Serifada*, *Sem Serifa*, *Monospace*), tamanho (13sp a 28sp), espaçamento de linhas e alinhamento de texto.
- **Termômetro da Comunidade**: Avaliações de 1 a 5 estrelas, aprovação pré-lançamento e análise de ritmo e personagens.

---

## 🛠️ Tecnologias Utilizadas

- **Kotlin** & **Jetpack Compose** (Material Design 3)
- **Room Database** (Persistência local e modo offline)
- **Coroutines & Flow** para reatividade
- **GitHub Actions** para build e automação de releases
