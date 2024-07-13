package br.com.alura.literalura.principal;

import br.com.alura.literalura.model.Autor;
import br.com.alura.literalura.model.DadosAutor;
import br.com.alura.literalura.model.DadosLivro;
import br.com.alura.literalura.model.Livro;
import br.com.alura.literalura.repository.AutorRepository;
import br.com.alura.literalura.repository.LivroRepository;
import br.com.alura.literalura.service.ConsumoApi;
import br.com.alura.literalura.service.ConverteDados;

import java.util.List;
import java.util.Scanner;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private LivroRepository livroRepository;
    private AutorRepository autorRepository;
    private final String ENDERECO = "https://gutendex.com/books?search=";

    public Principal(LivroRepository livroRepository, AutorRepository autorRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    public void exibeMenu() {
        var opcao = -1;

        while(opcao != 0) {
            var menu = """
                    \n**** Bem-vindo(a) ao LiterAlura! ****
                    
                    Escolha o número de sua opção:
                    
                    1 - Buscar livro pelo título
                    2 - Buscar autor pelo nome
                    3 - Listar livros de um autor
                    4 - Listar livros registrados
                    5 - Listar autores registrados
                    6 - Listar autores vivos em um determinado ano
                    7 - Listar livros em um determidado idioma
                    8 - Top 10 livros mais baixados
                    
                    0 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarLivroPeloTitulo();
                    break;
                case 2:
                    buscarAutorPeloNome();
                    break;
                case 3:
                    listarLivrosDeUmAutor();
                    break;
                case 4:
                    listarLivrosRegistrados();
                    break;
                case 5:
                    listarAutoresRegistrados();
                    break;
                case 6:
                    listarAutoresVivosPorAno();
                    break;
                case 7:
                    listarLivrosPorIdioma();
                    break;
                case 8:
                    buscarTop10Livros();
                    break;
                case 0:
                    System.out.println("Encerrando a aplicação!");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private void salvar(String json) {
        try {
            DadosLivro dadosLivro = conversor.obterDados(json, DadosLivro.class);
            DadosAutor dadosAutor = conversor.obterDados(json, DadosAutor.class);

            Autor autor = new Autor(dadosAutor);
            if (!autorRepository.existsByNome(autor.getNome())) {
                autor = autorRepository.save(autor);
            } else {
                autor = autorRepository.findByNome(autor.getNome());
            }

            Livro livro = new Livro(dadosLivro);
            livro.setAutor(autor);
            if (!livroRepository.existsByTitulo(livro.getTitulo())) {
                livroRepository.save(livro);
            }

            System.out.println("\nLivro salvo: " + livro);
        } catch (Exception e) {
            System.out.println("\nErro ao salvar o livro! ");
            e.getMessage();
        }
    }

    private void buscarLivroPeloTitulo() {
        System.out.println("Digite o título do livro que deseja buscar: ");
        var tituloLivro = leitura.nextLine();
        var dados = consumo.obterDados(ENDERECO+ tituloLivro.replace(" ","%20"));
        salvar(dados);
    }

    private void buscarAutorPeloNome() {
        System.out.println("Digite o nome do autor que deseja buscar: ");
        var busca = leitura.nextLine();
        var nomeAutor = autorRepository.encontrarPorNome(busca);
        if (!nomeAutor.isEmpty()){
            nomeAutor.forEach(System.out::println);
        } else {
            System.out.println("\nAutor não encontrado!");
        }
    }

    private void listarLivrosDeUmAutor() {
        System.out.println("Deseja listar livros de que autor?");
        var busca = leitura.nextLine();
        List<Livro> livros = autorRepository.listaLivrosPorAutor(busca);
        livros.forEach(System.out::println);
    }

    private void listarLivrosRegistrados() {
        var busca = livroRepository.findAll();
        if (!busca.isEmpty()) {
            System.out.println("\nLivros cadastrados no banco de dados: ");
            busca.forEach(System.out::println);
        } else {
            System.out.println("\nNenhum livro encontrado no banco de dados!");
        }
    }

    private void listarAutoresRegistrados() {
        var busca = autorRepository.findAll();
        if (!busca.isEmpty()) {
            System.out.println("\nAutores cadastrados no banco de dados:");
            busca.forEach(System.out::println);
        } else {
            System.out.println("\nNenhum autor encontrado no banco de dados!");
        }
    }

    private void listarAutoresVivosPorAno() {
        System.out.println("Qual ano deseja pesquisar?");
        var ano = leitura.nextInt();
        leitura.nextLine();
        var buscaAutores = autorRepository.buscarPorAnoDeFalecimento(ano);
        if (!buscaAutores.isEmpty()) {
            System.out.println("\nAtores vivos no ano de: " + ano);
            buscaAutores.forEach(System.out::println);
        } else {
            System.out.println("\nNenhum autor encontrado para esse ano!");
        }
    }

    private void listarLivrosPorIdioma() {
        var menuIdiomas = """
                    \nSelecione um idioma:
                    es - Espanhol
                    en - Inglês
                    fr - Francês
                    pt - Português
                    """;
        System.out.println(menuIdiomas);
        var idiomaSelecionado = leitura.nextLine();
        var buscaIdiomas = livroRepository.buscarIdiomas(idiomaSelecionado);
        if (!buscaIdiomas.isEmpty()) {
            System.out.println("\nLivros com o idioma selecionado: ");
            buscaIdiomas.forEach(System.out::println);
        } else {
            System.out.println("\nNenhum livro encontrado nesse idioma!");
        }
    }

    private void buscarTop10Livros() {
        List<Livro> topLivros = livroRepository.findTop10ByOrderByNumeroDeDownloadsDesc();
        topLivros.forEach(System.out::println);
    }
}
