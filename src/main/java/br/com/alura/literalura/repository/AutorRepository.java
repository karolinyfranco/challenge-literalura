package br.com.alura.literalura.repository;

import br.com.alura.literalura.model.Autor;
import br.com.alura.literalura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor,Long> {
    Boolean existsByNome(String nome);
    Autor findByNome(String nome);

    @Query("SELECT a FROM Autor a WHERE a.anoDeFalecimento >= :ano AND :ano >= a.anoDeNascimento")
    List<Autor> buscarPorAnoDeFalecimento(int ano);

    @Query("SELECT a FROM Autor a WHERE a.nome ILIKE %:busca%")
    List<Autor> encontrarPorNome(String busca);

    @Query("SELECT m FROM Autor a JOIN a.livros m WHERE a.nome ILIKE %:busca")
    List<Livro> listaLivrosPorAutor(String busca);
}
